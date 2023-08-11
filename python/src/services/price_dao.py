
from typing import TYPE_CHECKING

from python.src.services.cost import Cost

if TYPE_CHECKING:
    from sqlite3 import Cursor


class PriceDao:
    def __init__(self, connection):
        self.connection = connection
        self.cursor = self.connection.cursor()

    def find_holidays(self) -> "Cursor":
        return self.cursor.execute('SELECT * FROM holidays')

    def find_base_price(self, lift_pass_type: str) -> Cost:
        self.cursor.execute(f'SELECT cost FROM base_price '
                            + 'WHERE type = ? ', (lift_pass_type,))
        row = self.cursor.fetchone()
        result = {"cost": row[0]}
        return Cost(result["cost"])
