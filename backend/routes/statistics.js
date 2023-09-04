const express = require('express');
const router = express.Router();
const connect_db = require("../utils/dbconn");
/* GET statistics. */
router.get('/', async (req, res, next) => {
  
  //Get from and to from query
  const from = req.query.from.split('-');
  const to = req.query.to.split('-');
  //Generate a Date object
  const from_date = new Date(from[2], from[1] - 1, parseInt(from[0]), 1, 0, 0, 0);
  const to_date = new Date(to[2], to[1] - 1, parseInt(to[0]), 1, 0, 0, 0);

  //Connect and get receipts in database
  const db = await connect_db();
  const receipts_collection = db.collection("receipts");

  const receipts = await receipts_collection.find({
    $and: [
      { date: { $gte: from_date } },
      { date: { $lte: to_date } }
    ]
  }).toArray();

  //Generate statistics
  let statistics
  receipts.forEach(receipt => {
    //const year = receipt.date.getFullYear();
    if (!statistics) {
      statistics = {};
      statistics.total = 0;
      statistics.receipts = []
      statistics.total_category = {}
    }

    statistics.total += receipt.total;
    statistics.receipts.push({ date: receipt.date, total: receipt.total })

    for (const product of receipt.products) {
      if (statistics.total_category[product.category]) {
        statistics.total_category[product.category] += product.price
      } else {
        statistics.total_category[product.category] = product.price
      }
    }

  });
  //Format statistics 
  for (const year in statistics) {
    statistics.total = parseFloat(statistics.total.toFixed(2));
    for (const category in statistics.total_category) {
      statistics.total_category[category] = parseFloat(statistics.total_category[category].toFixed(2));
    }

  }
  res.status(200).json(statistics);
});

module.exports = router;
