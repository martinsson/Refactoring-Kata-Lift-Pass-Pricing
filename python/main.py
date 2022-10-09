import datetime
import math

from fastapi import FastAPI
from typing import Optional

from database import (
    database, insert, select,
    base_price as base_price_table,
    holidays as holidays_table,
)

app = FastAPI()


@app.on_event("startup")
async def startup():
    await database.connect()


@app.on_event("shutdown")
async def shutdown():
    await database.disconnect()


@app.put("/prices")
async def upsert_price(type: str, cost: int):
    await database.execute(
        insert(base_price_table)
        .values({"type": type, "cost": cost})
        .on_conflict_do_update(
            index_elements=['type'],
            set_={"cost": cost},
        )
    )


@app.get("/prices")
async def compute_price(
    type: str,
    age: Optional[int] = None,
    date: Optional[datetime.date] = None,
):
    result = await database.fetch_one(
        select(base_price_table.c.cost)
        .where(base_price_table.c.type == type),
    )

    if age and age < 6:
        return {"cost": 0}
    else:
        if type != 'night':
            holidays = await database.fetch_all(select(holidays_table))

            is_holiday = False
            reduction = 0

            for row in holidays:
                if date:
                    if date == row.holiday:
                        is_holiday = True

            if not is_holiday and date and date.weekday() == 0:
                reduction = 35

            # TODO apply reduction for others
            if age and age < 15:
                return {"cost": math.ceil(result.cost * .7)}
            else:
                if not age:
                    cost = result.cost * (1 - reduction / 100)
                    return {"cost": math.ceil(cost)}
                else:
                    if age > 64:
                        cost = result.cost * .75 * (1 - reduction / 100)
                        return {"cost": math.ceil(cost)}
                    else:
                        cost = result.cost * (1 - reduction / 100)
                        return {"cost": math.ceil(cost)}
        else:
            if age and age >= 6:
                if age and age > 64:
                    return {"cost": math.ceil(result.cost * .4)}
                else:
                    return result
            else:
                return {"cost": 0}
