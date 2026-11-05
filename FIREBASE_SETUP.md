# StackSave — Firebase connection setup

## What I found in this project

The Android package/application ID is `com.example.stacksave`.

The `app/google-services.json` currently included in this ZIP points to Firebase project **`stacksave1-9bcef`** and has the Android package `com.example.stacksave`. If that is not the Firebase project you currently use, the app is still configured for the old/wrong Firebase project until you replace that file. I cannot generate a valid configuration for a Firebase project I cannot access; it must be downloaded from your Firebase Console.

The app now displays the Firebase project ID on the login screen. Real email/password login and registration no longer silently pretend to succeed in local mode when Firebase is missing. The “Demo SSO Login” button is a clearly labelled local demo and does not authenticate with Firebase.

## Connect StackSave to your current Firebase project

1. Open the [Firebase Console](https://console.firebase.google.com/) and select the Firebase project you want StackSave to use (or create one).
2. Open **Project settings** (gear icon) → **General** → **Your apps**.
3. If an Android app is already registered, select the app whose package name is exactly `com.example.stacksave`. If not, add an Android app with that exact package name.
4. Download its `google-services.json`.
5. In Android Studio, replace this file:
   `StackSave2/app/google-services.json`
   with the downloaded file. Do not rename it and do not put it in the project root.
6. In Firebase Console → **Authentication** → **Sign-in method**, enable **Email/Password**.
7. In Firebase Console → **Firestore Database**, create a database. Choose the correct production region for your users.
8. In Android Studio, choose **Sync Project with Gradle Files**, then **Build → Clean Project**, **Build → Rebuild Project**. Uninstall the old app from the emulator and run it again.
9. On the login screen, check the line starting with **Firebase project:**. It must show the project ID you selected in the Firebase Console.
10. Register a new account using an email you can access. Then check Firebase Console → Authentication → Users. A successful registration should appear there. User profile data is saved in the Firestore `users` collection.

## Firestore security rules

For a basic authenticated-user setup, use rules that let a signed-in user read/write only their own profile document. Review these before publishing:

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, create, update: if request.auth != null
                                  && request.auth.uid == userId;
      allow delete: if false;
    }
  }
}
```

Do not leave Firestore in test mode for a public release. If the app later stores more than a user profile, add separate rules for those collections.

## Common errors

- **`Firebase project: stacksave1-9bcef` but you expected another project:** replace `app/google-services.json` with the file downloaded from the correct Firebase project.
- **`CONFIGURATION_NOT_FOUND` or `Default FirebaseApp is not initialized`:** confirm the JSON is in `app/`, matches `com.example.stacksave`, and Gradle sync/build completed.
- **`operation-not-allowed`:** enable Email/Password under Firebase Authentication sign-in providers.
- **`PERMISSION_DENIED` in Firestore:** check Firestore is created and the rules allow the signed-in user to access their own `users/{uid}` document.
- **Login fails with an invalid credential:** create an account in this Firebase project first, or use password reset/account recovery. The demo button is not a Firebase account.

## Notes

- Firebase client configuration includes an API key; this is not a substitute for security rules. Secure your data with Authentication and Firestore rules.
- The grocery-deals API key is separate from Firebase configuration. Do not put private server credentials in the Android app.
