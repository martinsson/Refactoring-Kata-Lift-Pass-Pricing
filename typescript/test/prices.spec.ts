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

    it('does something', async () => {

        const response = await request(app)
            .get('/prices?type=1jour')

        var exptectedResult = {cost: 35} // change this to make the test pass
        expect(response.body).deep.equal(exptectedResult)
    });

});
