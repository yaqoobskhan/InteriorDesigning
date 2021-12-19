package pk.event.booking.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.view.menu.MenuAdapter
import androidx.core.view.MenuCompat
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
import pk.event.booking.app.data.Category
import pk.event.booking.app.data.CategoryData
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils
import pk.event.booking.app.utils.Utils.Companion.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var recyclerview: RecyclerView
    var selectedCity: String = "Rawalpindi"
    var selectedCategory: String = ""
    var category: String = ""
    lateinit var autoTextView: AutoCompleteTextView
    lateinit var adapter: HomeRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args != null)
            category = args!!.getString("cat", "dining")
        setHasOptionsMenu(true)
        recyclerview = view.findViewById(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(activity)
        autoTextView = view.findViewById<AutoCompleteTextView>(R.id.autoTextView)

        swipeContainer = view.findViewById(R.id.swipe_controller)

        sessionManager = SessionManager(requireActivity())
        Utils.TOKEN = sessionManager.fetchAuthToken().toString()

        spinner_city_home.setSelection(2)

        spinner_city_home.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    if (position != 0) {
                        selectedCity = parent.getItemAtPosition(position).toString()
                        getDesignData(selectedCity)
                    }
                }

            }
        }

        /*    spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (parent != null) {
                        if (position != 0)
                            selectedCategory = parent.getItemAtPosition(position).toString()
                        else
                            selectedCategory = ""
                    }

                }
            }

            iv_filter.setOnClickListener(View.OnClickListener {

                if (selectedCategory.isEmpty() || selectedCity.isEmpty()) {
                    Toast.makeText(activity, "Select city and category", Toast.LENGTH_LONG).show()
                } else {
                    swipeContainer.isRefreshing = false;
                    getDesignData(selectedCity)
                }
            })*/

        getDesignData(selectedCity)
        getSearchItems()
    }

    private fun pullToRefresh() {

        swipeContainer.setOnRefreshListener(OnRefreshListener { // Your code to refresh the list here.
            swipeContainer.isRefreshing = false;
            getDesignData(selectedCity)
        })
        swipeContainer.setColorSchemeResources(
            R.color.colorPrimaryDark,
            R.color.colorPrimary,
            R.color.colorAccent
        )
    }


    private fun getDesignData(city: String) {

        swipeContainer.isRefreshing = true
            var apiInterface: Call<JsonObject>? = null

             if (city == "Rawalpindi")
                 apiInterface = ApiInterface.create().getDesignListRawalpindi()
             else if (city == "Islamabad")
                 apiInterface = ApiInterface.create().getDesignListIslamabad()
  /*      val cat = Category(cat = category)
        val apiInterface = ApiInterface.create()
            .getDesignListRawalpindi()*/ //ApiInterface.create().getCategoryWiseDesign(cat)

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

                            if (selectedCategory == "" || selectedCategory
                                    .contains(category, ignoreCase = true)
                            )
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
                        adapter = HomeRecyclerViewAdapter(data)
                        recyclerview.adapter = adapter

                        var tempDataList = ArrayList<DesignViewModel>()
                        autoTextView.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (s != null) {
                                    if (s.length > 3) {
                                        for (row in data) {
                                            if (s.toString()
                                                    .contains(row.Category, ignoreCase = true)
                                                ||
                                                s.toString()
                                                    .contains(row.City, ignoreCase = true))
                                            tempDataList.add(row)
                                            recyclerview?.adapter?.notifyDataSetChanged()

                                        }
                                        if (tempDataList != null && tempDataList.size > 0) {
                                            adapter = HomeRecyclerViewAdapter(tempDataList)
                                            recyclerview.adapter = adapter
                                            adapter.notifyDataSetChanged()
                                        }


                                    }
                                }
                            }

                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }
                            })


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

               /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            requireActivity().menuInflater.inflate(R.menu.search_menu, menu)

            val item = menu?.findItem(R.id.action_search);
            val searchView = item?.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d("onQueryTextChange", "query: $query")
                    adapter?.filter?.filter(query)
                    return true
                }
            })

            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    adapter?.filter?.filter("")
                    showToast(requireContext(), "Action Collapse")
                    return true
                }

                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    showToast(requireContext(), "Action Expand")
                    return true
                }
            })

            super.onCreateOptionsMenu(menu, inflater)
        }
*/

                private fun getSearchItems() {

            var apiInterface = ApiInterface.create().getSearchItems()

            val dataList = ArrayList<String>()
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

                            if (jArray != null) {
                                for (i in 0 until jArray.length()) {
                                    dataList.add(jArray.getString(i))
                                }
                            }

                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1, dataList
                            )
                            autoTextView.setAdapter(adapter)


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