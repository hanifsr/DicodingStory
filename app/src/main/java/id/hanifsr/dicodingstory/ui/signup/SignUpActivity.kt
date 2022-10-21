package id.hanifsr.dicodingstory.ui.signup

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.hanifsr.dicodingstory.R
import id.hanifsr.dicodingstory.databinding.ActivitySignupBinding
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.ui.signin.SignInActivity
import id.hanifsr.dicodingstory.utils.UserPreference
import id.hanifsr.dicodingstory.viewmodels.SignUpViewModel
import id.hanifsr.dicodingstory.viewmodels.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignUpActivity : AppCompatActivity() {

	private lateinit var binding: ActivitySignupBinding
	private lateinit var signUpViewModel: SignUpViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

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
	}

	private fun setupViewModel() {
		signUpViewModel = ViewModelProvider(
			this,
			ViewModelFactory(UserPreference.getInstance(dataStore))
		)[SignUpViewModel::class.java]

		signUpViewModel.message.observe(this) { message ->
			if (message.equals("User created", true)) {
				AlertDialog.Builder(this).apply {
					setTitle(getString(R.string.success))
					setMessage(getString(R.string.sign_up_success))
					setPositiveButton(getString(R.string.okay)) { _, _ ->
						startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
						finish()
					}
					create()
					show()
				}
			} else {
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun setupAction() {
		binding.signInTextView.setOnClickListener {
			startActivity(Intent(this, SignInActivity::class.java))
			finish()
		}

		binding.signUpButton.setOnClickListener {
			val name = binding.nameEditText.text.toString()
			val email = binding.emailEditText.text.toString()
			val password = binding.passwordEditText.text.toString()
			when {
				name.isEmpty() -> {
					binding.nameEditTextLayout.error = getString(R.string.empty_name)
				}
				email.isEmpty() -> {
					binding.emailEditTextLayout.error = getString(R.string.empty_email)
				}
				password.isEmpty() -> {
					binding.passwordEditTextLayout.error = getString(R.string.empty_password)
				}
				else -> {
					signUpViewModel.signUp(
						User(
							"",
							email,
							password,
							name,
							"",
							false
						)
					)
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

		val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
		val name =
			ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
		val email =
			ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
		val password =
			ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
		val signUp = ObjectAnimator.ofFloat(binding.signUpButton, View.ALPHA, 1f).setDuration(250)
		val signInMessage =
			ObjectAnimator.ofFloat(binding.signInMessageTextView, View.ALPHA, 1f).setDuration(250)
		val signIn = ObjectAnimator.ofFloat(binding.signInTextView, View.ALPHA, 1f).setDuration(250)

		AnimatorSet().apply {
			playSequentially(
				title, name, email, password, signUp, signInMessage, signIn
			)
			startDelay = 500
			start()
		}
	}
}