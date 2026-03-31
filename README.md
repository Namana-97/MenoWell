# MenoWell Backend

FastAPI backend for the MenoWell mobile app.

## Features
- Register/login with JWT
- Chat endpoint powered by OpenAI
- Depression-signal detection
- Profile memory
- Daily check-ins
- Weekly letter generation

## Quick start
1. Create a virtual environment
2. Install dependencies: `pip install -r requirements.txt`
3. Set environment variables from `.env.example`
4. Run: `uvicorn app.main:app --reload`
