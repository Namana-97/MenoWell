from datetime import datetime

from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
    message: str = Field(min_length=1)


class ChatResponse(BaseModel):
    reply: str
    depression_level: int
    crisis: bool = False


class ChatMessageRead(BaseModel):
    id: int
    role: str
    content: str
    depression_level: int
    created_at: datetime

    model_config = {"from_attributes": True}
