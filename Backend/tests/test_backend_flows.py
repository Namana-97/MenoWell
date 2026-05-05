import os
import shutil
import tempfile
import unittest
from unittest.mock import AsyncMock, patch

from fastapi.testclient import TestClient


class BackendFlowTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp_dir = tempfile.mkdtemp(prefix="menowell-tests-")
        cls.db_path = os.path.join(cls.temp_dir, "test.db")
        os.environ["DATABASE_URL"] = f"sqlite:///{cls.db_path}"
        os.environ["SECRET_KEY"] = "test-secret-key"
        os.environ["ENVIRONMENT"] = "development"
        os.environ["MODEL_PROVIDER"] = "ollama"
        os.environ["OLLAMA_BASE_URL"] = "http://127.0.0.1:11434/api"
        os.environ["OLLAMA_MODEL"] = "gemma3"

        from app.core.config import get_settings

        get_settings.cache_clear()

        from app.db.session import Base, engine
        from app.main import app

        cls.Base = Base
        cls.engine = engine
        cls.Base.metadata.drop_all(bind=cls.engine)
        cls.Base.metadata.create_all(bind=cls.engine)
        cls.client = TestClient(app)

    @classmethod
    def tearDownClass(cls):
        from app.core.config import get_settings

        get_settings.cache_clear()
        shutil.rmtree(cls.temp_dir, ignore_errors=True)

    def setUp(self):
        from app.db.session import SessionLocal
        from app.models.checkin import CheckIn
        from app.models.conversation import ConversationMessage
        from app.models.profile import UserProfile
        from app.models.user import User

        db = SessionLocal()
        try:
            db.query(ConversationMessage).delete()
            db.query(CheckIn).delete()
            db.query(UserProfile).delete()
            db.query(User).delete()
            db.commit()
        finally:
            db.close()

    def register_and_login(self, email="user@example.com", password="password123"):
        register_response = self.client.post(
            "/auth/register",
            json={"email": email, "password": password, "full_name": "Test User"},
        )
        self.assertEqual(register_response.status_code, 200)

        login_response = self.client.post(
            "/auth/login",
            json={"email": email, "password": password},
        )
        self.assertEqual(login_response.status_code, 200)
        return {"Authorization": f"Bearer {login_response.json()['access_token']}"}

    def test_checkin_upserts_same_day(self):
        headers = self.register_and_login()

        first = self.client.post(
            "/checkin",
            json={"in_one_word": "foggy", "body_score": 2, "mind_score": 2, "hot_flashes": 4},
            headers=headers,
        )
        second = self.client.post(
            "/checkin",
            json={"in_one_word": "steadier", "body_score": 3, "mind_score": 4, "hot_flashes": 1},
            headers=headers,
        )

        self.assertEqual(first.status_code, 200)
        self.assertEqual(second.status_code, 200)
        self.assertEqual(first.json()["id"], second.json()["id"])
        self.assertEqual(second.json()["in_one_word"], "steadier")

    def test_chat_updates_profile_memory(self):
        headers = self.register_and_login("memory@example.com")

        chat = self.client.post(
            "/chat",
            json={"message": "I am exhausted and a short walk with tea helped me calm down."},
            headers=headers,
        )
        self.assertEqual(chat.status_code, 200)

        profile = self.client.get("/profile", headers=headers)
        self.assertEqual(profile.status_code, 200)
        body = profile.json()

        self.assertIn("walks", body["joy_anchors"])
        self.assertIn("tea", body["joy_anchors"])
        self.assertIn("fatigue affects emotions", body["physical_emotional_links"])

    def test_chat_includes_recent_conversation_history_for_follow_up_turns(self):
        headers = self.register_and_login("thread@example.com")

        with patch(
            "app.api.routes.chat.generate_mia_reply",
            new=AsyncMock(side_effect=["First reply", "Second reply"]),
        ) as mocked_generate:
            first = self.client.post(
                "/chat",
                json={"message": "I am overwhelmed."},
                headers=headers,
            )
            second = self.client.post(
                "/chat",
                json={"message": "This makes me feel overwhelmed too."},
                headers=headers,
            )

        self.assertEqual(first.status_code, 200)
        self.assertEqual(second.status_code, 200)
        self.assertEqual(mocked_generate.await_count, 2)

        second_call = mocked_generate.await_args_list[1]
        self.assertEqual(
            second_call.kwargs["conversation_history"],
            [
                {"role": "user", "content": "I am overwhelmed."},
                {"role": "assistant", "content": "First reply"},
                {"role": "user", "content": "This makes me feel overwhelmed too."},
            ],
        )

    def test_crisis_reply_includes_resources(self):
        headers = self.register_and_login("crisis@example.com")

        response = self.client.post(
            "/chat",
            json={"message": "I want to disappear and hurt myself."},
            headers=headers,
        )
        self.assertEqual(response.status_code, 200)
        payload = response.json()

        self.assertTrue(payload["crisis"])
        self.assertIn("988", payload["reply"])

    def test_default_provider_is_ollama(self):
        from app.core.config import settings

        self.assertEqual(settings.MODEL_PROVIDER, "ollama")


if __name__ == "__main__":
    unittest.main()
