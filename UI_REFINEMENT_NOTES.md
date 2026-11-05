# StackSave UI refinement

This update focuses on the existing Jetpack Compose Deals screen and preserves the current Firebase, AZ Labs, search, saved-list, and navigation implementation.

## UI changes
- Tightened vertical spacing on the Deals feed to reduce unnecessary scrolling.
- Refined search-field borders, cursor colour, and corner radius.
- Improved the promotional banner hierarchy and padding.
- Increased category heading clarity and refined category-pill sizing.
- Softened and enlarged the live-price data notice.
- Improved the deals feed heading hierarchy.
- Refined deal-card corner radius, elevation, retailer header spacing, and price panel.
- Deal cards now label non-live items as `PRODUCT OFFER` rather than incorrectly labelling every item `LIVE CATALOGUE`.
- Displays a `LOWEST FOUND` badge only when the API/model marks an offer as cheapest.

## Important configuration note
For safety, this distributable ZIP excludes `backend/.env` and `backend/firebase-service-account.json`, because they contain private API/Firebase credentials. Keep your original local copies in your working project. If you use this ZIP as a fresh project copy, recreate those two files from your private backups before starting the backend. Never commit or share them.

## Build verification
The Kotlin compile task could not be run in this environment because the Gradle wrapper attempted to download Gradle 9.6.0 and network DNS access to `services.gradle.org` was unavailable. Open the project in Android Studio, allow Gradle sync, then run **Build > Make Project**. The existing API/Firebase source files were not changed by this UI pass.
