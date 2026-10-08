package com.example.miprimeraaplicacion.models

import com.google.firebase.firestore.DocumentId

data class RegistroLuz(
    @DocumentId
    val id: String = "",
    val ubicacion: String = "",
    val nivelLuminosidad: Double = 0.0,
    val horaRegistro: String = ""
)
