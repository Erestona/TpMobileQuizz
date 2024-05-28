package com.example.quizz

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.quizz.databinding.FragmentQuestionBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        database = (requireActivity().application as QuizApplication).database
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_QuestionFragment_to_CategoryFragment)
        }

        val categoryId = arguments?.getInt("categoryId") ?: return

        lifecycleScope.launch {
            val questions = database.questionDao().getQuestionsByCategory(categoryId)
            displayQuestionsAndAnswers(questions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun displayQuestionsAndAnswers(questions: List<Question>) {
        // Clear any existing views
        binding.questionContainer.removeAllViews()

        for (question in questions) {
            // Create and add a TextView for the question
            val questionTextView = TextView(requireContext()).apply {
                text = question.text
                textSize = 18f
            }
            binding.questionContainer.addView(questionTextView)

            // Fetch and display answers for each question
            val answers = database.answersDao().getAnswersByQuestionId(question.id)
            for (answer in answers) {
                val answerButton = Button(requireContext()).apply {
                    text = answer.text
                    setOnClickListener {
                        // Handle answer click (if needed)
                    }
                }
                binding.questionContainer.addView(answerButton)
            }
        }
    }
}