package com.example.alarmclock.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.R
import com.example.alarmclock.adapter.AlarmsAdapter
import com.example.alarmclock.databinding.FragmentAlarmsListBinding
import com.example.alarmclock.interfaces.AlarmClickListener
import com.example.alarmclock.model.AlarmModel
import com.example.alarmclock.viewmodel.AlarmsListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmsListFragment : Fragment(), AlarmClickListener {

    private lateinit var binding: FragmentAlarmsListBinding
    private lateinit var alarmsAdapter: AlarmsAdapter
    private val alarmsViewModel: AlarmsListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (navController.currentDestination?.id != R.id.homeFragment) {
                navController.popBackStack(R.id.homeFragment, false)
            } else {
                activity?.finish()
            }
        }

        alarmsAdapter = AlarmsAdapter(emptyList())
        alarmsAdapter.setListener(this)
        binding.alarmslistrv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.alarmslistrv.adapter = alarmsAdapter

        binding.addalarmfab.setOnClickListener {
            navController.navigate(AlarmsListFragmentDirections.actionAlarmsListFragmentToAddOrUpdateAlarmFragment())
        }

        alarmsViewModel.getAllAlarms().observe(viewLifecycleOwner) { alarms ->
            alarmsAdapter.alarms = alarms
            alarmsAdapter.notifyDataSetChanged()
        }
    }

    override fun onAlarmClick(alarm: AlarmModel) {
        if (alarm.started) {
            context?.let { alarm.cancelAlarm(it) }
        }
        val args = Bundle().apply {
            putSerializable(getString(R.string.alarm_object), alarm)
        }

        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_alarmsListFragment_to_addOrUpdateAlarmFragment, args)
        }
    }

    override fun onAlarmDelete(alarm: AlarmModel) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (alarm.started)
                context?.let { alarm.cancelAlarm(it) }
            alarmsViewModel.delete(alarm.alarmId)
        }
    }

    override fun onToggle(alarm: AlarmModel) {
        if (alarm.started) {
            context?.let { alarm.cancelAlarm(it) }
            lifecycleScope.launch(Dispatchers.Main) {
                alarmsViewModel.update(alarm)
            }
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                context?.let { alarm.schedule(it) }
                alarmsViewModel.update(alarm)
            }
        }
    }
}