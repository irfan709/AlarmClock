package com.example.alarmclock.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.alarmclock.R
import com.example.alarmclock.databinding.FragmentAddOrUpdateAlarmBinding
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.model.AudioFile
import com.example.alarmclock.utils.getDay
import com.example.alarmclock.utils.getTimePickerHour
import com.example.alarmclock.utils.getTimePickerMinute
import com.example.alarmclock.viewmodel.AlarmViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

class AddOrUpdateAlarmFragment : Fragment() {

    private lateinit var binding: FragmentAddOrUpdateAlarmBinding
    private lateinit var alarmViewModel: AlarmViewModel
    private var alarm: AlarmModel? = null
    private var isVibrate: Boolean = false
    private lateinit var audio: AudioFile
    private var songResourceId: Int = 0

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            alarm = it.getSerializable(getString(R.string.alarm_object)) as AlarmModel?
//            alarm = it.getSerializable(getString(R.string.alarm_object), AlarmModel::class.java)
        }
        alarmViewModel = ViewModelProvider(this)[AlarmViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddOrUpdateAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (navController.currentDestination?.id != R.id.alarmsListFragment) {
                navController.popBackStack(R.id.alarmsListFragment, false)
            } else {
                activity?.finish()
            }
        }

        parentFragmentManager.setFragmentResultListener(
            "requestKey",
            viewLifecycleOwner
        ) { _, result ->
            val selectedAudio = result.getSerializable("selectedAudio") as? AudioFile
//            val selectedAudio = result.getSerializable("selectedAudio", AudioFile::class.java)
            selectedAudio?.let {
                audio = selectedAudio
                binding.songnametv.text = audio.name.uppercase()
                songResourceId = selectedAudio.songResource
                binding.songresourcetv.text = songResourceId.toString()
            }
        }

        if (alarm != null) {
            showAlarmDetails(alarm!!)
        }

        binding.recurringcheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.recurringoptions.visibility = View.VISIBLE
            } else {
                binding.recurringoptions.visibility = View.GONE
            }
        }

        binding.vibrateswitch.setOnCheckedChangeListener { _, b ->
            isVibrate = b
        }

        binding.selectsonglayout.setOnClickListener {
            navController.navigate(AddOrUpdateAlarmFragmentDirections.actionAddOrUpdateAlarmFragmentToSongsFragment())
        }

        binding.timepickerview.setOnTimeChangedListener { view, hourOfDay, minute ->
            binding.alarmtimetv.text = requireView().getDay(
                requireView().getTimePickerHour(binding.timepickerview),
                requireView().getTimePickerMinute(binding.timepickerview)
            )
            binding.timepickerview.hour = hourOfDay
            binding.timepickerview.minute = minute
        }

        binding.setalarmbtn.setOnClickListener {
            if (alarm != null) {
                scheduleAlarm()
            } else {
                scheduleAlarm()
            }

            Navigation.findNavController(it)
                .navigate(AddOrUpdateAlarmFragmentDirections.actionAddOrUpdateAlarmFragmentToAlarmsListFragment())
        }
    }

    private fun showAlarmDetails(alarm: AlarmModel) {
        binding.alarmtitleet.setText(alarm.title)
        binding.alarmtimetv.text = requireView().getDay(
            requireView().getTimePickerHour(binding.timepickerview),
            requireView().getTimePickerMinute(binding.timepickerview)
        )
        binding.timepickerview.hour = alarm.hour
        binding.timepickerview.minute = alarm.minute

        if (alarm.recurring) {
            binding.recurringcheckbox.isChecked = true
            binding.recurringoptions.visibility =
                View.VISIBLE
            if (alarm.monday)
                binding.mondaycheckbox.isChecked = true
            if (alarm.tuesday)
                binding.tuesdaycheckbox.isChecked = true
            if (alarm.wednesday)
                binding.wednesdaycheckbox.isChecked = true
            if (alarm.thursday)
                binding.thursdaycheckbox.isChecked = true
            if (alarm.friday)
                binding.fridaycheckbox.isChecked = true
            if (alarm.saturday)
                binding.saturdaycheckbox.isChecked = true
            if (alarm.sunday)
                binding.sundaycheckbox.isChecked = true

            songResourceId = alarm.alarmTone

            if (alarm.vibrate) {
                binding.vibrateswitch.isChecked = true
            }
        }
    }

    private fun scheduleAlarm() {
        var alarmTitle = getString(R.string.alarm_title)

        val alarmId = alarm?.alarmId ?: Random.nextInt(Int.MAX_VALUE)

        if (binding.alarmtitleet.text.isNotEmpty()) {
            alarmTitle = binding.alarmtitleet.text.toString()
        }

        val newAlarm = AlarmModel(
            alarmId,
            alarmTitle,
            requireView().getTimePickerHour(binding.timepickerview),
            requireView().getTimePickerMinute(binding.timepickerview),
            songResourceId,
            started = true,
            binding.recurringcheckbox.isChecked,
            isVibrate,
            binding.mondaycheckbox.isChecked,
            binding.tuesdaycheckbox.isChecked,
            binding.wednesdaycheckbox.isChecked,
            binding.thursdaycheckbox.isChecked,
            binding.fridaycheckbox.isChecked,
            binding.saturdaycheckbox.isChecked,
            binding.sundaycheckbox.isChecked
        )

        lifecycleScope.launch {
            newAlarm.let {
                alarmViewModel.insertOrUpdate(newAlarm)
                newAlarm.schedule(requireContext())
            }
        }
    }
}