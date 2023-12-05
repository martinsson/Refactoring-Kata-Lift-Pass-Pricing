import dojo.Prices
import io.ktor.server.engine.*
import io.restassured.RestAssured
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Connection
import kotlin.test.assertEquals

internal class PricesTest {
    private lateinit var connection: Connection
    private lateinit var app: ApplicationEngine

    @BeforeEach
    fun `create Prices`() {
        Prices.createApp().let {
            connection = it.first
            app = it.second
        }
        app.start()
    }

    @AfterEach
    fun `stop application`() {
        app.stop(200, 200)
        connection.close()
    }

    @Test
    fun `does something`() {
        val response = RestAssured.
            given()
                .port(4567)
                .When()
                .param("type","1jour")
                .get("/prices")
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .contentType("application/json")
                .extract()
                .jsonPath()

        println(response)

        assertEquals(35, response.getInt("cost"))
    }

    protected fun RequestSpecification.When(): RequestSpecification {
        return this.`when`()
    }

    // allows response.to<Widget>() -> Widget instance
    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return this.`as`(T::class.java)
    }
}

