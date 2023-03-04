import { sleep, spawn, Subprocess } from "bun";
import { expect, test, beforeAll, afterAll } from "bun:test";

const PORT = process.env.PORT || "3000";

let pricesServer: Subprocess;

beforeAll(async () => {
    pricesServer = spawn({ cmd: ["bun", "prices.ts"], env: { PORT } });
    await sleep(300);
});

afterAll(() => {
    pricesServer.kill();
});

test("does something", async () => {
    expect(
        await (await fetch(`localhost:${PORT}/prices?type=1jour`)).json()
    ).toEqual({
        cost: 35,
    });
});
