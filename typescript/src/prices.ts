import express from "express";
import mysql from "mysql2/promise"

async function createApp() {
    const app = express()

    let connectionOptions = {host:'localhost', user: 'root', database: 'test', password: 'mysql'}
    const connection = await mysql.createConnection(connectionOptions);

    app.put('/prices', async (req, res) => {
        const liftPassCost = req.query.cost//?
        const liftPassType = req.query.type
// get the client
        // create the connection
        // query database
        const [rows, fields] = await connection.execute(
            'INSERT INTO `liftpass` (type, cost) VALUES (?, ?) ' +
            'ON DUPLICATE KEY UPDATE cost = ?',
            [liftPassType, liftPassCost, liftPassCost], ()=> {});

        res.send()
    })
    app.get('/prices', async (req, res) => {
        await connection.query('SELECT cost FROM `liftpass` ')
        res.send({price: 35})
    })
    return {app, connection}
}

export {createApp}

