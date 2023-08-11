import math


class Cost:
    def __init__(self, cost: float):
        self.cost = cost

    def apply_percentage(self, reduction: int) -> None:
        self.cost = self.cost * (1 - reduction / 100)

    def round_up(self) -> int:
        return math.ceil(self.cost)
