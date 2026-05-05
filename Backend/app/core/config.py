from functools import lru_cache

from pydantic import model_validator
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    APP_NAME: str = "MenoWell API"
    DATABASE_URL: str = "sqlite:///./menowell.db"
    ENVIRONMENT: str = "development"
    SECRET_KEY: str = "change-me"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 3
    MODEL_PROVIDER: str = "ollama"
    OPENAI_API_KEY: str = ""
    OPENAI_MODEL: str = "gpt-4o-mini"
    OLLAMA_BASE_URL: str = "http://localhost:11434/api"
    OLLAMA_MODEL: str = "gemma3"
    OLLAMA_API_KEY: str = ""
    OLLAMA_TIMEOUT_SECONDS: float = 8.0
    CORS_ORIGINS: str = "*"

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    @model_validator(mode="after")
    def validate_security(self) -> "Settings":
        if self.ENVIRONMENT.lower() != "development" and self.SECRET_KEY == "change-me":
            raise ValueError("SECRET_KEY must be set to a non-default value outside development")
        return self


@lru_cache
def get_settings() -> Settings:
    return Settings()


settings = get_settings()
