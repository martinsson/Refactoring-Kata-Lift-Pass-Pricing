import express from "express";
import mysql from "mysql2/promise"

async function createApp() {
    const app = express()

    let connectionOptions = {host: 'localhost', user: 'root', database: 'lift_pass', password: 'mysql'}
    const connection = await mysql.createConnection(connectionOptions)

    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost
        const liftPassType = req.query.type
        const [rows, fields] = await connection.query(
            'INSERT INTO `base_price` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?',
            [liftPassType, liftPassCost, liftPassCost]);

        res.send()
    })
    app.get('/prices', async (req, res) => {
        const result = (await connection.query(
            'SELECT cost FROM `base_price` ' +
            'WHERE `type` = ? ',
            [req.query.type]))[0][0]

        let reduction;
        let isHoliday;
        if (req.query.age < 6) {
            res.send({cost: 0})
        } else {
            reduction = 0;
            if (req.query.type !== 'night') {
                const holidays = (await connection.query(
                    'SELECT * FROM `holidays`'
                ))[0]

                for (let row of holidays) {
                    let holiday: Date = row.holiday
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
                    res.send({cost: Math.ceil(result.cost * .7)})
                } else {
                    if (req.query.age === undefined) {
                        let cost = result.cost * (1 - reduction / 100)
                        res.send({cost: Math.ceil(cost)})
                    } else {
                        if (req.query.age > 64) {
                            let cost = result.cost * .75 * (1 - reduction / 100)
                            res.send({cost: Math.ceil(cost)})
                        } else {
                            let cost = result.cost * (1 - reduction / 100)
                            res.send({cost: Math.ceil(cost)})
                        }
                    }
                }
            } else {
                if (req.query.age >= 6) {
                    if (req.query.age > 64) {
                        res.send({cost: Math.ceil(result.cost * .4)})
                    } else {
                        res.send(result)
                    }
                } else {
                    res.send({cost: 0})
                }
            }
        }
    })
    return {app, connection}
}

export {createApp}

