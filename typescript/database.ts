import sqlite from "sqlite";
import * as fs from "fs";

(async () => {
    const connection = await sqlite.open("database.db")
    const sql = fs.readFileSync('../database/initDatabase.sql').toString();
    await connection.exec(sql)
})()
    .then(() => console.log("database created"))
    .catch(() => console.log("error while creating database"))