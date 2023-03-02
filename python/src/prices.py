from flask import Flask
from flask import request
from db import create_lift_pass_db_connection

app = Flask("lift-pass-pricing")


@app.route("/prices", methods=['GET', 'PUT'])
def prices():
    connection = create_lift_pass_db_connection()
    if request.method == 'PUT':
        return ["put prices"]
    elif request.method == 'GET':
        cursor = connection.cursor()
        query = f'SELECT cost FROM base_price ' + 'WHERE type = ? '

        cursor.execute(query, (request.args['type'],))
        row = cursor.fetchone()
        result = {"cost": row[0]}

        return result


if __name__ == "__main__":
    app.run(port=3005)
