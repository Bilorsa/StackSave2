# StackSave

### Compare. Save. Shop Smarter.

**StackSave** is an Android-based grocery price comparison and shopping application designed to help users discover grocery deals, compare prices across retailers, manage their shopping baskets, and make more informed purchasing decisions.

The application aims to simplify grocery shopping by bringing product information and available offers into one convenient platform. Instead of manually checking multiple retailers, users can search for products, explore categories, compare available prices, and organise their shopping through a centralised basket.

StackSave combines Android development, live API integration, Firebase authentication, and a Node.js backend to provide a connected shopping experience.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Project Objectives](#2-project-objectives)
3. [Key Features](#3-key-features)
4. [Application Workflow](#4-application-workflow)
5. [Technology Stack](#5-technology-stack)
6. [System Architecture](#6-system-architecture)
7. [Project Structure](#7-project-structure)
8. [Installation and Setup](#8-installation-and-setup)
9. [Firebase Configuration](#9-firebase-configuration)
10. [Backend Configuration](#10-backend-configuration)
11. [Running the Application](#11-running-the-application)
12. [Testing and Quality Assurance](#12-testing-and-quality-assurance)
13. [Security Considerations](#13-security-considerations)
14. [Known Limitations](#14-known-limitations)
15. [Future Enhancements](#15-future-enhancements)
16. [Development Challenges and Solutions](#16-development-challenges-and-solutions)
17. [Use of Artificial Intelligence](#17-use-of-artificial-intelligence)
18. [Contributing](#18-contributing)
19. [Project Status](#19-project-status)
20. [Author and Acknowledgements](#20-author-and-acknowledgements)

---

# 1. Project Overview

StackSave is a grocery shopping assistant developed to address a common challenge experienced by consumers: finding affordable grocery products without spending excessive time comparing prices across different retailers.

The application provides a centralised environment where users can:

* Search for grocery products.
* Discover available product offers.
* Browse product categories.
* Compare prices from different retailers.
* Identify potential savings.
* Add products to a shopping basket.
* Adjust product quantities.
* Review shopping totals.
* Proceed through a checkout workflow.
* Access their account through Firebase authentication.

The application uses an external grocery data provider through a dedicated backend service. This allows product searches to retrieve available retailer information without exposing sensitive API credentials directly inside the Android application.

StackSave is designed with a modern Android interface, prioritising usability, accessibility, straightforward navigation, and a convenient shopping experience.

# 2. Project Objectives

The primary objective of StackSave is to develop a practical mobile solution that helps consumers make better grocery purchasing decisions.

The project has the following objectives:

1. **Price transparency:** Allow users to compare available grocery prices across retailers.
2. **Convenience:** Reduce the effort required to search for grocery deals.
3. **Budget awareness:** Help users monitor their shopping expenses through basket totals.
4. **Product discovery:** Provide categories that make it easier to find relevant products.
5. **User accessibility:** Offer a straightforward interface suitable for everyday use.
6. **Secure authentication:** Allow users to register and access their accounts.
7. **Scalability:** Establish an architecture that can support additional shopping features in future versions.

# 3. Key Features

## 3.1 User Authentication

StackSave uses Firebase Authentication to support user account management.

Users can access the application through the available authentication interface.

Key functionality includes:

* User registration.
* Existing-user login.
* Authentication validation.
* Account-based access.
* Firebase integration.

Authentication provides a foundation for connecting individual users to future personalised features.

## 3.2 Grocery Product Search

The search functionality allows users to look for grocery products using product names or relevant search terms.

Examples include:

* Milk
* Bread
* Rice
* Cooking oil
* Eggs
* Cereals
* Household products

The application communicates with the backend, which forwards supported searches to the external grocery API.

Search results can contain product names, retailer information, pricing, and other available product details.

## 3.3 Product Categories

StackSave provides category-based product discovery.

Categories help users browse products according to their shopping needs rather than relying exclusively on manual searches.

Examples include:

* Dairy and Eggs
* Bakery
* Fruits and Vegetables
* Meat and Poultry
* Beverages
* Pantry Essentials
* Household Products
* Snacks and Confectionery

Category searches use relevant search terms to retrieve matching products from the available API data.

The category interface is designed to remain accessible while users scroll through the product catalogue.

## 3.4 Retailer Price Comparison

Price comparison is one of StackSave's central features.

When multiple retailer offers are available for a product, users can examine the available prices and identify potentially cheaper purchasing options.

The comparison functionality aims to provide:

* Product identification.
* Retailer identification.
* Available selling prices.
* Original prices where supplied.
* Discount information where available.
* Potential savings.
* Cheapest available offer identification.

The accuracy and completeness of comparisons depend on the information returned by the external API.

## 3.5 Product Information and Images

Product cards display available information to help users identify the products they want to purchase.

Depending on the provider's response, this information may include:

* Product name.
* Product image.
* Retailer.
* Current price.
* Original price.
* Discount percentage.
* Product description.
* Availability information.

Product images are dependent on the availability of image URLs from supported data sources.

## 3.6 Shopping Basket

The shopping basket allows users to organise selected products before proceeding to checkout.

Its functionality includes:

* Adding products.
* Increasing quantities.
* Decreasing quantities.
* Removing products.
* Displaying individual product prices.
* Calculating line totals.
* Calculating the basket subtotal.
* Displaying the number of selected items.
* Reviewing potential savings where applicable.

The basket provides a consolidated view of the user's selected groceries.

## 3.7 Checkout

StackSave includes a checkout workflow that allows users to review their basket and enter the information required to complete a checkout demonstration.

The checkout interface supports relevant information such as:

* Customer name.
* Email address.
* Contact number.
* Collection or delivery preference.
* Delivery address where applicable.
* Payment method selection.

The demonstration checkout generates an order reference and provides an order confirmation experience.

**Important:** The current checkout workflow should not be interpreted as a fully integrated retail transaction. Actual payment processing, retailer order submission, stock reservation, and confirmed delivery charges require additional integrations.

## 3.8 Notifications

StackSave includes local notification functionality for supported deal-search events.

The notification functionality is designed to inform users when relevant live search results are successfully retrieved, provided that notifications are enabled and Android notification permission has been granted.

The application also includes a notification centre interface.

Remote push notifications and automated price-drop monitoring require additional Firebase Cloud Messaging and backend integration.

## 3.9 User Profile and Settings

The profile area provides access to available account and application preferences.

These include:

* User account information.
* Notification preferences.
* Application settings.
* Available account-related functionality.

The profile provides a foundation for introducing more personalised shopping features.

---

# 4. Application Workflow

The general StackSave workflow is as follows:

### Step 1: Launch the Application

The user opens StackSave on an Android device.

### Step 2: Authenticate

The user registers a new account or signs in using the available Firebase Authentication functionality.

### Step 3: Access the Home Screen

After authentication, the user accesses the main application interface.

The home screen provides access to product searches, categories, available offers, and shopping functionality.

### Step 4: Discover Products

The user can browse available categories or enter a product name into the search field.

### Step 5: Retrieve Product Results

The Android application sends the search request to the StackSave backend.

The backend communicates with the external grocery API and returns the available results.

### Step 6: Compare Offers

The user reviews the available retailer offers and compares prices.

### Step 7: Add Products to the Basket

Selected products can be added to the shopping basket.

The user can modify quantities or remove products as required.

### Step 8: Review the Basket

The application calculates the relevant totals and displays a summary of the selected products.

### Step 9: Proceed to Checkout

The user enters the requested checkout information and reviews the order.

### Step 10: Receive Confirmation

The demonstration checkout generates an order reference and displays a confirmation.

---

# 5. Technology Stack

StackSave uses several technologies to support its Android application, backend services, authentication, and external data integration.

| Technology              | Purpose                                            |
| ----------------------- | -------------------------------------------------- |
| Kotlin                  | Primary Android programming language               |
| Android Studio          | Android development environment                    |
| Jetpack Compose         | Modern declarative user interface development      |
| Material Design 3       | Interface components and visual styling            |
| Firebase Authentication | User registration and authentication               |
| Firebase Admin SDK      | Backend authentication verification                |
| Node.js                 | Backend runtime environment                        |
| Express.js              | Backend API and routing                            |
| AZ Labs API             | External grocery product and retailer data         |
| Gradle                  | Android build automation and dependency management |
| JSON                    | Communication and data exchange                    |
| Git / GitHub            | Source control and project collaboration           |

### Android Development

The Android application is developed using Kotlin and Jetpack Compose.

Jetpack Compose provides a declarative approach to building the interface, making it easier to create reusable components and manage application state.

### Backend Development

The backend uses Node.js and Express.js to process requests from the Android application.

It acts as an intermediary between the mobile application and the external grocery API.

### Authentication

Firebase Authentication manages user authentication, while Firebase Admin SDK allows the backend to verify Firebase ID tokens.

### External Data Integration

The AZ Labs API provides access to supported grocery product and retailer information.

The availability of product information depends on the external provider.

---

# 6. System Architecture

StackSave follows a client-server architecture.

The Android application communicates with a Node.js backend, which handles external API requests and authentication verification.

### Architecture Overview

```text
             STACKSAVE ANDROID APPLICATION
                         |
                         |
                 Firebase Authentication
                         |
                         |
                  Firebase ID Token
                         |
                         |
                  STACKSAVE BACKEND
                   Node.js / Express
                         |
              Authentication Verification
                         |
                         |
                  AZ Labs API
                         |
                         |
                Grocery Product Data
                         |
                         |
                Backend Response
                         |
                         |
               Android Application
                         |
              -----------------------
              |          |          |
           Products    Basket    Checkout
              |          |          |
              -----------------------
                         |
                   User Interface
```

### Architecture Components

**1. Android Application**

The Android application provides the user interface and manages user interactions, product searches, basket operations, and checkout functionality.

**2. Firebase Authentication**

Firebase manages user authentication and provides ID tokens that can be used to verify authenticated requests.

**3. Node.js Backend**

The backend receives requests from the Android application, verifies authentication where required, and communicates with the external grocery API.

**4. AZ Labs API**

The external API provides grocery product and retailer information where available.

**5. Application Data Management**

The Android application manages relevant interface state and locally maintained shopping information.

---

# 7. Project Structure

The project is organised into an Android application and a separate backend directory.

A simplified representation is provided below. Exact package names and files may differ between project versions.

```text
StackSave2/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── example/
│   │       │           └── stacksave/
│   │       │               │
│   │       │               ├── data/
│   │       │               ├── model/
│   │       │               ├── ui/
│   │       │               ├── viewmodel/
│   │       │               └── MainActivity.kt
│   │       │
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── backend/
│   ├── server.js
│   ├── package.json
│   ├── .env
│   ├── firebase-service-account.json
│   └── node_modules/
│
├── gradle/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
├── .gitignore
└── README.md
```

**Security note:** The `.env`, Firebase service-account JSON, and `node_modules` entries represent local development resources. Private credentials and generated dependencies should not be committed to a public repository.

---

# 8. Installation and Setup

## 8.1 Requirements

Before setting up StackSave, ensure that the following software is installed:

* Android Studio.
* Android SDK.
* JDK version compatible with the project's Gradle configuration.
* Kotlin support through Android Studio.
* Node.js and npm.
* A Firebase project.
* An AZ Labs API account and API key.
* An Android emulator or physical Android device.

## 8.2 Download the Project

Clone the repository or download the project ZIP.

For Git:

```bash
git clone <YOUR_REPOSITORY_URL>
```

Open the downloaded project in Android Studio.

Allow Android Studio to synchronise the Gradle dependencies.

## 8.3 Configure Android SDK

Open the project in Android Studio.

Check that the Android SDK is installed and that the project uses the required SDK configuration.

The project should be opened using its root directory, where the Gradle settings file is located.

## 8.4 Configure the Backend

Open a terminal in the backend directory:

```bash
cd backend
```

Install the required Node.js dependencies:

```bash
npm install
```

Create a local `.env` file using the supplied `.env.example` template.

Configure the required environment variables:

```env
PORT=8080

AZ_LABS_API_KEY=YOUR_AZ_LABS_API_KEY

FIREBASE_SERVICE_ACCOUNT_JSON=./firebase-service-account.json
```

Replace the placeholder values with valid credentials.

Do not publish the completed `.env` file.

## 8.5 Configure Android Backend URL

The Android application must know where the backend is running.

For an Android emulator, the backend URL can be configured in the root `local.properties` file:

```properties
STACKSAVE_BACKEND_BASE_URL=http://10.0.2.2:8080/
```

The address `10.0.2.2` allows the Android emulator to access the host computer's local development server.

For a physical Android device, use the computer's local network IPv4 address:

```properties
STACKSAVE_BACKEND_BASE_URL=http://YOUR_PC_IPV4_ADDRESS:8080/
```

Ensure that the computer and Android device are connected to the same network.

The backend must listen on an address accessible to the device, and the Windows Firewall must permit the relevant development connection.

---

# 9. Firebase Configuration

Firebase is used to provide authentication services.

### Setup Instructions

1. Open the Firebase Console.
2. Create or select the StackSave Firebase project.
3. Register an Android application.
4. Use the package name:

```text
com.example.stacksave
```

5. Download the `google-services.json` configuration file.
6. Place it inside the Android application's `app/` directory.
7. Enable the required authentication provider in Firebase Authentication.
8. Configure the backend Firebase Admin SDK using a service-account credential from the same Firebase project.

### Authentication Flow

The authentication process follows this general sequence:

1. The user submits their login details.
2. Firebase Authentication validates the credentials.
3. Firebase provides an ID token for the authenticated session.
4. The Android application sends the token to protected backend endpoints.
5. The backend verifies the token using Firebase Admin SDK.
6. The backend processes the authenticated request if verification succeeds.

**Important:** The Firebase Android configuration file and Firebase Admin service-account credentials serve different purposes. The service-account private key must never be included in a publicly distributed Android application or GitHub repository.

---

# 10. Backend Configuration

The StackSave backend is responsible for connecting the Android application to the external grocery data provider.

### Backend Responsibilities

* Receive application requests.
* Validate authentication tokens where required.
* Process product search requests.
* Communicate with the AZ Labs API.
* Protect the external API key.
* Return available product information to the Android application.
* Provide a backend health-check endpoint.

### Starting the Backend

Open a terminal inside the backend folder.

Run:

```bash
npm install
```

Then start the server:

```bash
npm start
```

The terminal should display a message indicating that the backend is listening on the configured port.

For example:

```text
StackSave API proxy listening on port 8080
```

### Checking Backend Availability

Open the following address in a browser on the computer running the backend:

```text
http://localhost:8080/health
```

The health endpoint should return information about the backend service and its configuration.

An example response is:

```json
{
  "ok": true,
  "service": "stacksave-azlabs-proxy",
  "firebaseAdmin": true,
  "azLabsKeyConfigured": true
}
```

The exact response depends on the backend implementation.

A successful health check confirms that the service is running and reports whether the relevant integrations are configured. It does not, by itself, prove that live product searches are working.

---

# 11. Running the Application

After completing the Android and backend configuration, follow these steps.

### Step 1: Start the Backend

Navigate to the backend folder and execute:

```bash
npm start
```

Keep the backend terminal running.

### Step 2: Open Android Studio

Open the StackSave project.

Wait for Gradle synchronisation to complete.

### Step 3: Select a Device

Choose an Android emulator or connect a physical Android device.

Ensure that USB debugging is enabled when using a physical device.

### Step 4: Build the Application

Select:

**Build → Make Project**

Resolve any reported compilation errors before continuing.

### Step 5: Run StackSave

Select the Run button in Android Studio.

Android Studio will compile, install, and launch the application on the selected device.

### Step 6: Test the Application

Register or sign in, access the home screen, search for products, explore categories, add products to the basket, and test the checkout workflow.

---

# 12. Testing and Quality Assurance

Testing is important to ensure that StackSave behaves correctly and provides a reliable user experience.

The following areas should be tested.

| Test Area           | Test Description                    | Expected Result                                            |
| ------------------- | ----------------------------------- | ---------------------------------------------------------- |
| Application launch  | Open StackSave                      | Application launches without crashing                      |
| Registration        | Register a new account              | Valid account is created                                   |
| Login               | Sign in using valid credentials     | User accesses the application                              |
| Invalid login       | Submit incorrect credentials        | Appropriate error is displayed                             |
| Product search      | Search for a grocery product        | Available matching results are returned                    |
| Category browsing   | Select a product category           | Relevant products are displayed                            |
| Price comparison    | Review multiple retailer offers     | Available prices can be compared                           |
| Basket addition     | Add a product                       | Product appears in basket                                  |
| Quantity management | Increase or decrease quantity       | Basket totals update                                       |
| Product removal     | Remove a basket item                | Item is removed and totals update                          |
| Checkout            | Complete the demonstration checkout | Order confirmation is displayed                            |
| Notifications       | Perform a supported live search     | Local notification is generated when enabled and permitted |
| Backend connection  | Access the health endpoint          | Backend returns its status                                 |

### Testing Environments

StackSave can be tested using:

* Android Studio Emulator.
* Physical Android devices.
* Backend terminal output.
* Browser-based health checks.
* API testing tools such as Postman.
* Android Studio build and Logcat tools.

### Error Troubleshooting

**Backend connection failure**

Check that:

* The Node.js server is running.
* The configured backend address is correct.
* Port 8080 is available.
* The emulator or physical device can access the host computer.
* Windows Firewall is not blocking the connection.

**Firebase authentication failure**

Check that:

* The correct Firebase project is configured.
* The Android package name matches the registered application.
* The `google-services.json` file is correctly placed.
* The required authentication provider is enabled.
* The backend service-account credentials belong to the same Firebase project.

**Live product search failure**

Check that:

* The backend is running.
* The AZ Labs API key is valid.
* The backend environment variables are configured.
* The external API is accessible.
* The API request format and authentication requirements are correct.

**Gradle build failure**

Check that:

* Gradle dependencies are synchronised.
* The configured JDK is compatible.
* The Android SDK requirements are satisfied.
* Required Compose and Kotlin dependencies are available.
* The error details in the Build window are reviewed.

---

# 13. Security Considerations

Security is an important consideration when developing an application that uses user authentication and external API services.

StackSave follows several security principles.

### API Key Protection

The AZ Labs API key should be stored in the backend environment configuration rather than hardcoded into Android source files.

### Firebase Authentication

Firebase Authentication provides account authentication, while the backend verifies Firebase ID tokens before processing protected requests.

### Service Account Protection

Firebase service-account credentials must remain private.

They should never be uploaded to public repositories or included in publicly distributed application packages.

### Environment Variables

Sensitive configuration values should be stored in environment variables.

The `.env` file should be excluded from version control.

### HTTPS

Production backend communication must use HTTPS to protect information transmitted between the Android application and the backend.

Local HTTP connections should only be used for controlled development and testing.

### Input Validation

User input should be validated before processing.

This includes authentication details, search queries, basket information, and checkout fields.

### Production Readiness

Before publishing StackSave, review:

* API credential security.
* Firebase security configuration.
* Backend authentication.
* HTTPS configuration.
* Application backup settings.
* Android release configuration.
* Error handling.
* Privacy and data-handling requirements.

Previously exposed API keys or private service-account credentials should be rotated before production deployment.

---

# 14. Known Limitations

Although StackSave provides a foundation for grocery price discovery and shopping management, certain capabilities depend on additional integrations.

The following limitations should be considered.

### External Product Data

Product availability, retailer coverage, pricing accuracy, and product descriptions depend on the external API.

The application cannot guarantee that every retailer or product will be available.

### Product Images

Product images depend on the image URLs returned by supported data providers or other configured image sources.

Some products may not have available images.

### Checkout

The current checkout workflow is a demonstration of the order review and confirmation process.

It does not independently process real payments or submit confirmed orders to retailers.

### Delivery Information

Actual delivery fees, stock availability, and delivery estimates require reliable retailer integration.

### Notifications

Local notifications are supported for relevant search events when enabled and permitted.

Remote push notifications and automated price-drop alerts require further integration.

### Retailer Integration

StackSave does not automatically guarantee that a product can be purchased directly from every retailer displayed in the comparison results.

Retailer purchasing links and transaction integration depend on the capabilities and permissions of the relevant providers.

---

# 15. Future Enhancements

StackSave has the potential to develop into a more comprehensive grocery shopping platform.

The following improvements are proposed for future versions.

## 15.1 Automated Price-Drop Alerts

Introduce background monitoring that informs users when tracked products become cheaper.

## 15.2 Barcode Scanning

Allow users to scan product barcodes and retrieve matching product information.

## 15.3 Advanced Budget Management

Introduce monthly grocery budgets, spending summaries, and budget notifications.

## 15.4 Shopping History

Store completed shopping activity and allow users to review previous purchases.

## 15.5 Personalised Recommendations

Use shopping history and user preferences to suggest relevant grocery products and deals.

## 15.6 Firebase Cloud Messaging

Introduce remote push notifications for price drops, new offers, and relevant application updates.

## 15.7 Retailer Purchasing Integration

Explore supported retailer integrations to allow users to proceed from price comparison to actual purchasing.

## 15.8 Improved Product Comparison

Introduce more advanced comparison tools, including:

* Unit-price comparisons.
* Price-per-kilogram calculations.
* Price-per-litre calculations.
* Retailer filtering.
* Sorting by lowest price.
* Discount filtering.
* Product availability filtering.

## 15.9 Advanced Shopping Lists

Allow users to create, save, edit, and reuse multiple shopping lists.

## 15.10 Enhanced Analytics

Introduce dashboards that display spending patterns, frequently purchased products, and potential savings over time.

---

# 16. Development Challenges and Solutions

Several technical challenges may arise during the development of an Android application that integrates authentication, external APIs, and backend services.

The following areas were particularly important during StackSave development.

### 16.1 Android UI Development

Building a modern interface using Jetpack Compose required careful management of application state, reusable components, layouts, and navigation.

**Solution:** Reusable Compose components and structured state management were used to improve consistency and maintainability.

### 16.2 API Integration

Connecting the Android application directly to external services introduces challenges involving authentication, API credentials, request handling, and response processing.

**Solution:** A Node.js backend proxy was introduced to handle external API communication and protect sensitive API credentials.

### 16.3 Firebase Integration

Authentication required coordination between the Android Firebase SDK and the backend Firebase Admin SDK.

**Solution:** Firebase ID tokens were used as the basis for verifying authenticated requests on the backend.

### 16.4 Gradle and Dependency Issues

Android development sometimes involves dependency conflicts, incompatible versions, and unresolved references.

**Solution:** Dependencies, imports, Gradle configuration, and Kotlin code were reviewed and adjusted to resolve development issues.

### 16.5 Shopping Basket Calculations

The basket required accurate quantity management and automatic calculation of item totals and overall costs.

**Solution:** Basket state and calculation logic were structured to update the displayed totals when users modify their selected products.

### 16.6 Backend Connectivity

Connecting an Android emulator or physical device to a locally hosted backend required the correct network configuration.

**Solution:** The Android emulator host address and local network IP configuration were used to establish communication during development.

---

# 17. Use of Artificial Intelligence

Artificial Intelligence, particularly ChatGPT, was used as a development support tool throughout the StackSave project.

AI assisted with generating and improving Kotlin code, developing application features, refining the user interface, troubleshooting programming errors, and explaining technical concepts.

It also provided guidance on Firebase Authentication, backend development, API integration, Gradle dependencies, application testing, and debugging.

AI-assisted suggestions were reviewed and applied during development, while application functionality was evaluated through testing and troubleshooting.

The use of AI supported the development process and contributed to improving programming knowledge, technical problem-solving, and understanding of Android application development.

---

# 18. Contributing

Contributions and suggestions are welcome.

Developers interested in improving StackSave can contribute by:

1. Reviewing the existing source code.
2. Identifying bugs or areas requiring improvement.
3. Suggesting new features.
4. Improving application performance.
5. Enhancing the user interface.
6. Improving security and backend reliability.
7. Expanding testing coverage.

### Suggested Contribution Workflow

1. Fork the repository.
2. Create a new feature branch.
3. Implement the proposed changes.
4. Test the changes.
5. Commit the updates.
6. Submit a pull request describing the improvements.

---

# 19. Project Status

StackSave is an Android grocery price comparison and shopping application under active development.

The project has established the main application structure and integrates several important components.

| Component                        | Project Status                              |
| -------------------------------- | ------------------------------------------- |
| Android application              | Implemented                                 |
| Kotlin development               | Implemented                                 |
| Jetpack Compose interface        | Implemented                                 |
| Firebase Authentication          | Integrated; requires ongoing testing        |
| Node.js backend                  | Implemented                                 |
| AZ Labs API integration          | Integrated; provider availability dependent |
| Grocery product search           | Implemented                                 |
| Product categories               | Implemented                                 |
| Retailer price comparison        | Implemented for available results           |
| Shopping basket                  | Implemented                                 |
| Basket quantity management       | Implemented                                 |
| Checkout demonstration           | Implemented                                 |
| Local notifications              | Implemented for supported search events     |
| Remote push notifications        | Further integration required                |
| Automated price-drop monitoring  | Future enhancement                          |
| Barcode scanning                 | Future enhancement                          |
| Real payment processing          | Not implemented                             |
| Direct retailer order submission | Not implemented                             |

**Note:** This table describes the intended project functionality and known implementation scope. Individual features should be verified against the latest source code and tested build before a release is labelled production-ready.

---

# 20. Author and Acknowledgements

### Project Name

StackSave

### Application Category

Android Grocery Price Comparison and Shopping Application

### Development Environment

Android Studio

### Primary Programming Language

Kotlin

### Backend Language

JavaScript (Node.js)

### Main Frameworks and Services

* Jetpack Compose
* Firebase
* Express.js
* AZ Labs API

---

## Final Project Statement

StackSave demonstrates how mobile application development, backend technologies, authentication services, and external API integration can be combined to address a practical everyday challenge.

By bringing grocery product discovery, retailer price comparison, shopping basket management, and checkout functionality into one application, StackSave aims to make grocery shopping more convenient and help consumers make informed purchasing decisions.

**StackSave – Compare. Save. Shop Smarter.**
