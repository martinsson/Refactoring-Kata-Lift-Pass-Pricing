# Python version of Lift Pass Pricing Kata

As with the other language versions, this exercise requires a database. There is a description in the [top level README](../README.md) of how to set up MySQL. If you don't have that, this version should fall back on sqlite3, and create a local database file 'lift_pass.db' in the directory where you run the application.

For this python version you will also need to install the dependencies. I recommend you install them in a virtual environment like this:

    python -m venv venv

Check the [Python documentation](https://docs.python.org/3/library/venv.html) for how to activate this environment on your platform. Then install the requirements:

    python -m pip install -r requirements.txt

You can start the application like this:

    cd src 
    python -m prices

Note there is no webpage on the default url - try this url as an example to check it's running: http://127.0.0.1:3005/prices?type=1jour

You can run the tests with pytest:

    PYTHONPATH=src python -m pytest

or on Windows Powershell:

    $env:PYTHONPATH='src'; python -m pytest


