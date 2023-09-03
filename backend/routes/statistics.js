const express = require('express');
const router = express.Router();
const connect_db = require("../utils/dbconn");
/* GET statistics. */
router.get('/year', async (req, res, next) => {
  console.log(req.query);
  const from = req.query.from.split('-');
  const to = req.query.to.split('-');
  console.log(from);
  console.log(to);
  const from_date = new Date(from[2],from[1]-1,parseInt(from[0]),1,0,0,0);
  const to_date = new Date(to[2],to[1]-1,parseInt(to[0]),1,0,0,0);

  const db = await connect_db();
  const receipts_collection = db.collection("receipts");

  const receipts = await receipts_collection.find({
    $and: [
      { date: {$gte : from_date} },
      { date: {$lte : to_date}}
    ]
  }).toArray();
  let groupedByYear = {};
  let statistics = {};
  receipts.forEach(receipt => {
    const year = receipt.date.getFullYear();

    // Check if the year key already exists in groupedByYear
    if (groupedByYear[year]) {
        // If it exists, push the object to the existing array
        groupedByYear[year].push(receipt);
    } else {
        // If it doesn't exist, create a new array for that year key and add the object
        groupedByYear[year] = [receipt];
        statistics[year] = {};
    }
  });
    for(const year in groupedByYear){
      statistics[year].total = 0
      statistics[year].total_category = {}
      statistics[year].receipt = []
      for(const receipt of groupedByYear[year]){
        statistics[year].total += receipt.total;
        statistics[year].receipt.push({date:receipt.date,total:receipt.total})
        for(const product of receipt.products){
          if(statistics[year].total_category[product.category]){
            statistics[year].total_category[product.category] += product.price
          }else{
            statistics[year].total_category[product.category] = product.price
          }
        }
      }
      
      // Mise en forme 
      statistics[year].total = parseFloat(statistics[year].total.toFixed(2));
      for (const category in statistics[year].total_category){
        statistics[year].total_category[category] = parseFloat(statistics[year].total_category[category].toFixed(2));
      }

    }


  res.status(200).json(statistics);
});
router.get('/month', function(req, res, next) {
  res.status(200).json({page:"Statistiques"});
});
router.get('/week', function(req, res, next) {
  res.status(200).json({page:"Statistiques"});
});

module.exports = router;
