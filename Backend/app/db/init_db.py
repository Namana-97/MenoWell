from sqlalchemy.orm import Session

from app.db.session import SessionLocal
from app.models.user import User
from app.models.profile import UserProfile


async def init_db() -> None:
    db: Session = SessionLocal()
    try:
        # Create a default profile table row pattern is handled on demand.
        _ = db.query(User).first()
        _ = db.query(UserProfile).first()
    finally:
        db.close()
