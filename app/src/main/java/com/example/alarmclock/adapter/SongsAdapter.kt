package com.example.alarmclock.adapter

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.R
import com.example.alarmclock.databinding.ItemSongsBinding
import com.example.alarmclock.fragments.SongsFragmentDirections
import com.example.alarmclock.model.AudioFile

class SongsAdapter(private val context: Context, private val audioFiles: List<AudioFile>, private val onSongSelectedListener: OnSongSelectedListener) :
    RecyclerView.Adapter<SongsAdapter.SongsViewHolder>() {

    interface OnSongSelectedListener {
        fun onSongSelected(audioFile: AudioFile)
    }

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition: Int = -1

    class SongsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSongsBinding.bind(itemView)
        val songName: TextView = binding.songnametv
        val playImg: ImageView = binding.playimg
        val pauseImg: ImageView = binding.pauseimg
        val mainCard: CardView = binding.maincard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false)
        return SongsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return audioFiles.size
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        val audioFile = audioFiles[position]
        holder.apply {
            songName.text = audioFile.name

            playImg.visibility = if (position == currentlyPlayingPosition && mediaPlayer?.isPlaying == true)
                View.GONE
            else
                View.VISIBLE

            pauseImg.visibility = if (position == currentlyPlayingPosition && mediaPlayer?.isPlaying == true)
                View.VISIBLE
            else
                View.GONE

            playImg.setOnClickListener {
                playAudio(audioFile.songResource, position)
            }

            pauseImg.setOnClickListener {
                pauseAudio()
            }

            // Reset background color for all items
            itemView.setBackgroundResource(android.R.color.transparent)

            // Highlight the currently playing item
            if (position == currentlyPlayingPosition) {
                // Update UI to indicate the currently playing item
                itemView.setBackgroundResource(R.color.songcolor)
            }

            itemView.setOnClickListener { view ->
                val transitionName = "recyclerView_${audioFile.id}"
                mainCard.transitionName = transitionName
                onSongSelectedListener.onSongSelected(audioFile)

                val action = SongsFragmentDirections.actionSongsFragmentToAddOrUpdateAlarmFragment()
                val extras = FragmentNavigatorExtras(mainCard to transitionName)
                Navigation.findNavController(view).navigate(action, extras)
            }
        }
    }

    private fun playAudio(songResource: Int, position: Int) {
        stopAudio()

        mediaPlayer = MediaPlayer.create(context, songResource)
        mediaPlayer?.setOnCompletionListener {
            stopAudio()
        }
        mediaPlayer?.start()

        val previousPlayingPosition = currentlyPlayingPosition
        currentlyPlayingPosition = position

        // Notify the adapter to update the items that were previously playing and are currently playing
        if (previousPlayingPosition != -1) {
            notifyItemChanged(previousPlayingPosition)
        }
        notifyItemChanged(currentlyPlayingPosition)
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        notifyItemChanged(currentlyPlayingPosition)
    }

    fun stopAudio() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.release()
        mediaPlayer = null

        val previousPlayingPosition = currentlyPlayingPosition
        currentlyPlayingPosition = -1

        // Notify the adapter to update the item that was previously playing
        if (previousPlayingPosition != -1) {
            notifyItemChanged(previousPlayingPosition)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        stopAudio()
    }
}