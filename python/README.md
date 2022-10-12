# Setup


## Make sure you have Python installed

Run the following:

```bash
python --version
```

You should see the version number and hopefully it'll be >= 3.9 (Oct. 5, 2020).

If you don't start by installing it, using, for instance [pyenv][https://github.com/pyenv/pyenv].

## Install dependencies

Start by installing poetry, to manage dependencies.
See [here][https://python-poetry.org/docs/#osx--linux--bashonwindows-install-instructions]

Then you can install all dependencies with:

```bash
./python $ poetry install
```

## Setup the database

```bash
./python $ rm ski.db # To clean the DB, literally
./python $ poetry run python database.py createdb
```

# Run

## The tests

```bash
./python $ poetry run pytest
```

## The server

```bash
./python $ poetry run uvicorn main:app --reload
```
