package com.example.mytiktok.modal

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    val videos : List<PexelsVideo>?,
    @SerializedName("next_page") val nextPage : String?,
)

data class  PexelsVideo(
    @SerializedName("video_files") val videoFiles : List<VideoFile>?
)

data class VideoFile(
    @SerializedName("link") val link : String?,
    @SerializedName("quality") val quality : String?
)