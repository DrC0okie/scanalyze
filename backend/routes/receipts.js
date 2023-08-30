const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const products = require("../data/migros.json");
const fake_receipt = require("../data/migros-receipt.json");
const fake_receipt2 = require("../data/migros-receipt-2.json")
const index = require('../utils/indexation');
//const db = require("../utils/dbconn");
/* Post new receipts */
router.post('/',(req, res, next) => {

  if(!req.body.receipt){
    res.status(400).json({
      error: "Empty body"
    })
  }

  const receipt = req.body.receipt;
  // const receipt = {
  //   products: fake_receipt
  // };
  
  let indexed_product = []
  for(let receipt_product of receipt.products){
    // if indexed on cherche dans la db sinon
    indexed_product.push(index.run(receipt_product,products));
  }
  receipt.products = indexed_product;  
  res.status(200).json({
    receipt
  });
});
/* GET a receipt with products*/
router.get('/:id',(req,res,next)=>{
  res.status(200).json({
    route:"get single receipt with id : " + req.params.id
  });
})  
/* POST a new receipt */
router.get('/',(req, res, next) => {
  let results = [];

  results.push(index.test("Levenshtein + Jaro + dice price weighted",test.triple_jaro_levenshtei_dice_price,receipt,products,true));
  //results.push(test.execute("Dice + Jaro price weighted",test.combo_dice_jaro_price_weighted,receipt,products,false));

  res.status(200).json(results);
});


module.exports = router;
