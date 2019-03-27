package dojo.liftpasspricing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import spark.Spark;

public class PricesTest {

    private Connection connection;

    @BeforeEach
    public void createPrices() throws SQLException {
        connection = Prices.createApp();

        createPrice("1jour", 35);
        createPrice("night", 19);
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

    @AfterEach
    public void stopApplication() throws SQLException {
        Spark.stop();
        connection.close();
    }

    @Test
    public void defaultCost() {
        JsonPath json = obtainPrice("type", "1jour");
        int cost = json.get("cost");
        assertEquals(35, cost);
    }

    @ParameterizedTest
    @CsvSource({ "5, 0", //
                 "6, 25", //
                 "14, 25", //
                 "15, 35", //
                 "25, 35", //
                 "64, 35", //
                 "65, 27" })
    public void worksForAllAges(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "1jour", "age", Integer.toString(age));
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @Test
    public void defaultNightCost() {
        JsonPath json = obtainPrice("type", "night");
        int cost = json.get("cost");
        assertEquals(0, cost); // TODO should be 19
    }

    @ParameterizedTest
    @CsvSource({ "5, 0", //
                 "6, 19", //
                 "25, 19", //
                 "64, 19", // 
                 "65, 8" })
    public void worksForNightPasses(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "night", "age", age);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @ParameterizedTest
    @CsvSource({ "15, '2019-02-22', 35", // 
                 "15, '2019-02-25', 35", //
                 "15, '2019-03-11', 23", //
                 "65, '2019-03-11', 18" })
    public void worksForMondayDeals(int age, String date, int expectedCost) {
        JsonPath json = obtainPrice("type", "1jour", "age", age, "date", date);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }
    
    // TODO 2-4, and 5, 6 day pass
    
    private RequestSpecification given() {
        return RestAssured.given().
            accept("application/json").
            port(4567); // Java 
            //port(5010); // Typescript
            //port(5000); // C#
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
