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
        }

        public void Dispose()
        {
            prices.connection.Close();
        }

        [Fact]
        public void DoesSomething()
        {
            var result = browser.Get("/prices", with =>
            {
                // construct some proper url parameters
                with.HttpRequest();
            });

            Assert.Equal(HttpStatusCode.OK, result.Result.StatusCode);
            Assert.Equal("application/json", result.Result.ContentType);

            Response json = result.Result.Body.DeserializeJson<Response>();
            Assert.Equal(35, json.PutSomethingHere);
        }

    }

    class Response
    {
        public int PutSomethingHere { get; set; }
    }
}
