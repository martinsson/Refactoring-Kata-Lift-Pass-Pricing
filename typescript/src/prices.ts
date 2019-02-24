import express from "express";
import mysql from "mysql2/promise"

async function createApp() {
    const app = express()

    let connectionOptions = {host:'localhost', user: 'root', database: 'test', password: 'mysql'}
    const connection = await mysql.createConnection(connectionOptions);

    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost//?
        const liftPassType = req.query.type
        const [rows, fields] = await connection.execute(
            'INSERT INTO `liftpass` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?',
            [liftPassType, liftPassCost, liftPassCost]);

        res.send()
    })
    app.get('/prices', async (req, res) => {
        const liftPassType = req.query.type
        const age = req.query.age
        const [[result]] = await connection.query(
            'SELECT cost FROM `liftpass` ' +
            'WHERE `type` = ? ',
            [liftPassType])
        if (age < 6) {
            res.send({cost: 0})
        }
        else if (age < 15) {
            res.send({cost: Math.ceil(result.cost * .7)})
        } else if (age > 74) {
            res.send({cost: Math.ceil(result.cost * .42)})
        } else if (age > 64) {
            res.send({cost: Math.ceil(result.cost * .75)})
        } else {
            res.send(result)
        }
    })
    return {app, connection}
}

export {createApp}

