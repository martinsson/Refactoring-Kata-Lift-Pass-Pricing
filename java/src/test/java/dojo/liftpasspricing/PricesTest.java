package dojo.liftpasspricing;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricesTest {

    private static Connection connection;

    @BeforeAll
    public static void createPrices() throws SQLException {
        connection = Prices.createApp();
    }

    @AfterAll
    public static void stopApplication() throws SQLException {
        Spark.stop();
        connection.close();
    }

    @Test
    public void doesSomething() {
        JsonPath response = RestAssured.
            given().
                port(4567).
            when().
                // construct some proper url parameters
                get("/prices").
            then().
                assertThat().
                    statusCode(200).
                assertThat().
                    contentType("application/json").
            extract().jsonPath();

        assertEquals("putSomehtingHere", response.get("putSomehtingHere"));
    }

}
