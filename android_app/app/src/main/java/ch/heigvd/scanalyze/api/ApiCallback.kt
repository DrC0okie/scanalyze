package ch.heigvd.scanalyze.api

interface ApiCallback {
        fun onSuccess(response: String)
        fun onFailure(errorMessage: String)
}