package pk.event.booking.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_my_designs.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import pk.event.booking.app.adapter.HomeRecyclerViewAdapter
import pk.event.booking.app.adapter.MyDesignsRecyclerViewAdapter
import pk.event.booking.app.data.*
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MyDesignsFragment : Fragment() , MyDesignsRecyclerViewAdapter.EventListener {
    private lateinit var sessionManager: SessionManager
    lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_designs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview = view.findViewById(R.id.recyclerview_my_designs)
        recyclerview.layoutManager = LinearLayoutManager(activity)


        sessionManager = SessionManager(requireActivity())
        Utils.TOKEN = sessionManager.fetchAuthToken().toString()

        getDesignData()
    }


    private fun getDesignData() {
        progress_circular_mydesigns.visibility = View.VISIBLE

        var apiInterface: Call<JsonObject>? = null

        apiInterface = ApiInterface.create().getUserLikeDesign()

        val data = ArrayList<DesignViewModel>()
        apiInterface?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>?,
                response: Response<JsonObject>?
            ) {
                if (response?.isSuccessful == true) {
                    try {
                        progress_circular_mydesigns.visibility = View.GONE
                        var designData =
                            response.body().toString()

                        val jsonObject = JSONTokener(designData).nextValue() as JSONObject
                        val jsonArray = jsonObject.getJSONArray("Designs")
                        for (i in 0 until jsonArray.length()) {
                            var img = jsonArray.getJSONObject(i).getString("Image")
                            var city = jsonArray.getJSONObject(i).getString("City")
                            var style = jsonArray.getJSONObject(i).getString("Style")
                            var category = jsonArray.getJSONObject(i).getString("Category")
                           // var rating = jsonArray.getJSONObject(i).getInt("Rating")

                            // if (selectedCategory == "" || selectedCategory.equals(category, ignoreCase = true))
                            data.add(
                                DesignViewModel(
                                    Image = img,
                                    City = city,
                                    Style = style,
                                    Category = category,
                                    Liked = true,
                                    Rating = 0//rating

                                )
                            )
                        }
                        var adapter = MyDesignsRecyclerViewAdapter(data, this@MyDesignsFragment)

                        recyclerview.adapter = adapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "No data found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Toast.makeText(activity, call.toString(), Toast.LENGTH_LONG).show()
                progress_circular_mydesigns.visibility = View.GONE
            }
        })
    }


    override fun onEvent() {
        getDesignData()
    }
}