from datetime import datetime

from sqlalchemy.orm import Session

from app.models.profile import UserProfile
from app.models.user import User
from app.schemas.profile import UserProfileUpdate


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
        setattr(profile, field, value)
    profile.last_updated_at = datetime.utcnow()
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
