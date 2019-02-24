import {expect} from "chai";
import {createApp} from "../src/prices"
import request from 'supertest'

describe('prices', () => {
    let app, connection
    beforeEach(async function () {
        ({app, connection}  = await createApp())
        await request(app)
            .put(toUrl('/prices', {type: '1jour', cost: 35}))
            .expect(200)
        await request(app)
            .put(toUrl('/prices', {type: 'night', cost: 19}))
            .expect(200)
    });
    afterEach(async function () {
        await connection.close();
    });

    it('the 1 day full price pass is the standard', async () => {

        await obtainPrices({type: '1jour'})
            .expect((res) => {
                expect(res.body).property('cost', 35)
            })
    });

    [
        [25, 35],
        [14, 25],
        [5, 0],
        [65, 27],
        [75, 15]
    ].forEach(([age, expectedCost]) => {
        it('the 1 day children price pass is 30% off, rounded up', async () => {
            let params = {type: '1jour', age}
            await obtainPrices(params)
                .expect( res => {
                    expect(res.body).property('cost', expectedCost)
                })
        });
    });


    [
        [25, 19],
        [14, 19],
        [65, 19],
        [5, 0],
        [75, 8]
    ].forEach(([age, expectedCost]) => {
        it('the night pass is 19 for everyone but very young and very old people', async () => {
            let params = {type: 'night', age}
            await obtainPrices(params)
                .expect( res => {
                    expect(res.body).property('cost', expectedCost)
                })
        });
    });


    // todo 2-4, and 5, 6 day pass

    function obtainPrices(params) {
        return request(app)
            .get(toUrl('/prices', params))
    }

});


function toParamsString(params: object) {
    return Object.entries(params)
        .map(([k, v]) => k + "=" + v)
        .join('&');
}

function toUrl(path: string, params: object) {
    let urlParams = toParamsString(params)
    return path + '?' + urlParams
}

