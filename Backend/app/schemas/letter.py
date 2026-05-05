from pydantic import BaseModel


class WeeklyLetterResponse(BaseModel):
    letter: str
    summary_points: list[str]
