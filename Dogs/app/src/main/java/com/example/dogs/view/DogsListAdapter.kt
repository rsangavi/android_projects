package com.example.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs.R
import com.example.dogs.databinding.DogItemBinding
import com.example.dogs.model.DogBreed
import com.example.dogs.util.getProgressDrawable
import com.example.dogs.util.loadImage
import kotlinx.android.synthetic.main.dog_item.view.*

class DogsListAdapter(private val dogBreedList: ArrayList<DogBreed>):
        RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener {

    fun updateDogsList(newDogBreedList: ArrayList<DogBreed>) {
        dogBreedList.clear()
        dogBreedList.addAll(newDogBreedList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.dog_item, parent, false)
        val view = DataBindingUtil.inflate<DogItemBinding>(inflater, R.layout.dog_item, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogBreedList[position]
        holder.view.listener = this
//        holder.itemView.title.text = dogBreedList[position].dogBreed
//        holder.itemView.subTitle.text = dogBreedList[position].lifeSpan
//        holder.itemView.setOnClickListener {
//            val action = ListFragmentDirections.actionListFragmentToDetailFragment()
//            action.dogUuid = dogBreedList[position].uuid
//            Navigation.findNavController(it).navigate(action)
//        }
//        holder.itemView.dogImage.loadImage(dogBreedList[position].imageUrl,
//                getProgressDrawable(holder.itemView.context))
    }

    override fun getItemCount(): Int = dogBreedList.size

    class DogViewHolder(var view: DogItemBinding): RecyclerView.ViewHolder(view.root)

    override fun onDogClicked(view: View) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment()
        action.dogUuid = view.dogId.text.toString().toInt()
        Navigation.findNavController(view).navigate(action)
    }
}