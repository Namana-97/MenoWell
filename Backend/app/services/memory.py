from __future__ import annotations

from sqlalchemy.orm import Session

from app.models.profile import UserProfile
from app.models.user import User
from app.schemas.profile import UserProfileUpdate
from app.utils.time import utc_now

JOY_KEYWORDS = {
    "walk": "walks",
    "walking": "walks",
    "tea": "tea",
    "music": "music",
    "bath": "warm baths",
    "rest": "rest",
    "friend": "close friends",
    "daughter": "family connection",
    "nature": "time in nature",
    "prayer": "prayer",
    "sleep": "sleep",
}

TRIGGER_KEYWORDS = {
    "crowd": "crowded spaces",
    "noise": "noise",
    "work": "work stress",
    "sleep": "poor sleep",
    "insomnia": "insomnia",
    "hot flash": "hot flashes",
    "flash": "hot flashes",
    "argument": "conflict",
    "alone": "feeling alone",
    "ignored": "feeling ignored",
}

PHYSICAL_LINK_KEYWORDS = {
    "hot flash": "hot flashes affect mood",
    "flash": "hot flashes affect mood",
    "brain fog": "brain fog affects confidence",
    "sleep": "poor sleep affects emotions",
    "insomnia": "insomnia affects emotions",
    "tired": "fatigue affects emotions",
    "exhausted": "fatigue affects emotions",
}

DEPRESSION_PATTERN_MAP = {
    1: "early emotional drop",
    2: "hopelessness language",
    3: "crisis language",
}


def get_or_create_profile(db: Session, user: User) -> UserProfile:
    profile = db.query(UserProfile).filter(UserProfile.user_id == user.id).first()
    if profile:
        return profile

    profile = UserProfile(user_id=user.id)
    db.add(profile)
    db.commit()
    db.refresh(profile)
    return profile


def update_profile(db: Session, user: User, payload: UserProfileUpdate) -> UserProfile:
    profile = get_or_create_profile(db, user)
    for field, value in payload.model_dump().items():
        setattr(profile, field, _normalize_profile_value(field, value))
    profile.last_updated_at = utc_now()
    db.commit()
    db.refresh(profile)
    return profile


def build_profile_context(profile: UserProfile | None) -> str:
    if not profile:
        return "No stored profile memory yet."

    parts = []
    if profile.core_wounds:
        parts.append(f"Core wounds: {', '.join(profile.core_wounds)}")
    if profile.joy_anchors:
        parts.append(f"Joy anchors: {', '.join(profile.joy_anchors)}")
    if profile.anxiety_triggers:
        parts.append(f"Anxiety triggers: {', '.join(profile.anxiety_triggers)}")
    if profile.depression_patterns:
        parts.append(f"Depression patterns: {', '.join(profile.depression_patterns)}")
    if profile.physical_emotional_links:
        parts.append(f"Physical-emotional links: {', '.join(profile.physical_emotional_links)}")
    if profile.strength_narrative:
        parts.append(f"Strength narrative: {profile.strength_narrative}")
    return " | ".join(parts) if parts else "No stored profile memory yet."


def remember_from_chat(
    db: Session,
    user: User,
    user_message: str,
    assistant_reply: str,
    depression_level: int,
) -> UserProfile:
    profile = get_or_create_profile(db, user)
    lowered = user_message.lower()

    profile.joy_anchors = _merge_keywords(profile.joy_anchors, lowered, JOY_KEYWORDS)
    profile.anxiety_triggers = _merge_keywords(profile.anxiety_triggers, lowered, TRIGGER_KEYWORDS)
    profile.physical_emotional_links = _merge_keywords(profile.physical_emotional_links, lowered, PHYSICAL_LINK_KEYWORDS)

    if depression_level > 0:
        profile.depression_patterns = _append_unique(profile.depression_patterns, DEPRESSION_PATTERN_MAP[depression_level])

    if any(word in lowered for word in ["made it through", "kept going", "showed up", "trying", "trying my best"]):
        profile.strength_narrative = _merge_strength_narrative(
            profile.strength_narrative,
            "She keeps showing up for herself, even on the hard days.",
        )

    if "thank you" in assistant_reply.lower() and not profile.strength_narrative:
        profile.strength_narrative = "She is still reaching for support and honesty."

    profile.last_updated_at = utc_now()
    db.commit()
    db.refresh(profile)
    return profile


def remember_from_checkin(
    db: Session,
    user: User,
    *,
    hurt_today: str | None,
    helped_today: str | None,
    body_score: int | None,
    mind_score: int | None,
    hot_flashes: int,
) -> UserProfile:
    profile = get_or_create_profile(db, user)

    if hurt_today:
        lowered_hurt = hurt_today.lower()
        profile.anxiety_triggers = _merge_keywords(profile.anxiety_triggers, lowered_hurt, TRIGGER_KEYWORDS)
        profile.physical_emotional_links = _merge_keywords(profile.physical_emotional_links, lowered_hurt, PHYSICAL_LINK_KEYWORDS)

    if helped_today:
        lowered_helped = helped_today.lower()
        profile.joy_anchors = _merge_keywords(profile.joy_anchors, lowered_helped, JOY_KEYWORDS)
        if any(word in lowered_helped for word in ["rest", "walk", "breathe", "tea", "friend", "sleep"]):
            profile.strength_narrative = _merge_strength_narrative(
                profile.strength_narrative,
                "She notices and uses the small things that help.",
            )

    if hot_flashes >= 3:
        profile.physical_emotional_links = _append_unique(profile.physical_emotional_links, "hot flashes affect mood")
    if body_score is not None and body_score <= 2:
        profile.physical_emotional_links = _append_unique(profile.physical_emotional_links, "low body-energy days affect emotions")
    if mind_score is not None and mind_score <= 2:
        profile.depression_patterns = _append_unique(profile.depression_patterns, "low mind-score days")

    profile.last_updated_at = utc_now()
    db.commit()
    db.refresh(profile)
    return profile


def _normalize_profile_value(field: str, value):
    if isinstance(value, list):
        return _dedupe_list(value)
    if field == "strength_narrative":
        return (value or "").strip()
    return value


def _merge_keywords(target: list[str], text: str, mapping: dict[str, str]) -> list[str]:
    values = list(target or [])
    for keyword, label in mapping.items():
        if keyword in text:
            values = _append_unique(values, label)
    return values


def _append_unique(target: list[str], value: str) -> list[str]:
    values = list(target or [])
    normalized = value.strip()
    if not normalized:
        return values
    if normalized.lower() not in {item.lower() for item in values}:
        values.append(normalized)
    return values


def _dedupe_list(values: list[str]) -> list[str]:
    result: list[str] = []
    for value in values:
        normalized = value.strip()
        if normalized and normalized.lower() not in {item.lower() for item in result}:
            result.append(normalized)
    return result


def _merge_strength_narrative(current: str, addition: str) -> str:
    current = current.strip()
    if not current:
        return addition
    if addition.lower() in current.lower():
        return current
    return f"{current} {addition}".strip()
