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

        res.json()
    })
    app.get('/prices', async (req, res) => {
        const result = (await connection.query(
            'SELECT cost FROM `base_price` ' +
            'WHERE `type` = ? ',
            [req.query.type]))[0][0]

        if (req.query.age as any < 6) {
            res.json({cost: 0})
        } else {
            if (req.query.type !== 'night') {
                const holidays = (await connection.query(
                    'SELECT * FROM `holidays`'
                ))[0] as mysql.RowDataPacket[];

                let isHoliday;
                let reduction = 0
                for (let row of holidays) {
                    let holiday = row.holiday
                    if (req.query.date) {
                        let d = new Date(req.query.date as string)
                        if (d.getFullYear() === holiday.getFullYear()
                            && d.getMonth() === holiday.getMonth()
                            && d.getDate() === holiday.getDate()) {

                            isHoliday = true
                        }
                    }

                }

                if (!isHoliday && new Date(req.query.date as string).getDay() === 1) {
                    reduction = 35
                }

                // TODO apply reduction for others
                if (req.query.age as any < 15) {
                    res.json({cost: Math.ceil(result.cost * .7)})
                } else {
                    if (req.query.age === undefined) {
                        let cost = result.cost * (1 - reduction / 100)
                        res.json({cost: Math.ceil(cost)})
                    } else {
                        if (req.query.age as any > 64) {
                            let cost = result.cost * .75 * (1 - reduction / 100)
                            res.json({cost: Math.ceil(cost)})
                        } else {
                            let cost = result.cost * (1 - reduction / 100)
                            res.json({cost: Math.ceil(cost)})
                        }
                    }
                }
            } else {
                if (req.query.age as any >= 6) {
                    if (req.query.age as any > 64) {
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
