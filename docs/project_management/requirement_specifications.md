 

# Scanalyze - Requirement Specification

**Authors**: Anthony David, Jarod Streckeisen, Timothée Van Hove



## 1. Introduction

### 1.1 Purpose

The purpose of this document is to provide comprehensive guidance for the development and deployment of the Scanalyze application. This document aims to outline the user, functional and non-functional requirements that the system needs to fulfill. It serves as both a blueprint for the implementation phase and a standard for system validation.

### 1.2 Project Scope

Scanalyze is designed to revolutionize the way individuals manage and understand their grocery shopping expenses. Using cutting-edge technologies like Optical Character Recognition (OCR), the app will capture information from grocery receipts, eliminating the need for manual entry. The application is not just limited to tracking; it also offers data analytics and visualization features to provide actionable insights into the user's spending patterns.

This document is intended for a range of readers including but not limited to project managers, developers, testers, and stakeholders. It will serve as the go-to reference for understanding the functionalities that need to be developed, the quality standards that need to be met, and the data flows that need to be established.

### 1.4 Document Conventions

In this document, `Shall` is used to indicate a required feature. `Should` indicates a desirable but not mandatory feature. All technical terminologies used are explained in the glossary in the appendices.

### 1.5 Project Goals

- To provide a user-friendly mobile interface for scanning grocery receipts.
- To automate data extraction from receipts for efficient tracking.
- To offer robust data analytics for users to understand their spending habits.
- To ensure secure user authentication and data storage.

By offering these functionalities, Scanalyze aims to become an indispensable tool for smart grocery shopping and personal finance management.



## 2. System Overview

Scanalyze is designed as a multi-tiered application involving client-side,  server-side, and database components. Each of these elements has  specialized tasks and together they form a cohesive ecosystem for  effective grocery expense tracking. Below is a breakdown of the system's primary components and their interrelationships.

### 2.1 Android Mobile Application

The mobile application serves as the user interface and is responsible for capturing grocery receipts through the device's camera. It utilizes Optical Character Recognition (OCR) to extract textual data from these receipts. The app also handles user authentication and data visualization through various graphs and charts.

**Key Features:**

- User registration and login
- Receipt scanning
- Data visualization
- Offline data caching

### 2.2 Web Server

The web server acts as the intermediary between the mobile application and the database. The server processes the OCR data received from the mobile application and feeds the database. It is the server responsibility to link (index) the  abbreviated products on the receipt to the existing product in the database. It uses the database user data to generate statistics, then returns this statistics data to the mobile application. The server is also responsible for handling user authentication tokens.

**Key Features:**

- Data processing and analysis
- Receipt - database indexation
- API endpoints for client-server communication
- Token-based authentication

### 2.3 Database

The database stores detailed information about various grocery products, including their descriptions, categories, and indicative prices. This data is queried by the web server whenever a new grocery receipt is processed. The database also securely stores user information such as hashed passwords and expense histories.

**Key Features:**

- Storage of product descriptions and categories
- User data management
- Scalable data schema

### 2.4 System Interactions

- **User Authentication**: The user authentication is managed by AWS Cognito which is a user directory, an authentication server, and an authorization service for OAuth 2.0 access tokens and AWS credentials. With Amazon Cognito, the application can authenticate and authorize users with JSON Web Tokens to maintain secure sessions.
- **Data Transfer**: After a user scans a receipt, the mobile app sends the extracted text to the web server for processing.
- **Data Retrieval**: The web server queries the database based on the received text, retrieves matching product descriptions, and returns them to the mobile application for display and analysis.
- **Data Visualization**: The mobile application uses the received data to generate graphs and charts for a visual representation of the user’s expenses.



## 3. User Requirements

#### 3.1 User Registration and Account Management

UR1.1: Users shall be able to register with an email and password.

UR1.2: Users shall be able to recover forgotten passwords through email.

#### 3.2 Compatibility

UR2.1: At least, the application shall scan and recognize edible products.

UR2.2: The application shall work with the following grocery stores: Migos, Coop, Aldi, Lidl.

#### 3.3 Receipt Scanning

UR3.1: Users shall be able to capture images of grocery receipts using their phone's camera.

UR3.2: OCR shall be applied to extract readable text from the receipt images.

