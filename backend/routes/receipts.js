const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const products = require("../data/migros.json");
const receipt = require("../data/coop-receipt.json");
const sc = require('string-comparison')
const jw = require('jaro-winkler');
const test = require('./tests');

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

  // results.push(test.execute("Cosine",test.single_cosine,receipt,products,false));
  // results.push(test.execute("Cosine filtered",test.single_cosine,receipt,products,true));

  //results.push(test.execute("Dice",test.single_dice,receipt,products,false));
  //results.push(test.execute("Dice filtered",test.single_dice,receipt,products,true));
  //results.push(test.execute("Dice price weighted",test.single_dice_price_weighted,receipt,products,false));
  results.push(test.execute("Dice + Jaro price weighted",test.combo_dice_jaro_price_weighted,receipt,products,false));
  //results.push(test.execute("Dice + Jaro no price",test.combo_dice_jaro,receipt,products,false));
  //results.push(test.execute("Levenshtein + Jaro price weighted",test.combo_jaro_levenshtei_price,receipt,products,false));
  //results.push(test.execute("Levenshtein + Jaro price weighted",test.combo_jaro_levenshtei_price,receipt,products,false));
  results.push(test.execute("Levenshtein + Jaro + dice price weighted",test.triple_jaro_levenshtei_dice_price,receipt,products,false));

  // results.push(test.execute("Levenshtein",test.single_levenshtein,receipt,products,false));
  // results.push(test.e xecute("Levenshtein filtered",test.single_levenshtein,receipt,products,true));
  // results.push(test.execute("JaroWinkler",test.single_jarowinkler,receipt,products,false));
  // results.push(test.execute("JaroWinkler filtered",test.single_jarowinkler,receipt,products,true));
  // results.push(test.execute("Jaccard",test.single_jaccard,receipt,products,false));
  // results.push(test.execute("Jaccard filtered",test.single_jaccard,receipt,products,true));


  // for(let target of receipt){
  //   const filtered_products = products.filter((el)=>{
  //     return el.actual_price == target.price || el.actual_price  == 0 || el.regular_price == target.price
  //   })
  //   console.log(filtered_products);
  //   let compatible_products =[]
  //   for(let product of filtered_products){
  //      compatible_products.push({
  //         ...product,
  //         dice: test.single_dice(product.name,target.name),
  //         jaccard: test.single_jaccard(product.name,target.name),
  //         cosine: test.single_cosine(product.name,target.name),
  //         levenshtein: test.single_levenshtein(product.name,target.name),
  //         jarowinkler: test.single_jarowinkler(product.name,target.name)
  //       })
  //   }
  //   const dice_sorted_first_element = compatible_products.sort((el1,el2)=>{
  //     if(el1.dice < el2.dice){
  //       return 1
  //     }else{
  //       return -1
  //     }
  //   })[0];
  //   const jaccard_sorted_first_element = compatible_products.sort((el1,el2)=>{
  //     if(el1.jaccard < el2.jaccard){
  //       return 1
  //     }else{
  //       return -1
  //     }
  //   })[0];
  //   const cosine_sorted_first_element = compatible_products.sort((el1,el2)=>{
  //     if(el1.cosine < el2.cosine){
  //       return 1
  //     }else{
  //       return -1
  //     }
  //   })[0];
  //   const levenshtein_sorted_first_element = compatible_products.sort((el1,el2)=>{
  //     if(el1.levenshtein < el2.levenshtein){
  //       return 1
  //     }else{
  //       return -1
  //     }
  //   })[0];
  //   const jarowinkler_sorted_first_element = compatible_products.sort((el1,el2)=>{
  //     if(el1.jarowinkler < el2.jarowinkler){
  //       return 1
  //     }else{
  //       return -1
  //     }
  //   })[0];
  //   results.push({
  //     dice_sorted_first_element,
  //     jaccard_sorted_first_element,
  //     cosine_sorted_first_element,
  //     levenshtein_sorted_first_element,
  //     jarowinkler_sorted_first_element
  //   })
  //}
  
  res.status(200).json(results);
});


module.exports = router;
