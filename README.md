# Lift Pass Pricing

![Image logo](./mountain-snow.jpg)

This application solves the problem of calculating the pricing for ski lift passes.
There's some intricate logic linked to what kind of lift pass you want, your age
and the specific date at which you'd like to ski. There's a new feature request,
be able to get the price for several lift passes, not just one. Currently the pricing
for a single lift pass is implemented, unfortunately the code as it is designed
is ***not reusable***.
You could put some high level tests in place in order to do ***preparatory refactoring***
so that the new feature requires minimum effort to implement.

This kata models a common problem - code that makes no sense to unit test due to bad design.

You can find a [video pitch here](http://youtube.com/watch?v=-gSyD60WAvc)

## When am I done?

There are a few steps, you could do any of them.

1. Cover with high level tests.
1. Refactor the code to maximize unit testability and reuse for the new feature.
1. Pull down most of the high level tests.
1. Implement the new feature using unit tests and 1 or 2 high level tests.

## Installation

Set up a MySQL or MariaDB database on localhost 3306 with user `root` and password `mysql`.

    mysqladmin --user=root password mysql

Inject the data with

    mysql -u root -p mysql < ./database/initDatabase.sql

If you have Docker installed the easiest thing is to use this script, that will initialize a MySQL server.

    ./runLocalDatabase.sh

Then head on to the language of your choice and follow the Readme in there.

## Tips

There's a good chance you could find a design that is both easier to test, faster to
work with and that solves the problem with minimum amount of code. One such design
would be to rid the bulk of the logic from it's adherence to the http/rest framework
and from the sql specificities. This is sometimes called **hexagonal architecture**
and it facilitates respecting the ***Testing Pyramid*** which is not currently
possible - there can be only top-level tests

The typical workflow would be

1. Cover everything from the http layer, use a real DB.
1. Separate request data extraction and sending the response from the logic.
1. Extract a method with the pure logic, move that method to an object (ex PricingLogic).
1. Now extract the sql stuff from PricingLogic, first to some method with a signature that has nothing to do with sql, then move these methods to a new class (ex PricingDao).
1. There should be ~3/4 elements, the http layer should have the PricingLogic as an injected dependency and the PricingLogic should have the PricingDao as an injected dependency.
1. Move the bulk of the high level tests down onto PricingLogic using a fake dao, write some focused integration tests for the PricingDao using a real DB, there should be only a handful.

Now the HTTP layer and the integration of the parts can be tested with very few (one or two) high-level tests.

## Videos

Try it first ;)

[Introduction](https://www.youtube.com/watch?v=-gSyD60WAvc)

[extract the business logic](https://www.youtube.com/watch?v=A06nvXyJBbk)

[encapsulate primitives](https://www.youtube.com/watch?v=vcUCU_WB2uY)

## CONTRIBUTING

There are two branches, the master branch and the with_tests branch. The master is always merged into the with_tests branch. 
So typically if you want to contribute a new language or a *simple* version of a language you typically change the master branch, 
then switch to the with_tests branch and merge with master, then add tests. 

* The logic should be an exact port of existing logic, even with comments.
* There should be two files/modules/classes, one is called `Prices`, one is app, main or program.
* Main creates `Prices` and prints the message with URL and port.
* `Prices` creates its DB connection and returns to main.
* Main closes the connection in the end - if it is possible using the framework.
* The database is MySQL (or MariaDB which is compatible) because it must be difficult to run.
* Exercises should be agnostic of specific build tools or packet manager. Use the simplest way to manage dependencies.
* Omit specific IDE config files and put them into `.gitignore`.
* Do not commit lock files as everybody has a different setup.

There are github actions for all versions, please do provide one if you add a language. This allows
anyone having trouble running the tests with a baseline for getting the tests to work.

When you're ready please submit one pull request for each branch

Thanks for contributing!
