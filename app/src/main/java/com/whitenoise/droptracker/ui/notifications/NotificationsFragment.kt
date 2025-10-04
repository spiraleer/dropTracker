package com.whitenoise.droptracker.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.whitenoise.droptracker.databinding.FragmentNotificationsBinding
import com.whitenoise.droptracker.R

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update the text to use the string resource
        binding.textNotifications.text = getString(R.string.notifications_empty_message)   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}