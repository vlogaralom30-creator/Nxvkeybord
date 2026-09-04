<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/4f72ea69-f03a-48e9-bb49-d9afab9e55dc

## 🚀 GitHub Actions: Automatic APK Generation

যখনই আপনি GitHub-এ কোড Push করবেন, সাথে সাথে GitHub Actions স্বয়ংক্রিয়ভাবে Android APK বিল্ড করবে।

### কীভাবে ডাউনলোড করবেন (How to Download APK):
1. আপনার GitHub রিপোজিটরির **Actions** ট্যাবে যান।
2. সর্বশেষ চলা **"Android CI & APK Build"** ওয়ার্কফ্লোতে ক্লিক করুন।
3. পৃষ্ঠার নিচের দিকে **Artifacts** সেকশনে **`NXV-Keyboard-Debug-APK`** দেখতে পাবেন, সেটিতে ক্লিক করে সরাসরি জিপ ফাইল আকারে APK ডাউনলোড করে ফোনে ইন্সটল করে নিতে পারবেন।

### ম্যানুয়ালি APK বিল্ড করার নিয়ম (Manual Trigger):
1. GitHub রিপোজিটরির **Actions** ট্যাবে যান।
2. বাম পাশের তালিকা থেকে **"Android CI & APK Build"** সিলেক্ট করুন।
3. ডান পাশে **"Run workflow"** বাটনে ক্লিক করুন।

### সরাসরি রিলিজ তৈরি করতে (Automatic GitHub Release):
- আপনি যখন কোনো গিট ট্যাগ পুশ করবেন (যেমন `git tag v1.0.0 && git push origin v1.0.0`), তখন GitHub অটোমেটিক একটি নতুন **GitHub Release** তৈরি করবে এবং সেখানে সরাসরি `NXV-Keyboard-Debug.apk` ফাইলটি অ্যাটাচ করে দেবে।

---

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.
