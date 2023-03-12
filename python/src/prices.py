import math

from flask import Flask
from flask import request
from datetime import datetime
from db import create_lift_pass_db_connection

app = Flask("lift-pass-pricing")

connection_options = {
    "host": 'localhost',
    "user": 'root',
    "database": 'lift_pass',
    "password": 'mysql'}

connection = None

@app.route("/prices", methods=['GET', 'PUT'])
def prices():
    res = {}
    global connection
    if connection is None:
        connection = create_lift_pass_db_connection(connection_options)
    if request.method == 'PUT':
        lift_pass_cost = request.args["cost"]
        lift_pass_type = request.args["type"]
        cursor = connection.cursor()
        cursor.execute('INSERT INTO `base_price` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?', (lift_pass_type, lift_pass_cost, lift_pass_cost))
        return {}
    elif request.method == 'GET':
        cursor = connection.cursor()
        cursor.execute(f'SELECT cost FROM base_price '
                       + 'WHERE type = ? ', (request.args['type'],))
        row = cursor.fetchone()
        result = {"cost": row[0]}
        if 'age' in request.args and request.args.get('age', type=int) < 6:
             res["cost"] = 0
        else:
            if "type" in request.args and request.args["type"] != "night":
                cursor = connection.cursor()
                cursor.execute('SELECT * FROM holidays')
                is_holiday = False
                reduction = 0
                for row in cursor.fetchall():
                    holiday = row[0]
                    if "date" in request.args:
                        d = datetime.fromisoformat(request.args["date"])
                        if d.year == holiday.year and d.month == holiday.month and holiday.day == d.day:
                            is_holiday = True
                if not is_holiday and "date" in request.args and datetime.fromisoformat(request.args["date"]).weekday() == 0:
                    reduction = 35

                # TODO: apply reduction for others
                if 'age' in request.args and request.args.get('age', type=int) < 15:
                     res['cost'] = math.ceil(result["cost"]*.7)
                else:
                    if 'age' not in request.args:
                        cost = result['cost'] * (1 - reduction/100)
                        res['cost'] = math.ceil(cost)
                    else:
                        if 'age' in request.args and request.args.get('age', type=int) > 64:
                            cost = result['cost'] * .75 * (1 - reduction / 100)
                            res['cost'] = math.ceil(cost)
                        elif 'age' in request.args:
                            cost = result['cost'] * (1 - reduction / 100)
                            res['cost'] = math.ceil(cost)
            else:
                if 'age' in request.args and request.args.get('age', type=int) >= 6:
                    if request.args.get('age', type=int) > 64:
                        res['cost'] = math.ceil(result['cost'] * .4)
                    else:
                        res.update(result)
                else:
                    res['cost'] = 0

    return res


if __name__ == "__main__":
    app.run(port=3005)
