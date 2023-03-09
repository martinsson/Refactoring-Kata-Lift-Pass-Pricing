

def create_lift_pass_db_connection(connection_options):
    connection_functions = [try_to_connect_with_odbc, try_to_connect_with_pymysql]
    for fun in connection_functions:
        connection = fun(connection_options)
        if connection is not None:
            return connection
    raise RuntimeError("Unable to connect to the database.")


def try_to_connect_with_pymysql(connection_options):
    import pymysql.cursors

    class PyMySQLCursorWrapper(pymysql.cursors.Cursor):
        """
        The pymysql.cursors.Cursor class very nearly works the same as the odbc equivalent. Unfortunately it doesn't
        understand the '?' in a SQL statement as an argument placeholder, and instead uses '%s'. This wrapper fixes that.
        """
        def mogrify(self, query: str, args: object = ...) -> str:
            query = query.replace('?', '%s')
            return super().mogrify(query, args)

    connection = pymysql.connect(host=connection_options["host"],
                                 user=connection_options["user"],
                                 password=connection_options["password"],
                                 database=connection_options["database"],
                                 cursorclass=PyMySQLCursorWrapper)

    return connection


def try_to_connect_with_odbc(connection_options):
    driver = get_mariadb_driver()
    if driver:
        import pyodbc
        connection_string = make_connection_string_template(driver) % (
            connection_options["host"],
            connection_options["user"],
            connection_options["database"],
            connection_options["password"],
        )
        return pyodbc.connect(connection_string)
    return None


def get_mariadb_driver():
    import pyodbc
    drivers = []
    for driver in pyodbc.drivers():
        if driver.startswith("MySQL") or driver.startswith("MariaDB"):
            drivers.append(driver)

    if drivers:
        return max(drivers)
    else:
        return None


def make_connection_string_template(driver):
    return 'DRIVER={' + driver + '};SERVER=%s;USER=%s;OPTION=3;DATABASE=%s;PASSWORD=%s'