const express = require('express');
const router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  console.log("tamerfdp");
  res.status(200).json({page:"Hello World"});
});

module.exports = router;
