from prices import app

if __name__ == "__main__":
    print("""LiftPassPricing Api started on 3005,
you can open http://127.0.0.1:3005/prices?type=night&age=23&date=2019-02-18 in a navigator
and you'll get the price of the list pass for the day.""")
    app.run(port=3005, threaded=False)
