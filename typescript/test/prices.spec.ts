import got from "got"
import {expect} from "chai";
import {createApp} from "../src/prices"
import request from 'supertest'

describe('prices', () => {
    let app
    beforeEach(function () {
        app  = createApp()
    });
    it('the 1 day full price pass is the standard', async () => {
        await request(app)
            .put('/prices?type=1jour&cost=35')
            .expect(200)

        await request(app)
            .get('/prices?type=1jour')
            .expect((res) => {
                expect(res.body.price).to.equal(35)
            })

    });

});
