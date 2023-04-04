package com.fadalyis.weatherforecastapplication.alert

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.fadalyis.weatherforecastapplication.R
import com.fadalyis.weatherforecastapplication.databinding.FragmentAlertBinding
import com.fadalyis.weatherforecastapplication.db.*
import com.fadalyis.weatherforecastapplication.favorite.TAG
import com.fadalyis.weatherforecastapplication.model.Repository
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.network.AlertApiState
import com.fadalyis.weatherforecastapplication.network.CurrentWeatherClient
import com.fadalyis.weatherforecastapplication.utils.Constants
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment(), OnAlertClickListener {
    lateinit var binding: FragmentAlertBinding
    lateinit var viewModel: AlertViewModel
    private lateinit var viewModelFactory: AlertViewModelFactory
    private lateinit var mAlertAdapter: AlertAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dialog: Dialog
    private lateinit var workManager: WorkManager
    private lateinit var fullFormat: SimpleDateFormat
    private lateinit var fullStartDate: Date
    private lateinit var fullEndDate: Date
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var startDay: TextView
    private lateinit var endDay: TextView
    private lateinit var saveBtn: Button
    private lateinit var startCard: ConstraintLayout
    private lateinit var endCard: ConstraintLayout
    private lateinit var notificationRadioBtn: RadioButton
    private lateinit var alarmRadioBtn: RadioButton
    private lateinit var alertType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        workManager = WorkManager.getInstance(requireActivity().applicationContext)
        fullFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a")
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun checkDrawOverPermission() {
        if (!Settings.canDrawOverlays(context)) {
            val drawOverPermissionIntent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + requireActivity().packageName)
            )
            startActivityForResult(drawOverPermissionIntent, 4000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setupAlertRecyclerView()
        observeAlertState()

        initAlertDialog()
        initStartEndDate()



        binding.alertFab.setOnClickListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        saveBtn.setOnClickListener {
            determineAlertType()
        }

        startCard.setOnClickListener {
            showDatePicker(startDay, startTime, "start")
        }

        endCard.setOnClickListener {
            showDatePicker(endDay, endTime, "end")
        }
    }

    private fun determineAlertType() {
        if (alarmRadioBtn.isChecked) {
            alertType = "Alarm"
            if (Settings.canDrawOverlays(context)) {
                addAlert()
            } else {
                checkDrawOverPermission()
            }
        } else if (notificationRadioBtn.isChecked) {
            alertType = "Notification"
            createNotificationChannel()
            addAlert()
        }
    }

    private fun initStartEndDate() {
        var myCalendar = Calendar.getInstance()
        var calendarDate = myCalendar.time
        val dayFormatter = SimpleDateFormat("dd MMM, yyyy ")
        val timeFormatter = SimpleDateFormat("hh:mm a")
        startDay.text = dayFormatter.format(calendarDate)
        startTime.text = timeFormatter.format(calendarDate)

        fullStartDate = fullFormat.parse(startDay.text.toString() + startTime.text.toString())

        myCalendar.add(Calendar.DAY_OF_YEAR, 1)
        calendarDate = myCalendar.time
        endDay.text = dayFormatter.format(calendarDate)
        endTime.text = timeFormatter.format(calendarDate)

        fullEndDate = fullFormat.parse(endDay.text.toString() + endTime.text.toString())
    }

    private fun initAlertDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.alert_dialog)
        saveBtn = dialog.findViewById(R.id.save_btn)
        startCard = dialog.findViewById(R.id.date_start_constraintLayout)
        endCard = dialog.findViewById(R.id.date_end_constraintLayout)
        startTime = dialog.findViewById(R.id.alert_start_time_tv)
        endTime = dialog.findViewById(R.id.alert_end_time_tv)
        startDay = dialog.findViewById(R.id.alert_start_day_tv)
        endDay = dialog.findViewById(R.id.alert_end_day_tv)
        notificationRadioBtn = dialog.findViewById(R.id.notification_radio_btn)
        alarmRadioBtn = dialog.findViewById(R.id.alarm_radio_btn)
    }

    private fun addAlert() {
        val id = startWorkManager()

        dialog.dismiss()

        viewModel.saveAlert(
            AlertSchedule(
                id.toString(),
                fullStartDate.time,
                fullEndDate.time,
                alertType
            )
        )
    }

    private fun observeAlertState() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.alert.collectLatest { result ->
                when (result) {
                    is AlertApiState.Success -> {
                        Log.i(TAG, "onViewCreated success: ${result.data.size}")
                        withContext(Dispatchers.Main) {
                            if (result.data.isNotEmpty()) {
                                binding.emptyTv.visibility = View.GONE
                            } else {
                                binding.emptyTv.visibility = View.VISIBLE
                            }
                            updateAlertList(result)
                        }
                    }
                    is AlertApiState.Loading -> {
                        Log.i(TAG, "onViewCreated loading: $result")
                    }
                    is AlertApiState.Failure -> {
                        Log.i(TAG, "onViewCreated failure: ${result.msg}")
                    }
                }

            }
        }
    }

    private suspend fun updateAlertList(result: AlertApiState.Success) {
        mAlertAdapter.alertList = result.data
        withContext(Dispatchers.Main) {
            mAlertAdapter.notifyDataSetChanged()
        }
    }

    private fun setupAlertRecyclerView() {
        mAlertAdapter = AlertAdapter(listOf(), this, requireContext())
        mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.alertRecyclerview.apply {
            adapter = mAlertAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun initViewModel() {
        val weatherDao: WeatherDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getWeatherDao()
        }
        val favoriteDao: FavoriteDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getFavoriteDao()
        }
        val alertDao: AlertDAO by lazy {
            val db = AppDataBase.getInstance(requireContext())
            db.getAlertDao()
        }
        viewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                CurrentWeatherClient.getInstance(),
                ConcreteLocalSource(weatherDao, favoriteDao, alertDao)
            )
        )

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(AlertViewModel::class.java)

    }

    private fun showDatePicker(dateTV: TextView, timeTv: TextView, label: String) {
        val picker = MaterialDatePicker.Builder.datePicker()
        val dateValidator = DateValidatorPointForward.now()
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(dateValidator)
        picker.setCalendarConstraints(constraintsBuilder.build())
        val datePicker = picker.build()
        datePicker.show(requireActivity().supportFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd MMM, yyyy")
            val date = dateFormatter.format(Date(it))
            dateTV.text = date
            showTimePicker(timeTv, label)
        }
    }

    private fun showTimePicker(textView: TextView, label: String) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(3)
            .setMinute(30)
            .setTitleText("Pick the time")
            .build()
        timePicker.show(requireActivity().supportFragmentManager, "TimePicker")
        timePicker.addOnPositiveButtonClickListener {
            var hours:String = "1"
            var period = "AM"
             if(timePicker.hour >12){
                 hours = (timePicker.hour-12).toString()
                 period = "PM"
             } else {
                 hours = (timePicker.hour).toString()
                 period = "AM"
             }
            textView.text = "$hours:${timePicker.minute} $period"

            if (label == "start") {
                fullStartDate =
                    fullFormat.parse(startDay.text.toString() + " " + startTime.text.toString())
                Log.i("timmeeee", "onViewCreated start time: ${fullStartDate.time}")
            } else {
                fullEndDate =
                    fullFormat.parse(endDay.text.toString() + " " + endTime.text.toString())
                Log.i("timmeeee", "onViewCreated end time: ${fullEndDate.time}")
            }
        }

        timePicker.addOnDismissListener {
            validateDateStartBeforeEnd()
        }
    }

    private fun validateDateStartBeforeEnd() {
        if (fullEndDate.time < fullStartDate.time) {
            Toast.makeText(
                requireContext(),
                "invalid time! End time is before start time",
                Toast.LENGTH_LONG
            ).show()
            saveBtn.isEnabled = false
        } else
            saveBtn.isEnabled = true
    }

    private fun startWorkManager(): UUID {

        val request = buildPeriodicWorkRequest()

        workManager.enqueue(
            request
        )

        observeWorkState(request)
        return request.id
    }

    private fun buildPeriodicWorkRequest() = PeriodicWorkRequestBuilder<WeatherFetchingWorker>(
        1, TimeUnit.DAYS
    )
        .addTag(Constants.WeatherFetchingWorker_TAG)
        .setInputData(
            workDataOf(
                "alertType" to alertType,
                "endDate" to fullEndDate.time
            )
        )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .setInitialDelay(
            fullStartDate.time - Calendar.getInstance().timeInMillis,
            TimeUnit.MILLISECONDS
        )
        .build()

    private fun observeWorkState(request: PeriodicWorkRequest) {
        workManager.getWorkInfosByTagLiveData(Constants.WeatherFetchingWorker_TAG)
            .observe(requireActivity()) { workInfos ->
                val myInfos = workInfos?.find { it.id == request.id }
                when (myInfos?.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Log.i("Alerts_Fragment_TAG", "Congrats a success")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.i("Alerts_Fragment_TAG", "processing your request ")
                    }
                    WorkInfo.State.FAILED -> {
                        var reason = myInfos.outputData.getString(Constants.FAILURE_REASON)
                        Log.i("Alerts_Fragment_TAG", "falied $reason")
                    }
                    else -> {
                        Log.i("Alerts_Fragment_TAG", "hmmm.... ${myInfos?.state}")
                    }
                }
            }
    }

    override fun deleteAlert(id: String) {
        viewModel.deleteAlert(id)
        workManager.cancelWorkById(UUID.fromString(id))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "AlertsNotificationChannel"
            val description: String = "Channel for weather Alerts"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alertsChannel", name, importance)
            channel.description = description

            val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
    }

}