require('dotenv').config()
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const receiptsRouter = require('./routes/receipts');
const statisticsRouter = require('./routes/statistics');
const verify_jwt = require('./middlewares/verify-jwt')
const swagger = require("swagger-ui-express");
const yaml = require('yaml');
const fs = require("fs");
const app = express();
const cors = require('cors');
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(cors());

//app.use(verify_jwt);

const file  = fs.readFileSync('./swagger.yaml', 'utf8')
const swagger_doc = yaml.parse(file)

app.use('/api-docs', swagger.serve, swagger.setup(swagger_doc));
app.use('/receipts', receiptsRouter);
app.use('/statistics', statisticsRouter);

module.exports = app;