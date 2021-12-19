package pk.event.booking.app

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.fragment_profile.*
import pk.event.booking.app.retrofit.SessionManager

class MyProfileFramgment : Fragment() {
    private lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireActivity())
        val userData = sessionManager.getUserDataFromSp()
        val res: Resources = resources
        var listOfCity   = arrayOf<String>(*res.getStringArray(R.array.cities_array))

        if (userData != null) {
            et_firstname_profile.setText(userData.FirstName)
            et_lastname_profile.setText(userData.LastName)
            et_email_profile.setText(userData.Email)
            et_username_profile.setText(userData.Username)

            val i : Int = listOfCity.indexOf(userData.City)
            if(userData.City.isNotEmpty())
                spinner_city_profile.setSelection(i)
        }

    }
}