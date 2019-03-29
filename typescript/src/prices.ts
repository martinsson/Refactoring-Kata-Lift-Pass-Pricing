import express from "express";
import sqlite from "sqlite";

async function createApp() {
    const app = express()

    const connection = await sqlite.open("database.db")

    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost
        const liftPassType = req.query.type
        await connection.all(
            'INSERT OR REPLACE INTO `base_price` (type, cost) VALUES (?, ?)',
            [liftPassType, liftPassCost]
        );

        res.json()
    })
    app.get('/prices', async (req, res) => {
        const result = (await connection.get(
            'SELECT cost FROM `base_price` ' +
            'WHERE `type` = ? ',
            [req.query.type]))

        let reduction;
        let isHoliday;
        if (req.query.age < 6) {
            res.json({cost: 0})
        } else {
            reduction = 0;
            if (req.query.type !== 'night') {
                const holidays = (await connection.all(
                    'SELECT * FROM `holidays`'
                ))

                for (let row of holidays) {
                    let holiday = new Date(row.holiday)
                    if (req.query.date) {
                        let d = new Date(req.query.date)
                        if (d.getFullYear() === holiday.getFullYear()
                            && d.getMonth() === holiday.getMonth()
                            && d.getDate() === holiday.getDate()) {

                            isHoliday = true
                        }
                    }

                }

                if (!isHoliday && new Date(req.query.date).getDay() === 1) {
                    reduction = 35
                }

                // TODO apply reduction for others
                if (req.query.age < 15) {
                    res.json({cost: Math.ceil(result.cost * .7)})
                } else {
                    if (req.query.age === undefined) {
                        let cost = result.cost * (1 - reduction / 100)
                            res.json({cost: Math.ceil(cost)})
                    } else {
                        if (req.query.age > 64) {
                            let cost = result.cost * .75 * (1 - reduction / 100)
                                res.json({cost: Math.ceil(cost)})
                        } else {
                            let cost = result.cost * (1 - reduction / 100)
                                res.json({cost: Math.ceil(cost)})
                        }
                    }
                }
            } else {
                if (req.query.age >= 6) {
                    if (req.query.age > 64) {
                        res.json({cost: Math.ceil(result.cost * .4)})
                    } else {
                        res.json(result)
                    }
                } else {
                    res.json({cost: 0})
                }
            }
        }
    })
    return {app, connection}
}

export {createApp}
