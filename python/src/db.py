import pyodbc


def create_lift_pass_db_connection(connection_options):
    driver = get_mariadb_driver()
    connection_string = make_connection_string_template(driver) % (
        connection_options["host"],
        connection_options["user"],
        connection_options["database"],
        connection_options["password"],
    )
    return pyodbc.connect(connection_string)


def get_mariadb_driver():
    drivers = []
    for driver in pyodbc.drivers():
        if driver.startswith("MySQL") or driver.startswith("MariaDB"):
            drivers.append(driver)

    if drivers:
        return max(drivers)
    else:
        raise RuntimeError("No suitable drivers found for MySQL, is it installed?")


def make_connection_string_template(driver):
    return 'DRIVER={' + driver + '};SERVER=%s;USER=%s;OPTION=3;DATABASE=%s;PASSWORD=%s'