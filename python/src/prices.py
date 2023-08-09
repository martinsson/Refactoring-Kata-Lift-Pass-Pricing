from flask import Flask
from flask import request
from python.src.db import create_lift_pass_db_connection
from python.src.services.price_dao import PriceDao
from python.src.services.price_logic import PriceLogic
from python.src.setting import connection_options

app = Flask("lift-pass-pricing")

connection = None


class MissingParameterError(Exception):
    def __init__(self, parameter_name):
        self.parameter_name = parameter_name
        self.message = f"Mandatory parameter '{parameter_name}' is missing."


@app.route("/prices", methods=['GET', 'PUT'])
def prices():
    lift_pass_type = request.args.get('type')
    if lift_pass_type is None:
        raise MissingParameterError("toto")
    age = request.args.get('age', type=int)
    date = request.args.get('date')

    res = {}
    global connection
    if connection is None:
        connection = create_lift_pass_db_connection(connection_options)

    price_logic = PriceLogic(PriceDao(connection))
    if request.method == 'PUT':
        lift_pass_cost = request.args["cost"]
        lift_pass_type = lift_pass_type
        cursor = connection.cursor()
        cursor.execute('INSERT INTO `base_price` (type, cost) VALUES (?, ?) ' +
                       'ON DUPLICATE KEY UPDATE cost = ?',
                       (lift_pass_type, lift_pass_cost, lift_pass_cost))
        return {}
    elif request.method == 'GET':
        res = price_logic.calculate_price(lift_pass_type, age, date)
    return res


if __name__ == "__main__":
    app.run(port=3005)
