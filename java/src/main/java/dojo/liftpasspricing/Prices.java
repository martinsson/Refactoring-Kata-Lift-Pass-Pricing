package dojo.liftpasspricing;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Prices {

    public static Connection createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");

        port(4567);

        put("/prices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");

            try (PreparedStatement stmt = connection.prepareStatement( //
                    "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
            "ON DUPLICATE KEY UPDATE cost = ?")) {
                stmt.setString(1, liftPassType);
                stmt.setInt(2, liftPassCost);
                stmt.setInt(3, liftPassCost);
                stmt.execute();
            }

            return "";
        });

        get("/prices", (req, res) -> {
            try (PreparedStatement costStmt = connection.prepareStatement("SELECT cost FROM base_price WHERE type = ?")) {
                costStmt.setString(1, req.queryParams("type"));
                try (ResultSet result = costStmt.executeQuery()) {
                    result.next();

                    int reduction = 0;
                    boolean isHoliday = false;

                    if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) < 6) {
                        return "{ \"cost\": 0}";
                    } else {
                        if (!req.queryParams("type").equals("night")) {
                            DateFormat isoFormat = new SimpleDateFormat("YYYY-MM-DD");

                            try (PreparedStatement holidayStmt = connection.prepareStatement("SELECT * FROM holidays")) {
                                try (ResultSet holidays = holidayStmt.executeQuery()) {

                                    while (holidays.next()) {
                                        String holidayDate = isoFormat.format(holidays.getDate("holiday"));
                                        if (req.queryParams("date") != null && req.queryParams("date").equals(holidayDate)) {
                                            isHoliday = true;
                                        }
                                    }

                                }
                            }

                            if (req.queryParams("date") != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(isoFormat.parse(req.queryParams("date")));
                                if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                                    reduction = 60;
                                }
                            }

                            // TODO apply reduction for others
                            if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) < 15) {
                                return "{ \"cost\": " + Math.ceil(result.getInt("cost") * .7) + "}";
                            } else {
                                if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) > 74) {
                                    return "{ \"cost\": " + Math.ceil(result.getInt("cost") * .4) + "}";
                                } else {
                                    if (req.queryParams("age") == null) {
                                        double cost = result.getInt("cost");
                                        if (reduction > 0) {
                                            cost = cost / (1 + reduction / 100);
                                        }
                                        return "{ \"cost\": " + Math.ceil(cost) + "}";
                                    } else {
                                        if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) > 64) {
                                            double cost = result.getInt("cost") * .75;
                                            if (reduction > 0) {
                                                cost = cost / (1 + reduction / 100);
                                            }
                                            return "{ \"cost\": " + Math.ceil(cost) + "}";
                                        } else {
                                            double cost = result.getInt("cost");
                                            if (reduction > 0) {
                                                cost = cost / (1 + reduction / 100);
                                            }
                                            return "{ \"cost\": " + Math.ceil(cost) + "}";
                                        }
                                    }
                                }
                            }
                        } else {
                            if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) >= 6) {
                                if (req.queryParams("age") != null && Integer.parseInt(req.queryParams("age")) > 74) {
                                    return "{ \"cost\": " + Math.ceil(result.getInt("cost") / 2.5) + "}";
                                } else {
                                    return "{ \"cost\": " + result.getInt("cost") + "}";
                                }
                            } else {
                                return "{ \"cost\": 0}";
                            }
                        }
                    }
                }
            }
        });

        after((req, res) -> {
            res.type("application/json");
        });

        return connection;
    }

}
