package com.example.autotrolejapp.network

class AutotrolejApiEndpoints {

    companion object {
        const val baseUrl = "https://e-usluge2.rijeka.hr/OpenData/"

        const val linesEndpointExtension = "ATlinije.json"

        const val stationsEndpointExtension = "ATstanice.json"

        const val busLocationEndpointExtension = "AtPoz.php?type=json"

        const val scheduleTodayEndpointExtension = "ATvoznired.json"
        const val scheduleWorkDayEndpointExtension = "ATvoznired-tjedan.json"
        const val scheduleSaturdayEndpointExtension = "ATvoznired-subota.json"
        const val scheduleSundayEndpointExtension = "ATvoznired-nedjelja.json"
    }
}