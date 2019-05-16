import {assert, expect} from 'chai';
import request from 'supertest-as-promised';
import {createApp} from "../src/prices"

describe('prices', () => {

    let app, connection

    beforeEach(async () => {
        ({app, connection} = await createApp());
    });

    afterEach(function () {
        connection.close()
    });

    it('pass 1jour is €35', async () => {

        const response = await request(app)
            .get('/prices?type=1jour')

        var expectedResult = {cost: 35} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)
    });
    it('pass night is free', async () => {

        const response = await request(app)
            .get('/prices?type=night')
        var expectedResult = {cost: 0} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)
    });
    it('age under 6 is free', async () => {

        const response = await request(app)
            .get('/prices?age=5')

        var expectedResult = {cost: 0} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)
    });
    it('age greater or equal to 6, type is required and price is 25', async () => {

        const response = await request(app)
            .get('/prices?age=6&type=1jour')
        var expectedResult = {cost: 25} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)

    });
    it('age greater or equal to 15, type is required and price is 35', async () => {

        const response = await request(app)
            .get('/prices?age=15&type=1jour')
        var expectedResult = {cost: 35} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)

    });
    it('age greater than 64, type is required and price is 27', async () => {

        const response = await request(app)
            .get('/prices?age=65&type=1jour')
        var expectedResult = {cost: 27} // change this to make the test pass
        expect(response.body).deep.equal(expectedResult)

    });
});
