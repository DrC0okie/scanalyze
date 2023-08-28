const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const products = require("../data/migros.json");
const receipt = require("../data/coop-receipt.json");
let sc = require('string-comparison')

/* GET all receipts */
router.get('/',(req, res, next) => {
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
router.post('/',(req, res, next) => {
  let results = [];

  for(let target of receipt){
    const filtered_products = products.filter((el)=>{
      return el.price == target.actual_price
    })
    console.log(filtered_products);
    let compatible_products =[]
    for(let product of filtered_products){
        const dice_value = sc.diceCoefficient.similarity(product.desc,target.name);
        const jaccard_value = sc.jaccardIndex.similarity(product.desc,target.name);
        const cosine_value = sc.cosine.similarity(product.desc,target.name);
        const levenshtein_value = sc.levenshtein.similarity(product.desc,target.name);
        compatible_products.push({
          ...product,
          dice: dice_value,
          jaccard: jaccard_value,
          cosine: cosine_value,
          levenshtein: levenshtein_value
        })
    }
    const dice_sorted_first_element = compatible_products.sort((el1,el2)=>{
      if(el1.dice < el2.dice){
        return 1
      }else{
        return -1
      }
    })[0];
    const jaccard_sorted_first_element = compatible_products.sort((el1,el2)=>{
      if(el1.jaccard < el2.jaccard){
        return 1
      }else{
        return -1
      }
    })[0];
    const cosine_sorted_first_element = compatible_products.sort((el1,el2)=>{
      if(el1.cosine < el2.cosine){
        return 1
      }else{
        return -1
      }
    })[0];
    const levenshtein_sorted_first_element = compatible_products.sort((el1,el2)=>{
      if(el1.levenshtein < el2.levenshtein){
        return 1
      }else{
        return -1
      }
    })[0];
    results.push({
      dice_sorted_first_element,
      jaccard_sorted_first_element,
      cosine_sorted_first_element,
      levenshtein_sorted_first_element
    })
  }
  console.log(results);
  res.status(200).json(results);
});


module.exports = router;
