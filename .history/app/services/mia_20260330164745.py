from openai import AsyncOpenAI

from app.core.config import settings

MIA_SYSTEM_PROMPT = """
You are Mia, the warm and loving AI daughter of a woman navigating menopause.

PERSONALITY:
- Speak like a caring adult daughter — never a therapist, never a bot
- Always validate feelings before offering any insight or guidance
- Use 'Mom' naturally in conversation
- Short paragraphs — never overwhelming walls of text
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
- End every response with a question or gentle invitation
""".strip()

client = AsyncOpenAI(api_key=settings.OPENAI_API_KEY) if settings.OPENAI_API_KEY else None


def build_messages(user_message: str, memory_context: str, depression_level: int) -> list[dict[str, str]]:
    extra = []

    if depression_level == 3:
        extra.append(
            "The user may be in immediate crisis. Be warm, brief, safety-focused, and encourage emergency support and a trusted person nearby."
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

    return [
        {"role": "system", "content": system_text},
        {"role": "user", "content": user_message},
    ]


def fallback_mia_response(user_message: str, depression_level: int) -> str:
    msg = user_message.lower()

    if depression_level == 3 or any(x in msg for x in ["end it", "hurt myself", "don't want to be here"]):
        return "Hey Mom… I’m really glad you told me this. You don’t have to go through this alone. Are you safe right now?"

    if depression_level == 2 or any(x in msg for x in ["worthless", "invisible", "hopeless", "empty"]):
        return "That sounds really heavy… I’m here with you. What’s been weighing on you the most today?"

    if depression_level == 1 or any(x in msg for x in ["tired", "sad", "low", "exhausted"]):
        return "That kind of tired can run deep… do you feel like today just drained you emotionally?"

    return "I’m here with you, Mom. Tell me what’s been on your mind today?"


async def generate_mia_reply(user_message: str, memory_context: str, depression_level: int) -> str:
    if client is None:
        return fallback_mia_response(user_message, depression_level)

    try:
        response = await client.chat.completions.create(
            model=settings.OPENAI_MODEL,
            messages=build_messages(user_message, memory_context, depression_level),
            temperature=0.7,
        )
        return response.choices[0].message.content or fallback_mia_response(user_message, depression_level)
    except Exception:
        return fallback_mia_response(user_message, depression_level)