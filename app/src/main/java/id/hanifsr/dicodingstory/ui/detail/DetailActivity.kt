package id.hanifsr.dicodingstory.ui.detail

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.hanifsr.dicodingstory.R
import id.hanifsr.dicodingstory.databinding.ActivityDetailBinding
import id.hanifsr.dicodingstory.domain.Story
import id.hanifsr.dicodingstory.utils.UserPreference
import id.hanifsr.dicodingstory.viewmodels.DetailViewModel
import id.hanifsr.dicodingstory.viewmodels.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {

	private lateinit var binding: ActivityDetailBinding
	private lateinit var detailViewModel: DetailViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

		setupViewModel()
		setupData()
	}

	private fun setupView() {
		@Suppress("DEPRECATION")
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.insetsController?.hide(WindowInsets.Type.statusBars())
		} else {
			window.setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
			)
		}

		supportActionBar?.hide()
		binding.lifecycleOwner = this
	}

	private fun setupViewModel() {
		detailViewModel = ViewModelProvider(
			this,
			ViewModelFactory(UserPreference.getInstance(dataStore))
		)[DetailViewModel::class.java]

		binding.viewModel = detailViewModel
	}

	private fun setupData() {
		val story = intent.getParcelableExtra<Story>("story") as Story
		detailViewModel.getStoryData(story)
	}
}