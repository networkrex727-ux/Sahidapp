package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val networkMonitor = remember { NetworkMonitor(context.applicationContext) }
                val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(
                    initialValue = networkMonitor.isCurrentlyOnline()
                )

                var showSplash by remember { mutableStateOf(true) }

                // Splash Timer effect
                LaunchedEffect(Unit) {
                    delay(1800)
                    showSplash = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = showSplash,
                        animationSpec = tween(500, easing = EaseInOutCubic),
                        label = "AppStageTransition"
                    ) { splashActive ->
                        if (splashActive) {
                            SplashScreen()
                        } else {
                            MainAppContent(
                                isOnline = isOnline,
                                onOpenSettings = { openNetworkSettings(context) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openNetworkSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                // Ignore fallback failure
            }
        }
    }
}

// Visual Palette constants for Sahid Anime Theme
object SahidAnimeTheme {
    val DeepSpace = Color(0xFF07090F)
    val SecondarySpace = Color(0xFF111422)
    val CardBackground = Color(0xFF181B2C)
    val PrimaryAccent = Color(0xFFFF5722) // Neon Orange
    val SecondaryAccent = Color(0xFFE91E63) // Hot Pink
    val CyanAccent = Color(0xFF00E5FF) // Light Blue
    val YellowAccent = Color(0xFFFFD600) // Electric Yellow
    val OffWhite = Color(0xFFE2E8F0)
}

