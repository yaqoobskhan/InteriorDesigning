package pk.event.booking.app

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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import pk.event.booking.app.adapter.DesignCategoryAdapter
import pk.event.booking.app.adapter.HomeRecyclerViewAdapter
import pk.event.booking.app.data.CategoryData
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DesignCategoryFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var recyclerview: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview = view.findViewById(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(activity)

        swipeContainer = view.findViewById(R.id.swipe_controller)

        sessionManager = SessionManager(requireActivity())
        Utils.TOKEN = sessionManager.fetchAuthToken().toString()

        getCategoryData()
    }

    private fun pullToRefresh() {

        swipeContainer.setOnRefreshListener(OnRefreshListener { // Your code to refresh the list here.
            swipeContainer.isRefreshing = false;
            getCategoryData()
        })
        swipeContainer.setColorSchemeResources(
            R.color.colorPrimaryDark,
            R.color.colorPrimary,
            R.color.colorAccent
        )
    }

    private fun getCategoryData() {

        swipeContainer.isRefreshing = true
        var apiInterface = ApiInterface.create().getDesignCategories()

        val data = ArrayList<DesignViewModel>()
        apiInterface?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>?,
                response: Response<JsonObject>?
            ) {
                swipeContainer.isRefreshing = false
                if (response?.isSuccessful == true) {
                    try {

                        var designData =
                            response.body().toString()

                        val jsonObject = JSONTokener(designData).nextValue() as JSONObject
                        val jArray = jsonObject.getJSONArray("msg")

                        val list = ArrayList<CategoryData>()
                        if (jArray != null) {
                            for (i in 0 until jArray.length()) {
                                list.add(CategoryData(R.drawable.image_six, jArray.getString(i)))
                            }
                        }

                        var adapter = DesignCategoryAdapter(list)
                        recyclerview.adapter = adapter
                        pullToRefresh()
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
                swipeContainer.isRefreshing = false
                Toast.makeText(activity, call.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }
}