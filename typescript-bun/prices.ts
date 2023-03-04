import mysql from "mysql2/promise";

let connectionOptions = {
    host: "localhost",
    user: "root",
    database: "lift_pass",
    password: "mysql",
};
const connection = await mysql.createConnection(connectionOptions);

Bun.serve({
    async fetch(req) {
        const url = new URL(req.url);

        if (url.pathname != "/prices") {
            return new Response("only `prices` path allowed", { status: 404 });
        }

        if (req.method == "PUT") {
            const liftPassCost = url.searchParams.get("cost");
            const liftPassType = url.searchParams.get("type");

            await connection.query(
                "INSERT INTO `base_price` (type, cost) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE cost = ?",
                [liftPassType, liftPassCost, liftPassCost]
            );

            return new Response();
        } else if (req.method == "GET") {
            // @ts-ignore
            const result = (
                await connection.query(
                    "SELECT cost FROM `base_price` " + "WHERE `type` = ? ",
                    [url.searchParams.get("type")]
                )
            )[0][0];

            const res = (obj: Object) => new Response(JSON.stringify(obj));

            if (
                url.searchParams.get("age") != null &&
                +url.searchParams.get("age")! < 6
            ) {
                return res({ cost: 0 });
            } else {
                if (url.searchParams.get("type") !== "night") {
                    const holidays = (
                        await connection.query("SELECT * FROM `holidays`")
                    )[0] as mysql.RowDataPacket[];

                    let isHoliday;
                    let reduction = 0;
                    for (let row of holidays) {
                        let holiday = row.holiday;
                        if (url.searchParams.get("date")) {
                            let d = new Date(
                                url.searchParams.get("date") as string
                            );
                            if (
                                d.getFullYear() === holiday.getFullYear() &&
                                d.getMonth() === holiday.getMonth() &&
                                d.getDate() === holiday.getDate()
                            ) {
                                isHoliday = true;
                            }
                        }
                    }

                    if (
                        !isHoliday &&
                        new Date(
                            url.searchParams.get("date") as string
                        ).getDay() === 1
                    ) {
                        reduction = 35;
                    }

                    // TODO apply reduction for others
                    if (
                        url.searchParams.get("age") != null &&
                        (url.searchParams.get("age") as any) < 15
                    ) {
                        return res({
                            cost: Math.ceil(result.cost * 0.7),
                        });
                    } else {
                        if (url.searchParams.get("age") === null) {
                            let cost = result.cost * (1 - reduction / 100);
                            return res({ cost: Math.ceil(cost) });
                        } else {
                            if ((url.searchParams.get("age") as any) > 64) {
                                let cost =
                                    result.cost * 0.75 * (1 - reduction / 100);
                                return res({ cost: Math.ceil(cost) });
                            } else {
                                let cost = result.cost * (1 - reduction / 100);
                                return res({ cost: Math.ceil(cost) });
                            }
                        }
                    }
                } else {
                    if (
                        url.searchParams.get("age") != null &&
                        (url.searchParams.get("age") as any) >= 6
                    ) {
                        if ((url.searchParams.get("age") as any) > 64) {
                            return res({
                                cost: Math.ceil(result.cost * 0.4),
                            });
                        } else {
                            return res(result);
                        }
                    } else {
                        return res({ cost: 0 });
                    }
                }
            }
        }

        return new Response("only `GET` or `PUT` allowed", { status: 404 });
    },
    error(error: Error) {
        return new Response(`<pre>${error.message}</pre>`, {
            headers: {
                "Content-Type": "text/html",
            },
            status: 500,
        });
    },
});
