package pk.event.booking.app.data


data class Category(val cat : String)
data class ImageProperties(val image: String)
data class ImageRating(val image: String, val rating : Int)
data class LikeDesginResponse(val Designs: String)
data class ImageRatingResponse(val msg: String)
data class UserLoginResponse(val token: String)
data class UserLogin(var username: String, val password: String)
data class SignUpResponse(val msg: String)
data class UserBody(
    val username: String,
    val firstname: String,
    val lastname: String,
    val city: String,
    val email: String,
    val date: String,
    val password: String
)

data class UserResponseBody(
    val Username: String,
    val FirstName: String,
    val LastName: String,
    val City: String,
    val Email: String,
    val Password: String,
    val Date: String
)

data class CategoryData(val image: Int, val name : String)

data class User(val user: UserResponseBody)
data class DesignData(val Image: String, val City: String, val Style: String, val Category: String)
data class Designs(val Designs: DesignData)
data class DesignViewModelSample(
    val Image: Int,
)
data class DesignViewModel(
    val Image: String,
    val City: String,
    val Style: String,
    val Category: String,
    var Liked: Boolean,
    var Rating : Int
)
