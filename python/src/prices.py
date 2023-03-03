from flask import Flask
from flask import request
from datetime import datetime
from db import create_lift_pass_db_connection

app = Flask("lift-pass-pricing")

connection_options = {"host": 'localhost', "user": 'root', "database": 'lift_pass', "password": ''}
connection = create_lift_pass_db_connection(connection_options)


@app.route("/prices", methods=['GET', 'PUT'])
def prices():
    res = {}
    if request.method == 'PUT':
        return ["put prices"]
    elif request.method == 'GET':
        cursor = connection.cursor()
        cursor.execute(f'SELECT cost FROM base_price '
                       + 'WHERE type = ? ', (request.args['type'],))
        row = cursor.fetchone()
        result = {"cost": row[0]}
        res.update(result)
        if 'age' in request.args and request.args.get('age', type=int) < 6:
             res["cost"] = 0
        # else:
        #     if request.args["type"] != "night":
        #         cursor = connection.cursor()
        #         cursor.execute('SELECT * FROM holidays')
        #         is_holiday = None
        #         reduction = 0
        #         for row in cursor.fetchall():
        #             holiday = row[0]
        #             if "date" in request.args:
        #                 d = datetime.fromisoformat(request.args["date"])


        return res


if __name__ == "__main__":
    app.run(port=3005)
