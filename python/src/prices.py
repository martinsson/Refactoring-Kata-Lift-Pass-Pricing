from flask import Flask
from flask import request


app = Flask("lift-pass-pricing")


@app.route("/prices", methods=['GET', 'PUT'])
def prices():
    if request.method == 'PUT':
        return ["put prices"]
    elif request.method == 'GET':
        return ["get prices"]


if __name__ == "__main__":
    app.run(port=3005)
