package com.example.chatapp.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Token
import com.example.chatapp.data.User
import com.example.chatapp.data.UserToken
import com.example.chatapp.data.UserTokenResponse
import com.example.chatapp.repository.UserRepository
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboGroupProfile
import com.mesibo.api.MesiboMessage
import com.mesibo.api.MesiboPresence
import com.mesibo.api.MesiboProfile
import com.mesibo.api.MesiboReadSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel(),
    Mesibo.ConnectionListener, Mesibo.MessageListener,
    Mesibo.PresenceListener,
    Mesibo.ProfileListener, Mesibo.GroupListener {

    private val _userTokenResp = MutableLiveData<Result<String>>()
    val userTokenResp: LiveData<Result<String>> get() = _userTokenResp

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _messages = MutableStateFlow<List<MesiboMessage>>(emptyList())
    val messages: StateFlow<List<MesiboMessage>> = _messages


    private var mRemoteUser: UserTokenResponse? = null
    private var mProfile: MesiboProfile? = null
    private var mReadSession: MesiboReadSession? = null


    fun createUserToken(token: String, address: String, appid: String, expiry: Int) {
        viewModelScope.launch {
            val userTokenRequest = UserToken(
                token = token,
                user = User(
                    address = address,
                    token = Token(
                        apiId = appid,
                        expiry = expiry
                    )
                )
            )

            try {
                val response = repository.createUserToken(userTokenRequest)

                // Wait for the LiveData to emit a value
                response.observeForever { result ->
                    // Parse the response
                    result?.let {
                        if (it.isSuccess) {
                            userTokenResp
                            _userTokenResp.postValue(Result.success(token))
                        } else {
                            _userTokenResp.postValue(Result.failure(Exception("User token creation failed")))
                        }
                    }
                }
            } catch (e: Exception) {
                _userTokenResp.postValue(Result.failure(e))
            }

        }
    }


    fun mesiboInit(user: UserTokenResponse, remoteUser: UserTokenResponse, context: Context) {
        Mesibo.getInstance().init(context.applicationContext)
        Mesibo.addListener(this@MainViewModel)
        Mesibo.setAccessToken(user.user.token)
        Mesibo.setDatabase("mydb")
        Mesibo.start()

        mRemoteUser = remoteUser
        mProfile = Mesibo.getProfile(remoteUser.op)

        Mesibo.setAppInForeground(context,0, true)

        mReadSession = mProfile?.createReadSession(this)
        mReadSession?.enableReadReceipt(true)
        mReadSession?.read(100)
    }


    fun sendTextMessage(message: String) {
        val msg = mProfile?.newMessage()
        msg?.message = message
        msg?.send()
    }


    override fun Mesibo_onConnectionStatus(status: Int) {
        viewModelScope.launch {
            _connectionStatus.value = when (status) {
                Mesibo.STATUS_ONLINE -> "Online"
                Mesibo.STATUS_OFFLINE -> "Offline"
                else -> "Connecting..."
            }
        }
    }


    override fun Mesibo_onMessage(msg: MesiboMessage) {
        viewModelScope.launch {
            if (msg.isIncoming) {
                _messages.value = _messages.value + msg
            }
        }
    }


    override fun Mesibo_onMessageStatus(p0: MesiboMessage) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onMessageUpdate(p0: MesiboMessage) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onPresence(p0: MesiboPresence) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onPresenceRequest(p0: MesiboPresence) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onProfileUpdated(p0: MesiboProfile?) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupCreated(p0: MesiboProfile?) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupJoined(p0: MesiboProfile?) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupLeft(p0: MesiboProfile?) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupMembers(
        p0: MesiboProfile?,
        p1: Array<out MesiboGroupProfile.Member>?
    ) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupMembersJoined(
        p0: MesiboProfile?,
        p1: Array<out MesiboGroupProfile.Member>?
    ) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupMembersRemoved(
        p0: MesiboProfile?,
        p1: Array<out MesiboGroupProfile.Member>?
    ) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupSettings(
        p0: MesiboProfile?,
        p1: MesiboGroupProfile.GroupSettings?,
        p2: MesiboGroupProfile.MemberPermissions?,
        p3: Array<out MesiboGroupProfile.GroupPin>?
    ) {
        TODO("Not yet implemented")
    }

    override fun Mesibo_onGroupError(p0: MesiboProfile?, p1: Long) {
        TODO("Not yet implemented")
    }
}