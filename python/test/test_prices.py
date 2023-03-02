import multiprocessing

import pytest
import requests

from prices import app

TEST_PORT = 3006


def server(port):
    app.run(port=port)


@pytest.fixture(autouse=True, scope="session")
def lift_pass_pricing_app():
    """ starts the lift pass pricing flask app running on localhost """
    p = multiprocessing.Process(target=server, args=(TEST_PORT, ))
    p.start()
    yield
    p.terminate()


@pytest.fixture
def base_url():
    return f"http://127.0.0.1:{TEST_PORT}"


def test_1jour(lift_pass_pricing_app, base_url):
    response = requests.get(base_url + '/prices', params={'type': '1jour', 'cost': 35})
    assert response.status_code == 200


def test_night(lift_pass_pricing_app, base_url):
    response = requests.get(base_url + '/prices', params={'type': 'night', 'cost': 19})
    assert response.status_code == 200

def test_default_cost(lift_pass_pricing_app, base_url):
    response = requests.get(base_url + "/prices")
    assert response.json() == ["get prices"]


