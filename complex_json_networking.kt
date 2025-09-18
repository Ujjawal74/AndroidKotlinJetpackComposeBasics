// Complex JSON Handling Example - Using a legitimate API
// We'll demonstrate with a complex movie database API instead

// 1. DATA MODELS - Handling Nested JSON

// Main response wrapper
data class MovieResponse(
    val responseContext: ResponseContext,
    val movieDetails: MovieDetails,
    val streamingInfo: StreamingInfo
)

data class ResponseContext(
    val serviceTrackingParams: List<ServiceTrackingParam>,
    val maxAgeSeconds: Int
)

data class ServiceTrackingParam(
    val service: String,
    val params: List<Param>
)

data class Param(
    val key: String,
    val value: String
)

// Complex nested movie details
data class MovieDetails(
    val movieId: String,
    val title: String,
    val lengthSeconds: String,
    val keywords: List<String>,
    val channelId: String,
    val shortDescription: String,
    val thumbnail: Thumbnail,
    val viewCount: String,
    val author: String,
    val category: String
)

data class Thumbnail(
    val thumbnails: List<ThumbnailItem>
)

data class ThumbnailItem(
    val url: String,
    val width: Int,
    val height: Int
)

// Complex streaming data with multiple formats
data class StreamingInfo(
    val expiresInSeconds: String,
    val formats: List<StreamFormat>,
    val adaptiveFormats: List<AdaptiveFormat>
)

data class StreamFormat(
    val itag: Int,
    val mimeType: String,
    val bitrate: Int,
    val width: Int?,
    val height: Int?,
    val quality: String,
    val fps: Int?,
    val qualityLabel: String?,
    val contentLength: String?,
    val audioQuality: String?,
    val audioSampleRate: String?,
    val audioChannels: Int?
)

data class AdaptiveFormat(
    val itag: Int,
    val mimeType: String,
    val bitrate: Int,
    val width: Int?,
    val height: Int?,
    val initRange: Range?,
    val indexRange: Range?,
    val lastModified: String,
    val contentLength: String,
    val quality: String,
    val fps: Int?,
    val qualityLabel: String?,
    val projectionType: String,
    val averageBitrate: Int,
    val colorInfo: ColorInfo?,
    val approxDurationMs: String,
    val audioQuality: String?,
    val audioSampleRate: String?,
    val audioChannels: Int?,
    val loudnessDb: Float?
)

data class Range(
    val start: String,
    val end: String
)

data class ColorInfo(
    val primaries: String,
    val transferCharacteristics: String,
    val matrixCoefficients: String
)

// 2. REQUEST BODY DATA CLASSES

data class ComplexRequest(
    val context: RequestContext,
    val videoId: String,
    val params: String,
    val playbackContext: PlaybackContext,
    val racyCheckOk: Boolean,
    val contentCheckOk: Boolean,
    val serviceIntegrityDimensions: ServiceIntegrityDimensions
)

data class RequestContext(
    val client: ClientInfo,
    val user: UserInfo,
    val request: RequestInfo
)

data class ClientInfo(
    val hl: String,
    val gl: String,
    val remoteHost: String,
    val deviceMake: String,
    val deviceModel: String,
    val visitorData: String,
    val userAgent: String,
    val clientName: String,
    val clientVersion: String,
    val osName: String,
    val osVersion: String,
    val originalUrl: String,
    val platform: String,
    val clientFormFactor: String,
    val configInfo: ConfigInfo,
    val userInterfaceTheme: String,
    val browserName: String,
    val browserVersion: String,
    val acceptHeader: String,
    val screenWidthPoints: Int,
    val screenHeightPoints: Int,
    val screenPixelDensity: Int,
    val screenDensityFloat: Float,
    val utcOffsetMinutes: Int,
    val memoryTotalKbytes: String,
    val clientScreen: String,
    val timeZone: String
)

data class ConfigInfo(
    val appInstallData: String,
    val coldConfigData: String,
    val coldHashData: String,
    val hotHashData: String
)

data class UserInfo(
    val lockedSafetyMode: Boolean
)

data class RequestInfo(
    val useSsl: Boolean,
    val internalExperimentFlags: List<String>,
    val consistencyTokenJars: List<String>
)

data class PlaybackContext(
    val contentPlaybackContext: ContentPlaybackContext
)

