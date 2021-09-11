package com.rsschool.animals.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rsschool.animals.R
import com.rsschool.animals.database.Animal
import com.rsschool.animals.databinding.FragmentSecondBinding
import com.rsschool.animals.viewmodel.AnimalViewModel


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var animalViewModel: AnimalViewModel


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animalViewModel = ViewModelProvider(requireActivity()).get(AnimalViewModel::class.java)

        val editId = arguments?.getInt("editId")

        if (editId != null && editId != 0){
            binding.buttonInsert.text = getString(R.string.btn_edit)
            binding.toolbar.title = getString(R.string.fragment_edit_title)
                animalViewModel.allAnimals.observe(viewLifecycleOwner) {
                    val editedAnimal = it.find { animal -> animal.id == editId }
                    binding.textInputName.setText(editedAnimal?.name)
                    binding.textInputAge.setText(editedAnimal?.age.toString())
                    binding.textInputBreed.setText(editedAnimal?.breed)

                    animalViewModel.allAnimals.removeObservers(viewLifecycleOwner)
                }
        }

        //TextInputs checks
        checkEmptyTexts()
        binding.textInputName.addTextChangedListener(myTextWatcher)
        binding.textInputAge.addTextChangedListener(myTextWatcher)
        binding.textInputBreed.addTextChangedListener(myTextWatcher)

        binding.textInputName.setOnEditorActionListener { textView, i, _ ->
            if (textView.text.isEmpty() && i == EditorInfo.IME_ACTION_NEXT) {
                binding.textInputLayName.error = getString(R.string.empty_name_error)
            } else {
                binding.textInputLayName.error = null
                binding.textInputAge.requestFocus()
            }
            return@setOnEditorActionListener true
        }
        binding.textInputAge.setOnEditorActionListener { textView, i, _ ->
            if ((textView.text.isEmpty() || textView.text.toString().toIntOrNull() == null
                        || textView.text.toString().toInt() <= 0)
                && i == EditorInfo.IME_ACTION_NEXT){
                binding.textInputLayAge.error = getString(R.string.negative_age_error)
            } else {
                binding.textInputLayAge.error = null
                binding.textInputBreed.requestFocus()
            }
            return@setOnEditorActionListener true
        }
        binding.textInputBreed.setOnEditorActionListener { textView, i, _ ->
            if (textView.text.isEmpty() && i == EditorInfo.IME_ACTION_DONE) {
                binding.textInputLayBreed.error = getString(R.string.empty_breed_error)
            } else {
                binding.textInputLayBreed.error = null
                binding.textInputLayBreed.clearFocus()
                hideKeyboard(view)
            }
            return@setOnEditorActionListener true
        }



        binding.buttonInsert.setOnClickListener {
            hideKeyboard(view)
            if (editId != null && editId != 0){
                val newAnimal = Animal(editId, binding.textInputName.text.toString(),
                    Integer.parseInt(binding.textInputAge.text.toString()),
                    binding.textInputBreed.text.toString())

                animalViewModel.update(newAnimal)
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

            } else {
                val newAnimal = Animal(0, binding.textInputName.text.toString(),
                    Integer.parseInt(binding.textInputAge.text.toString()),
                    binding.textInputBreed.text.toString())

                animalViewModel.allAnimals.observe(viewLifecycleOwner) {
                        val editedAnimal = it.find { a -> a.name == newAnimal.name &&
                           a.age == newAnimal.age && a.breed == newAnimal.breed }
                        if (editedAnimal == null){
                            animalViewModel.insert(newAnimal)
                            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                        }else{
                            Toast.makeText(context,getString(R.string.duplicate_error),Toast.LENGTH_SHORT).show()
                        }

                        animalViewModel.allAnimals.removeObservers(viewLifecycleOwner)
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private val myTextWatcher = object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            checkEmptyTexts()
        }
    }

    private fun hideKeyboard(view: View){
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkEmptyTexts(){
        val name = binding.textInputName.text
        val age = binding.textInputAge.text.toString().toIntOrNull()
        val breed = binding.textInputBreed.text
        if (name != null && age != null && breed != null){
            binding.buttonInsert.isEnabled = name.isNotEmpty() &&
                    (age > 0) &&
                    breed.isNotEmpty()
        }else binding.buttonInsert.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}