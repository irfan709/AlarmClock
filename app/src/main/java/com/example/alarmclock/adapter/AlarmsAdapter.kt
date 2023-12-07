package com.example.alarmclock.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.R
import com.example.alarmclock.databinding.ItemAlarmBinding
import com.example.alarmclock.interfaces.AlarmClickListener
import com.example.alarmclock.model.AlarmModel

class AlarmsAdapter(
    var alarms: List<AlarmModel>,
) : RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder>() {

    private var listener: AlarmClickListener? = null

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAlarmBinding.bind(itemView)
        val alarmTime: TextView = binding.itemalarmtime
        val recurringDays: TextView = binding.itemrecurringdays
        val recurringImage: ImageView = binding.recurringimg
        val alarmTitle: TextView = binding.itemalarmtitle
        val alarmDay: TextView = binding.itemalarmday
        val alarmOnOrOff: Switch = binding.alarmonoffswitch
        val deleteAlarm: ImageButton = binding.deleteimg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.apply {

            val getAlarmTime = String.format("%02d:%02d", alarm.hour, alarm.minute)
            alarmTime.text = getAlarmTime
            alarmOnOrOff.isChecked = alarm.started

            if (alarm.recurring) {
                recurringImage.setImageResource(R.drawable.repeat)
                recurringDays.text = alarm.getRecurringDaysText()
            } else {
                recurringImage.setImageResource(R.drawable.one_time)
                recurringDays.text = "Once"
            }

            alarmOnOrOff.isChecked = alarm.started

            if (alarm.title.isNotEmpty()) {
                alarmTitle.text = alarm.title
            } else {
                alarmTitle.text = "My alarm"
            }

            if (alarm.recurring) {
                alarmDay.text = alarm.getRecurringDaysText()
            } else {
//                alarmDay.text =
            }

            alarmOnOrOff.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView.isShown or buttonView.isPressed) {
                    listener?.onToggle(alarm)
                }
            }

            deleteAlarm.setOnClickListener {
                listener?.onAlarmDelete(alarm)
            }

            itemView.setOnClickListener {
                listener?.onAlarmClick(alarm)
            }
        }
    }

    fun setListener(listener: AlarmClickListener) {
        this.listener = listener
    }
}