@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashRotation")
    
    // Rotating portal swirl effect
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SwirlRotation"
    )

    // Pulsing text effect
    val textPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TextPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SahidAnimeTheme.DeepSpace, SahidAnimeTheme.SecondarySpace)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stylized Custom Anime Portal Logo in Compose Canvas
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2 - 12f

                    // 1. Swirling Gradient Ring 1
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                SahidAnimeTheme.PrimaryAccent,
                                SahidAnimeTheme.SecondaryAccent,
                                SahidAnimeTheme.CyanAccent,
                                SahidAnimeTheme.PrimaryAccent
                            ),
                            center = center
                        ),
                        startAngle = rotationAngle,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 2. Play Triangle / Star Icon in the middle
                    val playPath = Path().apply {
                        val startX = center.x - 20f
                        val startY = center.y - 30f
                        moveTo(startX, startY)
                        lineTo(center.x + 35f, center.y)
                        lineTo(startX, center.y + 30f)
                        close()
                    }

                    drawPath(
                        path = playPath,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White, Color(0xFFFFCCFF)),
                            start = Offset(center.x - 20f, center.y),
                            end = Offset(center.x + 35f, center.y)
                        )
                    )

                    // 3. Highlight line on Play Icon
                    drawPath(
                        path = Path().apply {
                            moveTo(center.x - 20f, center.y - 30f)
                            lineTo(center.x + 8f, center.y - 3f)
                            lineTo(center.x - 20f, center.y + 24f)
                        },
                        color = SahidAnimeTheme.CyanAccent,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title "Sahid Anime"
            Text(
                text = "Sahid Anime",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = Color.White,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle / Slogan with pulse
            Text(
                text = "YOUR FREE ANIME PORTAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = SahidAnimeTheme.CyanAccent.copy(alpha = textPulse),
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MainAppContent(
    isOnline: Boolean,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    
    // Maintain single instantiation of WebView to preserve full browse state
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_DEFAULT
            }
        }
    }

    var webProgress by remember { mutableFloatStateOf(0f) }
    var isLoadingPage by remember { mutableStateOf(false) }
    var webErrorDescription by remember { mutableStateOf<String?>(null) }
    var currentUrl by remember { mutableStateOf("https://sahidanime.in") }
    var pageTitle by remember { mutableStateOf("Sahid Anime") }

    // Setup Custom Clients
    LaunchedEffect(webView) {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                isLoadingPage = true
                webErrorDescription = null // Clear any transient state
                if (url != null) {
                    currentUrl = url
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoadingPage = false
                webProgress = 0f
                if (url != null) {
                    currentUrl = url
                }
                pageTitle = view?.title ?: "Sahid Anime"
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // Filter main frame loading errors (ignore subresources like missing ads scripts)
                if (request?.isForMainFrame == true) {
                    webErrorDescription = error?.description?.toString() ?: "Connection Timeout"
                    isLoadingPage = false
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                // Smooth down progress value
                webProgress = newProgress / 100f
            }
        }
    }

    // Trigger URL Loading based on Connectivity
    LaunchedEffect(isOnline) {
        if (isOnline) {
            webErrorDescription = null
            if (webView.url == null) {
                webView.loadUrl("https://sahidanime.in")
            } else if (webErrorDescription != null) {
                webView.reload()
            }
        }
    }

    // System Back Press handling so user navigates back inside WebView history!
    val backEnabled = isOnline && webErrorDescription == null && webView.canGoBack()
    BackHandler(enabled = backEnabled) {
        webView.goBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SahidAnimeTheme.DeepSpace)
        ) {
            if (!isOnline || webErrorDescription != null) {
                // Custom Professional Offline Screen with status bar padding for accessibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    OfflineScreen(
                        errorMessage = webErrorDescription,
                        onOpenSettings = onOpenSettings,
                        onRetry = {
                            // Force check and reload
                            if (isOnline) {
                                webErrorDescription = null
                                webView.reload()
                            }
                        }
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Native high-fidelity WebView renderer container - 100% IMMERSIVE FULL SCREEN
                    AndroidView(
                        factory = { webView },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("sahidanime_webview")
                    )

                    // Overlay Linear Dynamic Loading Indicator at the very top (understatus area)
                    if (isLoadingPage && webProgress < 1.0f && isOnline) {
                        LinearProgressIndicator(
                            progress = webProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .height(3.dp),
                            color = SahidAnimeTheme.PrimaryAccent,
                            trackColor = Color.Transparent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OfflineScreen(
    errorMessage: String?,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarBreath")
    
    // Pulsing warning/radar ring animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarPulse"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SahidAnimeTheme.DeepSpace)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated glowing offline illustration in Canvas
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Outer pulsing ring
                    drawCircle(
                        color = SahidAnimeTheme.PrimaryAccent,
                        radius = (size.minDimension / 2 - 15f) * pulseScale,
                        center = center,
                        alpha = pulseAlpha,
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Solid inner circle
                    drawCircle(
                        color = SahidAnimeTheme.SecondarySpace,
                        radius = size.minDimension / 2 - 16f,
                        center = center
                    )

                    // Abstract Wifi-Off cross icon drawing
                    val width = size.width
                    val height = size.height

                    // Wifi lines
                    drawArc(
                        color = SahidAnimeTheme.PrimaryAccent,
                        startAngle = 220f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(width * 0.2f, height * 0.2f),
                        size = Size(width * 0.6f, height * 0.6f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = SahidAnimeTheme.YellowAccent,
                        startAngle = 235f,
                        sweepAngle = 70f,
                        useCenter = false,
                        topLeft = Offset(width * 0.3f, height * 0.3f),
                        size = Size(width * 0.4f, height * 0.4f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Center dot
                    drawCircle(
                        color = SahidAnimeTheme.SecondaryAccent,
                        radius = 8f,
                        center = Offset(width * 0.5f, height * 0.65f)
                    )

                    // Slash line (Wifi off indicator)
                    drawLine(
                        color = Color.White,
                        start = Offset(width * 0.25f, height * 0.25f),
                        end = Offset(width * 0.75f, height * 0.75f),
                        strokeWidth = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Warning Heading
            Text(
                text = "Internet Band Hai",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "No Internet Connection",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SahidAnimeTheme.SecondaryAccent,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed bilingual prompt
            Text(
                text = "Sahid Anime chalane ke liye internet ki zaroorat hai. Kripya apne mobile settings mein jaakar internet (Wi-Fi ya Cellular Data) on karein.",
                fontSize = 13.sp,
                color = SahidAnimeTheme.OffWhite,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Deta: $errorMessage",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Direct Call to Action button (Opens networking settings dynamically)
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahidAnimeTheme.PrimaryAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(52.dp)
                    .testTag("open_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Internet Settings On Karo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Refresh retry back button
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SahidAnimeTheme.CyanAccent
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SahidAnimeTheme.CyanAccent.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(50.dp)
                    .testTag("retry_connection_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Fir Se Koshish Karo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
