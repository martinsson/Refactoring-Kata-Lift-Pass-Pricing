import {createApp} from "../src/prices"
import request from 'supertest'
import {expect} from 'chai';

describe('prices', () => {

    let app, connection

    beforeEach(async () => {
        ({app, connection} = await createApp())
    });

    afterEach(async () => {
        await connection.end()
    });

    it('does something', async () => {

        const {body} = await request(app)
            .get('/prices?type=1jour')

        expect(body.putSomethingHere).equal(35)
    });

});
