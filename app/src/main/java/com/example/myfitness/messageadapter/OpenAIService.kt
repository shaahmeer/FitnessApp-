//package com.example.myfitness.api
//
//// OpenAIService.kt
//
//import okhttp3.Interceptor
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.Body
//import retrofit2.http.Headers
//import retrofit2.http.POST
//
//// Define the data models
//data class OpenAIRequest(val prompt: String, val max_tokens: Int)
//data class OpenAIResponse(val choices: List<Choice>)
//data class Choice(val text: String)
//
//// Create an interface for the API
//interface OpenAIService {
//    @Headers("Content-Type: application/json")
//    @POST("completions")
//    suspend fun getCompletion(@Body request: OpenAIRequest): OpenAIResponse
//
//    companion object {
//        fun create(): OpenAIService {
//            val client = OkHttpClient.Builder()
//                .addInterceptor(object : Interceptor {
//                    override fun intercept(chain: Interceptor.Chain): Response {
//                        val newRequest: Request = chain.request().newBuilder()
//                            .addHeader("Authorization", "sk-proj-bPTQTZLl9d2BmcdRDF9aT3BlbkFJxInprj5pwZfQfzVlHOHc")
//                            .build()
//                        return chain.proceed(newRequest)
//                    }
//                })
//                .build()
//
//            return Retrofit.Builder()
//                .baseUrl("https://api.openai.com/v1/engines/davinci/")
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(OpenAIService::class.java)
//        }
//    }
//}
