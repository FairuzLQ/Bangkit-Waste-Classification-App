package com.example.ecohero.viewmodels



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecohero.data.response.UploadResponse
import com.example.ecohero.data.retrofit.ApiConfig
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class UploadViewModel : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _handling = MutableLiveData<String>()
    val handling: LiveData<String> = _handling

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun upload(imagePart: MultipartBody.Part, descriptionPart: MultipartBody.Part) {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceDetect().uploadImage(imagePart, descriptionPart)
        client.enqueue(object : retrofit2.Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _result.value = responseBody.prediction
                        _description.value = responseBody.description
                        _handling.value = responseBody.handling
                    } else {
                        _result.value = response.message()
                    }
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "UploadViewModel"
    }
}
