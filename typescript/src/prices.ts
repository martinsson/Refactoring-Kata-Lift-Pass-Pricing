import express from "express";


function createApp() {
    const app = express()

    app.put('/prices', (req, res) => {
        const liftPassCost = req.params.cost
        const liftPassType = req.params.type
        res.send()
    })
    app.get('/prices', (req, res) => {
        res.send({price: 35})
    })
    return app
}

export {createApp}

