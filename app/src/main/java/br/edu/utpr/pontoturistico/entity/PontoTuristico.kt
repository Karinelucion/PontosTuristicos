package br.edu.utpr.pontoturistico.entity


data class PontoTuristico(
    var _id : Int,
    var descricao : String,
    var nome : String,
    var lat : Double,
    var lon : Double,
    var img : ByteArray?) {}