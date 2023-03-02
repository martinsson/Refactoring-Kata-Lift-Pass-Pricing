import multiprocessing

import pytest
import requests

from prices import app


def server(port):
    app.run(port=port)


@pytest.fixture(autouse=True, scope="session")
def lift_pass_pricing_app():
    """ starts the lift pass pricing flask app running on localhost """
    p = multiprocessing.Process(target=server, args=(3006, ))
    p.start()
    yield
    p.terminate()


def test_default_cost(lift_pass_pricing_app):
    response = requests.get("http://127.0.0.1:3006/prices")
    assert response.json() == ["get prices"]
