package com.apex.codeassesment.ui.details

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
import com.apex.codeassesment.data.model.Coordinates
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.databinding.ActivityDetailsBinding
import com.apex.codeassesment.ui.location.LocationActivity
import com.bumptech.glide.Glide

// TODO (3 points): Convert to Kotlin
// TODO (3 points): Remove bugs or crashes if any
// TODO (1 point): Add content description to images
// TODO (2 points): Add tests
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("saved-user-key", User::class.java)
        } else {
            intent.getParcelableExtra("saved-user-key")
        }


        // TODO (1 point): Use Glide to load images

        user?.let {
            //Glide library loads image if its coming in user object and it showing default placeholder image
            // while loading the image or have any image loading error or image is coming ull
            Glide.with(this)
                .load(it.picture?.large)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.detailsImage)

            it.name?.let { name ->
                binding.detailsName.text = getString(R.string.details_name, name.first, name.last)
            }
            it.gender?.let {
                binding.detailsEmail.text = getString(R.string.details_email, user.gender)
            }
            it.dob?.let { dob ->
                binding.detailsAge.text = dob.age.toString()
            }
            it.location?.let { location ->
                location.coordinates?.let { coordinates ->
                    binding.detailsLocation.text =
                        getString(
                            R.string.details_location,
                            coordinates.latitude,
                            coordinates.longitude
                        )
                    binding.detailsLocationButton.setOnClickListener { x ->
                        navigateLocation(
                            coordinates
                        )
                    }
                }
            }
        }
    }

    private fun navigateLocation(coordinates: Coordinates) {
        val intent = Intent(this, LocationActivity::class.java)
            .putExtra("user-latitude-key", coordinates.latitude)
            .putExtra("user-longitude-key", coordinates.longitude)
        startActivity(intent)
    }
}