package com.yhjoo.dochef

object Constants {
    object RECIPE {
        object SEARCHBY {
            const val ALL = 0
            const val USERID = 1
            const val INGREDIENT = 2
            const val RECIPENAME = 3
            const val TAG = 4
        }

        object SORT {
            const val LATEST = "latest"
            const val POPULAR = "popular"
            const val RATING = "rating"
        }
    }

    object IMAGE {
        object SIZE {
            object POST {
                const val IMG_WIDTH = 1080
                const val IMG_HEIGHT = 1080
            }

            object PROFILE {
                const val IMG_WIDTH = 360
                const val IMG_HEIGHT = 360
            }
        }
    }

    object ANALYTICS {
        object ID {
            const val START = "A0"
            const val SIGNUP = "A1"
            const val SIGNIN = "A2"
            const val TERMINATED = "A3"
        }

        object NAME {
            const val START = "APP_START"
            const val SIGNUP = "APP_SIGNUP"
            const val SIGNIN = "APP_SIGNIN"
            const val TERMINATED = "APP_TERMINATED"
        }
    }

    object INTENTNAME{
        const val RECIPE_ID = "recipeId"
        const val USER_ID = "userId"
        const val POST_ID = "postId"
    }

    val ServerResult = mapOf(
        200 to "Success",
        404 to "Not found",
        409 to "Already Exists",
        500 to "Internal Error"
    )

    const val PERMISSION_CODE = 22
    const val GOOGLE_SIGNIN_CODE = 9001

    // Temp Admin user ID
    const val adminUserId = "777"
}


