package pk.event.booking.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONException
import pk.event.booking.app.DesignDetailActivity
import pk.event.booking.app.R
import pk.event.booking.app.data.*
import pk.event.booking.app.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeRecyclerViewAdapter(private val mList: List<DesignViewModel>) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>(), Filterable {
    var myList: List<DesignViewModel> = mList
    var countryFilterList: List<DesignViewModel>
    // exampleListFull . exampleList

    init {
        countryFilterList = myList as ArrayList<DesignViewModel>
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_row_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val designModel = mList[position]
        //myList = myList

        holder.textView.text = "${designModel.Style + " " + designModel.Category} "

        holder.checkBox.isChecked = designModel.Liked

        holder.rating.rating = designModel.Rating.toFloat()

        holder.rating.setOnRatingBarChangeListener { ratingBar, fl, b ->

            ratingBar.rating
            ratingCall(
                design = designModel,
                context = holder.rating.context,
                rating = ratingBar.rating.toInt()
            )

        }

        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.imageView.context,
                DesignDetailActivity::class.java
            )
            intent.putExtra("Image", designModel.Image)
            intent.putExtra("Phone", designModel.City)
            intent.putExtra("Title", designModel.Category + ":" + designModel.City)
            holder.imageView.context.startActivity(intent)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                postLikeDesign(designModel, holder.imageView.context, position)
            else
                postDisLikeDesign(designModel, holder.imageView.context)
        }

        Glide.with(holder.imageView.context).load(designModel.Image).into(holder.imageView);

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_event_main)
        val textView: TextView = itemView.findViewById(R.id.tv_event_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_like)
        val rating: RatingBar = itemView.findViewById(R.id.rating)
    }


    private fun ratingCall(design: DesignViewModel, context: Context, rating: Int) {

        var imageProperties = ImageRating(design.Image, rating)

        val apiInterface = ApiInterface.create().rateImage(imageProperties)

        apiInterface.enqueue(object : Callback<ImageRatingResponse> {
            override fun onResponse(
                call: Call<ImageRatingResponse>?,
                response: Response<ImageRatingResponse>?
            ) {

                if (response?.isSuccessful == true) {
                    try {

                        var response: String =
                            response.body().msg

                        myList?.find { it.Image == design.Image }?.Rating = rating

                        Toast.makeText(
                            context,
                            "Success.",
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ImageRatingResponse>?, t: Throwable?) {
                Toast.makeText(context, "Exception : ${call.toString()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postLikeDesign(design: DesignViewModel, context: Context, position: Int) {

        var imageProperties = ImageProperties(design.Image)

        val apiInterface = ApiInterface.create().userDesignLike(imageProperties)

        apiInterface.enqueue(object : Callback<LikeDesginResponse> {
            override fun onResponse(
                call: Call<LikeDesginResponse>?,
                response: Response<LikeDesginResponse>?
            ) {

                if (response?.isSuccessful == true) {
                    try {

                        var response: String =
                            response.body().Designs

                        myList?.find { it.Image == design.Image }?.Liked = true

                        Toast.makeText(
                            context,
                            "Success.",
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LikeDesginResponse>?, t: Throwable?) {
                Toast.makeText(context, "Exception : ${call.toString()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun postDisLikeDesign(design: DesignViewModel, context: Context) {

        var imageProperties = ImageProperties(design.Image)

        val apiInterface = ApiInterface.create().userDesignDisLike(imageProperties)

        apiInterface.enqueue(object : Callback<LikeDesginResponse> {
            override fun onResponse(
                call: Call<LikeDesginResponse>?,
                response: Response<LikeDesginResponse>?
            ) {

                if (response?.isSuccessful == true) {
                    try {

                        var response: String =
                            response.body().Designs

                        Toast.makeText(
                            context,
                            "Success.",
                            Toast.LENGTH_SHORT
                        ).show()

                        myList?.find { it.Image == design.Image }?.Liked = false

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LikeDesginResponse>?, t: Throwable?) {
                Toast.makeText(context, "Exception : ${call.toString()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList = myList
                } else {
                    val resultList = ArrayList<DesignViewModel>()
                    for (row in myList) {
                        if (row.Category.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                myList = results?.values as ArrayList<DesignViewModel>
                notifyDataSetChanged()
            }

        }
    }

}
