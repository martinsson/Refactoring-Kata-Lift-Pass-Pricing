class PriceDao:
    def __init__(self, connection):
        self.connection = connection
        self.cursor = self.connection.cursor()

    def find_holidays(self):
        return self.cursor.execute('SELECT * FROM holidays')

    def find_base_price(self, lift_pass_type):
        self.cursor.execute(f'SELECT cost FROM base_price '
                            + 'WHERE type = ? ', (lift_pass_type,))
        row = self.cursor.fetchone()
        result = {"cost": row[0]}
        return result
