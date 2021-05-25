package com.example.autotrolejapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


private const val BASE_URL = AutotrolejApiEndpoints.baseUrl

const val timeout: Long = 60

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient:OkHttpClient? = OkHttpClient().newBuilder()
    .connectTimeout(timeout, TimeUnit.SECONDS)
    .readTimeout(timeout, TimeUnit.SECONDS)
    .writeTimeout(timeout, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface AutotrolejApiService {
    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.linesEndpointExtension)
    suspend fun getLines(): List<LineResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.stationsEndpointExtension)
    suspend fun getStations(): List<StationResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.busLocationEndpointExtension)
    suspend fun getCurrentBusLocations(): List<BusLocationResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.scheduleTodayEndpointExtension)
    suspend fun getScheduleToday(): List<ScheduleLineResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.scheduleWorkDayEndpointExtension)
    suspend fun getScheduleWorkDay(): List<ScheduleLineResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.scheduleSaturdayEndpointExtension)
    suspend fun getScheduleSaturday(): List<ScheduleLineResponse>

    @GET(AutotrolejApiEndpoints.baseUrl + AutotrolejApiEndpoints.scheduleSundayEndpointExtension)
    suspend fun getScheduleSunday(): List<ScheduleLineResponse>
}

object AutotrolejApi {
    val retrofitService : AutotrolejApiService by lazy { retrofit.create(AutotrolejApiService::class.java) }
}
