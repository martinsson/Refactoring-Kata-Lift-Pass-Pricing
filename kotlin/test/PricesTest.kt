import dojo.Prices
import io.ktor.server.engine.*
import io.restassured.RestAssured
import io.restassured.path.json.JsonPath
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.sql.Connection
import kotlin.test.assertEquals

internal class PricesTest {
    private lateinit var connection: Connection
    private lateinit var app: ApplicationEngine

    @BeforeEach
    fun createPrices() {
        Prices.createApp().let {
            connection = it.first
            app = it.second
        }
        app.start()
    }

    @ParameterizedTest
    @CsvSource("1jour, 35",
            "night, 19")
    fun updateDefaultPrice(type: String, cost: Int) {
        createPrice(type, cost)
    }

    private fun createPrice(type: String, cost: Int) {
        given().
            `when`().
            params("type", type, "cost", cost).
            put("/prices").
        then().
            assertThat().
                contentType("application/json").
            assertThat().
                statusCode(200) // TODO should be 204
    }

    @AfterEach
    fun stopApplication() {
        app.stop(200, 200)
        connection.close()
    }

    @Test
    fun defaultCost() {
        val json = obtainPrice("type", "1jour")
        val cost = json.get<Int>("cost")
        assertEquals(35, cost)
    }

    @ParameterizedTest
    @CsvSource("5, 0",
            "6, 25",
            "14, 25",
            "15, 35",
            "25, 35",
            "64, 35",
            "65, 27") //
    fun worksForAllAges(age: String, expectedCost: Int) {
        val json = obtainPrice("type", "1jour", "age", age)
        val cost = json.get<Int>("cost")
        assertEquals(expectedCost, cost)
    }

    @Test
    fun realNightCost() {
        val json = obtainPrice("type", "night")
        val cost = json.get<Int>("cost")
        assertEquals(0, cost)
    }

    @Test
    @Disabled
    fun defaultNightCost() {
        val json = obtainPrice("type", "night")
        val cost = json.get<Int>("cost")
        assertEquals(19, cost)
    }

    @ParameterizedTest
    @CsvSource("5, 0",
            "6, 19",
            "25, 19",
            "64, 19",
            "65, 8") //
    fun worksForNightPasses(age: Int, expectedCost: Int) {
        val json = obtainPrice("type", "night", "age", age)
        val cost = json.get<Int>("cost")
        assertEquals(expectedCost, cost)
    }

    @ParameterizedTest
    @CsvSource("15, '2019-02-22', 35", 
            "15, '2019-02-25', 35",
            "15, '2019-03-11', 23",
            "65, '2019-03-11', 18") //
    fun worksForMondayDeals(age: Int, date: String, expectedCost: Int) {
        val json = obtainPrice("type", "1jour", "age", age, "date", date)
        val cost = json.get<Int>("cost")
        assertEquals(expectedCost, cost)
    }

    // TODO 2-4, and 5, 6 day pass

    private fun given(): RequestSpecification {
        return RestAssured.given().
            accept("application/json").
            port(4567)
    }

    private fun obtainPrice(paramName: String, paramValue: Any, vararg otherParamPairs: Any): JsonPath {
        return given().
            `when`().
                params(paramName, paramValue,  *otherParamPairs).
                get("/prices").
            then().
                assertThat().
                    contentType("application/json").
                assertThat().
                    statusCode(200).
                extract().jsonPath()
    }

    protected fun RequestSpecification.When(): RequestSpecification {
        return this.`when`()
    }

    // allows response.to<Widget>() -> Widget instance
    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return this.`as`(T::class.java)
    }
}
