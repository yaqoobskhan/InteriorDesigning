package pk.event.booking.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pk.event.booking.app.adapter.EventDetailRecyclerViewAdapter
import pk.event.booking.app.data.DesignViewModel
import pk.event.booking.app.data.DesignViewModelSample

class EventDetailActivity : AppCompatActivity() {
    var HorizontalLayout: LinearLayoutManager? = null
    var RecyclerViewLayoutManager: RecyclerView.LayoutManager? = null
    var phoneNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        RecyclerViewLayoutManager = LinearLayoutManager(
            applicationContext
        )

        phoneNumber = intent.getStringExtra("Phone").toString()
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
            R.drawable.image_six,
            R.drawable.image_six,
            R.drawable.image_six,
            R.drawable.image_six,
            R.drawable.image_six,
            R.drawable.image_six
        )

        for (i in 1..5) {
            data.add(DesignViewModelSample(drawables[i]))
        }

        val adapter = EventDetailRecyclerViewAdapter(data)

        HorizontalLayout = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerview.layoutManager = (HorizontalLayout)


        recyclerview.adapter = adapter


    }

    fun callButtonOnclick(view: View) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(dialIntent)
    }



}