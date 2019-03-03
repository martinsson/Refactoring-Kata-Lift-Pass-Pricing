import {createApp} from "../src/prices"

import request from 'supertest'
import { assert, expect } from 'chai';

describe('prices', () => {

    let app, connection

    before(async () => {
        ({connection, app} = await createApp())
        await request(app).put('/prices?type=1jour&cost=35')
    });

    after(async () => {
        await connection.close()
    });

    it('works', async () => {
        const {body} = await request(app)
            .get('/prices?type=1jour')

        // assert something


    });

});
