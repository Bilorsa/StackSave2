# StackSave product images and AZ Labs API

## Why images were missing
The Firebase-updated project had a demo-only `DealFeedService`: it did not call the grocery API and its sample deals had empty `imageUrl` values. The product card also showed only a grocery icon. This update restores the AZ Labs API client, maps common image fields (`imageUrl`, `image_url`, `image`, `thumbnail`), and loads returned image URLs with Coil.

## Configure the API key
1. Open the project root `local.properties` (same level as `settings.gradle.kts`).
2. Add `AZ_LABS_API_KEY=YOUR_REAL_KEY`.
3. Sync Gradle and run the app. Do not commit or share this key. A key compiled into an APK can be extracted; use a server-side proxy for production.

## Test
Type a product such as `milk`, `bread`, or `rice` into the app search field and wait about half a second. The app calls `/api/v1/grocery/compare` and falls back to `/api/v1/grocery/search`. Product cards display the image only when the API response contains a valid, publicly reachable image URL.

## Important limitation
The Android app cannot manufacture the retailer's official product image if the API response does not provide an image URL, or if the retailer blocks image hotlinking. In that case the card intentionally shows a neutral placeholder. Check the raw Postman response for `imageUrl`, `image_url`, `image`, `thumbnail`, or another documented image field. If none is present, ask the API provider whether image URLs are included in your subscription/endpoint.

## Security and licensing
Do not ship the API key in a public APK. Move requests to your backend and keep the key server-side. Confirm the provider's image and retailer-content licensing terms before caching or republishing images.
