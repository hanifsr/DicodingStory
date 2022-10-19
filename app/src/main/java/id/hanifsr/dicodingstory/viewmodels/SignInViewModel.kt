package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.network.NetworkSignIn
import id.hanifsr.dicodingstory.utils.UserPreference
import kotlinx.coroutines.launch

class SignInViewModel(private val pref: UserPreference) : ViewModel() {

	private val _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	fun signIn(email: String, password: String) {
		viewModelScope.launch {
			try {
				val networkSignIn: NetworkSignIn =
					DicodingStoryNetwork.dicodingStoryService.signIn(email, password)
				if (!networkSignIn.error) {
					pref.saveUser(
						User(
							networkSignIn.loginResult.userId,
							email,
							password,
							networkSignIn.loginResult.name,
							networkSignIn.loginResult.token,
							true
						)
					)
				}
				_message.value = networkSignIn.message
			} catch (e: Exception) {
				_message.value = e.message
			}
		}
	}
}