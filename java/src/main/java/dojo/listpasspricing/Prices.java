package dojo.listpasspricing;

import static spark.Spark.get;
import static spark.Spark.port;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.LoggerFactory;

public class Prices {

    public static void createApp() throws SQLException {

        final Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lift_pass", "root", "mysql");

        port(4567);

        get("/putprices", (req, res) -> {
            int liftPassCost = Integer.parseInt(req.queryParams("cost"));
            String liftPassType = req.queryParams("type");
            try (PreparedStatement stmt = connection.prepareStatement(//
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
            try (PreparedStatement stmt = connection.prepareStatement("SELECT cost FROM base_price WHERE type = ?")) {
                stmt.setString(1, req.queryParams("type"));
                ResultSet result = stmt.executeQuery();
                result.next();

                boolean isHoliday = false;
                if (Integer.parseInt(req.queryParams("age")) < 6) {
                    return "{cost: 0}";
                } else {
                    int reduction = 0;
                    if (!req.queryParams("type").equals("night")) {
                        try (PreparedStatement s2 = connection.prepareStatement("SELECT * FROM holidays")) {
                            ResultSet holidays = s2.executeQuery();
                            while (holidays.next()) {
                                String holidayDate = new SimpleDateFormat("YYYY-MM-DD").format(holidays.getDate("holiday"));
                                if (req.queryParams("date") != null && req.queryParams("date").equals(holidayDate)) {
                                    isHoliday = true;
                                }
                            }
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new SimpleDateFormat("YYYY-MM-DD").parse(req.queryParams("date")));
                        if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                            reduction = 60;
                        }

                        // TODO apply reduction for others
                        if (Integer.parseInt(req.queryParams("age")) < 15) {
                            return "{cost: " + Math.ceil(result.getInt("cost") * .7) + "}";
                        } else {
                            if (Integer.parseInt(req.queryParams("age")) > 74) {
                                return "{cost: " + Math.ceil(result.getInt("cost") * .4) + "}";
                            } else {
                                if (req.queryParams("age") == null) {
                                    double cost = result.getInt("cost");
                                    if (reduction > 0) {
                                        cost = cost / (1 + reduction / 100);
                                    }
                                    return "{cost: " + Math.ceil(cost) + "}";
                                } else {
                                    if (Integer.parseInt(req.queryParams("age")) > 64) {
                                        double cost = result.getInt("cost") * .75;
                                        if (reduction > 0) {
                                            cost = cost / (1 + reduction / 100);
                                        }
                                        return "{cost: " + Math.ceil(cost) + "}";
                                    } else {
                                        double cost = result.getInt("cost");
                                        if (reduction > 0) {
                                            cost = cost / (1 + reduction / 100);
                                        }
                                        return "{cost: " + Math.ceil(cost) + "}";
                                    }
                                }
                            }
                        }
                    } else {
                        if (Integer.parseInt(req.queryParams("age")) >= 6) {
                            if (Integer.parseInt(req.queryParams("age")) > 74) {
                                return "{cost: " + Math.ceil(result.getInt("cost") / 2.5) + "}";
                            } else {
                                return "{cost: " + result.getInt("cost") + "}";
                            }
                        } else {
                            return "{cost: 0}";
                        }
                    }
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
            } catch (SQLException e) {
                LoggerFactory.getLogger(Prices.class).error("connection close", e);
            }
        }));
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        createApp();
    }
}
