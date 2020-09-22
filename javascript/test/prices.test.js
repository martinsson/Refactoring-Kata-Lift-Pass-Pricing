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
        const response = await request(app)
            .get('/prices?type=1jour')

        const expectedResult = {cost: 123} // change this to make the test pass
        expect(response.body).toEqual(expectedResult)    
    })    
})