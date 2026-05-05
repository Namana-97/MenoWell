import os
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from slowapi.errors import RateLimitExceeded

from app.api.routes import auth, chat, checkins, letter, profile
from app.core.config import settings
from app.db.session import Base, engine
from app.db.init_db import init_db
from .api.routes.insights import router as insights_router
from .api.routes.analytics import router as analytics_router
from .api.routes.token import router as token_router
from .middleware.rate_limit import limiter, rate_limit_exceeded_handler


@asynccontextmanager
async def lifespan(_: FastAPI):
    Base.metadata.create_all(bind=engine)
    await init_db()
    yield


app = FastAPI(title=settings.APP_NAME, lifespan=lifespan)

origins = []
if settings.CORS_ORIGINS.strip() != "*":
    origins = [origin.strip() for origin in settings.CORS_ORIGINS.split(",") if origin.strip()]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins or [
        "http://localhost",
        "http://10.0.2.2",
        os.getenv("FRONTEND_ORIGIN", "http://localhost"),
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, rate_limit_exceeded_handler)

app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(chat.router, tags=["chat"])
app.include_router(checkins.router, prefix="/checkin", tags=["checkins"])
app.include_router(letter.router, prefix="/letter", tags=["letters"])
app.include_router(profile.router, prefix="/profile", tags=["profile"])
app.include_router(insights_router)
app.include_router(analytics_router)
app.include_router(token_router)
@app.get("/")
async def root() -> dict:
    return {"message": "MenoWell API is running"}


@app.get("/health")
async def health() -> dict:
    return {"status": "ok"}