UR3.3: Receipts data shall  be sent to the backend server via a REST API.

UR3.4: The application shall allow users to upload images to be processed.

#### 3.4 Data Management and Visualization

UR4.1: Users should be able to see a numeric version of their receipts in the mobile application.

UR4.2: Users shall see a summary of their monthly and yearly expenditures.

UR4.3: Users shall be able to view graphical representations of their spending by product category.

#### 3.5 Offline Access

UR5.1: Users shall be able to scan and store receipts locally on their device without requiring an internet connection.

UR5.2: Users shall be able to visualize charts and graphs event without an internet connection.

### 3.6 Use case diagram



![](..\figures\use_case.png)





## 4. Functional Requirements

### 4.1 Mobile Application

#### 4.1.1 Technologies

FR1.1: The app must be developed with Kotlin.

FR1.2: The app must be developed with Android studio.

FR1.3: The app must be compatible with Android 8.0 "Oreo" (API 24) and above.

#### 4.1.2 User Authentication

FR2.1: The app shall allow users to register with a unique, valid email and password.

FR2.2: The app should provide a "Forgot Password" option that will send a reset link to the registered email address.

FR2.3: The app should offer multi-factor authentication for enhanced security.

FR2.4: The app shall use AWS Cognito for the account creation, log-in.

FR2.5: The app shall use a JWT provided by AWS Cognito to interact with the server REST API.

#### 4.1.3 Receipt Scanning

FR3.1: Use ML Kit or Tesseract for android to extract text from the scanned receipts.

FR3.2: Users shall be able to scan grocery receipts using the camera of their mobile device.

FR3.3: OCR functionality shall extract the product names and prices from the scanned image.

FR3.4: When receiving a "correction request" , the app shall prompt the user to select from a range of products, the one that the server didn't recognise on the receipt data.

#### 4.1.4 Data Analysis and Visualization

FR4.1: The application must use MPAndroidChart for mobile app graphs.

FR4.2: The app should display an expense summary in a dashboard format, including metrics like 'Monthly Expenditure,' 'Average Basket Size,' etc.

FR4.3:Users should be able to view their spending trends over different time frames: weekly, monthly, and annually.

FR4.4: The app should provide different types of visualizations, such as pie charts for category-wise spending and line graphs for temporal trends.

FR4.5: The app should provide temporal price visualization for any every product.

#### 4.1.5 Data Caching

FR5.1: The app shall cache scanned receipt data when the app is used offline, or if the server is unreachable.

FR5.2: The app should synchronize with the server when online access is restored.

FR5.3: The app should have the capability to validate cached data against the most recent version in the database.



### 4.2 Web Server

#### 4.2.1 Technologies

FR6.1: The web server shall be deployed on AWS Elastic Beanstalk, using S3 and EC2 instances as infrastructure.

FR6.2: The webserver must be developed using Node.js and Express.

#### 4.2.2 Data Processing

FR7.1: The server shall parse the text data received from the mobile app and correlate it with stored product descriptions in the database.

FR7.2: The server shall send a "correction request" to the mobile app if some products do not match the database

#### 4.2.3 User Authentication

FR8.1: The server shall validate JWT (JSON Web Token) provided by the mobile application to authenticate the user's identity.

#### 4.2.4 RESTful API

FR9.1: The server shall provide RESTful API endpoints to facilitate communication with the mobile app.



### 4.3 Database

#### 4.3.1 Technologies

FR10.1: The database is hosted on an AWS DocumentDB cluster

#### 4.3.4 Data Schema and Indexing

FR11.1: The documents in the database shall have the following minimal structure:

`Tickets`

```json
{
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



### 4.4 Architecture diagram

![](..\figures\architecture.png)



### 4.5 Main scan sequence



![](..\figures\sequence_scan.png)



## 5. Non-Functional Requirements

NFR1: The system shall be designed to support up to 10 users

NFR2: All data transmission between the mobile app and the web server shall be encrypted using HTTPS.

NFR3: The initial release shall support English, with the architecture designed to easily add additional languages in the future.

NFR4: receipts shall be scanned in a maximum of 3 seconds

NFR5: The system shall implement mechanisms to verify the integrity of the data, ensuring that it is free of corruption during its life cycle.

