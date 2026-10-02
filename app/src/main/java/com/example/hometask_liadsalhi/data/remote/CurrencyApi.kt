package com.example.hometask_liadsalhi.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// describes the server endpoint - retrofit writes the actual http code for it
interface CurrencyApi {

    // GET https://api.freecurrencyapi.com/v1/latest?base_currency=ILS&currencies=USD,EUR
    @GET("v1/latest")
    suspend fun getLatestRates(
        @Header("apikey") apiKey: String,
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): LatestRatesResponse

    companion object {
        const val BASE_URL = "https://api.freecurrencyapi.com/"
    }
}