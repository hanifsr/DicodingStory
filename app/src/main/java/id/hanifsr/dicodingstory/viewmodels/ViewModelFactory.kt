package id.hanifsr.dicodingstory.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.hanifsr.dicodingstory.util.UserPreference

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		return when {
			modelClass.isAssignableFrom(AddViewModel::class.java) -> {
				AddViewModel(pref) as T
			}
			modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
				DetailViewModel(pref) as T
			}
			modelClass.isAssignableFrom(MainViewModel::class.java) -> {
				MainViewModel(pref) as T
			}
			modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
				SignInViewModel(pref) as T
			}
			modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
				SignUpViewModel(pref) as T
			}
			else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
		}
	}
}