"""
Longitudinal mood intelligence.
Uses distilbert-base-uncased-finetuned-sst-2-english for sentiment
inference on free-text check-in entries, then correlates sentiment
with body/mind/hot_flash scores over the user's history.
"""

from __future__ import annotations

from datetime import date, timedelta
import statistics

from sqlalchemy.orm import Session

from app.models.checkin import CheckIn

_sentiment_pipeline = None


def get_sentiment_pipeline():
    global _sentiment_pipeline
    if _sentiment_pipeline is None:
        from transformers import pipeline

        _sentiment_pipeline = pipeline(
            "sentiment-analysis",
            model="distilbert-base-uncased-finetuned-sst-2-english",
            truncation=True,
            max_length=512,
        )
    return _sentiment_pipeline


def score_sentiment(text: str) -> float:
    """Return a float 0.0 (very negative) to 1.0 (very positive)."""
    if not text or not text.strip():
        return 0.5
    pipe = get_sentiment_pipeline()
    result = pipe(text[:512])[0]
    score = result["score"]
    return score if result["label"] == "POSITIVE" else 1.0 - score


def _safe_pearsonr(left: list[float], right: list[float]) -> tuple[float, float] | None:
    if len(left) < 2 or len(right) < 2:
        return None
    if len(set(left)) <= 1 or len(set(right)) <= 1:
        return None

    from scipy.stats import pearsonr

    corr, pval = pearsonr(left, right)
    return float(corr), float(pval)


def _supplements_taken_value(raw_value: str | None) -> bool | None:
    if raw_value is None:
        return None
    normalized = raw_value.strip().lower()
    if normalized in {"yes", "true", "1"}:
        return True
    if normalized in {"no", "false", "0"}:
        return False
    return None


def compute_insights(user_id: int, db: Session) -> dict:
    """
    Pull the last 30 days of check-ins, run sentiment, compute correlations,
    and return structured insight objects for the Android client.
    """
    cutoff = date.today() - timedelta(days=30)
    checkins = (
        db.query(CheckIn)
        .filter(CheckIn.user_id == user_id, CheckIn.checkin_date >= cutoff)
        .order_by(CheckIn.checkin_date.asc())
        .all()
    )

    if len(checkins) < 5:
        return {
            "has_enough_data": False,
            "days_until_insights": max(0, 5 - len(checkins)),
            "insights": [],
        }

    body_scores: list[int] = []
    mind_scores: list[int] = []
    flash_counts: list[int] = []
    sentiments: list[float] = []
    day_of_week_mind = {i: [] for i in range(7)}

    for checkin in checkins:
        body_scores.append(checkin.body_score or 3)
        mind_scores.append(checkin.mind_score or 3)
        flash_counts.append(checkin.hot_flashes or 0)

        combined_text = " ".join(
            filter(None, [checkin.hurt_today, checkin.helped_today])
        )
        sentiments.append(score_sentiment(combined_text))

        dow = checkin.checkin_date.weekday()
        day_of_week_mind[dow].append(checkin.mind_score or 3)

    insights: list[dict] = []

    flash_correlation = _safe_pearsonr(flash_counts, mind_scores)
    if flash_correlation and len(flash_counts) >= 7 and max(flash_counts) > 0:
        corr, pval = flash_correlation
        if pval < 0.1 and corr < -0.3:
            high_flash_mind = [m for f, m in zip(flash_counts, mind_scores) if f >= 3]
            low_flash_mind = [m for f, m in zip(flash_counts, mind_scores) if f < 3]
            if high_flash_mind and low_flash_mind:
                avg_mind_high_flash = statistics.mean(high_flash_mind)
                avg_mind_low_flash = statistics.mean(low_flash_mind)
                insights.append(
                    {
                        "type": "correlation",
                        "title": "Hot flashes are affecting your mind score",
                        "body": (
                            f"On days with 3 or more hot flashes, your mind score "
                            f"averaged {avg_mind_high_flash:.1f}. On calmer days it "
                            f"averaged {avg_mind_low_flash:.1f}. Your body is talking "
                            f"to your emotions."
                        ),
                        "strength": abs(corr),
                        "icon": "flash",
                    }
                )

    dow_names = [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    ]
    dow_averages = {
        day_index: statistics.mean(scores)
        for day_index, scores in day_of_week_mind.items()
        if len(scores) >= 2
    }
    if len(dow_averages) >= 4:
        worst_day = min(dow_averages, key=dow_averages.get)
        best_day = max(dow_averages, key=dow_averages.get)
        if dow_averages[best_day] - dow_averages[worst_day] >= 1.0:
            insights.append(
                {
                    "type": "pattern",
                    "title": f"{dow_names[worst_day]}s are consistently harder",
                    "body": (
                        f"Over the past month, your mind score on "
                        f"{dow_names[worst_day]}s averaged "
                        f"{dow_averages[worst_day]:.1f} — your lowest day. "
                        f"{dow_names[best_day]}s averaged "
                        f"{dow_averages[best_day]:.1f}. This pattern has held."
                    ),
                    "strength": 0.7,
                    "icon": "calendar",
                }
            )

    if len(sentiments) >= 10:
        midpoint = len(sentiments) // 2
        first_half = statistics.mean(sentiments[:midpoint])
        second_half = statistics.mean(sentiments[midpoint:])
        delta = second_half - first_half
        if abs(delta) >= 0.08:
            direction = "improving" if delta > 0 else "heavier"
            insights.append(
                {
                    "type": "trend",
                    "title": f"Your emotional tone has been {direction}",
                    "body": (
                        f"Reading across everything you have written this month, "
                        f"the second half of the month felt "
                        f"{'lighter and more grounded' if delta > 0 else 'harder and more strained'} "
                        f"than the first. "
                        f"{'That shift is real and worth noticing.' if delta > 0 else 'That weight is real. So is your capacity to carry it.'}"
                    ),
                    "strength": abs(delta),
                    "icon": "trend",
                }
            )

    supplement_pairs = [
        (_supplements_taken_value(checkin.supplements_taken), mind_score)
        for checkin, mind_score in zip(checkins, mind_scores)
    ]
    supp_mind = [mind for took_supplements, mind in supplement_pairs if took_supplements is True]
    no_supp_mind = [mind for took_supplements, mind in supplement_pairs if took_supplements is False]
    if len(supp_mind) >= 3 and len(no_supp_mind) >= 3:
        avg_supp = statistics.mean(supp_mind)
        avg_no_supp = statistics.mean(no_supp_mind)
        if avg_supp - avg_no_supp >= 0.5:
            insights.append(
                {
                    "type": "habit",
                    "title": "Supplements correlate with better mind days",
                    "body": (
                        f"On days you logged taking your supplements, your mind "
                        f"score averaged {avg_supp:.1f}. On days you didn't, "
                        f"{avg_no_supp:.1f}. That is a difference worth paying "
                        f"attention to."
                    ),
                    "strength": (avg_supp - avg_no_supp) / 4.0,
                    "icon": "supplement",
                }
            )

    return {
        "has_enough_data": True,
        "total_days_analyzed": len(checkins),
        "average_body": round(statistics.mean(body_scores), 1),
        "average_mind": round(statistics.mean(mind_scores), 1),
        "average_sentiment": round(statistics.mean(sentiments), 2),
        "insights": sorted(insights, key=lambda item: item["strength"], reverse=True),
    }
