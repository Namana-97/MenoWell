from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session

from app.db.deps import get_current_user, get_db
from app.middleware.rate_limit import limiter
from app.models.conversation import ConversationMessage
from app.models.user import User
from app.schemas.chat import ChatMessageRead, ChatRequest, ChatResponse
from app.services.depression import detect_depression_level, is_crisis
from app.services.memory import build_profile_context, get_or_create_profile, remember_from_chat
from app.services.mia import generate_mia_reply

router = APIRouter()


def build_recent_conversation_history(db: Session, user_id: int, limit: int = 12) -> list[dict[str, str]]:
    rows = (
        db.query(ConversationMessage)
        .filter(ConversationMessage.user_id == user_id)
        .order_by(ConversationMessage.created_at.desc(), ConversationMessage.id.desc())
        .limit(limit)
        .all()
    )
    ordered_rows = list(reversed(rows))
    return [{"role": row.role, "content": row.content} for row in ordered_rows]


@router.post("/chat", response_model=ChatResponse)
@limiter.limit("30/minute")
async def chat(
    request: Request,
    payload: ChatRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    profile = get_or_create_profile(db, current_user)
    memory_context = build_profile_context(profile)
    depression_level = detect_depression_level(payload.message)

    db.add(
        ConversationMessage(
            user_id=current_user.id,
            role="user",
            content=payload.message,
            depression_level=depression_level,
        )
    )
    db.commit()

    conversation_history = build_recent_conversation_history(db, current_user.id)
    reply = await generate_mia_reply(
        user_message=payload.message,
        memory_context=memory_context,
        depression_level=depression_level,
        conversation_history=conversation_history,
    )

    db.add(
        ConversationMessage(
            user_id=current_user.id,
            role="assistant",
            content=reply,
            depression_level=depression_level,
        )
    )
    db.commit()

    remember_from_chat(
        db,
        current_user,
        user_message=payload.message,
        assistant_reply=reply,
        depression_level=depression_level,
    )

    return ChatResponse(reply=reply, depression_level=depression_level, crisis=is_crisis(depression_level))


@router.get("/chat/history", response_model=list[ChatMessageRead])
def history(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    rows = (
        db.query(ConversationMessage)
        .filter(ConversationMessage.user_id == current_user.id)
        .order_by(ConversationMessage.created_at.desc())
        .limit(50)
        .all()
    )
    return list(reversed(rows))
