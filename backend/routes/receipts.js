const express = require('express');
const dice = require('fast-dice-coefficient');
const router = express.Router();
const products = require("../data/coop.json");
const receipt = require("../data/coop-receipt.json");
/* GET home page. */
router.get('/',(req, res, next) => {
  const target = receipt[5];
  const filtered_products = products.filter((el)=>{
    return el.price == target.price
  })
  console.log(filtered_products);
  let compatible_products =[]
  for(let product of filtered_products){
      const dice_value = dice(product.name,target.name);
      // if(dice_value > 0.5){
          compatible_products.push({...product,dice: dice_value})
      // }
  }
  const sorted = compatible_products.sort((el1,el2)=>{
    if(el1.dice < el2.dice){
      return 1
    }else{
      return -1
    }
  });
  
  res.status(200).json(sorted);
  console.log(sorted[0])
});


module.exports = router;
