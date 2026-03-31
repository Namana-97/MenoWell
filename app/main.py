from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.routes import auth, chat, checkins, letter, profile
from app.core.config import settings
from app.db.session import Base, engine
from app.db.init_db import init_db

app = FastAPI(title=settings.APP_NAME)

origins = [origin.strip() for origin in settings.CORS_ORIGINS.split(",") if origin.strip()]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins or ["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(chat.router, tags=["chat"])
app.include_router(checkins.router, prefix="/checkin", tags=["checkins"])
app.include_router(letter.router, prefix="/letter", tags=["letters"])
app.include_router(profile.router, prefix="/profile", tags=["profile"])


@app.on_event("startup")
async def on_startup() -> None:
    Base.metadata.create_all(bind=engine)
    await init_db()


@app.get("/")
async def root() -> dict:
    return {"message": "MenoWell API is running"}


@app.get("/health")
async def health() -> dict:
    return {"status": "ok"}
