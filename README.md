# Chef

17.4 - 17.6 진행한 요리 + SNS 어플리케이션 최신화

## About this Project
### Introduction

### Function
1. ACCOUNT
   1. Firebase Auth - Google / Email 로그인 
   2. 서버연동 - 유저 정보 자동 동기화
2. HOME
   1. 사용자의 Recipe와 Post, Follow 정보를 한 눈에 볼 수 있는 기능
3. RECIPE
   1. 레시피 및 리뷰를 생성, 관리, 재생하는 기능
   2. 근접센서 감지 -> 음성인식 기능
4. POST
   1. Post 및 댓글을 생성, 관리하는 기능
5. NOTIFICATION
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

-   [RxJava2 / RxAndriod](http://reactivex.io/)
-   [Retrofit2](https://square.github.io/retrofit/)
-   [Glide](https://github.com/bumptech/glide)
-   [Firebase(Auth, Admob, Analytics, Crashlytics, Cloud Messaging, Storage)](https://firebase.google.com/docs?hl=ko)
