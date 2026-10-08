package com.example.miprimeraaplicacion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.models.RegistroLuz

// El constructor recibe dos funciones (lambdas): una para editar y otra para eliminar
class LecturaAdapter(
    private var listaRegistros: List<RegistroLuz> = listOf(),
    private val onEditarClick: (RegistroLuz) -> Unit,
    private val onEliminarClick: (RegistroLuz) -> Unit
) : RecyclerView.Adapter<LecturaAdapter.LecturaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lectura, parent, false)
        return LecturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: LecturaViewHolder, position: Int) {
        val registro = listaRegistros[position]
        // Le pasamos al bind el registro de luz y ambas funciones de clic
        holder.bind(registro, onEditarClick, onEliminarClick)
    }

    override fun getItemCount(): Int {
        return listaRegistros.size
    }

    fun actualizarLista(nuevaLista: List<RegistroLuz>) {
        listaRegistros = nuevaLista
        notifyDataSetChanged()
    }

    class LecturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        private val txtValor: TextView = itemView.findViewById(R.id.txtValor)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(registro: RegistroLuz, onEditarClick: (RegistroLuz) -> Unit, onEliminarClick: (RegistroLuz) -> Unit) {
            // Muestra al menos dos campos con textos descriptivos en lugar de rótulos genéricos
            txtDescripcion.text = "Ubicación: ${registro.ubicacion}"
            txtValor.text = "Nivel de luminosidad: ${registro.nivelLuminosidad} lux"

            // Configuramos qué pasa cuando alguien toca CUALQUIER PARTE de la fila (para editar)
            itemView.setOnClickListener {
                onEditarClick(registro)
            }

            // Configuramos qué pasa cuando alguien toca ESPECÍFICAMENTE el basurero (para eliminar)
            btnEliminar.setOnClickListener {
                onEliminarClick(registro)
            }
        }
    }
}
