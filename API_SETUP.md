# StackSave — AZ Labs + Firebase setup

## Architecture
The Android app signs in with Firebase Authentication and obtains a Firebase ID token. It sends that token to the StackSave Node.js backend. The backend verifies the token with Firebase Admin, then calls AZ Labs using the AZ Labs API key stored only in `backend/.env`. The AZ Labs key is never compiled into the Android APK.

## 1. Rotate the AZ Labs key
The previous Postman collection contained a populated API-key variable. Treat that key as exposed and rotate it in your AZ Labs account. Do not paste the new key into Android code, `local.properties`, Git, or screenshots.

## 2. Configure Firebase
1. In Firebase Console, confirm Email/Password sign-in is enabled.
2. Download `google-services.json` for Android package `com.example.stacksave` and place it in `app/google-services.json`.
3. Generate a Firebase Admin service-account JSON from the same Firebase project.
4. Store the Admin JSON outside the project folder if possible. Set `FIREBASE_SERVICE_ACCOUNT_JSON` in `backend/.env` to its absolute path.

## 3. Configure backend
From the `backend` folder, copy `.env.example` to `.env` and set:
- `AZ_LABS_API_KEY` to the newly rotated key
- `FIREBASE_SERVICE_ACCOUNT_JSON` to the service-account JSON path

Then run:
```bash
npm install
npm start
```
Check `http://localhost:8080/health`. It should return `ok: true`, `firebaseAdmin: true`, and `azLabsKeyConfigured: true`.

## 4. Configure Android emulator
In the project-root `local.properties`, keep your existing `sdk.dir` line and add:
```properties
STACKSAVE_BACKEND_BASE_URL=http://10.0.2.2:8080/
```
`10.0.2.2` is the Android emulator's route to the host computer. For a physical phone, use the computer's LAN IP and ensure the backend port is reachable. Use HTTPS for production and disable cleartext traffic.

## 5. Test end-to-end
1. Start the backend and confirm `/health`.
2. Run the Android app and register/sign in using a real Firebase email/password account. Local/demo mode cannot access the live API.
3. Search `bread`, `milk`, or `rice`.
4. The app should display only products returned by AZ Labs. No sample prices are substituted.

## Troubleshooting
- **401 from app/backend:** Firebase ID token invalid/expired, or backend Admin credentials belong to a different Firebase project. Sign out/in and check both Firebase project IDs.
- **403 from AZ Labs:** rotate/check the AZ Labs key and verify its required subscription/permissions.
- **429:** provider quota/rate limit reached.
- **502:** backend cannot reach `https://azlabs.ai`; inspect backend terminal output.
- **Health says `azLabsKeyConfigured: false`:** `.env` is missing or has the wrong variable name.
- **No products:** check the Postman response shape and try a term known to return offers.

AZ Labs API key and Firebase Admin service-account JSON must remain server-side. Never commit `.env` or service-account JSON.
