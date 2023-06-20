package com.jacob.lipsky.giniappstest.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacob.lipsky.giniappstest.R
import com.jacob.lipsky.giniappstest.models.MyPhoto

class PhotoAdapter(val photos: List<MyPhoto>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var photo = photos[position]
        holder.itemView.apply {
            var image = findViewById<ImageView>(R.id.imageView)
            var likeButton = findViewById<TextView>(R.id.likesButton)
            var commentButton = findViewById<TextView>(R.id.commentsButton)

            likeButton.text = "Likes: ${photo.likes}"
            commentButton.text = "Comments: ${photo.comments}"
            image.setImageBitmap(photo.bitmap)
        }

    }
}
