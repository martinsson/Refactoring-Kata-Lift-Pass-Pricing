package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import spark.Spark;

public class PricesTest {

    private Connection connection;

    @BeforeEach
    public void createPrices() throws SQLException {
        connection = Prices.createApp();
    }

    @AfterEach
    public void stopApplication() throws SQLException {
        Spark.stop();
        connection.close();
    }

    @Test
    public void doesSomething() {
        JsonPath response = RestAssured.
            given().
                port(4567).
            when().
                param("type", "1jour").
                get("/prices").

            then().
                assertThat().
                    statusCode(200).
                assertThat().
                    contentType("application/json").
            extract().jsonPath();

        assertEquals(35, response.getInt("cost"));
    }

}
