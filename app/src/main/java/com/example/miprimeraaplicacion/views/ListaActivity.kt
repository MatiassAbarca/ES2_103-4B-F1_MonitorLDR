package com.example.miprimeraaplicacion.views

// Importaciones necesarias para interactuar con Android y Firebase
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.adapters.LecturaAdapter
import com.example.miprimeraaplicacion.models.RegistroLuz
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Declara la clase ListaActivity que hereda de AppCompatActivity (representa una pantalla de Android)
class ListaActivity : AppCompatActivity() {

    // Variable para manejar la base de datos Firestore, se inicializará más adelante (lateinit)
    private lateinit var db: FirebaseFirestore
    // Variable para manejar el adaptador que conectará los datos con la lista visual
    private lateinit var adapter: LecturaAdapter
    // Variable para referenciar el componente visual RecyclerView definido en el XML
    private lateinit var rvLecturas: RecyclerView

    // Método que se ejecuta automáticamente cuando se crea la pantalla (Activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama al método padre para realizar las configuraciones base del sistema Android
        super.onCreate(savedInstanceState)
        // Asigna el archivo de diseño XML activity_lista.xml como la interfaz gráfica de esta pantalla
        setContentView(R.layout.activity_lista)

        // Inicializa la instancia de la base de datos Firestore
        db = Firebase.firestore

        // Inicializa el adaptador pasándole las dos funciones: qué hacer al editar y qué hacer al eliminar
        adapter = LecturaAdapter(
            onEditarClick = { registroAEditar ->
                // Cuando tocan la fila, abrimos el formulario en "Modo Editar" pasándole los datos
                val intent = Intent(this, FormularioLecturaActivity::class.java)
                intent.putExtra("lecturaId", registroAEditar.id)
                intent.putExtra("ubicacion", registroAEditar.ubicacion)
                intent.putExtra("nivelLuminosidad", registroAEditar.nivelLuminosidad)
                startActivity(intent)
            },
            onEliminarClick = { registroAEliminar ->
                // Cuando tocan el basurero, mostramos el diálogo
                mostrarDialogoConfirmacion(registroAEliminar)
            }
        )
        
        // Busca y enlaza el componente RecyclerView del diseño XML usando su ID (rvLecturas)
        rvLecturas = findViewById(R.id.rvLecturas)
        // Configura el RecyclerView con un LinearLayoutManager para que muestre los elementos en una lista vertical estándar
        rvLecturas.layoutManager = LinearLayoutManager(this)
        // Le asigna nuestro adaptador (adapter) al RecyclerView para que sepa cómo dibujar los datos
        rvLecturas.adapter = adapter

        // Llama al método personalizado que se encarga de escuchar los cambios en la base de datos en tiempo real
        escucharLectura()

        // Busca el FAB en el diseño XML
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarLectura)
        // Le asigna un escuchador para que haga algo cuando sea presionado
        fabAgregar.setOnClickListener {
            // Crea un Intent para ir a la pantalla del formulario en "Modo Crear" (sin enviarle ID)
            val intent = Intent(this, FormularioLecturaActivity::class.java)
            // Lanza la nueva pantalla
            startActivity(intent)
        }
    }

    // Método personalizado para conectarse a Firestore y escuchar la colección de registros
    private fun escucharLectura() {
        // Accede a la colección llamada "registros_luminosidad" en Firestore y añade un listener (escuchador) de eventos en tiempo real
        db.collection("registros_luminosidad").addSnapshotListener { snapshot, error ->
            // Si ocurre un error al intentar leer los datos de Firebase, entra en esta condición
            if (error != null) {
                // Imprime un mensaje de advertencia (Warning) en la consola de depuración (Logcat) con el detalle del error
                Log.w("ListaActivity", "Error al escuchar las lecturas.", error)
                // Detiene la ejecución de este bloque de código porque hubo un fallo, evitando que la app colapse
                return@addSnapshotListener
            }

            // Si el snapshot no es nulo, significa que recibimos información exitosamente
            if (snapshot != null) {
                // Convierte automáticamente todos los documentos de Firestore en una lista de objetos de 'RegistroLuz'
                val listaRegistros = snapshot.toObjects(RegistroLuz::class.java)
                
                // Imprime en la consola (Debug) la cantidad total de registros que se recuperaron
                Log.d("ListaActivity", "Registros recuperados: ${listaRegistros.size}")
                
                // Llama al método 'actualizarLista' de nuestro adaptador pasándole la nueva lista de datos 
                adapter.actualizarLista(listaRegistros)
            }
        }
    }

    // Muestra una ventana emergente preguntando al usuario si realmente desea eliminar
    private fun mostrarDialogoConfirmacion(registro: RegistroLuz) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Registro")
        builder.setMessage("¿Estás seguro de que deseas eliminar el registro de '${registro.ubicacion}'?")
        
        // Si el usuario presiona "Sí, eliminar"
        builder.setPositiveButton("Sí, eliminar") { dialog, _ ->
            eliminarRegistro(registro)
            dialog.dismiss() // Cierra el diálogo
        }
        
        // Si el usuario presiona "Cancelar"
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss() // Simplemente cierra el diálogo sin hacer nada
        }
        
        // Crea y muestra el diálogo en pantalla
        builder.create().show()
    }

    // Lógica para conectarse a Firestore y borrar el documento
    private fun eliminarRegistro(registro: RegistroLuz) {
        // En Firestore, los documentos se borran buscando su ID específico en la colección
        db.collection("registros_luminosidad").document(registro.id)
            .delete()
            .addOnSuccessListener {
                android.widget.Toast.makeText(this, "Registro eliminado correctamente", android.widget.Toast.LENGTH_SHORT).show()
                Log.d("ListaActivity", "Documento eliminado con éxito!")
            }
            .addOnFailureListener { e ->
                android.widget.Toast.makeText(this, "Error al eliminar: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                Log.w("ListaActivity", "Error al eliminar documento", e)
            }
    }
}
