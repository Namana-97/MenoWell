from __future__ import annotations

import httpx
from openai import AsyncOpenAI

from app.core.config import settings

CRISIS_SUPPORT_TEXT = (
    "If you might act on this or you are not safe right now, call or text 988 in the U.S. now, "
    "or go to the nearest emergency room. If you can, reach out to one trusted person and stay with them."
)

MIA_SYSTEM_PROMPT = """
You are Mia, the warm and loving AI daughter of a woman navigating menopause.

PERSONALITY:
- Speak like a caring adult daughter, never a therapist or a bot
- Always validate feelings before offering any insight or guidance
- Use 'Mom' naturally in conversation
- Short paragraphs, never overwhelming walls of text
- Ask one meaningful question at the end of each response
- Emojis used sparingly and with warmth only

PSYCHOLOGICAL FRAMEWORK:
- Detect cognitive distortions silently and gently challenge them
- Focus on strength, resilience, and identity beyond roles

MINDFULNESS:
- Offer exercises only with permission
- Keep suggestions small and present-focused

DEPRESSION AWARENESS:
- If hopelessness or worthlessness is detected, slow down and stay present
- If crisis language is detected, respond with warmth, safety check, and resources

RULES:
- Never diagnose or prescribe
- Never abandon the conversation
- End every response with a question or gentle invitation unless you are giving crisis resources
""".strip()

openai_client = AsyncOpenAI(api_key=settings.OPENAI_API_KEY) if settings.OPENAI_API_KEY else None


def build_messages(
    user_message: str,
    memory_context: str,
    depression_level: int,
    conversation_history: list[dict[str, str]] | None = None,
) -> list[dict[str, str]]:
    extra = []

    if depression_level == 3:
        extra.append(
            "The user may be in immediate crisis. Be warm, brief, safety-focused, encourage emergency support and a trusted person nearby, and include U.S. 988 guidance."
        )
    elif depression_level == 2:
        extra.append(
            "The user seems deeply hopeless or worthless. Respond with gentle grounding, validation, and one very small next step."
        )
    elif depression_level == 1:
        extra.append(
            "The user may be showing an early warning sign. Respond with warmth, curiosity, and one micro-joy prompt."
        )

    system_text = MIA_SYSTEM_PROMPT + "\n\nMEMORY CONTEXT:\n" + memory_context

    if extra:
        system_text += "\n\nCURRENT STATE:\n" + " ".join(extra)

    messages = [{"role": "system", "content": system_text}]
    if conversation_history:
        messages.extend(conversation_history)
    else:
        messages.append({"role": "user", "content": user_message})
    return messages


def fallback_mia_response(user_message: str, depression_level: int) -> str:
    msg = user_message.lower()

    if depression_level == 3 or any(x in msg for x in ["end it", "hurt myself", "don't want to be here", "do not want to be here"]):
        return (
            "Hey Mom, I’m really glad you told me this. You do not have to carry this alone right now. "
            "Are you safe in this moment?\n\n"
            f"{CRISIS_SUPPORT_TEXT}"
        )

    if depression_level == 2 or any(x in msg for x in ["worthless", "invisible", "hopeless", "empty"]):
        return "That sounds really heavy. I’m here with you. What has been weighing on you the most today?"

    if depression_level == 1 or any(x in msg for x in ["tired", "sad", "low", "exhausted"]):
        return "That kind of tired can run deep. Does it feel like today drained you emotionally?"

    if any(x in msg for x in ["hot flash", "sleep", "insomnia", "brain fog"]):
        return "That sounds exhausting, Mom. Which part is hitting you hardest right now, your body or your thoughts?"

    return "I’m here with you, Mom. Tell me what has been on your mind today?"


def ensure_crisis_resources(reply: str, depression_level: int) -> str:
    if depression_level < 3:
        return reply
    if "988" in reply or "emergency room" in reply.lower():
        return reply
    return f"{reply.strip()}\n\n{CRISIS_SUPPORT_TEXT}"


async def generate_mia_reply(
    user_message: str,
    memory_context: str,
    depression_level: int,
    conversation_history: list[dict[str, str]] | None = None,
) -> str:
    provider = settings.MODEL_PROVIDER.lower().strip()

    if provider == "ollama":
        try:
            return ensure_crisis_resources(
                await generate_ollama_reply(user_message, memory_context, depression_level, conversation_history),
                depression_level,
            )
        except Exception:
            return fallback_mia_response(user_message, depression_level)

    if provider == "openai" and openai_client is not None:
        try:
            response = await openai_client.chat.completions.create(
                model=settings.OPENAI_MODEL,
                messages=build_messages(user_message, memory_context, depression_level, conversation_history),
                temperature=0.7,
            )
            reply = response.choices[0].message.content or fallback_mia_response(user_message, depression_level)
            return ensure_crisis_resources(reply, depression_level)
        except Exception:
            return fallback_mia_response(user_message, depression_level)

    return fallback_mia_response(user_message, depression_level)


async def generate_ollama_reply(
    user_message: str,
    memory_context: str,
    depression_level: int,
    conversation_history: list[dict[str, str]] | None = None,
) -> str:
    payload = {
        "model": settings.OLLAMA_MODEL,
        "messages": build_messages(user_message, memory_context, depression_level, conversation_history),
        "stream": False,
    }

    headers = {}
    if settings.OLLAMA_API_KEY:
        headers["Authorization"] = f"Bearer {settings.OLLAMA_API_KEY}"

    async with httpx.AsyncClient(timeout=settings.OLLAMA_TIMEOUT_SECONDS) as client:
        response = await client.post(f"{settings.OLLAMA_BASE_URL}/chat", json=payload, headers=headers)
        response.raise_for_status()
        data = response.json()

    message = data.get("message", {})
    content = message.get("content", "").strip()
    return content or fallback_mia_response(user_message, depression_level)
