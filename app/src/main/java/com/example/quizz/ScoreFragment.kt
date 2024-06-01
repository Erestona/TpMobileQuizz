package com.example.quizz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.SharedPreferences
import androidx.activity.OnBackPressedCallback
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

        // Save the current player's score
        savePlayerScore(username, score)

        // Retrieve the top 10 players' scores
        val playerScores = getTopPlayerScores(sharedPreferences, 10)
        displayPlayerScores(playerScores)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_ScoreFragment_to_CategoryFragment)
            }
        })
    }

    private fun savePlayerScore(username: String, score: Int) {
        val playerScores = getTopPlayerScores(sharedPreferences, 10).toMutableList()
        playerScores.add(PlayerScore(username, score))
        savePlayerScores(sharedPreferences, playerScores)
    }

    private fun displayPlayerScores(playerScores: List<PlayerScore>) {
        val marginTopInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)
        val scoresText = playerScores.joinToString("\n") { "Pseudo: ${it.username} - Score: ${it.score}/10" }
        binding.scoreTextView.text = scoresText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
