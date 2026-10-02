package com.example.hometask_liadsalhi.data.remote

import java.math.BigDecimal

// the json the server returns
data class LatestRatesResponse(
    val data: Map<String, BigDecimal>?
)