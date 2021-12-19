package pk.event.booking.app

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_drawer_main.*
import org.json.JSONException
import pk.event.booking.app.data.User
import pk.event.booking.app.data.UserResponseBody
import pk.event.booking.app.retrofit.ApiInterface
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DrawerMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sessionManager: SessionManager
    lateinit var navUsername: TextView
    lateinit var navEmail: TextView
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        //val navController = findNavController(R.id.nav_host_fragment)

        val headerView: View = navView.getHeaderView(0)
        navUsername = headerView.findViewById(R.id.nav_username)
        navEmail = headerView.findViewById(R.id.nav_email)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_my_events, R.id.nav_my_profile
            ), drawerLayout
        )

        sessionManager = SessionManager(this)
        navView.setNavigationItemSelectedListener(this)
        loadFragment(DesignCategoryFragment(), "Categories")
        navView.menu.getItem(0).isChecked = true

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        getUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer_main, menu)
        return true
    }

    /* override fun onSupportNavigateUp(): Boolean {
          val navController = findNavController(R.id.nav_host_fragment)
          return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
      }*/


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                loadFragment(DesignCategoryFragment(), "Categories")
                navView.menu.getItem(0).isChecked = true
            }
            R.id.nav_my_events -> {
                navView.menu.getItem(1).isChecked = true
                loadFragment(MyDesignsFragment(), "My Designs")
            }
            R.id.nav_my_profile -> {
                navView.menu.getItem(2).isChecked = true
                loadFragment(MyProfileFramgment(), "My Profile")
            }
            R.id.nav_log_out -> {
                // navView.menu.getItem(3).isChecked = true
                showLogOutAlert()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun getUserData() {
        try {
            Utils.TOKEN = sessionManager.fetchAuthToken().toString()

            val apiInterface = ApiInterface.create()
                .getUserDetail()//token = "Bearer ${sessionManager.fetchAuthToken()}")

            apiInterface.enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>?,
                    response: Response<User>?
                ) {

                    if (response?.isSuccessful == true) {
                        try {

                            val email: String =
                                response.body().user.Email

                            val username: String =
                                response.body().user.FirstName + " " + response.body().user.LastName

                            if (email != null) {
                                navUsername.text = username
                                navEmail.text = email
                            }

                            val userdata = UserResponseBody(
                                Email = email,
                                FirstName = response.body().user.FirstName,
                                LastName = response.body().user.LastName,
                                City = response.body().user.City,
                                Username = response.body().user.Username,
                                Password = "",
                                Date = ""
                            )

                            sessionManager.saveUserDataInSp(userdata)


                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "User not found ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>?, t: Throwable?) {
                    Toast.makeText(applicationContext, call.toString(), Toast.LENGTH_LONG).show()
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {

        supportActionBar?.title = title
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        //transaction.disallowAddToBackStack()
        transaction.commit()
    }

    private fun showLogOutAlert() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setMessage("Are you sure?")
            .setPositiveButton("Logout", DialogInterface.OnClickListener { dialog, which ->
                logout() // Last step. Logout function
            }).setNegativeButton("Cancel", null)
        val alert1: AlertDialog = alert.create()
        alert1.show()
    }

    private fun logout() {
        sessionManager.clearPreferences()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}