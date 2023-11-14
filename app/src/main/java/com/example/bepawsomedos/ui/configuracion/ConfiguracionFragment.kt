package com.example.bepawsomedos.ui.configuracion

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bepawsomedos.R
import com.example.bepawsomedos.databinding.FragmentConfiguracionBinding
import com.example.bepawsomedos.viewModels.SharedViewModel

class ConfiguracionFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        val root: View = _binding.root

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


        sharedViewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isDarkModeEnabled ->
            // Actualiza tu UI o realiza acciones en función de isDarkModeEnabled
            // Por ejemplo, puedes aplicar un tema oscuro al fragmento.
            if (isDarkModeEnabled) {
                requireActivity().setTheme(R.style.Theme_BepawsomeDos_Dark)
            } else {
                requireActivity().setTheme(R.style.Theme_BepawsomeDos)
            }
            // Necesitas recrear la actividad para aplicar el nuevo tema.
            requireActivity().recreate()
        }


        return root
    }
}