package com.example.miprimeraaplicacion.views
import com.example.miprimeraaplicacion.R


// Importa Intent para permitir la navegación hacia la pantalla de Preferencias
import android.content.Intent
// Importa Bundle para el ciclo de vida de la Activity
import android.os.Bundle
// Importa View para recibir la vista que disparó el evento de clic
import android.view.View
// Importa TextView para manipular etiquetas de texto en pantalla
import android.widget.TextView
// Importa la función para soporte edge-to-edge
import androidx.activity.enableEdgeToEdge
// Importa la clase base AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
// Importa ViewCompat para listeners de ventanas del sistema
import androidx.core.view.ViewCompat
// Importa WindowInsetsCompat para obtener dimensiones de barras de estado/navegación
import androidx.core.view.WindowInsetsCompat

// Declaración de la clase para la segunda pantalla (Bienvenida)
class BienvenidaActivity : AppCompatActivity() {

    // Variable a nivel de clase para almacenar el nombre de usuario recibido y reutilizarlo
    private var nombreUsuario: String? = null

    // Método que se ejecuta al crearse esta pantalla
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama a la inicialización de la superclase
        super.onCreate(savedInstanceState)
        // Habilita diseño de pantalla completa detrás de las barras del sistema
        enableEdgeToEdge()
        // Carga y muestra el archivo XML correspondiente (activity_bienvenida.xml)
        setContentView(R.layout.activity_bienvenida)
        // Ajusta el padding para evitar que el contenido quede oculto bajo las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Obtiene dimensiones de las barras del sistema
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Aplica el relleno perimetral a la vista raíz
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Devuelve los insets procesados
            insets
        }

        // Lee el email del usuario enviado a través del Intent (clave "email" o "usuario")
        val email = intent.getStringExtra("email") ?: intent.getStringExtra("usuario") ?: ""
        nombreUsuario = email
        // Busca y obtiene la referencia del TextView txtBienvenida definido en el XML
        val txtBienvenida = findViewById<TextView>(R.id.txtBienvenida)
        // Modifica el texto en pantalla saludando con el email obtenido
        txtBienvenida.text = if (email.isNotEmpty()) "Bienvenido, $email" else "Bienvenido"
    }

    // Método público ejecutado automáticamente al hacer clic en el botón btnPreferencias (vía android:onClick)
    fun onPreferenciasClick(view: View) {
        // Crea un Intent explícito indicando la pantalla actual (this) y la pantalla destino (PreferenciasActivity)
        val intent = Intent(this, PreferenciasActivity::class.java)
        // Empaqueta el nombre de usuario recibido como un dato extra en el Intent con la clave "usuario"
        intent.putExtra("usuario", nombreUsuario)
        // Inicia la navegación y lanza la Activity de Preferencias
        startActivity(intent)
    }

    // Método público ejecutado automáticamente al hacer clic en el botón btnVerLecturas (vía android:onClick)
    fun onVerLecturasClick(view: View) {
        // Crea un Intent para ir a ListaActivity
        val intent = Intent(this, ListaActivity::class.java)
        // Inicia la navegación
        startActivity(intent)
    }
}
