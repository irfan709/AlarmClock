package com.example.alarmclock.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.alarmclock.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        updateTimeRunnable = Runnable { updateDateTime() }
        handler.post(updateTimeRunnable)

        binding.alarmscard.setOnClickListener {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToAlarmsListFragment())
        }
    }

    private fun updateDateTime() {
        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        binding.datetv.text = currentDate
        binding.timetv.text = currentTime

        handler.postDelayed(updateTimeRunnable, 1000)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(updateTimeRunnable)
        super.onDestroyView()
    }
}