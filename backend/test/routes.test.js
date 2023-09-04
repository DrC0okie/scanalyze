const request = require('supertest')
const server = require('../app');

describe('POST receipts', () => {
    it('Correct POST request should return 200 and a non empty body', async () => {
        const res = await request(server)
            .post('/receipts')
            .send({
                receipt: {
                    shop_branch: "",
                    shop_name: "migros",
                    total: 0.0,
                    user_id: "jest",
                    products: [
                        {
                            "discount_amount": 0.0,
                            "product_name": "raisins uittoria",
                            "quantity": 3.5,
                            "total_price": 0.0,
                            "unit_price": 2.15
                        },
                    ]
                }
            })
        expect(res.statusCode).toEqual(200);
    })
    it('POST request with no receipt in body should return 400', async () => {
        const res = await request(server)
            .post('/receipts')
            .send({});
        expect(res.statusCode).toEqual(400);
        expect(res.body).toHaveProperty('error');
        expect(res.body.error).toEqual("Empty body");
    })
    it('POST request with no product should return 400', async () => {
        const res = await request(server)
            .post('/receipts')
            .send({
                receipt: {
                    shop_branch: "",
                    shop_name: "migros",
                    total: 0.0,
                    user_id: "jest",
                    products: []
                }
            });
        expect(res.statusCode).toEqual(400);
        expect(res.body).toHaveProperty('error');
        expect(res.body.error).toEqual("No products in receipt");
    })
})