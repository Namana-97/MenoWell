from datetime import date, datetime

from pydantic import BaseModel, Field


class CheckInCreate(BaseModel):
    in_one_word: str | None = None
    body_score: int | None = Field(default=None, ge=1, le=5)
    mind_score: int | None = Field(default=None, ge=1, le=5)
    hurt_today: str | None = None
    helped_today: str | None = None
    hot_flashes: int = Field(default=0, ge=0)
    supplements_taken: bool | None = None
    checkin_date: date | None = None


class CheckInRead(BaseModel):
    id: int
    user_id: int
    checkin_date: date
    in_one_word: str | None = None
    body_score: int | None = None
    mind_score: int | None = None
    hurt_today: str | None = None
    helped_today: str | None = None
    hot_flashes: int
    supplements_taken: str | None = None
    created_at: datetime

    model_config = {"from_attributes": True}
