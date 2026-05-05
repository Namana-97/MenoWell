# MenoWell

**AI mental health companion for women navigating menopause.**

Mia is not a chatbot. She is a conversational presence with longitudinal memory, she reads 30 days of a woman's emotional and physical data, runs sentiment analysis across her written entries, surfaces patterns she would never notice herself, and responds with the warmth of someone who has been paying attention.

> *"Your hardest moments were Wednesday evenings. Your mind score drops 1.4 points on high hot-flash days. The second half of this month felt lighter than the first."*
>
> This is not generated text. It is derived from her own data.

---

## What the app does

A woman opens MenoWell on a hard day. She does not fill out a clinical form. She writes one word - *heavy* - and moves two sliders. That is enough. Over time, those entries become a dataset. Mia reads that dataset and tells her what it means.

The product has five layers:

**Mia - the AI companion.** A conversational assistant with a defined psychological persona: CBT-informed, crisis-aware, never clinical. She holds context across sessions and responds like someone who remembers.

**Daily check-in.** Body score, mind score, hot flash count, free text - one entry per day, under 60 seconds. The emotional raw material everything else is built from.

**Mood intelligence engine.** A longitudinal analysis pipeline that runs `distilbert-base-uncased-finetuned-sst-2-english` across the user's written entries, then correlates sentiment scores with physical symptom data using Pearson correlation. Statistically significant patterns are surfaced as human-readable insights.

**Patterns dashboard.** A 30-day visualisation of body and mind trends rendered with MPAndroidChart, alongside computed correlation cards - for example, the average mind score on high hot-flash days versus calm days.

**Weekly letter.** Every Sunday, Mia writes a narrative summary of the week - pattern observations, emotional themes, one gentle focus - not statistics, not a report card.

---

## Technical architecture

```text
Android (Kotlin)          Backend (Python)            AI layer
-----------------         ----------------            -----------------------------
Jetpack Compose     --->  FastAPI                     GPT-4o (conversation)
MVVM + Clean Arch         SQLAlchemy ORM              distilbert (sentiment)
Hilt DI                   PostgreSQL                  Pearson correlation (scipy)
Retrofit + OkHttp         Alembic migrations          Day-of-week pattern analysis
DataStore                 JWT + refresh tokens        7-day window analysis
MPAndroidChart            Rate limiting (slowapi)
```

---

## Stack

| Layer | Technology |
|---|---|
| Android UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM, Clean Architecture, Hilt dependency injection |
| Networking | Retrofit, OkHttp (header-only logging - no body logging of sensitive data) |
| Backend | Python, FastAPI |
| Database | PostgreSQL with Alembic migration management |
| Auth | JWT access tokens (15-minute expiry) + refresh tokens (7-day expiry) |
| AI - conversation | OpenAI GPT-4o via prompt-engineered persona with persistent session context |
| AI - sentiment | HuggingFace `distilbert-base-uncased-finetuned-sst-2-english` |
| AI - pattern analysis | Pearson correlation, day-of-week aggregation, rolling 7-day window analysis |
| Rate limiting | slowapi, 30 requests/minute on the chat endpoint |
| Deployment | Docker Compose (PostgreSQL + FastAPI in one command) |
| Testing | JUnit + Mockito (Android ViewModels), pytest flow tests (backend) |

---

## AI and data pipeline - how it actually works

Most "AI" apps in this space are wrappers around a chat API. MenoWell has a second layer that does something more specific.

### Conversation layer

Mia's conversational intelligence is prompt-engineered with a defined psychological framework: she detects cognitive distortions silently (catastrophising, self-blame, worthlessness, all-or-nothing thinking) and challenges them with Socratic questions rather than direct correction. She has three-level crisis detection with escalating response protocols and mandatory professional resource surfacing at level 3.

Her memory is not stateless. Every session is prefixed with a psychological profile built from the user's own words - joy anchors, anxiety triggers, physical-emotional links, depression patterns, strength narrative. This profile is updated after each session.

### Longitudinal intelligence layer

This is the technically interesting part.

