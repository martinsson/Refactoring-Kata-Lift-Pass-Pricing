from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_read_main():
    response = client.get("/prices?type=1jour")
    assert response.json() == {"cost": 35}
