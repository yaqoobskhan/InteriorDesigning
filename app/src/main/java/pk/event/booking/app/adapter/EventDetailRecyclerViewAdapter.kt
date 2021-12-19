package pk.event.booking.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import pk.event.booking.app.EventDetailActivity
import pk.event.booking.app.R
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.data.DesignViewModelSample

class EventDetailRecyclerViewAdapter(private val mList: List<DesignViewModelSample>) : RecyclerView.Adapter<EventDetailRecyclerViewAdapter.ViewHolder>() {

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

		val ItemsViewModel = mList[position]

		holder.imageView.setImageResource(ItemsViewModel.Image)



		/*holder.itemView.setOnClickListener(View.OnClickListener {
				Toast.makeText(holder.imageView.context, "clicked", Toast.LENGTH_LONG).show()
			})*/
				//onItemClick?.invoke(contacts[adapterPosition])
				/*holder.imageView.context.startActivity(
					Intent(
						holder.imageView.context,
						EventDetailActivity::class.java
					)
				)*/




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
