 

# Scanalyze project - User Requirement Specification (URS)



## Table of Contents



## 1. Introduction

### 1.1 Purpose

This document describes the user requirements for Scanalyze, a mobile application intended to help users manage and visualize their grocery expenses by scanning and interpreting grocery receipts.

### 1.2 Scope

This document covers the requirements for the mobile application, associated web interface, a backend server and a no-SQL database



## 2. Overall Description

### 2.1 System Interfaces

- **Mobile Application**: android platform used to scan receipts and display simple statistics on consumer purchasing habits
- **Web Interface**: accessible with any recent browser, the web interface contains at least one landing page
- **Backend Server**: API for communication between android application, web interface and database
- **Database**: Storage of user data, scanned receipts and user statistics



## 3. User Requirements

#### 3.1 User Registration and Account Management

- **UR1.1**: Users must be able to register with an email and password.
- **UR1.2**: Users must be able to recover forgotten passwords through email.

#### 3.2 Compatibility

- **UR2.1**: At least, the application must scan and recognize edible products
- **UR2.2**: The application must work with the following grocery stores: Migos, Coop, Aldi, Lidl

#### 3.3 Receipt Scanning

- **UR3.1**: Users should be able to capture images of grocery receipts using their phone's camera.
- **UR3.2**: OCR should be applied to extract readable text from the receipt images.
- **UR3.3**: Receipts data must be sent to the backend server via a REST API
- **UR3.4**: The application must allow users to upload images to be processed

#### 3.4 Data Management and Visualization

- **UR4.1:** Users should be able to see a numeric version of their receipts in the mobile application
- **UR4.2**: Users should see a summary of their monthly and yearly expenditures.
- **UR4.3**: Users should be able to view graphical representations of their spending by product category.

#### 3.5 Offline Access

- **UR5.1**: Users must be able to scan and store receipts locally on their device without requiring an internet connection.
- **UR5.2**: Once the device is connected to the internet, the locally stored data should be synchronized with the server.



## 4. Data Requirements

- **DR1**: User account information must be stored securely by a trusted third party entity.
- **DR2**: Receipt data should be linked to the user account for retrieval and analysis.



## 5. Non-Functional Requirements

- **NFR1**: The system must be compatible with Android 8.0 "Oreo" and above.
- **NFR2**: Data should be encrypted during transmission between client and server.
- **NFR3**: System should be designed for possible future extension to other platforms (iOS, etc.)
- **NFR4**: receipts must be scanned in a maximum of 3 seconds
