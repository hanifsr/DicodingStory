package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.*
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.utils.UserPreference
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val pref: UserPreference) : ViewModel() {

	val user: LiveData<User> = pref.getUser().asLiveData()

	private val _message = MutableLiveData<String>()
	val message: LiveData<String>
		get() = _message

	fun uploadStory(token: String, description: RequestBody, file: MultipartBody.Part) {
		viewModelScope.launch {
			try {
				val upload = DicodingStoryNetwork.dicodingStoryService.addNewStory(
					"Bearer $token",
					description,
					file
				)
				_message.value = upload.message
			} catch (e: Exception) {
				_message.value = e.message
			}
		}
	}

}