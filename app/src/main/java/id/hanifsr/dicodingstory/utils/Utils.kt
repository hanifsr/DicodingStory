package id.hanifsr.dicodingstory.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private val timeStamp: String
	get() = SimpleDateFormat(
		"dd-MMM-yyyy-HH-mm-ss",
		Locale.getDefault()
	).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
	val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
	return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImage: Uri, context: Context): File {
	val contentResolver: ContentResolver = context.contentResolver
	val mFile = createTempFile(context)

	val inputStream = contentResolver.openInputStream(selectedImage) as InputStream
	val outputStream: OutputStream = FileOutputStream(mFile)
	val buf = ByteArray(1024)
	var len: Int
	while (inputStream.read(buf).also { len = it } > 0) {
		outputStream.write(buf, 0, len)
	}
	outputStream.close()
	inputStream.close()

	return mFile
}

fun reduceFileImage(file: File): File {
	val bitmap = BitmapFactory.decodeFile(file.path)
	var compressQuality = 100
	var streamLength: Int
	do {
		val bmpStream = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
		val bmpPicByteArray = bmpStream.toByteArray()
		streamLength = bmpPicByteArray.size
		compressQuality -= 5
	} while (streamLength > 1000000)
	bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

	return file
}

fun String.withDateFormat(): String {
	val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(this) as Date
	return SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(date)
}