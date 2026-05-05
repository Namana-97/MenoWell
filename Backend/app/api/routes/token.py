from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.services.token_service import create_access_token, decode_refresh_token

router = APIRouter(prefix="/auth", tags=["auth"])


class RefreshRequest(BaseModel):
    refresh_token: str


@router.post("/refresh")
def refresh_access_token(body: RefreshRequest):
    try:
        user_id = decode_refresh_token(body.refresh_token)
        return {"access_token": create_access_token(user_id)}
    except Exception as exc:
        raise HTTPException(status_code=401, detail="Invalid or expired refresh token") from exc
