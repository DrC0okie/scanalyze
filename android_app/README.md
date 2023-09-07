## Running the Project Locally on Android Studio

### 1. Installing Android Studio

If you haven't already installed Android Studio, you'll need to do that first. Follow the steps below to install it:

- Visit the [Android Studio download page](https://developer.android.com/studio).
- Download the installer suitable for your operating system.
- Run the installer and follow the on-screen instructions.

### 2. Generating a Key to Sign the App (local release)

Before deploying your APK, you need to create a signing key.

- Open Android Studio and go to `Build` > `Generate Signed Bundle / APK...`
- Choose `Android App Bundle` or `APK` as needed.
- Click `Next`. If you haven't already created a key, click `Create new`.
  - Fill in the required information for the key.
  - Note down the Key Alias and Key Password as you'll need them later.
- Choose the path to save your `.jks` key file and set a password for the keystore. Remember these credentials as you'll need to add them to your `local.properties` file.

#### 2.1 Configuring `build.gradle.kts`

Configure your `build.gradle.kts` file to use the key for signing. The actual configuration might differ based on your project setup, but a typical signing configuration in `build.gradle.kts` might look like this:

```kotlin
import java.util.Properties
import java.io.FileInputStream

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

// Read properties values
val keyAliasProp = localProperties.getProperty("keyAlias") ?: ""
val keyPasswordProp = localProperties.getProperty("keyPassword") ?: ""
val storeFileProp = localProperties.getProperty("storeFile") ?: ""
val storePasswordProp = localProperties.getProperty("storePassword") ?: ""

android {
    // ...
    signingConfigs {
        create("release") {
            storeFile = file(storeFileProp)
            storePassword = storePasswordProp
            keyAlias = keyAliasProp
            keyPassword = keyPasswordProp
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    // ...
}
```

#### 2.2 Adding Credentials to `local.properties` File

The last step is to add your key alias, key password, and store password to your `local.properties` file. If the file doesn't exist, create it in the root directory of your Android project. Make sure this file is not pushed on github.  Add the following lines:

```
keyAlias=my-alias
keyPassword=password
storePassword=password
```

Replace the values accordingly based on the key you've generated. 

### 3 Use our deployment pipeline

If you plan to use our deployment pipeline, please [contact](timothee.vanhove@heig-vd.ch) us to receive credentials to store in the local.properties file, and don't forget to add this line:

```
storeFile=..\\scanalyze-release-key.jks
```

