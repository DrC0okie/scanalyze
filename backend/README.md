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

Add a new receipt.Request body data should be as follows:

```json
{
    "receipt": {
        "user_id": "0",
        "date": "2023-11-01T14:56:49.724Z",
        "shop_branch": "Vaud",
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
Response body : 

```json
{
	"receipt": {
		"user_id": "0",
		"date": "2023-02-15T14:56:49.724Z",
		"shop_branch": "Vaud",
		"shop_name": "migros",
		"total": 6.5,
		"products": [
			{
				"discount_amount": 0,
				"product_name": "Fresca  Raisins Vittoria",
				"quantity": 1,
				"total_price": 3.5,
				"unit_price": 3.5,
				"category": "fruits-vegetables"
			},
			{
				"discount_amount": 0,
				"product_name": "Longobardi  Tomates pelées en  morceaux au jus",
				"quantity": 2,
				"total_price": 3,
				"unit_price": 1.5,
				"category": "starches"
			}
		],
		"_id": "64f86b5b5aaee0bdd9a31b7d"
	}
}
```


### Statistics


