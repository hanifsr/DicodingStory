package id.hanifsr.dicodingstory.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.hanifsr.dicodingstory.R
import id.hanifsr.dicodingstory.databinding.ActivitySigninBinding
import id.hanifsr.dicodingstory.ui.main.MainActivity
import id.hanifsr.dicodingstory.ui.signup.SignUpActivity
import id.hanifsr.dicodingstory.utils.UserPreference
import id.hanifsr.dicodingstory.viewmodels.SignInViewModel
import id.hanifsr.dicodingstory.viewmodels.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignInActivity : AppCompatActivity() {

	private lateinit var binding: ActivitySigninBinding
	private lateinit var signInViewModel: SignInViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)

		setupView()
		setupViewModel()
		setupAction()
		setupAnimation()
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
		signInViewModel = ViewModelProvider(
			this,
			ViewModelFactory(UserPreference.getInstance(dataStore))
		)[SignInViewModel::class.java]

		binding.viewModel = signInViewModel

		signInViewModel.status.observe(this) {
			if (!it.error) {
				startActivity(Intent(this, MainActivity::class.java))
				finish()
			}
			Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
		}
	}

	private fun setupAction() {
		binding.signUpTextView.setOnClickListener {
			startActivity(Intent(this, SignUpActivity::class.java))
			finish()
		}

		binding.signInButton.setOnClickListener {
			val email = binding.emailEditText.text.toString()
			val password = binding.passwordEditText.text.toString()
			when {
				email.isEmpty() -> {
					binding.emailEditTextLayout.error = getString(R.string.empty_email)
				}
				password.isEmpty() -> {
					binding.passwordEditTextLayout.error = getString(R.string.empty_password)
				}
				else -> {
					signInViewModel.signIn(email, password)
				}
			}
		}
	}

	private fun setupAnimation() {
		ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
			duration = 6000
			repeatCount = ObjectAnimator.INFINITE
			repeatMode = ObjectAnimator.REVERSE
		}.start()

		val email =
			ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
		val password =
			ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
		val signIn = ObjectAnimator.ofFloat(binding.signInButton, View.ALPHA, 1f).setDuration(250)
		val signUpMessage =
			ObjectAnimator.ofFloat(binding.signUpMessageTextView, View.ALPHA, 1f).setDuration(250)
		val signUp = ObjectAnimator.ofFloat(binding.signUpTextView, View.ALPHA, 1f).setDuration(250)

		AnimatorSet().apply {
			playSequentially(
				email, password, signIn, signUpMessage, signUp
			)
			startDelay = 500
			start()
		}
	}
}