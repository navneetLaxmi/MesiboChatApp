package com.example.chatapp.Screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatapp.viewModel.MainViewModel

@Composable
fun ChatScreen(viewModel: MainViewModel = viewModel()) {
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val messages by viewModel.messages.collectAsState()


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(text = "Connection Status: $connectionStatus")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Text(text = "${msg.profile?.nameOrAddress}: ${msg.message}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var messageText by remember { mutableStateOf("") }
        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Enter message") }
        )

        Button(
            onClick = {
                viewModel.sendTextMessage(messageText)
                messageText = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
