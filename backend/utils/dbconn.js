const { MongoClient } = require('mongodb');

const connectionString = process.env.AWS_DOCUMENTDB_URI || "";

const client = new MongoClient(connectionString,{
  tlsCAFile :'./utils/global-bundle.pem'
});

let conn;
conn = client.connect().then((client)=>{
  conn = client;
  let db = conn.db(process.env.DOCUMENT_DB_NAME);
  console.log(db.getMongo().getDBNames());
  module.exports = db
}).catch(e =>{
  console.error(e);
})


