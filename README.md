# StackSave - Android Studio Prototype

University prototype for comparing store deals, saving coupon codes, Firebase authentication/cloud profiles, offline caching, settings, notifications, and multilingual UI.

## Essential store deals
The app includes a local catalogue of 48 illustrative sample offers across groceries, fresh food, household cleaning, personal care, baby products, school supplies, electronics, and everyday essentials. It supports search, category filtering, essential-deal filtering, biggest-savings filtering, coupon-code filtering, saved deals, and offline caching.

**Important:** All current prices, discounts, coupon codes, cashback values, and expiry dates are demonstration data, not verified live retailer promotions. Do not advertise them as current offers. Before publishing, replace the sample catalogue in `DealFeedService.kt` with retailer-authorised feeds or APIs, and set `isSample = false` only for offers verified by your data source.

## Firebase setup
Read `FIREBASE_SETUP.md` for the full setup and troubleshooting guide.

The included `app/google-services.json` currently points to Firebase project `stacksave1-9bcef`. If that is not your current Firebase project, replace it with the JSON downloaded from Firebase Console for the Android app package `com.example.stacksave`. Enable Email/Password Authentication and create Firestore. The login screen displays the configured Firebase project ID.

Real login and registration require Firebase configuration; the Demo SSO Login button is a local-only demo and does not create a Firebase session. The Firebase status on the login screen helps confirm which project is configured.

## Android Studio
Use JDK 17. Open this folder, allow Gradle sync, then run on an emulator or Android phone.

## Rubric coverage
- Register/login and masked password input
- Firebase Auth + Firestore profile storage when configured
- Settings
- Local sample deal catalogue with offline caching; retailer feed integration still required for verified live offers
- Offline cache and re-sync on refresh
- Google SSO placeholder marked POE-only
- Firebase Cloud Messaging service scaffold marked POE-only
- Local notification demonstration
- English + isiZulu language selector

## UX improvements implemented
- Reworked the home hero around the core task: finding deals and planning a shopping list.
- Replaced the misleading `LIVE` badge with `DEMO DATA` and removed unverified promotional banners.
- Updated deal cards to clearly mark sample offers, show potential rather than guaranteed savings, provide unit-price estimates for recognised pack sizes, and use larger bookmark touch targets with descriptive accessibility labels.
- Prioritised the filters `Essentials`, `Lowest Price`, `Biggest Savings`, and `Coupon Code`; added horizontally scrollable sorting options for price, savings, and product name.
- Added a clear-search-and-filters action for empty results.
- Renamed navigation to `Deals`, `My List`, and `Savings` for clarity.
- Added shopping-list quantity controls, per-item subtotals, and quantity-aware estimated totals.
- Removed the fake cashback-claim action and unverified rating/delivery/cashback claims from the deal detail experience.
- Updated analytics and descriptions to clearly identify the catalogue as demonstration data.

Unit-price estimates are derived from pack sizes recognised in the product name. They are indicative and should be checked against the exact product size, especially for multipacks and unusual packaging.

## Build verification
The source was reviewed after the UX changes, but a Gradle build could not be completed in this environment because the Gradle 9.6.0 distribution could not be downloaded (network DNS access to `services.gradle.org` was unavailable). Open the project in Android Studio with network access, sync Gradle, and run `assembleDebug` to verify the build in your environment.


## Product images and live API
See `PRODUCT_IMAGES_AND_API.md` for API-key setup, image troubleshooting, and the limitations when the provider does not return image URLs.
