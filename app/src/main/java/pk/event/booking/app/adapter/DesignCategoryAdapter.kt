package pk.event.booking.app.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import pk.event.booking.app.DrawerMainActivity
import pk.event.booking.app.EventDetailActivity
import pk.event.booking.app.HomeFragment
import pk.event.booking.app.R
import pk.event.booking.app.data.CategoryData
import androidx.appcompat.app.AppCompatActivity


class DesignCategoryAdapter(private val mList: List<CategoryData>) :
    RecyclerView.Adapter<DesignCategoryAdapter.ViewHolder>() {
    var myList: List<CategoryData> = mList

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_row_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val category = mList[position]

        holder.textView.text = "${category.name} "

        holder.itemView.setOnClickListener {

           loadFragment(holder.imageView, category.name)
        }

        holder.imageView.setImageResource(category.image)

        //Glide.with(holder.imageView.context).load(category.Image).into(holder.imageView);

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_event_main)
        val textView: TextView = itemView.findViewById(R.id.tv_event_name)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun loadFragment(view: View, cat: String) {
        try {
            val args = Bundle()
            args.putString("cat", cat)

            val activity = view.context as AppCompatActivity
            val myFragment: Fragment = HomeFragment()
            myFragment.arguments = args
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, myFragment).addToBackStack(null).commit()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
