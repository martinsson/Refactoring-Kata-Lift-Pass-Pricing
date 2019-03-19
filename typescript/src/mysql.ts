import mysql from "mysql2/promise"

export async function createDatabaseConnection(connectionOptions) {
    let connection = await mysql.createConnection(connectionOptions)

    // make the database connection not too fast so that
    // we actually feel a slight slowness in the tests
    return {
        query(query, params: any[] = []) {
            return new Promise((resolve) => {
                setTimeout(resolve, 50)
            }).then(() => {
                return connection.execute(query, params)
            })
        },
        close() {
            return connection.close()
        }
    }
}
