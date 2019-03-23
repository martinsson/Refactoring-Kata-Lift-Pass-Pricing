using Xunit;
using Nancy;
using Nancy.Testing;
using LiftPassPricing;

namespace LiftPassPricingTests
{
    /// <seealso>"http://www.marcusoft.net/2013/01/NancyTesting1.html"</seealso>
    public class PricesTest
    {
        private readonly Prices prices;

        public PricesTest()
        {
            this.prices = new Prices();
        }

        public void Dispose()
        {
            prices.connection.Close();
        }

        [Fact]
        public void Should_return_status_ok_when_route_exists()
        {
            // Given
            var browser = new Browser(with => with.Module(prices));

            // When
            var result = browser.Get("/", with =>
            {
                with.HttpRequest();
            });

            // Then
            Assert.Equal(HttpStatusCode.OK, result.Result.StatusCode);
        }
    }
}
