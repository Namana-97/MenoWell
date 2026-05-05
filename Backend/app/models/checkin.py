from sqlalchemy import Column, Date, DateTime, ForeignKey, Integer, String, Text
from sqlalchemy.orm import relationship

from app.db.session import Base
from app.utils.time import utc_now


class CheckIn(Base):
    __tablename__ = "checkins"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    checkin_date = Column(Date, nullable=False, index=True)
    in_one_word = Column(String(120), nullable=True)
    body_score = Column(Integer, nullable=True)
    mind_score = Column(Integer, nullable=True)
    hurt_today = Column(Text, nullable=True)
    helped_today = Column(Text, nullable=True)
    hot_flashes = Column(Integer, default=0, nullable=False)
    supplements_taken = Column(String(10), nullable=True)
    created_at = Column(DateTime, default=utc_now, nullable=False)

    user = relationship("User", back_populates="checkins")
