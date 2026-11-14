package com.example.goals_app.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Conversão de datas entre o formato da API (ISO) e o formato brasileiro da tela.
// Usa java.time (o mesmo do Java 8+). No Android ele só existe a partir da API 26 (Android 8.0),
// por isso o minSdk do projeto é 26.
object Datas {

    private val formatoTela = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // API -> tela:  "2026-12-31" -> "31/12/2026". Retorna null se a data não existir.
    fun paraTela(dataIso: String?): String? {
        if (dataIso.isNullOrBlank()) return null
        return try {
            LocalDate.parse(dataIso).format(formatoTela)
        } catch (e: Exception) {
            null
        }
    }

    // API (data e hora) -> tela:  "2026-11-14T13:32:11.514" -> "14/11/2026"
    // LocalDateTime.parse aceita com ou sem fração de segundos.
    fun dataHoraParaTela(dataHoraIso: String?): String? {
        if (dataHoraIso.isNullOrBlank()) return null
        return try {
            LocalDateTime.parse(dataHoraIso).format(formatoTela)
        } catch (e: Exception) {
            null
        }
    }

    // tela -> API:  "31/12/2026" -> "2026-12-31". Usado ao enviar o formulário.
    fun paraApi(dataTela: String?): String? {
        if (dataTela.isNullOrBlank()) return null
        return try {
            LocalDate.parse(dataTela, formatoTela).toString()   // toString() já é ISO
        } catch (e: Exception) {
            null
        }
    }

    // Monta o ISO a partir do DatePicker (que devolve mês de 0 a 11!).
    fun deDatePicker(ano: Int, mesZeroBase: Int, dia: Int): String =
        LocalDate.of(ano, mesZeroBase + 1, dia).toString()
}