Every night (or on demand via `/insights`), the backend pulls the user's last 30 check-in entries. For each entry with free text, it runs inference through `distilbert-base-uncased-finetuned-sst-2-english` to produce a sentiment score between 0.0 (very negative) and 1.0 (very positive). The model is loaded once at module level and cached - warm on first call, near-instant after.

Those sentiment scores are then correlated against physical symptom data using `scipy.stats.pearsonr`. Correlations that pass a p < 0.1 threshold and meet a minimum effect size are surfaced as insights. The system also computes day-of-week mood averages (surfacing consistent hard days), rolling 7-day window analysis (identifying best and worst weekly stretches), and first-half vs second-half sentiment trends.

The result is a set of insight objects that Mia surfaces in natural language - not the raw numbers, but what they mean for this specific woman.

This is not model training or fine-tuning. It is inference-over-longitudinal-data - a genuinely different problem from single-turn chat.

---

## Why this demographic

Over 1 billion women will be in menopause by 2025. The psychological toll - depression, anxiety, identity disruption, emotional isolation - is almost entirely ignored by existing tools. Symptom trackers record what happened to the body. MenoWell records what happened to the person.

There is no dominant app in this space. The closest competitors are generic wellness apps with a menopause content pack. None of them have longitudinal AI analysis. None of them have a conversational persona with psychological frameworks. None of them remember who she is.

---

## Security and responsible design

This app handles intimate mental health disclosures. That shaped every engineering decision.

- OkHttp logs headers only - request and response bodies are never written to logcat in any build
- Cleartext traffic disabled globally; emulator loopback exempted via `network_security_config.xml`
- CORS locked to explicit origin list - no wildcard
- JWT access tokens expire in 15 minutes; refresh tokens rotate on use with 7-day expiry
- Chat endpoint rate-limited to 30 requests/minute per IP
- Crisis language detection triggers immediate compassionate response with professional resources - Mia never abandons the conversation
- No diagnosis, no prescription, no medical claims anywhere in the app or system prompt
- Ethical disclaimer present at first launch and in the weekly letter footer

---

## Screens

| Screen | What it does |
|---|---|
| Home | Contextual greeting based on last session mood. Quick access to Mia, check-in, weekly letter, profile. Motivational strip. |
| Talk to Mia | Full conversational UI. Quick-emotion chips. Crisis-aware mode accessible instantly. Warm message bubbles with Mia's memory active. |
| Daily check-in | Body/mind sliders, hot flash count, free text ("something that hurt", "something that helped"), supplement toggle. Under 60 seconds. |
| Mia noticed something | Insight cards derived from 30-day longitudinal analysis. Requires 5+ check-ins to unlock. |
| Patterns | 30-day body/mind line chart (MPAndroidChart). Correlation cards with plain-language interpretation. |
| Weekly letter | Mia's Sunday narrative summary. Mood trend line. Emotional themes. Affirmation from her own words. |
| Memory map | Editable psychological profile - joy anchors, triggers, patterns, physical links. Read-only view shows what Mia knows. |

---

## API endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register with email + password (PBKDF2-SHA256 hashed) |
| POST | `/auth/login` | Returns access token (15 min) + refresh token (7 days) |
| POST | `/auth/refresh` | Exchange refresh token for new access token |
| POST | `/chat` | Send message to Mia - returns AI response with full session memory and crisis escalation logic |
| GET | `/chat/history` | Retrieve conversation history |
| POST | `/checkin` | Submit daily check-in (one per day, upsert on same date) |
| GET | `/checkin/week` | Last 7 days of check-in data for charts |
| GET | `/insights` | Run longitudinal sentiment + correlation analysis over last 30 days |
| GET | `/analytics/trends` | Time-series data + correlation cards for the Patterns dashboard |
| GET | `/letter/weekly` | Generate and retrieve weekly narrative letter |
| GET | `/profile` | Retrieve psychological memory profile |
| PUT | `/profile` | Update profile (manual or auto after session) |

---

## Run locally

**Prerequisites:** Docker, Android Studio, an OpenAI API key.

