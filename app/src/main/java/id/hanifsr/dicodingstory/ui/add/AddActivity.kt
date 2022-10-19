package id.hanifsr.dicodingstory.ui.add

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import id.hanifsr.dicodingstory.R
import id.hanifsr.dicodingstory.databinding.ActivityAddBinding
import id.hanifsr.dicodingstory.domain.User
import id.hanifsr.dicodingstory.utils.UserPreference
import id.hanifsr.dicodingstory.utils.createTempFile
import id.hanifsr.dicodingstory.utils.reduceFileImage
import id.hanifsr.dicodingstory.utils.uriToFile
import id.hanifsr.dicodingstory.viewmodels.AddViewModel
import id.hanifsr.dicodingstory.viewmodels.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddActivity : AppCompatActivity() {

	companion object {
		private val REQUIRED_PERMISSION = arrayOf(CAMERA)
		private const val REQUEST_CODE_PERMISSION = 10
	}

	private lateinit var binding: ActivityAddBinding
	private lateinit var addViewModel: AddViewModel
	private lateinit var currentPhotoPath: String
	private lateinit var user: User

	private var file: File? = null
	private val launcherIntentCamera = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == RESULT_OK) {
			val mFile = File(currentPhotoPath)
			file = mFile
			val result = BitmapFactory.decodeFile(mFile.path)
			binding.previewImageView.setImageBitmap(result)
		}
	}

	private val launcherIntentGallery = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) {
		if (it.resultCode == RESULT_OK) {
			val selectedImage: Uri = it.data?.data as Uri
			val mFile = uriToFile(selectedImage, this@AddActivity)
			file = mFile
			binding.previewImageView.setImageURI(selectedImage)
		}
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if (!allPermissionGranted()) {
				Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
					.show()
				finish()
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_add)

		setupView()
		setupViewModel()
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

		if (!allPermissionGranted()) {
			ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
		}
	}

	private fun setupViewModel() {
		addViewModel = ViewModelProvider(
			this,
			ViewModelFactory(UserPreference.getInstance(dataStore))
		)[AddViewModel::class.java]

		addViewModel.user.observe(this) {
			user = it
		}

		addViewModel.message.observe(this) {
			if (it.equals("success", true)) {
				Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show()
			} else {
				Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun setupAction() {
		binding.cameraButton.setOnClickListener { startTakePhoto() }
		binding.galleryButton.setOnClickListener { startGallery() }
		binding.uploadButton.setOnClickListener { uploadImage() }
	}

	private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
		ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
	}

	private fun startTakePhoto() {
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
		intent.resolveActivity(packageManager)

		createTempFile(application).also {
			val photoUri: Uri = FileProvider.getUriForFile(
				this@AddActivity,
				"id.hanifsr.dicodingstory",
				it
			)
			currentPhotoPath = it.absolutePath
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
			launcherIntentCamera.launch(intent)
		}
	}

	private fun startGallery() {
		val intent = Intent()
		intent.action = ACTION_GET_CONTENT
		intent.type = "image/*"
		val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
		launcherIntentGallery.launch(chooser)
	}

	private fun uploadImage() {
		if (file != null) {
			val mFile = reduceFileImage(file as File)

			val description = binding.descriptionEditText.text.toString()
				.toRequestBody("text/plain".toMediaType())
			val requestImageFile = mFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
			val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
				"photo",
				mFile.name,
				requestImageFile
			)

			addViewModel.uploadStory(user.token, description, imageMultipart)
		} else {
			Toast.makeText(this, getString(R.string.upload_warning), Toast.LENGTH_SHORT).show()
		}
	}
}