package com.example.alarmclock.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.R
import com.example.alarmclock.adapter.SongsAdapter
import com.example.alarmclock.databinding.FragmentSongsBinding
import com.example.alarmclock.model.AudioFile

class SongsFragment : Fragment() {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val audioFiles = getAudioFiles()

        songsAdapter = SongsAdapter(
            requireContext(),
            audioFiles,
            object : SongsAdapter.OnSongSelectedListener {
                override fun onSongSelected(audioFile: AudioFile) {
                    val result = Bundle().apply {
                        putSerializable("selectedAudio", audioFile)
                    }
                    parentFragmentManager.setFragmentResult("requestKey", result)
                }
            })

        binding.songsrv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.songsrv.adapter = songsAdapter
    }

    @SuppressLint("DiscouragedApi")
    private fun getAudioFiles(): List<AudioFile> {
        // Use context.resources to access resources
        val resources = context?.resources
        val audioFileNames = resources?.getStringArray(R.array.audio_file_names)

        val audioFiles = mutableListOf<AudioFile>()

        audioFileNames?.forEachIndexed { index, fileName ->
            val resourceId = resources.getIdentifier(
                "raw/${fileName}",
                null,
                context?.packageName
            )
            if (resourceId != 0) {
                audioFiles.add(AudioFile(0, fileName, resourceId))
            }
        }

        return audioFiles
    }

    private fun onSongSelected(selectedAudio: AudioFile) {
        val result = Bundle().apply {
            putSerializable("selectedAudio", selectedAudio)
        }
        parentFragmentManager.setFragmentResult("requestKey", result)
        // Optional: Navigate back to the previous fragment (AddOrUpdateAlarmFragment)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        songsAdapter.stopAudio()
    }
}