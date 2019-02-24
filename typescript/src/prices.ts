import express from "express";
import mysql from "mysql2/promise"

async function createApp() {
    const app = express()

    let connectionOptions = {host: 'localhost', user: 'root', database: 'test', password: 'mysql'}
    const connection = await mysql.createConnection(connectionOptions);

    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost
        const liftPassType = req.query.type
        const [rows, fields] = await connection.execute(
            'INSERT INTO `liftpass` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?',
            [liftPassType, liftPassCost, liftPassCost]);

        res.send()
    })
    app.get('/prices', async (req, res) => {
        const [[result]] = await connection.query(
            'SELECT cost FROM `liftpass` ' +
            'WHERE `type` = ? ',
            [req.query.type])

        let reduction;
        let isHoliday;
        if (req.query.age < 6) {
            res.send({cost: 0})
        } else {
            reduction = 0;
            if (req.query.type !== 'night') {
                const [holidays] = await connection.query(
                    'SELECT * FROM `holidays'
                )
                for (let row of holidays) {
                    const holidayDate = row.holiday.toISOString().split('T')[0]
                    if (req.query.date && req.query.date === holidayDate ) {
                        isHoliday = true
                    }

                }
                if (!isHoliday && new Date(req.query.date).getDay() === 0) {
                    reduction = 35
                }

                // TODO apply reduction for others
                if (req.query.age < 15) {
                    res.send({cost: Math.ceil(result.cost * .7)})
                } else {
                    if (req.query.age > 74) {
                        res.send({cost: Math.ceil(result.cost * .4)})
                    } else {
                        if (req.query.age === undefined) {
                            res.send(result)
                        } else {
                            if (req.query.age > 64) {
                                res.send({cost: Math.ceil(result.cost * .75)})
                            } else {
                                res.send({cost: Math.ceil(result.cost / (1 + reduction /100))})
                            }
                        }
                    }
                }
            } else {
                if (req.query.age >= 6) {
                    if (req.query.age > 74) {
                        res.send({cost: Math.ceil(result.cost / 2.5)})
                    } else {
                        res.send(result)
                    }
                } else {
                    res.send({cost: 0})
                }
                return
            }
        }
    })
    return {app, connection}
}

export {createApp}

