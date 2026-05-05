from pydantic import BaseModel, Field


class UserProfileBase(BaseModel):
    core_wounds: list[str] = Field(default_factory=list)
    joy_anchors: list[str] = Field(default_factory=list)
    anxiety_triggers: list[str] = Field(default_factory=list)
    depression_patterns: list[str] = Field(default_factory=list)
    physical_emotional_links: list[str] = Field(default_factory=list)
    strength_narrative: str = ""


class UserProfileRead(UserProfileBase):
    user_id: int

    model_config = {"from_attributes": True}


class UserProfileUpdate(UserProfileBase):
    pass
