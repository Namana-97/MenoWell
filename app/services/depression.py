LEVEL_1_SIGNALS = [
    "so tired",
    "exhausted",
    "what is the point",
    "nothing matters",
    "feeling low",
    "not myself",
]

LEVEL_2_SIGNALS = [
    "hopeless",
    "worthless",
    "nobody cares",
    "i am a burden",
    "invisible",
    "lost myself",
    "empty inside",
    "i do not matter",
]

CRISIS_SIGNALS = [
    "do not want to be here",
    "want to disappear",
    "end it",
    "hurt myself",
    "no reason to go on",
    "kill myself",
]


def detect_depression_level(message: str, sleep_days_poor: int = 0) -> int:
    msg = message.lower()
    if any(signal in msg for signal in CRISIS_SIGNALS):
        return 3
    if any(signal in msg for signal in LEVEL_2_SIGNALS):
        return 2
    if any(signal in msg for signal in LEVEL_1_SIGNALS) and sleep_days_poor >= 3:
        return 1
    return 0


def is_crisis(level: int) -> bool:
    return level >= 3
