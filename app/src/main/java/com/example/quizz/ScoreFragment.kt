package com.example.quizz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentScoreBinding

class ScoreFragment : Fragment() {

    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = sharedPreferences.getString("username", "Unknown") ?: "Unknown"
        val score = arguments?.getInt("score") ?: 0

        binding.scoreTextView.text = "Score: $score"
        binding.usernameTextView.text = "Pseudo: $username"

        binding.buttonCategory.setOnClickListener {
            findNavController().navigate(R.id.action_ScoreFragment_to_CategoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
