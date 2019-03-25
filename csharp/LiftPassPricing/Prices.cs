using System;
using System.Globalization;
using Nancy;
using MySql.Data.MySqlClient;

namespace LiftPassPricing
{
    public class Prices : NancyModule
    {

        public readonly MySqlConnection connection;

        public Prices()
        {
            this.connection = new MySqlConnection
            {
                ConnectionString = @"Database=lift_pass;Data Source=localhost;User Id=root;Password=mysql"
            };
            connection.Open();

            Put("/prices", _ =>
            {
                int liftPassCost = Int32.Parse(this.Request.Query["cost"]);
                string liftPassType = this.Request.Query["type"];

                using (var command = new MySqlCommand( //
                       "INSERT INTO base_price (type, cost) VALUES (@type, @cost) " + //
                       "ON DUPLICATE KEY UPDATE cost = @cost;", connection))
                {
                    command.Parameters.AddWithValue("@type", liftPassType);
                    command.Parameters.AddWithValue("@cost", liftPassCost);
                    command.Prepare();
                    command.ExecuteNonQuery();
                }

                // TODO should return 204 in all languages
                // see https://stackoverflow.com/questions/31819021/override-the-default-content-type-for-responses-in-nancyfx
                return "";
            });

            Get("/prices", _ =>
            {
                using (var costCmd = new MySqlCommand("SELECT cost FROM base_price WHERE type = @type", connection))
                {
                    costCmd.Parameters.AddWithValue("@type", this.Request.Query["type"]);
                    costCmd.Prepare();
                    double result = (int)costCmd.ExecuteScalar();

                    var reduction = 0;
                    var isHoliday = false;

                    if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) < 6)
                    {
                        return "{ \"cost\": 0}";
                    }
                    else
                    {
                        if (!"night".Equals(this.Request.Query["type"]))
                        {
                            using (var holidayCmd = new MySqlCommand("SELECT * FROM holidays", connection))
                            {
                                holidayCmd.Prepare();
                                using (var holidays = holidayCmd.ExecuteReader())
                                {

                                    while (holidays.Read())
                                    {
                                        var holiday = holidays.GetDateTime("holiday");
                                        if (this.Request.Query["date"] != null)
                                        {
                                            DateTime d = DateTime.ParseExact(this.Request.Query["date"], "yyyy-MM-dd", CultureInfo.InvariantCulture);
                                            if (d.Year == holiday.Year &&
                                                d.Month == holiday.Month &&
                                                d.Date == holiday.Date)
                                            {
                                                isHoliday = true;
                                            }
                                        }
                                    }

                                }
                            }

                            if (this.Request.Query["date"] != null)
                            {
                                DateTime d = DateTime.ParseExact(this.Request.Query["date"], "yyyy-MM-dd", CultureInfo.InvariantCulture);
                                if (!isHoliday && (int)d.DayOfWeek == 1)
                                {
                                    reduction = 60;
                                }
                            }

                            // TODO apply reduction for others
                            if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) < 15)
                            {
                                return "{ \"cost\": " + (int)Math.Ceiling(result * .7) + "}";
                            }
                            else
                            {
                                if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) > 74)
                                {
                                    return "{ \"cost\": " + (int)Math.Ceiling(result * .4) + "}";
                                }
                                else
                                {
                                    if (this.Request.Query["age"] == null)
                                    {
                                        double cost = result;
                                        if (reduction > 0)
                                        {
                                            cost = cost / (1 + reduction / 100.0);
                                        }
                                        return "{ \"cost\": " + (int)Math.Ceiling(cost) + "}";
                                    }
                                    else
                                    {
                                        if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) > 64)
                                        {
                                            double cost = result * .75;
                                            if (reduction > 0)
                                            {
                                                cost = cost / (1 + reduction / 100.0);
                                            }
                                            return "{ \"cost\": " + (int)Math.Ceiling(cost) + "}";
                                        }
                                        else
                                        {
                                            double cost = result;
                                            if (reduction > 0)
                                            {
                                                cost = cost / (1 + reduction / 100.0);
                                            }
                                            return "{ \"cost\": " + (int)Math.Ceiling(cost) + "}";
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) >= 6)
                            {
                                if (this.Request.Query["age"] != null && Int32.Parse(this.Request.Query["age"]) > 74)
                                {
                                    return "{ \"cost\": " + (int)Math.Ceiling(result / 2.5) + "}";
                                }
                                else
                                {
                                    return "{ \"cost\": " + result + "}";
                                }
                            }
                            else
                            {
                                return "{ \"cost\": 0}";
                            }
                        }
                    }
                }

            });

        }

    }
}
