using System;
using System.IO;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Nancy.Owin;

namespace LiftPassPricing
{
    /// <summary>Start Nancy.</summary>
    /// <seealso>"https://www.hanselman.com/blog/ExploringAMinimalWebAPIWithNETCoreAndNancyFX.aspx"</seealso>
    /// <seealso>"https://github.com/NancyFx/Nancy/tree/master/samples/Nancy.Demo.Hosting.Kestrel"</seealso>
    public class Program
    {

        public static void Main(string[] args)
        {
            var host = new WebHostBuilder()
                .UseContentRoot(Directory.GetCurrentDirectory())
                .UseKestrel()
                .UseStartup<Startup>()
                .Build();

            Console.WriteLine(@"LiftPassPricing Api started on 5000,
you can open http://localhost:5000/prices?type=night&age=23&date=2019-02-18 in a navigator
and you'll get the price of the list pass for the day.");
            host.Run();
        }
    }

    public class Startup
    {
        public void Configure(IApplicationBuilder app)
        {
            app.UseOwin(x => x.UseNancy());
        }
    }

}
