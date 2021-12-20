package pk.event.booking.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.event.booking.app.DesignDetailActivity
import pk.event.booking.app.R
import pk.event.booking.app.data.DesignViewModel

class DesignDetailRecyclerViewAdapter(private val mList: List<DesignViewModel>) : RecyclerView.Adapter<DesignDetailRecyclerViewAdapter.ViewHolder>() {

	// create new views
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		// inflates the card_view_design view
		// that is used to hold list item
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.event_detail_images_row_layout, parent, false)

		return ViewHolder(view)
	}

	// binds the list items to a view
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {

		val designModel = mList[position]

		Glide.with(holder.imageView.context).load(designModel.Image).into(holder.imageView)

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

	}

	// return the number of the items in the list
	override fun getItemCount(): Int {
		return mList.size
	}

	// Holds the views for adding it to image and text
	class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
		val imageView: ImageView = itemView.findViewById(R.id.iv_event_sub_image)

	}
}
