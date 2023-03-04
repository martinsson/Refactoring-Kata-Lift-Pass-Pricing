import { spawn, Subprocess } from "bun";
import { expect, test, beforeAll, afterAll } from "bun:test";
import pRetry from "p-retry";

const PORT = process.env.PORT || "3000";

let pricesServer: Subprocess;

beforeAll(async () => {
    pricesServer = spawn({ cmd: ["bun", "prices.ts"], env: { PORT } });
    await pRetry(() => fetch(`localhost:${PORT}/prices?type=1jour`), {
        minTimeout: 30,
        maxTimeout: 30,
        factor: 1,
        retries: 30,
    });
});

afterAll(() => {
    pricesServer.kill();
});

test("does something", async () => {
    expect(
        await (await fetch(`localhost:${PORT}/prices?type=1jour`)).json()
    ).toEqual({
        cost: 123, // change this to make the test pass
    });
});
