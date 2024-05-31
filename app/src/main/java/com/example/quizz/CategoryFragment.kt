package com.example.quizz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

        val playerName = view.findViewById<TextView>(R.id.playerName)
        playerName.text = activity?.intent?.getStringExtra("username") ?: ""

        lifecycleScope.launch {
            val categories = database.categoryDao().getAllCategories()
            createCategoryButtons(categories)
        }

        binding.backToMain.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createCategoryButtons(categories: List<Category>) {
        binding.categoryButtons.removeAllViews() // Clear any existing buttons
        val marginTopInPixels = resources.getDimensionPixelSize(R.dimen.fab_margin)

        for (category in categories) {
            val button = Button(requireContext()).apply {
                text = category.name
                typeface = resources.getFont(R.font.montserrat_bold)
                setTextColor(resources.getColor(R.color.white))
                setBackgroundResource(R.drawable.rounded_button)
                textSize = 15f

                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Width
                    ViewGroup.LayoutParams.WRAP_CONTENT // Height
                ).apply {
                    setMargins(0, marginTopInPixels, 0, 0)
                }

                setOnClickListener {
                    val bundle = createBundle(category.id)
                    findNavController().navigate(R.id.action_CategoryFragment_to_QuestionFragment, bundle)
                }
            }
            binding.categoryButtons.addView(button)
        }
    }

    private fun createBundle(id: Int) : Bundle
    {
        val bundle = Bundle().apply {
            putInt("categoryId", id)
        }
        return bundle;
    }
}
