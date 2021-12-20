package pk.event.booking.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.event_row_layout.view.*
import org.json.JSONException
import pk.event.booking.app.DesignDetailActivity
import pk.event.booking.app.R
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.data.ImageProperties
import pk.event.booking.app.data.LikeDesginResponse
import pk.event.booking.app.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyDesignsRecyclerViewAdapter(private val mList: List<DesignViewModel> , listener : EventListener) :
    RecyclerView.Adapter<MyDesignsRecyclerViewAdapter.ViewHolder>() {
    var listener: EventListener? = listener

    interface EventListener {
        fun onEvent()
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

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(designModel.Image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = "${designModel.Style + " " + designModel.Category} "
        holder.checkBox.isChecked = true
        holder.rating.visibility = View.INVISIBLE

        holder.itemView.setOnClickListener {

            holder.imageView.context.startActivity(
                Intent(
                    holder.imageView.context,
                    DesignDetailActivity::class.java
                )
            )
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                postDisLikeDesign(designModel.Image, holder.imageView.context)
            }
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


    private fun postDisLikeDesign(image: String, context: Context) {

        var imageProperties = ImageProperties(image)

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

                        listener?.onEvent()

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

}
