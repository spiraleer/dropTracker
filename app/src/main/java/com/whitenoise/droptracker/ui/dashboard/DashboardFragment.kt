package com.whitenoise.droptracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.whitenoise.droptracker.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*
import com.whitenoise.droptracker.R

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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
        updateGreeting()

        // Add click listener (does nothing yet)
        binding.addButton.setOnClickListener {
            // Will add functionality later
        }
    }
    private fun updateGreeting() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

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