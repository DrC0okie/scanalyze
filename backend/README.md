# SCANALYZE API 

Official scanalyze API.

## GETTING STARTED

### Prerequisites
- NodeJS  > v18.16.0. Download [here](https://nodejs.org/en/download)

### Installation

- Clone repository
- Go inside root folder
- Install dependencies by running :    ```npm install ```
- Copy the **.env-exemple** file and rename it to **.env**

The .env contain two variables, **ATLAS_URI** is the connection URI to the MongoDB database and **ATLAS_DB_NAME** is the name of the database.

Set **ATLAS_DB_NAME** to **scanalyze** and you will find the **ATLAS_URI** in the credentials file on teams.

It should look like this : 
```
ATLAS_URI="mongodb+srv://exemple.com"
ATLAS_DB_NAME="scanalyze"
```
You can also choose to create a databse locally, to do so refer to the database documentation.


Once installed, and configured you can run the API using : ```npm run start```

you can also run the API with hot reloading using : ```npm run watch```

## API ENDPOINTS

All API endpoints are documented using a Swagger UI. 
You can read it at http://localhost:300/api-docs if you run the API on your machine
or at http://scanalyze-backend.eba-mjtcbsxb.eu-central-1.elasticbeanstalk.com/api-docs/

## AUTHENTICATION

An authentication middleware was implemented but isn't used because the login system wasn't implemented in the Android app.
you can find it in **middlewares/verify-jwt.js**

## TESTS

I used [Jest](https://jestjs.io/fr/) and [supertest](https://github.com/ladjs/supertest) to write them.

Test file are located in the **test** folder.

you can run the test with the ```npm run test``` command.

