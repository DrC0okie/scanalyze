const express = require('express');
const router = express.Router();
const connect_db = require("../utils/dbconn");
/* GET statistics. */
router.get('/', async (req, res, next) => {

  const date_regex = /^(0[1-9]|[12]\d|3[01])-(0[1-9]|1[0-2])-\d{4}$/;
  //Return 400 and error message if from and to are missing or malformated
  if(!req.query.from || !req.query.to){
    res.status(400).json({
      error: "Missing <from> or <to> in query"
    })
    return;
  }
  if(!req.query.from.match(date_regex) || !req.query.to.match(date_regex)){
    res.status(400).json({
      error: "query <from> or <to> are malformated, use the following format [day]-[month]-[fullYear]"
    })
    return;
  }
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
  //Return an empty object if no receipt is found
  if(!receipts.length){
    res.status(200).json({});
    return;
  }
  //Generate statistics
  let statistics;
  receipts.forEach(receipt => {
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
        statistics.total_category[product.category] += product.unit_price
      } else {
        statistics.total_category[product.category] = product.unit_price
      }
    }

  });
  //Format statistics (only 2 decimal)
    statistics.total = parseFloat(statistics.total.toFixed(2));
    for (const category in statistics.total_category) {
      statistics.total_category[category] = parseFloat(statistics.total_category[category].toFixed(2));
    }

  res.status(200).json(statistics);
});

module.exports = router;
