const { MongoClient } = require('mongodb');

const connectionString = process.env.LOCAL_MONGODB_URI || "";

const client = new MongoClient(connectionString);

let conn;
// Connect to the server and export the database object
const  connect_db = async () => {
  try {
    await client.connect();
    console.log('Connected to MongoDB');
    return client.db(process.env.LOCAL_MONGODB_NAME);
  } catch (error) {
    console.error('Error connecting to MongoDB:', error);
    throw error;
  }
}

module.exports = connect_db;