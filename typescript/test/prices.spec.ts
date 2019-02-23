import {expect} from "chai";
import {createApp} from "../src/prices"
import request from 'supertest'

function toParamsString(params: object) {
    return Object.entries(params)
        .map(([k, v]) => k + "=" + v)
        .join('&');
}

function toUrl(path: string, params: object) {
    let urlParams = toParamsString(params)
    return path + '?' + urlParams
}

describe('prices', () => {
    let app, connection
    beforeEach(async function () {
        ({app, connection}  = await createApp())
        let url = toUrl('/prices', {type: '1jour', cost: 35})
        await request(app)
            .put(url)
            .expect(200)
    });
    afterEach(async function () {
        await connection.close();
    });

    it('the 1 day full price pass is the standard', async () => {

        await request(app)
            .get(toUrl('/prices', {type: '1jour'}))
            .expect((res) => {
                expect(res.body).property('cost', 35)
            })
    });

    it('the 1 day children price pass is 30% off, rounded up', async () => {
        await request(app)
            .get(toUrl('/prices', {type: '1jour', age: 14}))
            .expect( res => {
                expect(res.body).property('cost', 25)
            })

    });




});
