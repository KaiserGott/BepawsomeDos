package com.example.bepawsomedos.ui.configuracion

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.bepawsomedos.R
import com.example.bepawsomedos.databinding.FragmentConfiguracionBinding


class ConfiguracionFragment : Fragment() {

    companion object {
        fun newInstance() = ConfiguracionFragment()
        const val PREFS_NAME = "prefs"
        const val IS_DARK_MODE_ENABLED = "isDarkModeEnabled"
    }
    private var _binding: FragmentConfiguracionBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val configurationViewModel =
            ViewModelProvider(this).get(ConfiguracionViewModel::class.java)

        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textConfiguration
        val switchModoOscuro = binding.root.findViewById<Switch>(R.id.darkMode)
        switchModoOscuro.isChecked = isDarkModeEnabled()
        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            toggleDarkMode(isChecked)
        }

        configurationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        if (isDarkModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        return root
    }

    fun toggleDarkMode(isDarkModeEnabled: Boolean) {
        // Almacena el estado en SharedPreferences u otra lógica según tus necesidades
        saveDarkModeState(isDarkModeEnabled)

        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Recrea la actividad para aplicar los cambios en el tema
        requireActivity().recreate()
    }

    private fun saveDarkModeState(isDarkModeEnabled: Boolean) {
        val editor = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(IS_DARK_MODE_ENABLED, isDarkModeEnabled)
        editor.apply()
    }

    fun isDarkModeEnabled(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_DARK_MODE_ENABLED, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}