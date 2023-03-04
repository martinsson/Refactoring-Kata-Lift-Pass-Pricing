# Installs

-   Install [Bun](https://bun.sh/)
-   Install deps

    ```shell
    bun install
    ```

# Start the server

```shell
bun --hot prices.ts
```

with different port (default is 3000)

```shell
PORT=3001 bun --hot prices.ts
```

# Run the (failing, e2e) test

```shell
bun test
```
