package pk.event.booking.app.retrofit

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import pk.event.booking.app.data.*
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface ApiInterface {

    @Headers("Content-Type:application/json")
    @GET("designs/islamabad")
    fun getDesignListIslamabad() : retrofit2.Call<JsonObject>

    @Headers("Content-Type:application/json")
    @GET("designs/cat/city")
    fun getCategoryWiseDesign(@Body cat : Category) : retrofit2.Call<JsonObject>

    @Headers("Content-Type:application/json")
    @GET("catlist")
    fun getDesignCategories() : retrofit2.Call<JsonObject>

    @Headers("Content-Type:application/json")
    @GET("searchlist")
    fun getSearchItems() : retrofit2.Call<JsonObject>

    @Headers("Content-Type:application/json")
    @GET("designs/userdesign")
    fun getUserLikeDesign() : retrofit2.Call<JsonObject>

    @Headers("Content-Type:application/json")
    @GET("designs/rawalpindi")
    fun getDesignListRawalpindi() : retrofit2.Call<JsonObject>

   //
   // @FormUrlEncoded

   // fun signIn(@Field("username") username: String, @Field("password") password: String): retrofit2.Call<ResponseBody>
    @Headers("Content-Type:application/json")
    @POST("login")
    fun signIn(@Body user: UserLogin): retrofit2.Call<UserLoginResponse>

    @Headers("Content-Type:application/json")
    @POST("designs/userdesign")
    fun userDesignLike(@Body image: ImageProperties): retrofit2.Call<LikeDesginResponse>

    @Headers("Content-Type:application/json")
    @POST("designs/userdesign")
    fun rateImage(@Body image: ImageRating): retrofit2.Call<ImageRatingResponse>

    @Headers("Content-Type:application/json")
    @PUT("designs/userdesign")
    fun userDesignDisLike(@Body image: ImageProperties): retrofit2.Call<LikeDesginResponse>


    @Headers("Content-Type:application/json")
    @POST("useractions")
    fun registerUser(
        @Body body: UserBody
    ): retrofit2.Call<SignUpResponse>

    //@Headers("x-access-token: {x-access-token}")
    @GET("user")
    fun getUserDetail( //@Path( "x-access-token") token : String
    ): retrofit2.Call<User>



        companion object {

            var BASE_URL = "https://scrapper-web-app.herokuapp.com/api/"

            fun create(): ApiInterface {
                var interceptor = HeaderInterceptor()

                var client =  OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .baseUrl(BASE_URL)
                    .build()
                return retrofit.create(ApiInterface::class.java)

            }

            class HeaderInterceptor : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response = chain.run {
                    proceed(
                        request()
                            .newBuilder()
                            .addHeader("Accept", "application/json;versions=1")
                            .addHeader("x-access-token",  Utils.TOKEN)
                            .build()
                    )
                }
            }

        }
    }
