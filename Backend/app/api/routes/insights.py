from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.models.user import User
from app.services.insights_service import compute_insights

router = APIRouter(prefix="/insights", tags=["insights"])


@router.get("/")
async def get_insights(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    return compute_insights(current_user.id, db)
