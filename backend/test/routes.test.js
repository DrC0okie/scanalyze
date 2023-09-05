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

describe('GET receipts', () => {
    it('GET receipts should return all receipt',async ()=>{
        const res = await request(server)
        .get('/receipts')
        
        expect(res.statusCode).toEqual(200);
        expect(res.body).toHaveProperty('receipts');
    })

})

describe('GET receipt with id', () => {
    it('GET receipts with correct id should return the receipt with his products',async ()=>{
        const res = await request(server)
        .get('/receipts/64f1eda16af940a672e01ca1')
        
        expect(res.statusCode).toEqual(200);
        expect(res.body).toHaveProperty('receipt');
        expect(res.body.receipt).toHaveProperty('products');
        expect(res.body.receipt.products.length).not.toEqual(0);
    })
    it('GET receipts with werong id should return status code 400',async ()=>{
        const res = await request(server)
        .get('/receipts/wrongid')
        
        expect(res.statusCode).toEqual(400);
        expect(res.body).toHaveProperty('error');
        expect(res.body.error).toEqual('Receipt not found');
    })

})


describe('GET statistics', () => {
    it('Get statistics with a correct <from> and <to> should return 200',async ()=>{
        const res = await request(server)
        .get('/statistics?to=01-01-2022&from=20-12-2019')
        
        expect(res.statusCode).toEqual(200);
        expect(res.body).toHaveProperty('total');
        expect(res.body).toHaveProperty('total_category');
        expect(res.body).toHaveProperty('receipts');
        expect(res.body.receipts.length).not.toEqual(0)
    })
    it('GET statistics with missing <from> and <to> should return 400',async ()=>{
        const res = await request(server)
        .get('/statistics')
        
        expect(res.statusCode).toEqual(400);
        expect(res.body).toHaveProperty('error');
        expect(res.body.error).toEqual('Missing <from> or <to> in query');
    })
    it('GET statistics with malformated <from> and <to> should return 400',async ()=>{
        const res = await request(server)
        .get('/statistics?to=01.01.2022&from=32-53-2019')
        
        expect(res.statusCode).toEqual(400);
        expect(res.body).toHaveProperty('error');
        expect(res.body.error).toEqual('query <from> or <to> are malformated, use the following format [day]-[month]-[fullYear]');
    })

})

