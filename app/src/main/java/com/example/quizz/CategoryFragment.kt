package com.example.quizz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentCategoryBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerName = view.findViewById<TextView>(R.id.player_name)
        playerName.text = activity?.intent?.getStringExtra("username") ?: ""

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_CategoryFragment_to_QuestionFragment)
        }

        binding.buttonCultureGenerale.setOnClickListener {
            findNavController().navigate(
                R.id.action_CategoryFragment_to_QuestionFragment,createBundle("generalCulture"));
        }

        binding.buttonCinema.setOnClickListener {
            findNavController().navigate(
                R.id.action_CategoryFragment_to_QuestionFragment,createBundle("cinema"));
        }

        binding.buttonLitterature.setOnClickListener {
            findNavController().navigate(
                R.id.action_CategoryFragment_to_QuestionFragment,createBundle("literature"));
        }

        binding.buttonVideoGames.setOnClickListener {
            findNavController().navigate(
                R.id.action_CategoryFragment_to_QuestionFragment,createBundle("videoGames"));
        }

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories()
            categories.forEach { category ->
                println("Category: ${category.name}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createBundle(theme: String) : Bundle
    {
        val bundle = Bundle().apply {
            putString("info", theme)
        }
        return bundle;
    }
}
