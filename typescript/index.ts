import {createAppWithConnection} from "./src/prices";
import mysql from "mysql2/promise"

async function main() {
    const connectionOptions = {
        host: 'localhost',
        user: 'root',
        database: 'lift_pass',
        password: 'mysql'
    };
    const connection = await mysql.createConnection(connectionOptions);
    const app = createAppWithConnection(connection);
    app.listen(5010)
}

main().then(() =>{
    console.log(`LiftPassPricing Api started on 5010,
you can open http://localhost:5010/prices?type=night&age=23&date=2019-02-18 in a navigator
and you'll get the price of the list pass for the day.`);
});
