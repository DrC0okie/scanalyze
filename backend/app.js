require('dotenv').config()
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const receiptsRouter = require('./routes/receipts');
const statisticsRouter = require('./routes/statistics');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
    
app.use('/receipts', receiptsRouter);
app.use('/statistics', statisticsRouter);

module.exports = app;
