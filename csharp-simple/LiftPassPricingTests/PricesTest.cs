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

        [Theory]
        [InlineData(5, 0)]
        [InlineData(6, 25)]
        [InlineData(14, 25)]
        [InlineData(15, 35)]
        [InlineData(25, 35)]
        [InlineData(64, 35)]
        [InlineData(65, 27)]
        public void WorksForAllAges(int age, int expectedCost)
        {
            Response json = ObtainPrice("type", "1jour", "age", age.ToString());
            Assert.Equal(expectedCost, json.Cost);
        }

        [Theory]
        [InlineData(5, 0)]
        [InlineData(6, 19)]
        [InlineData(25, 19)]
        [InlineData(64, 19)]
        [InlineData(65, 8)]
        public void WorksForNightPasses(int age, int expectedCost)
        {
            Response json = ObtainPrice("type", "night", "age", age.ToString());
            Assert.Equal(expectedCost, json.Cost);
        }

        [Theory]
        [InlineData(15, "2019-02-22", 35)]
        [InlineData(15, "2019-02-25", 35)]
        [InlineData(15, "2019-03-11", 23)]
        [InlineData(65, "2019-03-11", 18)]
        public void WorksForMondayDeals(int age, string date, int expectedCost)
        {
            Response json = ObtainPrice("type", "1jour", "age", age.ToString(), "date", date);
            Assert.Equal(expectedCost, json.Cost);
        }

        // TODO 2-4, and 5, 6 day pass

        private Response ObtainPrice(params string[] keyValuePairs)
        {
            var result = browser.Get("/prices", with =>
            {
                for (int i = 0; i < keyValuePairs.Length; i += 2)
                {
                    with.Query(keyValuePairs[i], keyValuePairs[i + 1]);
                }
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
