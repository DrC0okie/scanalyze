 # Scanalyse - database

## Introduction
Documentation about scanalyze's projet database.

## Table of Contents
- [Technologies](#technologies)
- [Specifications](#specifications)
- [Architecture](#architecture)
- [Datebase filling](#database_filling)


## Technologies
### AWS DocumentDB
For this project, we decide to use a Amazon DocumentDB solutions hosted on AWS.

In analyzing the requirement for our project we quickky established that the use of a NoSQL document databse was the most appropriate.
As the entire backend was on AWS, we decided to do the same for the database.
Note tha the solution proposed by AWS il fully compatible with MangoDB.

### Instance-Based Cluster vs Elastic Cluster
Amazon DocumentDB, Amazon Web Services' managed document database service, offers two types of clusters: instance-based clusters and serverless clusters. The main differences between these two cluster types lie in their management, scalability, and costs.

The main difference lies in capacity management and ease of use. Instance-based clusters require manual capacity management, while elastic clusters automatically adjust capacity based on workload. Elastic clusters can be more cost-effective if your resource needs are variable, but they may be slightly more expensive if your workload is constant and predictable. The choice between the two depends on your application's requirements and your preference for management.


FOr this project, we decided to use Elastic Clusters to limit the need for intervention on our database and to provide scalability better suited to our application.

### AWS CLoud9
MongoDB Comppass is a user-friendly graphical interface for MongoDB databases.
As AWS DocumentDB is comptabile with MOngoDB, we can use to manage the database filling.

## Specifications



## Architecture
`Tickets`

```json

    "_id" : ObjectId,
    "user_id" : ObjectId,
    "date" : Date,
    "shop_name" : String,
    "shop_branch" : String,
    "Products" : [
        {
            "product_id" : ObjectId,
            "product_name" : String,
            "quantity" : Double,
            "unit_price" : Double,
            "discount_amount" : Double
        },
        {
           ... 
        },
        ...
    ],
    "Total" : Double
    
}
```

`Products`

````json
{
    "[shop_name]": [
        {
            "_id" : ObjectId,
            "product_name" : String,
            "abbreviated_name" : String,
            "indicative_price" : Double,
            "category" : String,
        }

    ]
}
````

## Databse information
### AWS DocuementDB

### AWS Cloud9
Environment name : `databaseTerminalEnv`


## Database filling


## Security
### IAM user :
User name : scanalyze-database

Password : 1cp6AA3|

Console sign-in URL : https://496526263747.signin.aws.amazon.com/console

### db-scalayze admin
User name : user1234

Password : arg@UTG-kym7xjw0fqg
