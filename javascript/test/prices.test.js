const { createApp } = require("../src/prices");
const request = require("supertest");

describe('prices', () => {

    let app, connection

    beforeEach(async () => {
        ({app, connection} = await createApp())
    });

    afterEach(function () {
        connection.close()
    });

    it('does something', async () => {
        const {body} = await request(app)
            .get('/prices')

        expect(body.putSomethingHere).toEqual(35)
    })
})