package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.network.NetworkSignUp
import id.hanifsr.dicodingstory.utils.ApiStatus
import id.hanifsr.dicodingstory.utils.UserPreference
import kotlinx.coroutines.launch

class SignUpViewModel(private val pref: UserPreference) : ViewModel() {

	private val _status = MutableLiveData<ApiStatus>()
	val status: LiveData<ApiStatus>
		get() = _status

	private val _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	fun signUp(user: User) {
		viewModelScope.launch {
			_status.value = ApiStatus.LOADING
			try {
				val networkSignUp: NetworkSignUp = DicodingStoryNetwork.dicodingStoryService.signUp(
					user.name,
					user.email,
					user.password
				)
				_message.value = networkSignUp.message
				_status.value = ApiStatus.DONE
			} catch (e: Exception) {
				_status.value = ApiStatus.ERROR
				_message.value = e.message
			}
		}
	}
}