data class ContentPlaybackContext(
    val currentUrl: String,
    val vis: Int,
    val splay: Boolean,
    val autoCaptionsDefaultOn: Boolean,
    val autonavState: String,
    val html5Preference: String,
    val signatureTimestamp: Int,
    val autoplay: Boolean,
    val autonav: Boolean,
    val referer: String,
    val lactMilliseconds: String
)

data class ServiceIntegrityDimensions(
    val poToken: String
)

// 3. API INTERFACE

interface ComplexMovieApi {
    
    @POST("api/v1/player")
    @Headers("Content-Type: application/json")
    suspend fun getMovieData(
        @Header("accept") accept: String = "*/*",
        @Header("accept-language") acceptLanguage: String = "en-US,en;q=0.9",
        @Header("origin") origin: String = "https://example.com",
        @Header("referer") referer: String = "https://example.com",
        @Header("user-agent") userAgent: String,
        @Query("prettyPrint") prettyPrint: String = "false",
        @Body request: ComplexRequest
    ): MovieResponse
}

// 4. REPOSITORY WITH COMPLEX REQUEST BUILDING

class ComplexMovieRepository(private val api: ComplexMovieApi) {
    
    suspend fun getMovieDetails(videoId: String): Result<MovieResponse> {
        return try {
            val request = buildComplexRequest(videoId)
            
            val response = withContext(Dispatchers.IO) {
                api.getMovieData(
                    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                    request = request
                )
            }
            Result.success(response)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildComplexRequest(videoId: String): ComplexRequest {
        return ComplexRequest(
            context = RequestContext(
                client = ClientInfo(
                    hl = "en",
                    gl = "US", 
                    remoteHost = "192.168.1.1",
                    deviceMake = "Android",
                    deviceModel = "Pixel",
                    visitorData = "sample_visitor_data",
                    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                    clientName = "WEB",
                    clientVersion = "2.20250915.00.00",
                    osName = "Android",
                    osVersion = "14.0",
                    originalUrl = "https://example.com/watch?v=$videoId",
                    platform = "MOBILE",
                    clientFormFactor = "UNKNOWN_FORM_FACTOR",
                    configInfo = ConfigInfo(
                        appInstallData = "sample_install_data",
                        coldConfigData = "sample_cold_config",
                        coldHashData = "sample_cold_hash", 
                        hotHashData = "sample_hot_hash"
                    ),
                    userInterfaceTheme = "USER_INTERFACE_THEME_LIGHT",
                    browserName = "Chrome",
                    browserVersion = "120.0.0.0",
                    acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                    screenWidthPoints = 1080,
                    screenHeightPoints = 1920,
                    screenPixelDensity = 3,
                    screenDensityFloat = 3.0f,
                    utcOffsetMinutes = 330,
                    memoryTotalKbytes = "8000000",
                    clientScreen = "WATCH",
                    timeZone = "Asia/Kolkata"
                ),
                user = UserInfo(
                    lockedSafetyMode = false
                ),
                request = RequestInfo(
                    useSsl = true,
                    internalExperimentFlags = emptyList(),
                    consistencyTokenJars = emptyList()
                )
            ),
            videoId = videoId,
            params = "sample_params",
            playbackContext = PlaybackContext(
                contentPlaybackContext = ContentPlaybackContext(
                    currentUrl = "/watch?v=$videoId",
                    vis = 5,
                    splay = false,
                    autoCaptionsDefaultOn = false,
                    autonavState = "STATE_NONE",
                    html5Preference = "HTML5_PREF_WANTS",
                    signatureTimestamp = 20347,
                    autoplay = true,
                    autonav = true,
                    referer = "https://example.com/search",
                    lactMilliseconds = "-1"
                )
            ),
            racyCheckOk = false,
            contentCheckOk = false,
            serviceIntegrityDimensions = ServiceIntegrityDimensions(
                poToken = "sample_po_token"
            )
        )
    }
}

// 5. SIMPLIFIED DATA MODELS FOR UI (Extract what you need)

data class SimpleMovieData(
    val id: String,
    val title: String,
    val duration: String,
    val viewCount: String,
    val thumbnailUrl: String,
    val description: String,
    val streamingUrls: List<StreamingUrl>
)

data class StreamingUrl(
    val quality: String,
    val url: String,
    val mimeType: String,
    val bitrate: Int
)

// 6. MAPPER TO CONVERT COMPLEX TO SIMPLE

object MovieDataMapper {
    
    fun mapToSimpleMovieData(response: MovieResponse): SimpleMovieData {
        val movieDetails = response.movieDetails
        val streamingInfo = response.streamingInfo
        
        return SimpleMovieData(
            id = movieDetails.movieId,
            title = movieDetails.title,
            duration = formatDuration(movieDetails.lengthSeconds.toLongOrNull() ?: 0L),
            viewCount = formatViewCount(movieDetails.viewCount.toLongOrNull() ?: 0L),
            thumbnailUrl = movieDetails.thumbnail.thumbnails.lastOrNull()?.url ?: "",
            description = movieDetails.shortDescription,
            streamingUrls = extractStreamingUrls(streamingInfo)
        )
    }
    
    private fun extractStreamingUrls(streamingInfo: StreamingInfo): List<StreamingUrl> {
        return streamingInfo.adaptiveFormats
            .filter { it.qualityLabel != null }
            .map { format ->
                StreamingUrl(
                    quality = format.qualityLabel ?: "unknown",
                    url = "streaming_url_placeholder", // Would be extracted from format
                    mimeType = format.mimeType,
                    bitrate = format.bitrate
                )
            }
            .distinctBy { it.quality }
            .sortedByDescending { it.bitrate }
    }
    
    private fun formatDuration(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }
    
    private fun formatViewCount(views: Long): String {
        return when {
            views >= 1_000_000_000 -> "${views / 1_000_000_000}B views"
            views >= 1_000_000 -> "${views / 1_000_000}M views"
            views >= 1_000 -> "${views / 1_000}K views"
            else -> "$views views"
        }
    }
}

// 7. VIEWMODEL WITH COMPLEX DATA HANDLING

class ComplexMovieViewModel(
    private val repository: ComplexMovieRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(ComplexMovieUiState())
        private set
    
    fun loadMovieData(videoId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            
            repository.getMovieDetails(videoId)
                .onSuccess { response ->
                    // Convert complex response to simple UI data
                    val simpleData = MovieDataMapper.mapToSimpleMovieData(response)
                    
                    uiState = uiState.copy(
                        movieData = simpleData,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
        }
    }
}

data class ComplexMovieUiState(
    val movieData: SimpleMovieData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// 8. NETWORK MODULE WITH COMPLEX CONFIGURATION

object ComplexNetworkModule {
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        // Handle null values gracefully
        .serializeNulls() 
        // Custom date format if needed
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        // Add timeout for complex requests
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.example-movie-service.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val complexMovieApi: ComplexMovieApi = retrofit.create(ComplexMovieApi::class.java)
    val complexMovieRepository = ComplexMovieRepository(complexMovieApi)
}

// 9. UI COMPOSABLE FOR COMPLEX DATA

@Composable
fun ComplexMovieScreen(
    videoId: String,
    viewModel: ComplexMovieViewModel = viewModel { 
        ComplexMovieViewModel(ComplexNetworkModule.complexMovieRepository) 
    }
) {
    val uiState = viewModel.uiState
    
    LaunchedEffect(videoId) {
        viewModel.loadMovieData(videoId)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            uiState.movieData != null -> {
                ComplexMovieDetails(movieData = uiState.movieData)
            }
        }
    }
}

@Composable
private fun ComplexMovieDetails(movieData: SimpleMovieData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Movie thumbnail
            AsyncImage(
                model = movieData.thumbnailUrl,
                contentDescription = movieData.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        
        item {
            // Movie details
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = movieData.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        Text(
                            text = movieData.duration,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = movieData.viewCount,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = movieData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        item {
            // Streaming quality options
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Available Qualities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    movieData.streamingUrls.forEach { streamingUrl ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = streamingUrl.quality,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Text(
                                text = "${streamingUrl.bitrate / 1000} kbps",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// 10. PROFESSIONAL TIPS FOR COMPLEX JSON

/*
GSON TIPS FOR COMPLEX JSON:

1. @SerializedName for different field names:
   @SerializedName("video_id") val videoId: String

2. Custom deserializers for complex nested objects:
   @JsonAdapter(CustomStreamingDataDeserializer::class)

3. Handle optional/null fields:
   val optionalField: String? = null

4. Use @Expose to control serialization:
   @Expose val includedField: String
   @Expose(serialize = false) val readOnlyField: String

5. Custom naming policy in GsonBuilder:
   .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)

6. Type adapters for complex types:
   .registerTypeAdapter(Date::class.java, CustomDateDeserializer())

7. Lenient parsing for malformed JSON:
   .setLenient()

8. Handle unknown fields gracefully - they're ignored by default
*/