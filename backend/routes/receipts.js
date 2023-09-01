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
  const products_collection = db.collection("products_migros");
  const acronyms_collection = db.collection("acronyms");

  let indexed_product = []
  for (let receipt_product of receipt.products) {
    console.log(receipt_product);
    let found_product;
    // On cherche si le produit à déjà été indexé
    const indexed_acronym = await acronyms_collection.findOne({
      acronym:receipt_product.name
    })
    //Si on a trouver un produit déjà indexer on cherche ces informations, sinon on indexe le produit
    if(indexed_acronym != null){
      found_product = await products_collection.findOne({
        _id: indexed_acronym.product_id
      });
    }else{
      //On récupère tout les produits du magasin ayant le prix du produit scanné ou pas de prix
      const products = await products_collection.find({
        $or: [
          { actual_price: 0 },
          { actual_price: receipt_product.price }
        ]
      }).toArray();
      //On cherche le produit qui correspond le mieux
      found_product = index.run(receipt_product, products);
      //On enregistre l'indexaction dans le document acronym
      acronyms_collection.insertOne({
        acronym:receipt_product.name,
        product_id: found_product._id
      })
    }
    
    receipt_product.name = found_product.name;
    receipt_product.category= found_product.category;


    indexed_product.push(receipt_product);
  }
  receipt.products = indexed_product;
  //On enregistre le ticket
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
