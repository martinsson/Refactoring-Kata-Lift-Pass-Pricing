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
            .get('/prices') // construct some proper url parameters

        var putSomehtingHere = {}
        expect(response.body).deep.equal(putSomehtingHere)
    });

});
