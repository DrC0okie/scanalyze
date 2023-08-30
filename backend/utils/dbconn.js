const { MongoClient } = require('mongodb');

const connectionString = process.env.DOCUMENT_DB_URI || "";

const client = new MongoClient(connectionString);

let conn;
try{
  conn = client.connect();
} catch(e) {
  console.error(e);
}

let db = conn.db(process.env.DOCUMENT_DB_NAME);

module.exports = db