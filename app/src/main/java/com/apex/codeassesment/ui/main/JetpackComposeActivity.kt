package com.apex.codeassesment.ui.main

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.apex.codeassesment.R
import com.apex.codeassesment.data.Resource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.di.MainComponent
import com.apex.codeassesment.ui.UserViewModel
import com.apex.codeassesment.ui.details.DetailsActivity
import com.apex.codeassesment.ui.main.ui.theme.ApexCodeAssesmentTheme
import com.apex.codeassesment.ui.main.ui.theme.Purple40
import com.apex.codeassesment.ui.main.ui.theme.PurpleGrey80
import com.apex.codeassesment.ui.main.ui.theme.white
import com.apex.codeassesment.utils.navigate
import javax.inject.Inject

class JetpackComposeActivity : ComponentActivity() {
    @Inject
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApexCodeAssesmentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(userViewModel = userViewModel)
                }
            }
        }

        MainActivity.sharedContext = this

        (applicationContext as MainComponent.Injector).mainComponent.inject(this)

    }

    companion object {
        var sharedContext: Context? = null
    }
}

@Composable
fun MainView(userViewModel: UserViewModel) {

    var randomUser by remember {
        mutableStateOf(userViewModel.getSavedUser())
    }

    val context = LocalContext.current

    var usersList by remember { mutableStateOf<List<User>?>(null) }

    val remoteUserFlow = userViewModel.userFlow.collectAsState()
    val remoteUsersFlow = userViewModel.usersFlow.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
        Text(
            text = stringResource(id = R.string.my_random_user),
            style = MaterialTheme.typography.headlineLarge
        )
        Row(modifier = Modifier.padding(vertical = 5.dp)) {
            AsyncImage(modifier = Modifier.size(100.dp), model = randomUser.picture?.medium, contentDescription = "User Image")
            Column(modifier = Modifier.padding(horizontal = 5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.name),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = "${randomUser.name?.first} ${randomUser.name?.last}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                randomUser.email?.let { email ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.email),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = email,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Button(
                    onClick = {
                        //navigation with activity extension function
                        context.navigate<DetailsActivity> {
                            putExtra("saved-user-key", randomUser)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                ) {
                    Text(
                        text = stringResource(id = R.string.view_details),
                        color = white,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Button(
            onClick = { userViewModel.getUserFromRemote(forceUpdate = true) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_random_user),
                color = white,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(5.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleGrey80)
        )

        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = {
                userViewModel.getUsersFromRemote(count = 10)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
        ) {
            Text(
                text = stringResource(id = R.string.show_10_users),
                color = white,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        usersList?.let { users ->
            LazyColumn(
                modifier = Modifier
//                .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                items(items = users) { userX ->
                    UserItemView(user = userX) {
                        context.navigate<DetailsActivity> {
                            putExtra("saved-user-key", userX)
                        }
                    }
                }
            }
        }
    }

    remoteUserFlow.value?.let {
        randomUser = it
    }

    remoteUsersFlow.value?.let {
        when(it) {
            is Resource.Success -> {
                it.response?.results?.let { users ->
                    usersList = users
                } ?: run {
                    usersList = listOf() //assigning empty users if user list is coming null from server
                }
            }
            is Resource.Failure -> {
                Toast.makeText(context, it.error.toString(),LENGTH_LONG).show()
            }
            else -> {
                Loader()
            }
        }
    }
}

@Composable
fun UserItemView(
    user: User,
    onItemClick: () -> Unit
) {
    Text(text = user.toString(), modifier = Modifier.clickable { onItemClick.invoke() })
    Spacer(modifier = Modifier.height(5.dp))
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .background(PurpleGrey80)
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApexCodeAssesmentTheme {
//        MainView("Android")
    }
}