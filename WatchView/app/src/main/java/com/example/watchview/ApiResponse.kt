package com.example.watchview

data class ApiResponse(
    val result: List<StreamingResult>
)

data class StreamingResult(
    val title: String,
    val year: Int,
    val streamingInfo: StreamingInfo?
)

data class StreamingInfo(
    val services: List<ServiceInfo>?
)

data class ServiceInfo(
    val service: String,
    val link: String
)