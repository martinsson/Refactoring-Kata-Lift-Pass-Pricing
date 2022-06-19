import pytest

from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_read_main():
    response = client.get("/prices?type=1jour")
    assert response.json() == {"cost": 35}


@pytest.mark.parametrize(
    argnames=("age", "expected_cost"),
    argvalues=[(5, 0),
               (6, 25),
               (14, 25),
               (15, 35),
               (25, 35),
               (64, 35),
               (65, 27)],
)
def test_1jour_age_variation(age, expected_cost):
    response = client.get(f"/prices?type=1jour&age={age}")
    assert response.json() == {"cost": expected_cost}


@pytest.mark.parametrize(
    argnames=("age", "expected_cost"),
    argvalues=[(5, 0),
               (6, 19),
               (25, 19),
               (64, 19),
               (65, 8)],
)
def test_night_age_variation(age, expected_cost):
    response = client.get(f"/prices?type=night&age={age}")
    assert response.json() == {"cost": expected_cost}


@pytest.mark.parametrize(
    argnames=("age", "date", "expected_cost"),
    argvalues=[(15, "2019-02-22", 35),
               (15, "2019-02-25", 35),
               (15, "2019-03-11", 23),
               (65, "2019-03-11", 18)] # monday
)
def test_monday_deals(age, date, expected_cost):
    response = client.get(f"/prices?type=1jour&age={age}&date={date}")
    assert response.json() == {"cost": expected_cost}
