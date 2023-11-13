package com.example.bepawsomedos.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bepawsomedos.R
import com.example.bepawsomedos.models.Animal

class AdaptadorAnimal(
var listaAnimales: ArrayList<Animal>
): RecyclerView.Adapter<AdaptadorAnimal.ViewHolder>() {

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre = itemView.findViewById(R.id.animalNameTextView) as TextView
        val tvRaza = itemView.findViewById(R.id.animalBreedTextView) as TextView
        val tvAge = itemView.findViewById(R.id.animalAgeTextView) as TextView
        val tvSex = itemView.findViewById(R.id.animalSexTextView) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_animal_view, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = listaAnimales[position]

        holder.tvNombre.text = animal.nombre
        holder.tvRaza.text = animal.raza
        holder.tvAge.text = animal.edad.toString()
        holder.tvSex.text = animal.sexo
    }

    override fun getItemCount(): Int {
        return listaAnimales.size
    }

    fun filtrar(listaFiltrada: ArrayList<Animal>) {
        this.listaAnimales = listaFiltrada
        notifyDataSetChanged()
    }
}