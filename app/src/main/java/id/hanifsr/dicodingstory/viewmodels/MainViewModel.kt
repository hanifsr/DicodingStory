package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.*
import id.hanifsr.dicodingstory.domain.Story
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.network.asDomainModel
import id.hanifsr.dicodingstory.utils.ApiStatus
import id.hanifsr.dicodingstory.utils.UserPreference
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MainViewModel(private val pref: UserPreference) : ViewModel() {

	val user: LiveData<User> = pref.getUser().asLiveData()

	private val _apiStatus = MutableLiveData<ApiStatus>()
	val apiStatus: LiveData<ApiStatus>
		get() = _apiStatus

	private val _stories = MutableLiveData<List<Story>>()
	val stories: LiveData<List<Story>>
		get() = _stories

	private val _navigateToSelectedStory = MutableLiveData<Story>()
	val navigateToSelectedStory: LiveData<Story>
		get() = _navigateToSelectedStory

	fun signOut() {
		viewModelScope.launch {
			pref.deleteUser()
		}
	}

	fun getDicodingStories(token: String) {
		_apiStatus.value = ApiStatus.LOADING
		viewModelScope.launch {
			try {
				_stories.value =
					DicodingStoryNetwork.dicodingStoryService.getAllStories("Bearer $token")
						.asDomainModel()
				_apiStatus.value = ApiStatus.DONE
			} catch (e: Exception) {
				when (e) {
					is UnknownHostException -> _apiStatus.value = ApiStatus.NO_CONNECTION
					else -> _apiStatus.value = ApiStatus.ERROR
				}
				_stories.value = ArrayList()
			}
		}
	}


	fun displayStoryDetails(story: Story) {
		_navigateToSelectedStory.value = story
	}
}