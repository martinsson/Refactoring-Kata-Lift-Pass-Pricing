using Xunit;
using Nancy;
using Nancy.Testing;
using LiftPassPricing;

namespace LiftPassPricingTests
{
    /// <seealso>"http://www.marcusoft.net/2013/01/NancyTesting1.html"</seealso>
    public class PricesTest
    {

        [Fact]
        public void Should_return_status_ok_when_route_exists()
        {
            // Given
            // var bootstrapper = new DefaultNancyBootstrapper();
            var browser = new Browser(with => with.Module(new Prices()));

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
