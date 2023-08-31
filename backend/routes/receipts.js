const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const fake_receipt = require("../data/migros-receipt.json");
const fake_receipt2 = require("../data/migros-receipt-2.json")
const index = require('../utils/indexation');
const connect_db = require("../utils/dbconn");
const { ObjectId} = require("mongodb");
/* Post new receipts */

function randomIntFromInterval(min, max) { // min and max included 
  return Math.floor(Math.random() * (max - min + 1) + min)
}

router.post('/', async (req, res, next) => {

  if (!req.body.receipt) {
    res.status(400).json({
      error: "Empty body"
    })
  }
  const receipt = req.body.receipt;
  receipt.date = new Date(Date.now());
  receipt.date.setMonth(randomIntFromInterval(1,12))
  receipt.date.setFullYear(randomIntFromInterval(2019,2023))
  const db = await connect_db();
  const products_collection = db.collection("migros_products");


  let indexed_product = []
  for (let receipt_product of receipt.products) {
    // if indexed on cherche dans la db sinon

    const products = await products_collection.find({
      $or: [
        { actual_price: 0 },
        { actual_price: parseFloat(receipt_product.price) }
      ]
    }).toArray();


    indexed_product.push(index.run(receipt_product, products));
  }
  receipt.products = indexed_product;

  const receipt_collection = db.collection("receipts");
  receipt_collection.insertOne(receipt);

  res.status(200).json({
    receipt
  });
});
/* GET a receipt with products*/
router.get('/:id', async (req, res, next) => {
  const db = await connect_db();
  const receipts_collection = db.collection("receipts");
  let receipt;


  try {
    receipt = await receipts_collection.findOne({
      _id: new ObjectId(req.params.id)
    });
  } catch (e) {
    res.status(400).json({
      error: "Receipt not found"
    });
    return;
  }

  res.status(200).json({
    receipt
  });


})
/* Get a all recipes */
router.get('/', async (req, res, next) => {
  const from = req.query.from;
  const to = req.query.to;
  console.log(new Date(from) + "-" + new Date(to));
  //results.push(index.test("Levenshtein + Jaro + dice price weighted",test.triple_jaro_levenshtei_dice_price,receipt,products,true));
  //results.push(test.execute("Dice + Jaro price weighted",test.combo_dice_jaro_price_weighted,receipt,products,false));

  const db = await connect_db();
  const receipts_collection = db.collection("receipts");
  const receipts = await receipts_collection.find({},
    {
      projection: {
        _id: 1,
        user_id: 1,
        shop_name: 1,
        shop_branch: 1
      }
    }).toArray();


  res.status(200).json({
    receipts
  });
});


module.exports = router;
