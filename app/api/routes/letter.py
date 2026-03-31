from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.models.user import User
from app.schemas.letter import WeeklyLetterResponse
from app.services.weekly_letter import generate_weekly_letter

router = APIRouter()


@router.get("/weekly", response_model=WeeklyLetterResponse)
def weekly_letter(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    letter, summary_points = generate_weekly_letter(db, current_user.id)
    return WeeklyLetterResponse(letter=letter, summary_points=summary_points)
