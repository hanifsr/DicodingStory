package id.hanifsr.dicodingstory.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.hanifsr.dicodingstory.R
import id.hanifsr.dicodingstory.domain.Story

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imageUrl: String?) {
	imageUrl?.let {
		val imageUri = imageUrl.toUri().buildUpon().scheme("https").build()
		Log.d("ggwp", "bindImage: imageUri: $imageUri")

		Glide.with(imageView.context)
			.load(imageUri)
			.apply(
				RequestOptions()
					.placeholder(R.drawable.loading_animation)
					.error(R.drawable.ic_baseline_broken_image_24)
			)
			.into(imageView)
	}
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Story>?) {
	val adapter = recyclerView.adapter as StoriesAdapter
	adapter.submitList(data)
}

@BindingAdapter("apiStatus")
fun bindStatus(statusImageView: ImageView, status: ApiStatus?) {
	when (status) {
		ApiStatus.LOADING -> {
			statusImageView.visibility = View.VISIBLE
			statusImageView.setImageResource(R.drawable.loading_animation)
		}

		ApiStatus.ERROR -> {
			statusImageView.visibility = View.VISIBLE
			statusImageView.setImageResource(R.drawable.ic_baseline_cloud_off_24)
		}

		else -> {
			statusImageView.visibility = View.GONE
		}
	}
}