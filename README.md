# Chef

17.4 - 17.6 진행한 요리 + SNS 어플리케이션 최신화

## About this Project

1. ACCOUNT
3. (local) recipe - main init -> main recipes -> main favorite -> detail 
4. (server) recipe play - timer, proximity sensor -> review
5. (local) Postlist -> postdetail -> post write
6. (local) home

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
