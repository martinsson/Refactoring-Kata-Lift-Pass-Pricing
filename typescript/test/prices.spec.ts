import {createApp} from "../src/prices"

import request from 'supertest'
import {expect} from 'chai';

describe('prices', () => {

    let app, connection

    before(async () => {
        ({connection, app} = await createApp())
        await request(app).put('/prices?type=1jour&cost=35')
    });

    after(async () => {
        await connection.close()
    });

    it('default cost', async () => {
        const {body} = await request(app).get('/prices?type=1jour')
        expect(body.cost).equal(35)
    });

    [
        {age: 15, expectedCost: 35},
        {age: 14, expectedCost: 25},
        {age: 5, expectedCost: 0},
        {age: 65, expectedCost: 27},
    ]
        .forEach(({age, expectedCost}) => {
            it('works for all ages', async () => {
                const {body} = await request(app)
                    .get(`/prices?type=1jour&age=${age}`)

                expect(body.cost).equal(expectedCost)

            });
        });

    [
        {age: 25, expectedCost: 19},
        {age: 65, expectedCost: 8},
        {age: 5, expectedCost: 0},
    ]
        .forEach(({age, expectedCost}) => {
            it('works for night passes', async () => {
                const {body} = await request(app)
                    .get(`/prices?type=night&age=${age}`)

                expect(body.cost).equal(expectedCost)

            });
        });

    [
        {age: 15, expectedCost: 35, date: '2019-02-22'},
        {age: 15, expectedCost: 35, date: '2019-02-24'},
        {age: 15, expectedCost: 22, date: '2019-03-10'},
        {age: 65, expectedCost: 17, date: '2019-03-10'},
    ]
        .forEach(({age, expectedCost, date}) => {
            it('works for monday deals', async () => {
                const {body} = await request(app)
                    .get(`/prices?type=1jour&age=${age}&date=${date}` )

                expect(body.cost).equal(expectedCost)

            });
        })



});
