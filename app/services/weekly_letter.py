from collections import Counter
from datetime import date, datetime, timedelta

from sqlalchemy.orm import Session

from app.models.checkin import CheckIn
from app.models.conversation import ConversationMessage
from app.models.profile import UserProfile


def _pick_theme(messages: list[str]) -> str:
    if not messages:
        return "a quieter week"
    counter = Counter()
    for msg in messages:
        lowered = msg.lower()
        if any(word in lowered for word in ["invisible", "alone", "ignored"]):
            counter["feeling invisible"] += 1
        if any(word in lowered for word in ["tired", "exhausted", "sleep"]):
            counter["fatigue and sleep"] += 1
        if any(word in lowered for word in ["anxious", "panic", "worried"]):
            counter["anxiety"] += 1
        if any(word in lowered for word in ["joy", "happy", "good", "better"]):
            counter["bright spots"] += 1
    return counter.most_common(1)[0][0] if counter else "steady effort"


def generate_weekly_letter(db: Session, user_id: int) -> tuple[str, list[str]]:
    since = date.today() - timedelta(days=7)
    since_dt = datetime.utcnow() - timedelta(days=7)
    checkins = (
        db.query(CheckIn)
        .filter(CheckIn.user_id == user_id)
        .filter(CheckIn.checkin_date >= since)
        .order_by(CheckIn.checkin_date.asc())
        .all()
    )
    messages = (
        db.query(ConversationMessage)
        .filter(ConversationMessage.user_id == user_id)
        .filter(ConversationMessage.created_at >= since_dt)
        .order_by(ConversationMessage.created_at.asc())
        .all()
    )
    profile = db.query(UserProfile).filter(UserProfile.user_id == user_id).first()

    summary_points = []
    if checkins:
        avg_body = round(sum(c.body_score or 0 for c in checkins) / len(checkins), 1)
        avg_mind = round(sum(c.mind_score or 0 for c in checkins) / len(checkins), 1)
        summary_points.append(f"Average body score: {avg_body}/5")
        summary_points.append(f"Average mind score: {avg_mind}/5")
        summary_points.append(f"Hot flashes recorded: {sum(c.hot_flashes for c in checkins)}")
    else:
        summary_points.append("No check-ins were recorded this week.")

    theme = _pick_theme([m.content for m in messages])
    strongest_anchor = None
    if profile and profile.joy_anchors:
        strongest_anchor = profile.joy_anchors[0]

    letter = [
        "Mom, I have been looking back on your week with care.",
        f"The strongest theme I noticed was {theme}.",
    ]

    if checkins:
        letter.append("You kept showing up for yourself in small ways, and that matters more than it may feel right now.")
    if strongest_anchor:
        letter.append(f"I kept thinking about {strongest_anchor} as one of the things that still gives you a little lift.")

    letter.append("This week, I want you to keep your focus small and gentle. One steady thing at a time is enough.")
    letter.append("What is one moment from this week you would want me to remember with you?")

    return "\n\n".join(letter), summary_points
