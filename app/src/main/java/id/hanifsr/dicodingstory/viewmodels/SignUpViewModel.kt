package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.network.NetworkSignUp
import id.hanifsr.dicodingstory.util.UserPreference
import kotlinx.coroutines.launch

class SignUpViewModel(private val pref: UserPreference) : ViewModel() {

	private val _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	fun signUp(user: User) {
		viewModelScope.launch {
			try {
				val networkSignUp: NetworkSignUp = DicodingStoryNetwork.dicodingStoryService.signUp(
					user.name,
					user.email,
					user.password
				)
				_message.value = networkSignUp.message
			} catch (e: Exception) {
				_message.value = e.message
			}
		}
	}
}