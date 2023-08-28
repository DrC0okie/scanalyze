const express = require('express');
const router = express.Router();

/* GET statistics. */
router.get('/', function(req, res, next) {
  res.status(200).json({page:"Statistiques"});
});

module.exports = router;
