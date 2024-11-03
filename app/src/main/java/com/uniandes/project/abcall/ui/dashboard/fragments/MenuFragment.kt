package com.uniandes.project.abcall.ui.dashboard.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.project.abcall.R
import com.uniandes.project.abcall.databinding.FragmentMenuBinding
import com.uniandes.project.abcall.ui.dashboard.intefaces.FragmentChangeListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
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
        _binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.btnIncidences.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(IncidencesFragment.newInstance())
        }

        binding.btnReports.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(ReportFragment.newInstance())
        }

        binding.btnDashboard.setOnClickListener {
            fragmentChangeListener?.onFragmentChange(DashboardFragment.newInstance())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TITLE = "Home"
        @JvmStatic
        fun newInstance() =
            MenuFragment()
    }
}