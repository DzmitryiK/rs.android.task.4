package com.rsschool.animals.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rsschool.animals.utils.AnimalListAdapter.AnimalViewHolder
import com.rsschool.animals.database.Animal
import com.rsschool.animals.databinding.AnimalItemBinding

class AnimalListAdapter(private val listener: AnimalListener)
    : ListAdapter<Animal, AnimalViewHolder>(ANIMALS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AnimalItemBinding.inflate(layoutInflater, parent, false)
        return AnimalViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class AnimalViewHolder(private val binding: AnimalItemBinding,
                           private val listener: AnimalListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(animal: Animal) {
            binding.animalNameValue.text = animal.name
            binding.animalAgeValue.text = animal.age.toString()
            binding.animalBreedValue.text = animal.breed

            binding.deleteButton.setOnClickListener {
                listener.deleteAnimal(animal)
            }

            binding.updateButton.setOnClickListener {
                listener.updateAnimal(animal)
            }
        }
    }

    companion object {
        private val ANIMALS_COMPARATOR = object : DiffUtil.ItemCallback<Animal>() {

            override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.age == newItem.age &&
                        oldItem.breed == newItem.breed
            }
        }
    }
}