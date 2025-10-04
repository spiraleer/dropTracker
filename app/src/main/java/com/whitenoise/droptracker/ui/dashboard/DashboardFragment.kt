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

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var todayTotal: Int = 0
    private lateinit var sharedPreferences: SharedPreferences

    // Keys for saving data
    companion object {
        private const val PREFS_NAME = "WaterTrackerPrefs"
        private const val KEY_TODAY_TOTAL = "today_total"
        private const val KEY_LAST_DATE = "last_date"
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
        updateTotalDisplay()
    }

    private fun loadOrResetData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = sharedPreferences.getString(KEY_LAST_DATE, "")

        if (lastDate != today) {
            // It's a new day - reset counter
            todayTotal = 0
            saveData()
        } else {
            // Load yesterday's data
            todayTotal = sharedPreferences.getInt(KEY_TODAY_TOTAL, 0)
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

    private fun addWater(amount: Int) {
        todayTotal += amount
        saveData() // Save immediately after adding water
        updateTotalDisplay()

        // Show confirmation snackbar
        Snackbar.make(binding.root, getString(R.string.water_added, amount), Snackbar.LENGTH_SHORT)
            .setAction("Undo") {
                // Undo functionality
                todayTotal -= amount
                saveData() // Save after undo too
                updateTotalDisplay()
            }
            .show()
    }

    private fun updateTotalDisplay() {
        binding.waterCounterText.text = getString(R.string.today_total, todayTotal)
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