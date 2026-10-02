package com.example.hometask_liadsalhi.di

import com.example.hometask_liadsalhi.BuildConfig
import com.example.hometask_liadsalhi.data.remote.CurrencyApi
import com.example.hometask_liadsalhi.data.repository.InMemoryPaymentRepository
import com.example.hometask_liadsalhi.data.repository.RemoteExchangeRateRepository
import com.example.hometask_liadsalhi.domain.repository.ExchangeRateRepository
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import com.example.hometask_liadsalhi.domain.usecase.ConvertAmountUseCase
import com.example.hometask_liadsalhi.domain.usecase.CreatePaymentUseCase
import com.example.hometask_liadsalhi.domain.usecase.ValidateAmountUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// creates the app's shared objects once and connects them (manual dependency injection)
class AppContainer {

    // payment flow
    val paymentRepository: PaymentRepository = InMemoryPaymentRepository()

    val validateAmountUseCase = ValidateAmountUseCase()
    val createPaymentUseCase = CreatePaymentUseCase(paymentRepository, validateAmountUseCase)

    // exchange rates (bonus) - created only the first time the convert screen needs them
    private val currencyApi: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl(CurrencyApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    private val exchangeRateRepository: ExchangeRateRepository by lazy {
        RemoteExchangeRateRepository(currencyApi, BuildConfig.CURRENCY_API_KEY)
    }

    val convertAmountUseCase: ConvertAmountUseCase by lazy {
        ConvertAmountUseCase(exchangeRateRepository)
    }
}