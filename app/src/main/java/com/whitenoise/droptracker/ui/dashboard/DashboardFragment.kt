package com.whitenoise.droptracker.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.whitenoise.droptracker.R
import com.whitenoise.droptracker.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import android.graphics.Color

class DashboardFragment : Fragment() {

    private val DAILY_GOAL = 2000
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var todayTotal: Int = 0
    private lateinit var sharedPreferences: SharedPreferences

    // Keys for saving data
    companion object {
        private const val PREFS_NAME = "WaterTrackerPrefs"
        private const val KEY_TODAY_TOTAL = "today_total"
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_LAST_AMOUNT = "last_amount"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Load saved data or reset if it's a new day
        loadOrResetData()

        updateGreeting()
        binding.addButton.setOnClickListener {
            showAddWaterDialog()
        }

        // Initialize progress circle
        binding.progressCircle.progress = 0
        binding.progressCircle.max = 100

        updateTotalDisplay()
    }

    @SuppressLint("StringFormatInvalid")
    private fun loadOrResetData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = sharedPreferences.getString(KEY_LAST_DATE, "")

        if (lastDate != today) {
            // It's a new day - reset counter and last amount
            todayTotal = 0
            binding.lastAddedText.text = getString(R.string.last_added_default)
            saveData()
        } else {
            // Load yesterday's data
            todayTotal = sharedPreferences.getInt(KEY_TODAY_TOTAL, 0)
            val lastAmount = sharedPreferences.getInt(KEY_LAST_AMOUNT, 0)
            if (lastAmount > 0) {
                binding.lastAddedText.text = getString(R.string.last_added, lastAmount)
            }
        }
    }

    @SuppressLint("UseKtx")
    private fun saveData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        with(sharedPreferences.edit()) {
            putInt(KEY_TODAY_TOTAL, todayTotal)
            putString(KEY_LAST_DATE, today)
            apply()
        }
    }

    private fun showAddWaterDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_water, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set up button click listeners
        dialogView.findViewById<View>(R.id.btnSmall).setOnClickListener {
            addWater(250)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnMedium).setOnClickListener {
            addWater(500)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnLarge).setOnClickListener {
            addWater(750)
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("StringFormatInvalid", "UseKtx")
    private fun addWater(amount: Int) {
        val previousTotal = todayTotal
        todayTotal += amount

        // Save last amount
        with(sharedPreferences.edit()) {
            putInt(KEY_LAST_AMOUNT, amount)
            apply()
        }

        saveData()
        updateTotalDisplay()

        // Update last added display
        binding.lastAddedText.text = getString(R.string.last_added, amount)

        // Determine which message to show
        when {
            previousTotal < 2000 && todayTotal >= 2000 -> {
                // First time reaching goal
                Snackbar.make(binding.root, getString(R.string.goal_reached), Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.purple_500))
                    .show()
            }
            todayTotal >= 2000 -> {
                // Already over goal - encouraging message
                Snackbar.make(binding.root, getString(R.string.goal_crushing, amount), Snackbar.LENGTH_SHORT)                    .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.purple_500))
                    .show()
            }
            else -> {
                // Under goal - regular confirmation
                Snackbar.make(binding.root, getString(R.string.water_added, amount), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.undo)) {
                        todayTotal -= amount
                        saveData()
                        updateTotalDisplay()
                        binding.lastAddedText.text = getString(R.string.last_added_default)
                    }
                    .show()
            }
        }
    }

    private fun updateTotalDisplay() {
        binding.waterCounterText.text = getString(R.string.today_total, todayTotal)

        // Update progress circle
        val progress = (todayTotal.toFloat() / DAILY_GOAL * 100).coerceAtMost(100f)
        binding.progressCircle.progress = progress.toInt()

        // Update percentage text
        binding.progressText.text = "${progress.toInt()}%"

        // Change color based on progress
        val color = when {
            progress >= 100 -> Color.GREEN
            progress >= 75 -> Color.YELLOW
            else -> ContextCompat.getColor(requireContext(), R.color.purple_500)
        }
        binding.progressCircle.setIndicatorColor(color)
    }

    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        val greetingRes = when (currentHour) {
            in 5..11 -> R.string.greeting_morning
            in 12..17 -> R.string.greeting_afternoon
            in 18..21 -> R.string.greeting_evening
            else -> R.string.greeting_night
        }
        binding.greetingText.text = getString(greetingRes)
    }

    override fun onResume() {
        super.onResume()
        updateGreeting()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}