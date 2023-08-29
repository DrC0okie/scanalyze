const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const products = require("../data/migros.json");
const receipt = require("../data/migros-receipt.json");
const receipt2 = require("../data/migros-receipt-2.json")
const sc = require('string-comparison')
const jw = require('jaro-winkler');
const test = require('../utils/indexation');

/* GET all receipts */
router.post('/',(req, res, next) => {
  res.status(200).json({
    route:"Get receipts"
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

  results.push(test.execute("Levenshtein + Jaro + dice price weighted",test.triple_jaro_levenshtei_dice_price,receipt2,products,true));
  //results.push(test.execute("Dice + Jaro price weighted",test.combo_dice_jaro_price_weighted,receipt,products,false));

  res.status(200).json(results);
});


module.exports = router;
