from datetime import date, timedelta
import statistics

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.models.checkin import CheckIn
from app.models.user import User

router = APIRouter(prefix="/analytics", tags=["analytics"])


@router.get("/trends")
async def get_trends(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    cutoff = date.today() - timedelta(days=30)
    checkins = (
        db.query(CheckIn)
        .filter(CheckIn.user_id == current_user.id, CheckIn.checkin_date >= cutoff)
        .order_by(CheckIn.checkin_date.asc())
        .all()
    )

    if len(checkins) < 3:
        return {"has_data": False, "points": [], "correlations": []}

    points = [
        {
            "date": checkin.checkin_date.isoformat(),
            "body": checkin.body_score or 3,
            "mind": checkin.mind_score or 3,
            "hot_flashes": checkin.hot_flashes or 0,
        }
        for checkin in checkins
    ]

    correlations = []
    body_scores = [checkin.body_score or 3 for checkin in checkins]
    mind_scores = [checkin.mind_score or 3 for checkin in checkins]
    flash_counts = [checkin.hot_flashes or 0 for checkin in checkins]

    if len(body_scores) >= 5:
        high_body_days = [m for b, m in zip(body_scores, mind_scores) if b >= 4]
        low_body_days = [m for b, m in zip(body_scores, mind_scores) if b <= 2]
        if high_body_days and low_body_days:
            correlations.append(
                {
                    "label": "Body affects mind",
                    "stat_a": f"{statistics.mean(high_body_days):.1f}/5",
                    "stat_a_label": "mind on good body days",
                    "stat_b": f"{statistics.mean(low_body_days):.1f}/5",
                    "stat_b_label": "mind on hard body days",
                    "interpretation": "Your physical and emotional days move together.",
                }
            )

    high_flash = [m for f, m in zip(flash_counts, mind_scores) if f >= 3]
    low_flash = [m for f, m in zip(flash_counts, mind_scores) if f < 3]
    if high_flash and low_flash:
        correlations.append(
            {
                "label": "Hot flashes and your mind",
                "stat_a": f"{statistics.mean(high_flash):.1f}/5",
                "stat_a_label": "mind score on high flash days",
                "stat_b": f"{statistics.mean(low_flash):.1f}/5",
                "stat_b_label": "mind score on calm days",
                "interpretation": "Symptom intensity shapes emotional capacity.",
            }
        )

    if len(mind_scores) >= 14:
        windows = [
            statistics.mean(mind_scores[index:index + 7])
            for index in range(len(mind_scores) - 6)
        ]
        best_week_avg = max(windows)
        worst_week_avg = min(windows)
        correlations.append(
            {
                "label": "Your range this month",
                "stat_a": f"{best_week_avg:.1f}/5",
                "stat_a_label": "best 7-day stretch",
                "stat_b": f"{worst_week_avg:.1f}/5",
                "stat_b_label": "hardest 7-day stretch",
                "interpretation": "The distance between your best and hardest weeks.",
            }
        )

    return {
        "has_data": True,
        "total_days": len(checkins),
        "overall_body_avg": round(statistics.mean(body_scores), 1),
        "overall_mind_avg": round(statistics.mean(mind_scores), 1),
        "points": points,
        "correlations": correlations,
    }
