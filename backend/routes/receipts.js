const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const fake_receipt = require("../data/migros-receipt.json");
const fake_receipt2 = require("../data/migros-receipt-2.json")
const index = require('../utils/indexation');
const connect_db = require("../utils/dbconn");
const { ObjectId} = require("mongodb");
const products = require("../data/migros.json");
/* Post new receipts */

function randomIntFromInterval(min, max) { // min and max included 
  return Math.floor(Math.random() * (max - min + 1) + min)
}
/* Test route to test indexaction algorithm */

router.get('/test',async (req,res,next)=>{
  const receipt = fake_receipt;
  const result = []
    
  result.push(index.test("Triple",index.triple_jaro_levenshtei_dice_price,receipt,products,false))
  
  res.status(200).json(result);
})
router.post('/', async (req, res, next) => {

  if (!req.body.receipt) {
    res.status(400).json({
      error: "Empty body"
    })
    return;
  }
  const receipt = req.body.receipt;
  receipt.date = new Date(Date.now());
  receipt.date.setMonth(randomIntFromInterval(1,12))
  receipt.date.setFullYear(randomIntFromInterval(2019,2023))
  const db = await connect_db();
  const products_collection = db.collection("products_"+receipt.shop_name);
  const acronyms_collection = db.collection("acronyms");

  let indexed_product = []
  for (let receipt_product of receipt.products) {
    receipt.total += receipt_product.total_price;
    console.log(receipt_product);
    let found_product;
    //Searching in the acronym document if the product is already indexed
    const indexed_acronym = await acronyms_collection.findOne({
      acronym:receipt_product.product_name
    })
    //If the product is already indexed we just retrive the product
    //Otherwise we get all products that match the price and index it
    if(indexed_acronym != null){
      found_product = await products_collection.findOne({
        _id: indexed_acronym.product_id
      });
    }else{
      const products = await products_collection.find({
        $or: [
          { actual_price: 0 },
          { actual_price: receipt_product.unit_price }
        ]
      }).toArray();
      //Running the algorithm to match the best product
      found_product = index.run(receipt_product, products);
      //Once we find the best match, we save it in the acronym document so we don't need
      // to reindex it 
      acronyms_collection.insertOne({
        acronym:receipt_product.product_name,
        product_id: found_product._id
      })
    }
    
    receipt_product.product_name = found_product.name;
    receipt_product.category= found_product.category;

    indexed_product.push(receipt_product);
  }
  receipt.products = indexed_product;
  receipt.total = parseFloat(receipt.total.toFixed(2));

  //Saving the receipt in the database
  const receipt_collection = db.collection("receipts");
  receipt_collection.insertOne(receipt);

  res.status(200).json({
    receipt
  });
});
/* GET a receipt with products by id*/
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
