package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.hanifsr.dicodingstory.domain.Story
import id.hanifsr.dicodingstory.utils.UserPreference

class DetailViewModel(private val pref: UserPreference) : ViewModel() {

	private val _story = MutableLiveData<Story>()
	val story: LiveData<Story>
		get() = _story

	fun getStoryData(story: Story) {
		_story.value = story
	}
}