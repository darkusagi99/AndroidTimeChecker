package com.gmail.darkusagi99.androidtimechecker

import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TimeCheckerActivity : AppCompatActivity() {

    private var morningStartDate : Date = Calendar.getInstance().time
    private var morningEndDate : Date = Calendar.getInstance().time
    private var afternoonStartDate : Date = Calendar.getInstance().time
    private val dureeJournee = 480

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_time_checker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val outpoutField = view.tag.toString()

        val btnField = findViewById<Button>(resources.getIdentifier(outpoutField, "id", packageName))
        val morningDurationField = findViewById<TextView>(R.id.morningDuration)
        val lunchDurationField = findViewById<TextView>(R.id.lunchDuration)
        val afternoonEndTime = findViewById<TextView>(R.id.afternoonEndTime)


        val tpd = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener(function = { v, h, m ->

            // extraction de la date
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            cal.set(Calendar.SECOND, 0)

            // extraction de la date
            when (outpoutField) {
                "morningStartButton" -> morningStartDate = cal.time
                "morningEndButton" -> morningEndDate = cal.time
                else -> afternoonStartDate = cal.time
            }

            // Recalcul des différents champs.
            val morningMinutes = (morningEndDate.time - morningStartDate.time) / 60000
            val launchMinutes = (afternoonStartDate.time - morningEndDate.time) / 60000
            morningDurationField.text = getString(R.string.morningDurationText, (morningMinutes/60), (morningMinutes%60))
            lunchDurationField.text = getString(R.string.lunchDurationText, (launchMinutes/60), (launchMinutes%60))

            var tempsRestant = (dureeJournee - morningMinutes).toInt()
            if (launchMinutes < 30) { tempsRestant += (30 - launchMinutes).toInt();}
            val calEndDay = Calendar.getInstance()
            calEndDay.time = afternoonStartDate
            calEndDay.add(Calendar.MINUTE, tempsRestant)

            afternoonEndTime.text = getString(R.string.afternoonEndTimeText, calEndDay.time)

            // Mise à jour du texte du bouton
            btnField.text = SimpleDateFormat("HH:mm").format(cal.time)

        }),hour,minute,false)

        tpd.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
