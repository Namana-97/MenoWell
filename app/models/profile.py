from datetime import datetime

from sqlalchemy import Column, DateTime, ForeignKey, Integer, JSON, Text
from sqlalchemy.orm import relationship

from app.db.session import Base


class UserProfile(Base):
    __tablename__ = "user_profiles"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True, nullable=False)
    core_wounds = Column(JSON, default=list)
    joy_anchors = Column(JSON, default=list)
    anxiety_triggers = Column(JSON, default=list)
    depression_patterns = Column(JSON, default=list)
    physical_emotional_links = Column(JSON, default=list)
    strength_narrative = Column(Text, default="")
    last_updated_at = Column(DateTime, default=datetime.utcnow, nullable=False)

    user = relationship("User", back_populates="profile")
