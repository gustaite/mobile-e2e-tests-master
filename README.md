# mobile-e2e-tests

# How To
If you want to setup mobile automation project environment using our existing [Ansible script](https://github.com/vinted/automation-environment-setup), check out this setup instructions: https://vinted.atlassian.net/wiki/spaces/QAWIKI/pages/2608988282/Setup+Running+mobile+auto+tests+locally

If you want to setup everything manually, step-by-step setup instructions are below ⬇️

## Configure Environment
- install git
- check out code
- install java 1.8 (8) won't work with java 10
- install [IntelliJ](https://www.jetbrains.com/idea/download) or [Android studio](https://developer.android.com/studio/)
- set [JAVA_HOME](http://www.sajeconsultants.com/how-to-set-java_home-on-mac-os-x/)
- set ANDROID_HOME:

   In terminal:
```
nano ~/.bash_profile
```

   Add lines (and update path to Android SDK):
```
export ANDROID_HOME=/YOUR_PATH_TO/android-sdk
export PATH=$ANDROID_HOME/platform-tools:$PATH
export PATH=$ANDROID_HOME/tools:$PATH
```
   Save & Exit
   
   Check if it worked:
```
source ~/.bash_profile
echo $ANDROID_HOME
```
   
## Appium

- install Appium
```
brew install node
npm install -g appium
```
- run appium
   in terminal run
```
appium
```
## Other required tools

### - Allure

<summary>How to install:</summary>
<details>
   
```
brew untap qameta/allure
brew update
brew install allure
```

</details>

<summary>How to generate report:</summary>
<details>
   
```
cd PATH_TO_PROJECT
allure serve app/build/allure-results
```

</details>

### - OpenCV
<summary>How to install:</summary>
<details>
   
```
npm install -g opencv4nodejs
```

</details>

### - FFmpeg
<summary>How to install:</summary>
<details>
   
```
brew install ffmpeg
```

</details>

### - Applesimutils
<summary>How to install:</summary>
<details>
   
```
brew tap wix/brew
brew install applesimutils
```
After that, add it to the path in bash profile:

```
echo 'export PATH=$PATH:/usr/local/bin/applesimutils' >>~/.bash_profile && source ~/.bash_profile
```

</details>

# Run test
- default local.properties file should look like this:
```
remote_selenium_grid = false

platform=Android
portal=LT

max_retry_count=0
# ↓ Set this if you need to use common from your local m2 repo
localCommon=true
# ↓ Set true if want to see api response in report
always_log_api_response=true    
DEFAULT_USER_PASSWORD_PLAIN=Actual password can be found on 1Password (Quality-Assurance vault: auto_test_defaultUserAndKey.txt) 
DEFAULT_USER_PASSWORD_ENCODED=Actual password can be found on 1Password (Quality-Assurance vault: auto_test_defaultUserAndKey.txt)
AFTERSHIP_API_KEY=Actual key can be found on 1 Password (Quality-Assurance vault: Default user + auto_test_defaultUserAndKey.txt)
#Set ↓ this with full path (with app name) if want to run iOS tests on custom app
#APP_PATH=
#Set ↓ this with full path (with apk name) if want to run Android tests on custom apk
#APK_PATH=

#ios_app_version=20.23.1
#android_app_version=20.23.0.1

#delete_android_files=false
#thread_count=4
#device_name=S10e, S20, S20 FE
#run_all_on_sandbox=true
```
- portal:
   ```
   LT
   DE
   UK
   US
   PL
   CZ
   INT
   SB_INT
   SB_DE
   SB_LT
   SB_PL
   SB_UK
   SB_US
   SB_CZ
   ```
- platform: 
   ```
   Android
   Ios
   ```
- # android
   - set local.properties
   - connect android device (>= 5 version)
   - enable debug mode
   - run test
   
- # iOS
   - install xcode
   - follow steps in [link](http://appium.io/docs/en/drivers/ios-xcuitest-real-devices/)
   - set iphone 8 simulator with 13.2 version
   - add to local.properties platform=Ios
   - to run test on simulator .ipa file should be build in debug mode for simulators. That can be found in Nexus under [raw-hosted-qa-auto:ios](https://nexus.vinted.net/#browse/browse:raw-hosted-qa-auto:ios)
   - run test
   
# Create new test
## Get element locator
- open/install [appium desktop](http://appium.io/downloads.html)
   - use Custom Server
   - set desire capabilities:
      - Android example:
      ```
      {
        "platformName": "Android",
        "deviceName": "Android",
        "automationName": "UiAutomator2",
        "app": "/Users/YOUR_USERNAME/Dropbox/android-builds/master/UkRelease.apk",
        "noSign": true
      }
      "noSign": true - is a must if you using apk from dropbox/nexus. Withtout this one appium changes apk signature
      ```
      - iOS example:
      ```
      {
        "platformName": "iOS",
        "platformVersion": "11.3",
        "deviceName": "iPhone 8",
        "automationName": "XCUITest",
        "app": "PATH_TO_DEBUG_BUILD/Vinted.app",
        "noReset": false
      }
      ```
      - you can inspect element and see what appium can do
- if accessibility identifier for iOS is missing or is localized, add that one in iOS project
- if resource id is missing in Android, and that one in android project. (resource-id == Id)
- every method that will be used in test should have anotation @Step
- every new app window should have separate NAMERobot.kt class
- every test should have description attribute with proper test title
- every test if it is regression one should have regression group
   
# Run test

IntelliJ go to IntelliJ IDEA -> Preferences -> Build, Execution, Deployment -> Gradle -> Runner and select run test using: Gradle test runner or let me choose per test
