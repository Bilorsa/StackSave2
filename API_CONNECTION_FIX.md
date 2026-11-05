# StackSave API connection — AZ Labs and Firebase

The app uses a Firebase-authenticated Node.js proxy to call AZ Labs. Firebase ID tokens authenticate the user to StackSave; the AZ Labs API key authenticates the backend to the provider. These are separate credentials and must not be interchanged.

The backend exposes `GET /api/deals?search=bread&limit=20`, verifies the Firebase ID token, calls AZ Labs `/api/v1/grocery/compare`, and falls back to `/api/v1/grocery/search` when the comparison response contains no offers. The original provider JSON is passed to the Android parser so optional/nested response formats can be handled without inventing prices.

Follow `API_SETUP.md` to configure Firebase Android, Firebase Admin, the rotated AZ Labs key, and the emulator URL. The API key is deliberately excluded from this archive.
