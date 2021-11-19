# Chef

17.4 - 17.6 요리 + SNS 어플리케이션 리빌딩

## About this Project

### Introduction

-   요리, SNS 기능을 하나의 앱으로
-   SNS 로그인 및 간편한 회원가입으로 유저 데이터 동기화
-   레시피를 재생하면서 근접센서와 음성인식을 활용한 요리 재생 기능

### Function

1. ACCOUNT  
   ![account.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/account.gif?raw=true)
    1. Firebase Auth - Google / Email 로그인
    2. 서버연동 - 유저 정보 자동 동기화
2. HOME  
   ![home.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/home.gif?raw=true)
    1. 사용자의 Recipe와 Post, Follow 정보를 한 눈에 볼 수 있는 기능
3. RECIPE  
   ![recipe1.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/recipe1.gif?raw=true)
   ![recipe2.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/recipe2.gif?raw=true)
    1. 레시피 및 리뷰를 생성, 관리, 재생하는 기능
    2. 근접센서 감지, 음성인식 기능
4. POST  
   ![post1.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/post1.gif?raw=true)
   ![post2.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/post2.gif?raw=true)
    1. Post 및 댓글을 생성, 관리하는 기능
5. NOTIFICATION  
   ![notification.gif](https://github.com/nogardj3/dochef_android/blob/main/screenshots/notification.gif?raw=true)
    1. FCM 및 서버 연동, SQLite를 활용한 알림 기능

## How to use

### Prerequisites

1. requires Firebase project [Site](https://console.firebase.google.com/?hl=ko)

2. requires local server [Github](https://github.com/nogardj3/server_nodejs.git)

### Installation

1.  Clone the repo

    ```sh
    git clone https://github.com/nogardj3/server_nodejs
    ```

2.  Copy google-services.json to ./app

    ```sh
    cp your_service_account_key.json ./app/google-services.json
    ```

3.  add key to local.properties

    ```
    admob_app_id = ""
    admob_banner_id = ""
    ```

4.  add server url to values/string.xml

    ```xml
    <string name="server_url">http://10.0.2.2:4000/chef/</string>
    ```

## Tech stack & Open-source libraries

-   [Kotlin(Coroutine, Flow)](https://developer.android.com/kotlin/coroutines?hl=ko)
-   [AAC(Databinding, Lifecycle, Livedata, ViewModel, Room, Navigation)](https://developer.android.com/topic/libraries/architecture?hl=ko)
-   [Hilt](https://developer.android.com/training/dependency-injection/hilt-android?hl=ko)
-   [Junit, Mockito](https://developer.android.com/training/testing/unit-testing/local-unit-tests?hl=ko)
-   [Espresso](https://developer.android.com/training/testing/espresso?hl=ko)
-   [Firebase(Auth, Admob, Analytics, Crashlytics, Cloud Messaging, Storage)](https://firebase.google.com/docs?hl=ko)
-   [RxJava3 / RxAndriod](http://reactivex.io/)
-   [Retrofit2](https://square.github.io/retrofit/)
-   [Glide](https://github.com/bumptech/glide)