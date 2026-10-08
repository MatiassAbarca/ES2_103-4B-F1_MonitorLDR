package com.example.miprimeraaplicacion.views

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.models.RegistroLuz
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioLecturaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var txtTituloFormulario: TextView
    private lateinit var edtDescripcion: EditText
    private lateinit var edtValor: EditText

    // Variable para saber si estamos editando (guardará el ID) o creando (será null)
    private var lecturaIdEditar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_lectura)

        db = Firebase.firestore

        txtTituloFormulario = findViewById(R.id.txtTituloFormulario)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        edtValor = findViewById(R.id.edtValor)

        // Revisamos si el Intent trae datos adjuntos (lo que significa que venimos a Editar)
        lecturaIdEditar = intent.getStringExtra("lecturaId")

        if (lecturaIdEditar != null) {
            // MODO EDITAR
            txtTituloFormulario.text = "Editar Registro de Luz"
            // Llenamos los campos con los datos actuales que nos enviaron
            edtDescripcion.setText(intent.getStringExtra("ubicacion") ?: intent.getStringExtra("lecturaDescripcion"))
            val valor = intent.getDoubleExtra("nivelLuminosidad", intent.getDoubleExtra("lecturaValor", 0.0))
            edtValor.setText(valor.toString())
        } else {
            // MODO CREAR
            txtTituloFormulario.text = "Nuevo Registro de Luz"
        }
    }

    fun onGuardarLecturaClick(view: View) {
        val descripcion = edtDescripcion.text.toString().trim()
        val valorString = edtValor.text.toString().trim()

        // VALIDACIÓN: Bloquea el envío a Firebase y muestra un Toast si hay campos vacíos
        if (descripcion.isEmpty() || valorString.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos vacíos antes de continuar", Toast.LENGTH_SHORT).show()
            if (descripcion.isEmpty()) {
                edtDescripcion.error = "La ubicación no puede estar vacía"
            }
            if (valorString.isEmpty()) {
                edtValor.error = "El nivel de luminosidad no puede estar vacío"
            }
            return // Bloquea la ejecución evitando el envío a Firebase
        }

        // VALIDACIÓN DE NÚMERO: Comprueba que el valor numérico sea válido
        val valorDouble = valorString.toDoubleOrNull()
        if (valorDouble == null) {
            edtValor.error = "El valor debe ser un número decimal válido"
            Toast.makeText(this, "El nivel de luminosidad ingresado no es válido", Toast.LENGTH_SHORT).show()
            return // Bloquea la ejecución evitando el envío a Firebase
        }

        val horaActual = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        if (lecturaIdEditar != null) {
            // MODO EDITAR: Usamos el ID existente para SOBREESCRIBIR el documento en Firebase
            val registroActualizado = RegistroLuz(
                id = lecturaIdEditar!!,
                ubicacion = descripcion,
                nivelLuminosidad = valorDouble,
                horaRegistro = intent.getStringExtra("horaRegistro") ?: horaActual
            )
            
            db.collection("registros_luminosidad").document(lecturaIdEditar!!)
                .set(registroActualizado) // .set() reemplaza los datos del documento
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro actualizado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            // MODO CREAR: No enviamos ID, usamos .add() para que Firestore lo cree
            val nuevoRegistro = RegistroLuz(
                ubicacion = descripcion,
                nivelLuminosidad = valorDouble,
                horaRegistro = horaActual
            )

            db.collection("registros_luminosidad")
                .add(nuevoRegistro)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro guardado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
