package dojo.liftpasspricing;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    @ParameterizedTest
    @CsvSource({"1jour, 35", //
            "night, 19"})
    public void updateDefaultPrice(String type, int cost) {
        createPrice(type, cost);
    }

    @Test
    public void createNewPrice() throws SQLException {
        createPrice("summer", 15);
        try (PreparedStatement stmt = connection.prepareStatement( //
                "delete from lift_pass.base_price where lift_pass.base_price.type = 'summer';")){
            int row = stmt.executeUpdate();
            assertEquals(1, row);
        };
    }

    private void createPrice(String type, int cost) {
        given().
                when().
                params("type", type, "cost", cost).
                put("/prices").
                then().
                assertThat().
                contentType("application/json").
                assertThat().
                statusCode(200); // TODO should be 204
    }

    @Test
    public void defaultCost() {
        JsonPath json = obtainPrice("type", "1jour");
        int cost = json.get("cost");
        assertEquals(35, cost);
    }

    @ParameterizedTest
    @CsvSource({"5, 0", //
            "6, 25", //
            "14, 25", //
            "15, 35", //
            "25, 35", //
            "64, 35", //
            "65, 27"})
    public void worksForAllAges(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "1jour", "age", Integer.toString(age));
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @Test
    public void realNightCost() {
        JsonPath json = obtainPrice("type", "night");
        int cost = json.get("cost");
        assertEquals(0, cost);
    }

    @Test
    @Disabled
    public void defaultNightCost() {
        JsonPath json = obtainPrice("type", "night");
        int cost = json.get("cost");
        assertEquals(19, cost);
    }

    @ParameterizedTest
    @CsvSource({"5, 0", //
            "6, 19", //
            "25, 19", //
            "64, 19", //
            "65, 8"})
    public void worksForNightPasses(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "night", "age", age);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @ParameterizedTest
    @CsvSource({"15, '2019-02-22', 35", //
            "15, '2019-02-25', 35", //
            "15, '2019-03-11', 23", //
            "65, '2019-03-11', 18"})
    public void worksForMondayDeals(int age, String date, int expectedCost) {
        JsonPath json = obtainPrice("type", "1jour", "age", age, "date", date);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    // TODO 2-4, and 5, 6 day pass

    private RequestSpecification given() {
        return RestAssured.given().
                accept("application/json").
                // port(4567);
                        port(port());
    }

    /**
     * Determine port dynamic to test other languages.
     */
    private int port() {
        String port = System.getProperty("port");
        if (port != null && port.matches("\\d+")) {
            return Integer.parseInt(port);
        }

        String language = System.getProperty("language");
        language = language != null ? language.toLowerCase() : "java";
        switch (language) {
            case "ts":
            case "typescript":
            case "express":
                return 5010;
            case "java":
            case "spark":
                return 4567;
            case "cs":
            case "c#":
            case "csharp":
            case "nancy":
                return 5000;
            case "scala":
            case "akka":
                return 5010;
            default:
                throw new IllegalArgumentException("Unknown language \"" + language + "\"");
        }
    }

    private JsonPath obtainPrice(String paramName, Object paramValue, Object... otherParamPairs) {
        return given().
                when().
                params(paramName, paramValue, otherParamPairs).
                get("/prices").
                then().
                assertThat().
                contentType("application/json").
                assertThat().
                statusCode(200).
                extract().jsonPath();
    }

}
