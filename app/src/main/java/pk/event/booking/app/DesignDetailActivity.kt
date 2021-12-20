package pk.event.booking.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_event_detail.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import pk.event.booking.app.adapter.DesignDetailRecyclerViewAdapter
import pk.event.booking.app.adapter.HomeRecyclerViewAdapter
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.data.DesignViewModelSample
import pk.event.booking.app.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DesignDetailActivity : AppCompatActivity() {
    var HorizontalLayout: LinearLayoutManager? = null
    var RecyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    //lateinit var recyclerview: RecyclerView
    var phoneNumber = "03311418883"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        RecyclerViewLayoutManager = LinearLayoutManager(
            applicationContext
        )

        // phoneNumber = intent.getStringExtra("Phone").toString()
        val image = intent.getStringExtra("Image").toString()
        val title = findViewById<TextView>(R.id.tv_event_name)
        title.text = intent.getStringExtra("Title").toString()


        val recyclerview = findViewById<RecyclerView>(R.id.recycler_event_detail)
        //recyclerview.layoutManager = LinearLayoutManager(applicationContext)
        recyclerview.layoutManager =
            RecyclerViewLayoutManager

        Glide.with(this).load(image).into(findViewById(R.id.iv_event_main_detail));

        val data = ArrayList<DesignViewModelSample>()
        val drawables = arrayOf(
            R.drawable.category,
            R.drawable.accesories,
            R.drawable.bedrooms,
            R.drawable.dining,
            R.drawable.living,
            R.drawable.image_six
        )

        /*     for (i in 1..5) {
                 data.add(DesignViewModelSample(drawables[i]))
             }*/


        getDesignData()

    }

    fun callButtonOnclick(view: View) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(dialIntent)
    }


    private fun getDesignData() {

        progress_detail.visibility = View.VISIBLE
        var apiInterface: Call<JsonObject>? = null

        apiInterface = ApiInterface.create().getDesignListRawalpindi()

        val data = ArrayList<DesignViewModel>()
        apiInterface?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>?,
                response: Response<JsonObject>?
            ) {
                progress_detail.visibility = View.GONE
                if (response?.isSuccessful == true) {
                    try {

                        var designData =
                            response.body().toString()

                        val jsonObject = JSONTokener(designData).nextValue() as JSONObject
                        val jsonArray = jsonObject.getJSONArray("Designs") // "msg"
                        for (i in 0 until jsonArray.length()) {
                            var img = jsonArray.getJSONObject(i).getString("Image")
                            var city = jsonArray.getJSONObject(i).getString("City")
                            var style = jsonArray.getJSONObject(i).getString("Style")
                            var category = jsonArray.getJSONObject(i).getString("Category")
                            var rating = jsonArray.getJSONObject(i).getInt("Rating")
                            var liked =
                                jsonArray.getJSONObject(i).getString("Liked").replace( //Likes
                                    "Yes",
                                    "true"
                                ).replace("No", "false")

                            data.add(
                                DesignViewModel(
                                    Image = img,
                                    City = city,
                                    Style = style,
                                    Category = category,
                                    Liked = liked.toBoolean(),
                                    Rating = rating
                                )
                            )
                        }
                        val adapter = DesignDetailRecyclerViewAdapter(data)

                        HorizontalLayout = LinearLayoutManager(
                            this@DesignDetailActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        recycler_event_detail.layoutManager = (HorizontalLayout)


                        recycler_event_detail.adapter = adapter

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        this@DesignDetailActivity,
                        "No data found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Toast.makeText(this@DesignDetailActivity, call.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }


}