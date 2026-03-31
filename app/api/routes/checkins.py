from datetime import date, timedelta

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.models.checkin import CheckIn
from app.models.user import User
from app.schemas.checkin import CheckInCreate, CheckInRead

router = APIRouter()


@router.post("", response_model=CheckInRead)
def submit_checkin(
    payload: CheckInCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    record = CheckIn(
        user_id=current_user.id,
        checkin_date=payload.checkin_date or date.today(),
        in_one_word=payload.in_one_word,
        body_score=payload.body_score,
        mind_score=payload.mind_score,
        hurt_today=payload.hurt_today,
        helped_today=payload.helped_today,
        hot_flashes=payload.hot_flashes,
        supplements_taken=("yes" if payload.supplements_taken else "no") if payload.supplements_taken is not None else None,
    )
    db.add(record)
    db.commit()
    db.refresh(record)
    return record


@router.get("/week", response_model=list[CheckInRead])
def week_checkins(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    since = date.today() - timedelta(days=7)
    rows = (
        db.query(CheckIn)
        .filter(CheckIn.user_id == current_user.id)
        .filter(CheckIn.checkin_date >= since)
        .order_by(CheckIn.checkin_date.asc())
        .all()
    )
    return rows
