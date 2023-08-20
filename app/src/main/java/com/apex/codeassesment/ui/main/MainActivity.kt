package com.apex.codeassesment.ui.main

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.apex.codeassesment.R
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.databinding.ActivityMainBinding
import com.apex.codeassesment.databinding.ActivityMainConstraintBinding
import com.apex.codeassesment.di.MainComponent
import com.apex.codeassesment.ui.UserViewModel
import com.apex.codeassesment.ui.details.DetailsActivity
import com.apex.codeassesment.utils.navigate
import com.bumptech.glide.Glide
import javax.inject.Inject

// TODO (5 points): Move calls to repository to Presenter or ViewModel.
// TODO (5 points): Use combination of sealed/Dataclasses for exposing the data required by the view from viewModel .
// TODO (3 points): Add tests for viewmodel or presenter.
// TODO (1 point): Add content description to images
// TODO (3 points): Add tests
// TODO (Optional Bonus 10 points): Make a copy of this activity with different name and convert the current layout it is using in
//  Jetpack Compose.
class MainActivity : AppCompatActivity() {

  private lateinit var progressDialog: ProgressDialog
  @Inject lateinit var userViewModel: UserViewModel

  private lateinit var binding: ActivityMainConstraintBinding
  // TODO (2 points): Convert to view binding
//  private var userImageView: ImageView? = null
//  private var userNameTextView: TextView? = null
//  private var userEmailTextView: TextView? = null
//  private var seeDetailsButton: Button? = null
//  private var refreshUserButton: Button? = null
//  private var showUserListButton: Button? = null
//  private var userListView: ListView? = null

//  @Inject lateinit var userRepository: UserRepository
  private lateinit var arrayAdapter: ArrayAdapter<User>

  private var randomUser: User = User()
    set(value) {
      // TODO (1 point): Use Glide to load images after getting the data from endpoints mentioned in RemoteDataSource
      // userImageView.setImageBitmap(refreshedUser.image)

      //Glide library loads image if its coming in user object and it showing default placeholder image
      // while loading the image or have any image loading error or image is coming ull
      Glide.with(this)
        .load(value.picture?.medium)
        .placeholder(R.drawable.ic_launcher_background)
        .into(binding.mainImage)
      binding.mainName.text = value.name!!.first
      binding.mainEmail.text = value.email
      field = value
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainConstraintBinding.inflate(layoutInflater)
    setContentView(binding.root)
    sharedContext = this

    progressDialog = ProgressDialog(this@MainActivity)
    progressDialog.setMessage("Please wait...")

    (applicationContext as MainComponent.Injector).mainComponent.inject(this)

    initViewModel()

    arrayAdapter = ArrayAdapter<User>(this, android.R.layout.simple_list_item_1)

//    userImageView = findViewById(R.id.main_image)
//    userNameTextView = findViewById(R.id.main_name)
//    userEmailTextView = findViewById(R.id.main_email)
//    seeDetailsButton = findViewById(R.id.main_see_details_button)
//    refreshUserButton = findViewById(R.id.main_refresh_button)
//    showUserListButton = findViewById(R.id.main_user_list_button)
//    userListView = findViewById(R.id.main_user_list)
    binding.mainUserList.adapter = arrayAdapter
    binding.mainUserList.setOnItemClickListener { parent, _, position, _ ->
//      navigateDetails(parent.getItemAtPosition(position) as User)
      //navigatio using extension function
      navigate<DetailsActivity> {
        val user = parent.getItemAtPosition(position) as User
        putExtra("saved-user-key", user)
      }
    }

    randomUser = userViewModel.getSavedUser()

    binding.mainSeeDetailsButton.setOnClickListener {
//      navigateDetails(randomUser)
      //navigation using extension function
      navigate<DetailsActivity> {
        putExtra("saved-user-key", randomUser)
      }
    }

    binding.mainRefreshButton.setOnClickListener {
      progressDialog.setTitle("Fetching User")
      userViewModel.getUser(true)
    }

    binding.mainUserListButton.setOnClickListener {
      progressDialog.setTitle("Fetching Users")
      userViewModel.getUsers(count = 10) // you can send any count in this function as a parameter
    }

    /*userViewModel.usersFlow.value?.let {
      when(it) {
        is Resource.Success -> {
          it.response?.results?.let { users ->
            arrayAdapter.clear()
            arrayAdapter.addAll(users)
          }
        }
        is Resource.Failure -> {
          Log.d("Api Exception", it.error.toString())
        }
        is Resource.Loading -> {
          //show loading progress bar
        }
      }
    }*/
  }

  private fun initViewModel() {
    userViewModel.getUserLiveData.observe(this, Observer {
      if (it!= null && it.error.isNullOrEmpty()) {
        randomUser = it
      } else {
        it?.error?.let { error -> binding.mainName.text = error }
      }
    })

    userViewModel.getUsersLiveData.observe(this, Observer {
      if (it.isNullOrEmpty()) {
        userViewModel.showError.value
      } else {
        arrayAdapter.clear()
        arrayAdapter.addAll(it)
      }
    })

    userViewModel.showLoading.observe(this, Observer {
      if (it) {
        progressDialog.show()
      } else {
        progressDialog.hide()
      }
    })
  }

  //check this function in Extensions file in Utils package
  // TODO (2 points): Convert to extenstion function.
  private fun navigateDetails(user: User) {
    val putExtra = Intent(this, DetailsActivity::class.java).putExtra("saved-user-key", user)
    startActivity(putExtra)
  }

  companion object {
    var sharedContext: Context? = null
  }
}
