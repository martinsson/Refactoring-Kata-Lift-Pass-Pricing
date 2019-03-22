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
            statusCode(200);
    }

    @AfterEach
    public void stopApplication() throws SQLException {
        Spark.stop();
        connection.close();
    }

    @Test
    public void the_1_day_full_price_pass_is_the_standard() {
        JsonPath json = obtainPrice("type", "1jour");
        int cost = json.get("cost");
        assertEquals(35, cost);
    }

    @ParameterizedTest(name = "the 1 day age {0} price pass is rounded up to {1}")
    @CsvSource({ "25, 35", 
                 "14, 25", 
                 "5, 0", 
                 "65, 27", 
                 "75, 14"})
    public void the_1_day_price_pass_is_rounded_up(int age, int expectedCost) {
        JsonPath json = obtainPrice("type", "1jour", "age", Integer.toString(age));
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    @ParameterizedTest
    @CsvSource({ "25, 19", 
                 "14, 19", 
                 "5, 0", 
                 "65, 19", 
                 "75, 8"})
    public void the_night_pass_is_19_for_everyone_but_very_young_and_very_old_people(int age, int expectedCost){
        JsonPath json = obtainPrice("type", "night", "age", Integer.toString(age));
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }

    // Monday x percent off
    @ParameterizedTest(name = "Monday are 40 off, but not during holidays at {1}")
    @CsvSource({ "25, '2019-02-24', 35", // Sunday, no deal
                 "25, '2019-02-25', 35", // Monday holidays, no deal
                 "25, '2019-03-28', 35", // not a Monday
                 "25, '2019-03-25', 22", // ~40% off
                 "65, '2019-03-25', 17"}) // ~40% off
    public void mondays_are_40_off(int age, String date, int expectedCost){
        JsonPath json = obtainPrice("type", "1jour", "age", Integer.toString(age), "date", date);
        int cost = json.get("cost");
        assertEquals(expectedCost, cost);
    }
    
    // TODO 2-4, and 5, 6 day pass
    
    private RequestSpecification given() {
        return RestAssured.given().
            contentType("application/json").
            accept("application/json").
            port(4567); // Java 
            //port(5010); // Typescript
    }
    
    private JsonPath obtainPrice(String... keyValue) {
        RequestSpecification when = given().
                                    when();

        for (int i = 0; i < keyValue.length; i += 2) {
            when = when.params(keyValue[i], keyValue[i + 1]);
        }

        return when.
                    get("/prices").
                then().
                    statusCode(200).
                extract().jsonPath();
    }

}
