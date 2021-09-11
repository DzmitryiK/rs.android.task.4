package com.rsschool.animals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.rsschool.animals.utils.AnimalListAdapter
import com.rsschool.animals.utils.AnimalListener
import com.rsschool.animals.R
import com.rsschool.animals.database.Animal
import com.rsschool.animals.databinding.FragmentFirstBinding
import com.rsschool.animals.viewmodel.AnimalViewModel


class FirstFragment : Fragment(), AnimalListener {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var animalViewModel: AnimalViewModel
    private lateinit var adapter: AnimalListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = AnimalListAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.adapter = adapter

        animalViewModel = ViewModelProvider(requireActivity()).get(AnimalViewModel::class.java)
        animalViewModel.sort(getFieldsToSort())
        animalViewModel.allAnimals.observe(requireActivity()) { animals ->
            animals.let {adapter.submitList(it) }
        }


        //Menu
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        when (prefs.getString("db_impl_multilist", getString(R.string.room_impl_name))){
            getString(R.string.room_impl_name) ->{
                binding.toolbar.menu.findItem(R.id.action_db_impl).title = getString(R.string.room_impl_name)
            }
            getString(R.string.cursor_impl_name) ->{
                binding.toolbar.menu.findItem(R.id.action_db_impl).title = getString(R.string.cursor_impl_name)
            }
        }
        binding.toolbar.setOnMenuItemClickListener { mi ->
            when (mi.itemId) {
                R.id.action_filter -> {
                    findNavController().navigate(R.id.action_FirstFragment_to_filterSettingsFragment)
                }
                R.id.action_db_impl -> {
                    when (prefs.getString("db_impl_multilist", getString(R.string.room_impl_name))){
                        getString(R.string.room_impl_name) ->{
                            mi.title = getString(R.string.cursor_impl_name)
                            prefs.edit().putString("db_impl_multilist",getString(R.string.cursor_impl_name)).apply()
                        }
                        getString(R.string.cursor_impl_name) ->{
                            mi.title = getString(R.string.room_impl_name)
                            prefs.edit().putString("db_impl_multilist",getString(R.string.room_impl_name)).apply()
                        }
                    }
                    animalViewModel.sort(getFieldsToSort())
                    animalViewModel.allAnimals.observe(requireActivity()) { animals ->
                        animals.let {adapter.submitList(it) }
                    }
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun getFieldsToSort():String{
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val fieldsSetPref = prefs.getStringSet("sort_multilist", null)?.toMutableList()
        val orderPref = when (prefs.getString("order_dropdown", null)){
            resources.getStringArray(resources.getIdentifier("order_by", "array", requireActivity().packageName))[0] -> " ASC"
            resources.getStringArray(resources.getIdentifier("order_by", "array", requireActivity().packageName))[1] -> " DESC"
            else -> " ASC"
        }

        return if ((fieldsSetPref != null) && (fieldsSetPref.size != 0)){
            for(i in 0 until fieldsSetPref.size){
                fieldsSetPref[i] = fieldsSetPref[i].lowercase() + orderPref
            }
            fieldsSetPref.reversed().joinToString(", ")
        }else{
            "id$orderPref"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun deleteAnimal(animal: Animal) {
        animalViewModel.delete(animal, getFieldsToSort())
    }

    override fun updateAnimal(animal: Animal) {
        val bundle = Bundle()
        bundle.putInt("editId", animal.id)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
}