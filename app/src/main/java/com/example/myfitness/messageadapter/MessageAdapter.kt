package com.example.myfitness.messageadapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfitness.R
import com.example.myfitness.ui.UserDetailsActivity

class MessageAdapter(
    private val context: Context,
    private val messages: MutableList<UserDetailsActivity.Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        const val MESSAGE_TYPE_TEXT = 1
        const val MESSAGE_TYPE_IMAGE = 2
        const val MESSAGE_TYPE_VIDEO = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_TEXT -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_message_text, parent, false)
                TextMessageViewHolder(view)
            }
            MESSAGE_TYPE_IMAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_message_image, parent, false)
                ImageMessageViewHolder(view)
            }
            MESSAGE_TYPE_VIDEO -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_message_video, parent, false)
                VideoMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is TextMessageViewHolder -> holder.bindTextMessage(message)
            is ImageMessageViewHolder -> holder.bindImageMessage(message)
            is VideoMessageViewHolder -> holder.bindVideoMessage(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.text.isNotBlank() -> MESSAGE_TYPE_TEXT
            message.imageUrl.isNotBlank() -> MESSAGE_TYPE_IMAGE
            message.videoUrl.isNotBlank() -> MESSAGE_TYPE_VIDEO
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    fun addMessage(message: UserDetailsActivity.Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun addAllMessages(messages: List<UserDetailsActivity.Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class TextMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)

        fun bindTextMessage(message: UserDetailsActivity.Message) {
            messageTextView.text = message.text
        }
    }

    inner class ImageMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageMessage)

        fun bindImageMessage(message: UserDetailsActivity.Message) {
            Glide.with(context)
                .load(message.imageUrl)
                .into(imageView)
        }
    }

    inner class VideoMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val videoView: ImageView = itemView.findViewById(R.id.videoMessage)

        fun bindVideoMessage(message: UserDetailsActivity.Message) {
            // Placeholder logic for video thumbnail or loading the video
            // Here we can load a placeholder image or a play icon for the video
            // You can use libraries like Glide or ExoPlayer for loading video thumbnails or playing videos
            Glide.with(context)
                .load(Uri.parse("android.resource://${context.packageName}/${R.drawable.ic_play_circle_24}")) // Placeholder icon
                .into(videoView)
        }
    }
}
