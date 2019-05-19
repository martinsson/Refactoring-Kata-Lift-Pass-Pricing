package dojo.liftpasspricing;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        @SuppressWarnings("resource")
        Connection connection = Prices.createApp();

        System.out.println("LiftPassPricing Api started on 4567,\n"
                + "you can open http://localhost:4567/prices?type=night&age=23&date=2019-02-18 in a navigator\n"
                + "and you'll get the price of the list pass for the day.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
            } catch (SQLException e) {
                LoggerFactory.getLogger(Main.class).error("connection close", e);
            }
        }));
    }
}
