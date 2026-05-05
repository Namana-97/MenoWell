from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.models.user import User
from app.schemas.profile import UserProfileRead, UserProfileUpdate
from app.services.memory import get_or_create_profile, update_profile

router = APIRouter()


@router.get("", response_model=UserProfileRead)
def get_profile(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    return get_or_create_profile(db, current_user)


@router.put("", response_model=UserProfileRead)
def put_profile(
    payload: UserProfileUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return update_profile(db, current_user, payload)
