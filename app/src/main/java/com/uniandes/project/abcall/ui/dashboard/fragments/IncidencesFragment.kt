package com.uniandes.project.abcall.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.project.abcall.databinding.FragmentIncidencesBinding
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener
import com.uniandes.project.abcall.ui.dashboard.fragments.CrateIncidencesFragment

class IncidencesFragment : Fragment() {

    private var _binding: FragmentIncidencesBinding? = null
    private val binding get() = _binding!!

    private var fragmentChangeListener: FragmentChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentChangeListener) {
            fragmentChangeListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncidencesBinding.inflate(inflater, container, false)

        binding.incidenceMenuItemChatbot.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(IncidenceCreateChatbotFragment.newInstance())
        }

        binding.incidenceMenuItemManual.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(CrateIncidencesFragment.newInstance())
        }

        return binding.root
    }

    companion object {
        const val TITLE = "Incidencias"
        @JvmStatic
        fun newInstance() =
            IncidencesFragment()
    }
}