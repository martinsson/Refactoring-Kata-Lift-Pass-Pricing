using Nancy;

namespace LiftPassPricing
{
    public class Prices : NancyModule
    {

        public Prices()
        {
            Get("/", args => "Hello from Nancy running on CoreCLR");
        }

    }
}
