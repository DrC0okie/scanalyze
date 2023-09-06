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

you can also run the API with hot reload using : ```npm run watch```

## API ENDPOINTS

### Receipts

#### POST /receipts

Add a new receipt. Sent data should be as following :

```json
{
    "receipt": {
        "user_id": "0",
        "date": "",
        "shop_branch": "",
        "shop_name": "migros",
        "total": 0.0,
        "products": [
            {
                "discount_amount": 0.0,
                "product_name": "raisins uittoria ",
                "quantity": 3.5,
                "total_price": 0.0,
                "unit_price": 2.15
            },
            
            {
                "discount_amount": 0.0,
                "product_name": "tonates 80r ceaux 400g ",
                "quantity": 2.0,
                "total_price": 3.0,
                "unit_price": 1.5
            }
        ]
    }
}

```


### Statistics