```bash
# Clone
git clone https://github.com/Namana-97/MenoWell.git
cd MenoWell

# Configure environment
cp .env.example .env
# Add your OPENAI_API_KEY to .env

# Start backend + database
docker-compose up --build

# Backend will be live at http://localhost:8000
# Alembic migrations run automatically on startup

# Open Android project
# Open MobileApp/ in Android Studio
# Run on emulator (API 26+)
```

The backend runs two services: PostgreSQL and FastAPI. The HuggingFace sentiment model downloads on first `/insights` call (~250MB, cached after).

---

## Architecture decisions

**Why MVVM over MVI.** The app's state complexity does not justify the overhead of a unidirectional data flow architecture. Each screen has well-scoped, independent state. MVVM with Kotlin StateFlow gives clean separation and testable ViewModels without the boilerplate MVI requires at this scale.

**Why FastAPI over Django.** The backend is a thin API layer with no server-side rendering requirements. FastAPI's async-first design, automatic OpenAPI generation, and Pydantic validation make it significantly faster to develop and test for this use case than Django REST Framework.

**Why prompt-engineered memory over vector embeddings.** At the current user scale and session depth, building a full RAG pipeline with a vector store would add infrastructure complexity without meaningful quality improvement. The psychological profile passed as structured context to each session is interpretable, editable by the user, and produces consistent results. The tradeoff is manual curation - the next version would introduce embeddings for semantic retrieval across long conversation history.

**Why distilbert for sentiment.** The alternative was sending free-text entries to GPT-4o for sentiment scoring - which is expensive at scale and introduces a round-trip latency for something that does not need reasoning, only classification. distilbert is 66M parameters, runs locally in the backend container, produces consistent scores, and costs nothing per inference. For the nuance required here (detecting emotional tone in short personal writing), it performs well enough.

**What I would change for production scale.** PostgreSQL handles current load comfortably, but the `/insights` endpoint is compute-intensive for users with long check-in histories. At scale this becomes a background job triggered by check-in submission, with results cached and served immediately - not computed on request.

---

## Testing

**Backend** - flow tests covering: user registration and login, JWT token issuance and refresh, daily check-in submission and upsert logic, chat memory persistence across sessions, crisis language detection and response escalation, follow-up context continuity.

**Android** - ViewModel unit tests covering: InsightsViewModel loading, data-ready, and error states with mocked repository; coroutine dispatcher swapping with `UnconfinedTestDispatcher`.

```bash
# Backend tests
cd Backend
pytest

# Android unit tests
./gradlew test
```

---

## What's next

**Voice input.** She speaks to Mia naturally. No typing required on hard days.

**Wearable integration.** Pull sleep and heart rate from Google Fit or Wear OS. Correlate autonomic data with emotional state automatically - removing the self-reporting step from the insight pipeline.

**Predictive modelling.** With 60+ days of per-user data, train a lightweight LSTM or gradient boosted model per user to predict high-risk days 48 hours in advance. Mia reaches out proactively on those days.

**Doctor export.** A one-tap monthly PDF summary - mood trends, symptom patterns, notable weeks - formatted for a physician appointment. The one artefact that closes the loop between emotional self-tracking and clinical care.

**Localization.** Spanish, Hindi, Arabic. The menopause emotional support gap is a global problem, not an English one.

---

## Resume bullet

*Built MenoWell, a full-stack Android mental health companion for menopausal women using Kotlin, Jetpack Compose, FastAPI, and PostgreSQL. Engineered a longitudinal mood intelligence pipeline using distilbert sentiment inference and Pearson correlation to surface clinically meaningful patterns from 30 days of personal health data. Implemented CBT-informed conversational AI with three-level crisis detection, persistent psychological profiling, and JWT refresh token auth. Deployed via Docker Compose with Alembic migrations, rate limiting, and network-level security hardening for a sensitive health data context.*

---

*MenoWell is a portfolio project. It is not a medical device. Crisis resources are always surfaced within the app. If you are experiencing a mental health emergency, please contact a qualified professional or call 988.*
