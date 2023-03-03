import multiprocessing

import pytest
import requests
from datetime import datetime

from prices import app

TEST_PORT = 3006


def server(port):
    app.run(port=port)


@pytest.fixture(autouse=True, scope="session")
def lift_pass_pricing_app():
    """ starts the lift pass pricing flask app running on localhost """
    p = multiprocessing.Process(target=server, args=(TEST_PORT,))
    p.start()
    yield f"http://127.0.0.1:{TEST_PORT}"
    p.terminate()


def test_1jour(lift_pass_pricing_app):
    response = requests.get(lift_pass_pricing_app + '/prices', params={'type': '1jour', 'cost': 35})
    assert response.status_code == 200


def test_night(lift_pass_pricing_app):
    response = requests.get(lift_pass_pricing_app + '/prices', params={'type': 'night', 'cost': 19})
    assert response.status_code == 200


def test_default_cost(lift_pass_pricing_app):
    response = requests.get(lift_pass_pricing_app + "/prices", params={'type': '1jour'})
    assert response.json() == {'cost': 35}


@pytest.mark.parametrize(
    "age,expectedCost", [
        (5, 0),
        (6, 25),
        (14, 25),
        (15, 35),
        (25, 35),
        (64, 35),
        (65, 27),
    ])
def test_works_for_all_ages(lift_pass_pricing_app, age, expectedCost):
    response = requests.get(lift_pass_pricing_app + "/prices", params={'type': '1jour', 'age': age})
    assert response.json() == {'cost': expectedCost}


@pytest.mark.parametrize(
    "age,expectedCost", [
        (5, 0),
        (6, 19),
        (25, 19),
        (64, 19),
        (65, 8),
    ])
def test_works_for_night_passes(lift_pass_pricing_app, age, expectedCost):
    response = requests.get(lift_pass_pricing_app + "/prices", params={'type': 'night', 'age': age})
    assert response.json() == {'cost': expectedCost}


@pytest.mark.parametrize(
    "age,expectedCost,ski_date", [
        (15, 35, datetime.fromisoformat('2019-02-22')),
        (15, 35, datetime.fromisoformat('2019-02-25')), # monday, holiday
        (15, 23, datetime.fromisoformat('2019-03-11')), # monday
        (65, 18, datetime.fromisoformat('2019-03-11')),  # monday
    ])
def test_works_for_monday_deals(lift_pass_pricing_app, age, expectedCost, ski_date):
    response = requests.get(lift_pass_pricing_app + "/prices", params={'type': '1jour', 'age': age, 'date': ski_date})
    assert response.json() == {'cost': expectedCost}

# TODO 2-4, and 5, 6 day pass
