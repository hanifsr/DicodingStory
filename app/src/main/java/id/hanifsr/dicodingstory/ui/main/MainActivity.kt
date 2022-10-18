package id.hanifsr.dicodingstory.ui.main

import android.content.Context
import android.content.Intent
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
import id.hanifsr.dicodingstory.databinding.ActivityMainBinding
import id.hanifsr.dicodingstory.ui.add.AddActivity
import id.hanifsr.dicodingstory.ui.detail.DetailActivity
import id.hanifsr.dicodingstory.ui.signin.SignInActivity
import id.hanifsr.dicodingstory.util.StoriesAdapter
import id.hanifsr.dicodingstory.util.UserPreference
import id.hanifsr.dicodingstory.viewmodels.MainViewModel
import id.hanifsr.dicodingstory.viewmodels.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var mainViewModel: MainViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

		setupView()
		setupViewModel()
		setupRecyclerView()
		setupAction()
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
		mainViewModel = ViewModelProvider(
			this,
			ViewModelFactory(UserPreference.getInstance(dataStore))
		)[MainViewModel::class.java]

		binding.viewModel = mainViewModel

		mainViewModel.user.observe(this) { user ->
			if (user.isLogin) {
				mainViewModel.getDicodingStories(user.token)
			} else {
				startActivity(Intent(this, SignInActivity::class.java))
				finish()
			}
		}

		mainViewModel.navigateToSelectedStory.observe(this) {
			if (it != null) {
				val intent = Intent(this, DetailActivity::class.java)
				intent.putExtra("story", it)
				startActivity(intent)
			}
		}
	}

	private fun setupRecyclerView() {
		binding.recyclerView.adapter = StoriesAdapter(StoriesAdapter.OnClickListener {
			mainViewModel.displayStoryDetails(it)
		})
	}

	private fun setupAction() {
		binding.signOutButton.setOnClickListener {
			mainViewModel.signOut()
		}

		binding.newStoryFloatingActionButton.setOnClickListener {
			startActivity(Intent(this, AddActivity::class.java))
		}
	}
}