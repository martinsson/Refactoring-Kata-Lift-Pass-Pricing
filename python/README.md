# Lift Pass Pricing Python API

Lift Pass Pricing Refactoring Kata.

As with the other language versions, this exercise requires a database. There is a description in the [top level README](../README.md) of how to set up MySQL. If you don't have that, this version should fall back on sqlite3, and create a local database file 'lift_pass.db' in the directory where you run the application.

To install dependencies:

For this python version you will also need to install the dependencies. I recommend you install them in a virtual environment like this:

    python -m venv venv

Activate this environment on your platform. Then install the requirements:

    python -m pip install -r requirements.txt

To run the tests:

    PYTHONPATH=src python -m pytest

or on Windows Powershell:

    $env:PYTHONPATH='src'; python -m pytest

To start the application:

    cd src 
    python -m main
