package com.example.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.chatapp.Screens.ChatScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.viewModel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppTheme {
                viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
                viewModel.createUserToken("he7l07vq5lmkodacriv1nifczx2lkxzwek9u2a45ht9wmcgqvd6u3iaexp6ojf6k",
                    "navneet.laxmi@gmail.com",
                    "416626",
                    3600)
                ChatScreen()
            }
        }
    }
}


