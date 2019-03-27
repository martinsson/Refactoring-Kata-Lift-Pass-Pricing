using System;
using Xunit;
using Nancy;
using Nancy.Testing;
using LiftPassPricing;
using System.Collections.Generic;

namespace LiftPassPricingTests
{
    /// <seealso>"http://www.marcusoft.net/2013/01/NancyTesting1.html"</seealso>
    public class PricesTest : IDisposable
    {
        private readonly Prices prices;
        private readonly Browser browser;

        public PricesTest()
        {
            this.prices = new Prices();
            this.browser = new Browser(with => with.Module(prices));

            CreatePrice("1jour", 35);
            CreatePrice("night", 19);
        }

        private void CreatePrice(string type, int cost)
        {
            var result = browser.Put("/prices", with =>
            {
                with.Query("type", type);
                with.Query("cost", cost.ToString());
                with.HttpRequest();
            });

            Assert.Equal("application/json", result.Result.ContentType);
            Assert.Equal(HttpStatusCode.OK, result.Result.StatusCode); // TODO should be 204
        }

        public void Dispose()
        {
            prices.connection.Close();
        }

        [Fact]
        public void DefaultCost()
        {
            Response json = ObtainPrice("type", "1jour");
            Assert.Equal(35, json.Cost);
        }

        private Response ObtainPrice(string paramName, string paramValue)
        {
            var result = browser.Get("/prices", with =>
            {
                with.Query(paramName, paramValue);
                with.HttpRequest();
            });

            Assert.Equal("application/json", result.Result.ContentType);
            Assert.Equal(HttpStatusCode.OK, result.Result.StatusCode);

            return result.Result.Body.DeserializeJson<Response>();
        }
    }

    class Response
    {
        public int Cost { get; set; }
    }
}
