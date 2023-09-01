const express = require('express');
const router = express.Router();
const connect_db = require("../utils/dbconn");
/* GET statistics. */
router.get('/year', async (req, res, next) => {
  console.log(req.query);
  const from = req.query.from.split('-');
  const to = req.query.to.split('-');
  
  const from_date = new Date(from[2],from[1]-1,parseInt(from[0])+1,0,0,0);
  const to_date = new Date(to[2],to[1]-1,parseInt(to[0])+1,0,0,0);

  const db = await connect_db();
  const receipts_collection = db.collection("receipts");

  const receipts = await receipts_collection.find({
    $and: [
      { date: {$gte : from_date} },
      { date: {$lte : to_date}}
    ]
  }).toArray();
  let groupedByYear = {};
  receipts.forEach(receipt => {
    const year = receipt.date.getFullYear();

    // Check if the year key already exists in groupedByYear
    if (groupedByYear[year]) {
        // If it exists, push the object to the existing array
        groupedByYear[year].push(receipt);
    } else {
        // If it doesn't exist, create a new array for that year key and add the object
        groupedByYear[year] = [receipt];
    }
  });
  
  res.status(200).json(groupedByYear);
});
router.get('/month', function(req, res, next) {
  res.status(200).json({page:"Statistiques"});
});
router.get('/week', function(req, res, next) {
  res.status(200).json({page:"Statistiques"});
});

module.exports = router;
