package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.*
import id.hanifsr.dicodingstory.domain.Story
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.network.DicodingStoryNetwork
import id.hanifsr.dicodingstory.network.asDomainModel
import id.hanifsr.dicodingstory.util.ApiStatus
import id.hanifsr.dicodingstory.util.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference) : ViewModel() {

	val user: LiveData<User> = pref.getUser().asLiveData()

	private val _status = MutableLiveData<ApiStatus>()
	val status: LiveData<ApiStatus>
		get() = _status

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
		viewModelScope.launch {
			_status.value = ApiStatus.LOADING
			try {
				_stories.value =
					DicodingStoryNetwork.dicodingStoryService.getAllStories("Bearer $token")
						.asDomainModel()
				_status.value = ApiStatus.DONE
			} catch (e: Exception) {
				_status.value = ApiStatus.ERROR
				_stories.value = ArrayList()
			}
		}
	}


	fun displayStoryDetails(story: Story) {
		_navigateToSelectedStory.value = story
	}
}