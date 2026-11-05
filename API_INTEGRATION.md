# StackSave API integration

## Live grocery prices — AZ Labs
The Android app obtains a Firebase Authentication ID token and calls the StackSave backend. The backend verifies that token using Firebase Admin, then calls AZ Labs `/api/v1/grocery/compare` and falls back to `/api/v1/grocery/search` when the compare response contains no offers. The AZ Labs API key is stored only in `backend/.env`; it is not compiled into the APK.

## Product images — Open Food Facts
The project also contains Open Food Facts image lookup support. Product image availability depends on the barcode/catalogue record and the provider's returned image URL; the app must not invent image links.

## Requirements
- Android `app/google-services.json` from the same Firebase project as the backend Firebase Admin service account.
- Firebase Email/Password authentication enabled and a real signed-in Firebase user.
- Backend `.env` with `AZ_LABS_API_KEY` and `FIREBASE_SERVICE_ACCOUNT_JSON`.
- Node.js 20 or newer and the backend running at the configured `STACKSAVE_BACKEND_BASE_URL`.

See `API_SETUP.md` for setup and troubleshooting. Never commit `.env`, provider API keys, or Firebase Admin service-account JSON.
