from typing import Optional
from datetime import datetime

from python.src.services.cost import Cost


class PriceLogic:
    def __init__(self, price_dao):
        self.price_dao = price_dao

    def calculate_price(self, lift_pass_type: str, age: Optional[int] = None,
                        reservation_date: Optional[str] = None,
                        ) -> dict[str, int]:
        res = {}
        base_price = self.price_dao.find_base_price(lift_pass_type)
        if age and age < 6:
            res["cost"] = 0
        else:
            if lift_pass_type and lift_pass_type != "night":
                holidays = self.price_dao.find_holidays()
                is_holiday = False
                reduction = 0
                for row in holidays.fetchall():
                    holiday = row[0]
                    if reservation_date:
                        d = datetime.fromisoformat(reservation_date)
                        if d.year == holiday.year and d.month == holiday.month and holiday.day == d.day:
                            is_holiday = True
                if not is_holiday and reservation_date and datetime.fromisoformat(
                        reservation_date).weekday() == 0:
                    reduction = 35

                if age and age < 15:
                    base_price.apply_percentage(30)
                    res['cost'] = base_price.round_up()
                elif age and age > 64:
                    base_price.apply_percentage(25)
                    base_price.apply_percentage(reduction)
                    res['cost'] = base_price.round_up()
                else:
                    base_price.apply_percentage(reduction)
                    res['cost'] = base_price.round_up()
            else:
                if age and age >= 6:
                    if age > 64:
                        base_price.apply_percentage(60)
                        res['cost'] = base_price.round_up()
                    else:
                        res['cost'] = base_price.round_up()
                else:
                    res['cost'] = 0
        return res
