package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.hanifsr.dicodingstory.domain.Status
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.utils.ApiStatus
import id.hanifsr.dicodingstory.utils.UserPreference
import id.hanifsr.dicodingstory.utils.exceptionHandling
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SignInViewModel(private val pref: UserPreference) : ViewModel() {

	private val _apiStatus = MutableLiveData<ApiStatus>()
	val apiStatus: LiveData<ApiStatus>
		get() = _apiStatus

	private val _status = MutableLiveData<Status>()
	val status: LiveData<Status>
		get() = _status

	fun signIn(email: String, password: String) {
		_apiStatus.value = ApiStatus.LOADING
		viewModelScope.launch {
			try {
				val networkSignIn =
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
				_status.value = Status(networkSignIn.error, networkSignIn.message)
				_apiStatus.value = ApiStatus.DONE
			} catch (e: Exception) {
				when (e) {
					is UnknownHostException -> _apiStatus.value = ApiStatus.NO_CONNECTION
					else -> _apiStatus.value = ApiStatus.ERROR
				}
				_status.value = Status(true, exceptionHandling(e))
			}
		}
	}
}