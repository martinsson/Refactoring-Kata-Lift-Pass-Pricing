import multiprocessing

import pytest
import requests
from datetime import datetime
import time

from prices import app

TEST_PORT = 3006


def server(port):
    app.run(port=port)


@pytest.fixture(autouse=True, scope="session")
def lift_pass_pricing_app():
    """ starts the lift pass pricing flask app running on localhost """
    p = multiprocessing.Process(target=server, args=(TEST_PORT,))
    p.start()
    # we need to give it time to start - one second usually seems to be enough
    time.sleep(1)
    yield f"http://127.0.0.1:{TEST_PORT}"
    p.terminate()


def test_something(lift_pass_pricing_app):
    response = requests.get(lift_pass_pricing_app + "/prices", params={'type': '1jour'})
    assert response.json() == {'cost': 35}
