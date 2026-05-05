package com.example.alliswelltemi.ui.components

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import androidx.compose.ui.platform.LocalContext

@Composable
fun AvatarWebViewComponent(
    modifier: Modifier = Modifier,
    onWebViewReady: (WebView) -> Unit = {}
) {
    val context = LocalContext.current

    val assetLoader = remember {
        WebViewAssetLoader.Builder()
            .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    AndroidView(
        factory = { factoryContext ->
            WebView(factoryContext).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    @Suppress("DEPRECATION")
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    mediaPlaybackRequiresUserGesture = false
                    builtInZoomControls = false
                    displayZoomControls = false
                    setSupportZoom(false)
                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                }

                webChromeClient = object : WebChromeClient() {
                    @Suppress("DEPRECATION")
                    override fun onConsoleMessage(
                        message: String,
                        lineNumber: Int,
                        sourceID: String
                    ) {
                        val tag = "AvatarView.JS"
                        val cleanMsg = message.take(180)
                        android.util.Log.d(tag, "$cleanMsg (l:$lineNumber)")
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        android.util.Log.d("AvatarWebView", "Page loaded: $url")
                        view?.let { onWebViewReady(it) }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: android.webkit.WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        android.util.Log.e(
                            "AvatarWebView",
                            "Error loading ${request?.url}: ${error?.description}"
                        )
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): android.webkit.WebResourceResponse? {
                        request?.url?.let { url ->
                            val urlStr = url.toString()
                            // Log all requests for debugging
                            if (urlStr.contains("appassets.androidplatform.net")) {
                                android.util.Log.d("AssetLoader", "Intercepting: $urlStr")
                            }
                            // Delegate to AssetLoader for all appassets requests
                            return assetLoader.shouldInterceptRequest(url)
                        }
                        return null
                    }
                }

                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                WebView.setWebContentsDebuggingEnabled(true)

                // Load avatar page from assets with proper base URL for asset resolution
                val pageUrl = "https://appassets.androidplatform.net/assets/avatar-view.html"
                android.util.Log.d("AvatarWebView", "Loading avatar page: $pageUrl")

                // Use loadUrl with the virtual domain - shouldInterceptRequest will handle asset loading
                loadUrl(pageUrl)
            }
        },
        modifier = modifier
    )
}

