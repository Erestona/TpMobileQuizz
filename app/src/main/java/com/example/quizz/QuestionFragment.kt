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
    private var currentPageIndex = 0

    companion object {
        private const val QUESTIONS_PER_PAGE = 1
    }

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
            if (currentPageIndex > 0) {
                currentPageIndex--
                lifecycleScope.launch {
                    displayCurrentPageQuestions(
                        database.questionDao()
                            .getQuestionsByCategory(arguments?.getInt("categoryId")!!)
                    )
                }
            } else {
                findNavController().navigateUp()
            }
        }

        val categoryId = arguments?.getInt("categoryId") ?: return

        lifecycleScope.launch {
            val questions = database.questionDao().getQuestionsByCategory(categoryId)
            displayCurrentPageQuestions(questions)

            binding.nextButton.setOnClickListener {
                currentPageIndex++
                lifecycleScope.launch {
                    displayCurrentPageQuestions(questions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun displayCurrentPageQuestions(questions: List<Question>) {
        binding.questionContainer.removeAllViews()

        val startIndex = currentPageIndex * QUESTIONS_PER_PAGE
        val endIndex = minOf(startIndex + QUESTIONS_PER_PAGE, questions.size)

        for (i in startIndex until endIndex) {
            val question = questions[i]

            val questionTextView = TextView(requireContext()).apply {
                text = question.text
                textSize = 18f
            }
            binding.questionContainer.addView(questionTextView)

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

        binding.nextButton.visibility = if (endIndex < questions.size) View.VISIBLE else View.GONE
    }
}
