import express from "express";
import mysql from "mysql2/promise"
import {PriceLogic} from "./price-logic"
import {PriceDao} from "./price-dao"

async function createApp() {
    const app = express()

    let connectionOptions = {host: 'localhost', user: 'root', database: 'lift_pass', password: 'mysql'}
    const connection = await mysql.createConnection(connectionOptions);
    let priceLogic = new PriceLogic(new PriceDao(connection))


    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost
        const liftPassType = req.query.type
        const [rows, fields] = await connection.execute(
            'INSERT INTO `base_price` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?',
            [liftPassType, liftPassCost, liftPassCost]);

        res.send()
    })
    app.get('/prices', async (req, res) => {
        let liftPassType = req.query.type
        let age = req.query.age
        let skiingDate = req.query.date

        let cost = await priceLogic.calculateCostFor(liftPassType, skiingDate, age)
        res.send(cost)
    })
    return {app, connection}
}

export {createApp}

