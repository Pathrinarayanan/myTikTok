package com.example.mytiktok.view

import android.media.browse.MediaBrowser
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalCursorBlinkEnabled
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.mytiktok.R
import com.example.mytiktok.modal.BottomItem
import com.example.mytiktok.viewmodal.HomeViewModal
import kotlinx.coroutines.flow.distinctUntilChanged


@Composable
fun Bottombar(){
    val data = listOf(
        BottomItem("Home", R.drawable.home_icon),
        BottomItem("Discover", R.drawable.search_icon),
        BottomItem("", R.drawable.add_icon),
        BottomItem("Inbox", R.drawable.message_icon),
        BottomItem("Me", R.drawable.you_icon),
    )
    Row(
        Modifier.fillMaxWidth()
            .background(Color.Black)
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly
    ) {
        data?.forEach {it->
            TikTokBottomItem(it.title, it.img)
        }
    }
}
@Composable
fun TikTokBottomItem( title : String,  img : Int){
    Column(
        Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(img),
            null,
            Modifier.size(
                if(title.isEmpty()) 45.dp
                else 24.dp
            )
        )
        Text(
            title,
            Modifier.padding(5.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun VideoPage(url : String,isActive : Boolean ){
    val context = LocalContext.current
   val exoplayer = remember(url){

       ExoPlayer.Builder(context).build().apply {
           setMediaItem(MediaItem.fromUri(url))
           repeatMode = ExoPlayer.REPEAT_MODE_ONE
           playWhenReady = true
           prepare()
       }
   }
    DisposableEffect(url) {
        onDispose {
            exoplayer.release()
        }
    }
    LaunchedEffect(isActive) {
        exoplayer.playWhenReady = isActive
        if(!isActive) exoplayer.pause()
    }
    AndroidView(factory = {ctx->
        PlayerView(ctx).apply {
            player = exoplayer
            useController = false
        }
    }, modifier = Modifier.fillMaxSize()
        )


}


@Composable
fun HomeScreen (viewModal: HomeViewModal){

    val state by viewModal.state.collectAsState()
    val urls = state.video

    val pagerState = rememberPagerState(0, pageCount = {urls.size.coerceAtLeast(1)})

    LaunchedEffect(Unit) {
        snapshotFlow {  pagerState.currentPage }.distinctUntilChanged().collect {
            viewModal.onpageChanged(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Bottombar()
        }
    ) {
        Column(Modifier.fillMaxSize().padding(it).background(Color.Black)) {

            when{
                state.error != null ->{
                    //no internet connection
                    Box(Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error ?:"" , color  =Color.White)
                            TextButton(onClick = {
                                viewModal.retry()
                            }) {
                                Text("retry" , color  =Color.White)
                            }
                        }
                    }
                }
                urls.isEmpty() && state.loading ->{
                    //show loader
                    Box(Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }
                else->{
                    VerticalPager(
                        state =  pagerState,
                        modifier =Modifier.fillMaxSize(),
                        userScrollEnabled = true,
                        beyondViewportPageCount = 1
                    ) { page ->
                        if (page < urls.size) {
                            VideoPage(
                                urls[page],
                                isActive = pagerState.currentPage == page
                            )
                        }
                    }
                }
            }
        }


    }
}