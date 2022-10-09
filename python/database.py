import sqlalchemy
import sys

from datetime import date
from databases import Database
from sqlalchemy import select
from sqlalchemy.dialects.sqlite import insert

# Wraps the database with databases.Database to get an async API
# on top of SQL alchemy.
database = Database("sqlite:///ski.db")

_metadata = sqlalchemy.MetaData()

base_price = sqlalchemy.Table(
    "base_price",
    _metadata,
    sqlalchemy.Column("id", sqlalchemy.Integer, primary_key=True),
    sqlalchemy.Column("type", sqlalchemy.String(
        length=255), nullable=False, unique=True),
    sqlalchemy.Column("cost", sqlalchemy.Integer, nullable=False),
)

holidays = sqlalchemy.Table(
    "holidays",
    _metadata,
    sqlalchemy.Column("holiday", sqlalchemy.Date, primary_key=True),
    sqlalchemy.Column("description", sqlalchemy.String(
        length=255), nullable=False),
)


def create_schema():
    engine = sqlalchemy.create_engine(str(database.url))
    _metadata.create_all(engine)


def seed_data():
    engine = sqlalchemy.create_engine(str(database.url))
    engine.execute(
        insert(base_price),
        [
            {"type": "1jour", "cost": 35},
            {"type": "night", "cost": 19},
        ],
    )
    engine.execute(
        insert(holidays),
        [
            {"holiday": date(2019, 2, 18), "description": "winter"},
            {"holiday": date(2019, 2, 25), "description": "winter"},
            {"holiday": date(2019, 3, 4), "description": "winter"},
        ],
    )


if __name__ == "__main__":
    if sys.argv[1] == "createdb":
        create_schema()
        seed_data()
