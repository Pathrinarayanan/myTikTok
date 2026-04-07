package com.example.mytiktok.viewmodal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytiktok.api.RetrofitHelper
import com.example.mytiktok.modal.FetchResult
import com.example.mytiktok.modal.HomeUiState
import com.example.mytiktok.modal.PexelsVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception


class HomeViewModal : ViewModel(){
    private val _state = MutableStateFlow(HomeUiState())
    val state : StateFlow<HomeUiState> = _state.asStateFlow()

    private var nextPage = 1

    init {
        fetchVideos()
    }
    fun onpageChanged(index  : Int){
        val s = _state.value
        if(s.video.isEmpty()) return
        if(index < s.video.size -2) return
        if(!s.hasMore || s.loading) return
        fetchVideos()
    }

    fun pickVideoUrl(video: PexelsVideo) : String?{
        val files = video.videoFiles ?: return null

        for(file in files){
            if(file.quality == "hd") return file.link
        }
        return files.firstOrNull()?.link
    }

    private suspend fun fetchPopularVideos( page : Int , perpage : Int  = 10): FetchResult{
        val response = RetrofitHelper.api.popular(page, perpage)
        val urls = response.videos.orEmpty().mapNotNull {
            pickVideoUrl(it)
        }
        val hasMore =( response.nextPage != null )
        return FetchResult(urls, hasMore)
    }
    fun retry(){
        _state.update { it.copy(error = null) }
        fetchVideos()
    }

    private fun fetchVideos(){
        viewModelScope.launch {
            val s = _state.value

            if(s.loading || !s.hasMore) return@launch

            _state.update { it.copy(loading =  true , error =  null) }
            try{
                val result = fetchPopularVideos(nextPage)
                nextPage++
                _state.update {
                    it.copy(video =  it.video  + result.urls,
                        hasMore = result.hasMore, //result . hasmore,
                        loading = false
                    )
                }
            }
            catch (e : IOException){
                //no internet connection
                fail("No internet connection")
            }
            catch (e : Exception){
                // normal error
                fail("Error : ${e.message}")
            }

        }
    }
    private fun fail(message : String){
        _state.update {
            if(it.video.isEmpty()) it.copy(loading = false, error =  message)
            else it.copy(loading = false)
        }
    }



}