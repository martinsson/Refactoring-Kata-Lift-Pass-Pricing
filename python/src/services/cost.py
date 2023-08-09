import math


class Cost:
    def __init__(self, cost: float):
        self.cost = cost

    def apply_percentage(self, reduction):
        self.cost = self.cost * (1 - reduction / 100)

    def round_up(self):
        return math.ceil(self.cost)