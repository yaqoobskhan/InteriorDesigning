package pk.event.booking.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import pk.event.booking.app.retrofit.SessionManager
import pk.event.booking.app.utils.Utils

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loadAnimation()
    }

    fun signInButtonOnclick(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun signUpButtonOnclick(view: View) {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun loadAnimation() {
        try {
            var splashImage = findViewById<ImageView>(R.id.SplashScreenImage)
            var btnLayout = findViewById<LinearLayout>(R.id.splash_btn_layout)
            splashImage.startAnimation(
				AnimationUtils.loadAnimation(
					applicationContext,
					R.anim.alpha_animation_out
				)
			)
            Handler().postDelayed({

				var sessionManager = SessionManager(this)
				val token = sessionManager.fetchAuthToken().toString()
				if (token !=null && token.isNotEmpty()) {
					startActivity(Intent(this, DrawerMainActivity::class.java))
					finish()
				} else {

					btnLayout.visibility = View.VISIBLE
					btnLayout.startAnimation(
						AnimationUtils.loadAnimation(
							applicationContext,
							R.anim.alpha_animation
						)
					)
					splashImage.visibility = View.GONE
				}
			}, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
