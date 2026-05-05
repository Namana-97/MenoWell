"""initial_schema

Revision ID: 0001_initial_schema
Revises:
Create Date: 2026-05-04 00:00:00.000000
"""

from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision: str = "0001_initial_schema"
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.create_table(
        "users",
        sa.Column("id", sa.Integer(), nullable=False),
        sa.Column("email", sa.String(length=255), nullable=False),
        sa.Column("hashed_password", sa.String(length=255), nullable=False),
        sa.Column("full_name", sa.String(length=255), nullable=True),
        sa.Column("is_active", sa.Boolean(), nullable=False),
        sa.Column("created_at", sa.DateTime(), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index(op.f("ix_users_email"), "users", ["email"], unique=True)
    op.create_index(op.f("ix_users_id"), "users", ["id"], unique=False)

    op.create_table(
        "checkins",
        sa.Column("id", sa.Integer(), nullable=False),
        sa.Column("user_id", sa.Integer(), nullable=False),
        sa.Column("checkin_date", sa.Date(), nullable=False),
        sa.Column("in_one_word", sa.String(length=120), nullable=True),
        sa.Column("body_score", sa.Integer(), nullable=True),
        sa.Column("mind_score", sa.Integer(), nullable=True),
        sa.Column("hurt_today", sa.Text(), nullable=True),
        sa.Column("helped_today", sa.Text(), nullable=True),
        sa.Column("hot_flashes", sa.Integer(), nullable=False),
        sa.Column("supplements_taken", sa.String(length=10), nullable=True),
        sa.Column("created_at", sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(["user_id"], ["users.id"]),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index(op.f("ix_checkins_checkin_date"), "checkins", ["checkin_date"], unique=False)
    op.create_index(op.f("ix_checkins_id"), "checkins", ["id"], unique=False)
    op.create_index(op.f("ix_checkins_user_id"), "checkins", ["user_id"], unique=False)

    op.create_table(
        "conversation_messages",
        sa.Column("id", sa.Integer(), nullable=False),
        sa.Column("user_id", sa.Integer(), nullable=False),
        sa.Column("role", sa.String(length=20), nullable=False),
        sa.Column("content", sa.Text(), nullable=False),
        sa.Column("depression_level", sa.Integer(), nullable=False),
        sa.Column("created_at", sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(["user_id"], ["users.id"]),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index(
        op.f("ix_conversation_messages_id"),
        "conversation_messages",
        ["id"],
        unique=False,
    )
    op.create_index(
        op.f("ix_conversation_messages_user_id"),
        "conversation_messages",
        ["user_id"],
        unique=False,
    )

    op.create_table(
        "user_profiles",
        sa.Column("id", sa.Integer(), nullable=False),
        sa.Column("user_id", sa.Integer(), nullable=False),
        sa.Column("core_wounds", sa.JSON(), nullable=True),
        sa.Column("joy_anchors", sa.JSON(), nullable=True),
        sa.Column("anxiety_triggers", sa.JSON(), nullable=True),
        sa.Column("depression_patterns", sa.JSON(), nullable=True),
        sa.Column("physical_emotional_links", sa.JSON(), nullable=True),
        sa.Column("strength_narrative", sa.Text(), nullable=True),
        sa.Column("last_updated_at", sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(["user_id"], ["users.id"]),
        sa.PrimaryKeyConstraint("id"),
        sa.UniqueConstraint("user_id"),
    )
    op.create_index(op.f("ix_user_profiles_id"), "user_profiles", ["id"], unique=False)


def downgrade() -> None:
    op.drop_index(op.f("ix_user_profiles_id"), table_name="user_profiles")
    op.drop_table("user_profiles")

    op.drop_index(op.f("ix_conversation_messages_user_id"), table_name="conversation_messages")
    op.drop_index(op.f("ix_conversation_messages_id"), table_name="conversation_messages")
    op.drop_table("conversation_messages")

    op.drop_index(op.f("ix_checkins_user_id"), table_name="checkins")
    op.drop_index(op.f("ix_checkins_id"), table_name="checkins")
    op.drop_index(op.f("ix_checkins_checkin_date"), table_name="checkins")
    op.drop_table("checkins")

    op.drop_index(op.f("ix_users_id"), table_name="users")
    op.drop_index(op.f("ix_users_email"), table_name="users")
    op.drop_table("users")
