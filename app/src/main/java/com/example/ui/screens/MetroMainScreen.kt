package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.AccentPickerDialog
import com.example.ui.components.MetroAppBar
import com.example.ui.components.MetroPivotHeader
import com.example.ui.theme.LocalMetroAccent
import com.example.ui.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

@Composable
fun MetroMainScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val accent by viewModel.accentFlow.collectAsState()
    val isDark by viewModel.isDarkFlow.collectAsState()
    val tempUnit by viewModel.tempUnitFlow.collectAsState()
    val pinnedCityIds by viewModel.pinnedCityIdsFlow.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val pivotTitles = remember {
        listOf("weather", "forecast", "hourly", "details", "live tiles", "places")
    }

    val pagerState = rememberPagerState(initialPage = 0) { pivotTitles.size }

    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            viewModel.applyGpsLocation(loc)
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                                snackbarHostState.showSnackbar("Loaded weather for your current location")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Could not detect location. Please try again.")
                            }
                        }
                    }
                    .addOnFailureListener {
                        scope.launch {
                            snackbarHostState.showSnackbar("Location lookup failed.")
                        }
                    }
            } catch (_: SecurityException) {
                scope.launch {
                    snackbarHostState.showSnackbar("Location permission required.")
                }
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Location permission denied. You can search any city manually.")
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("metro_main_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            MetroAppBar(
                onRefresh = { viewModel.refresh() },
                onSearchClick = {
                    scope.launch { pagerState.animateScrollToPage(5) }
                },
                onPlacesClick = {
                    scope.launch { pagerState.animateScrollToPage(5) }
                },
                onAccentPickerClick = { viewModel.openAccentPicker() },
                onToggleUnits = { viewModel.toggleUnits() },
                tempUnit = tempUnit,
                isDark = isDark,
                onToggleDark = { viewModel.toggleDarkTheme() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Iconic Metro Panorama Pivot Header
            MetroPivotHeader(
                titles = pivotTitles,
                currentPage = pagerState.currentPage,
                subtitle = uiState.currentReport?.city?.name?.let { "$it • METRO" } ?: "METRO WEATHER",
                onTitleClick = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )

            // Horizontal Swipeable Pivot Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("metro_horizontal_pager")
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> WeatherNowScreen(
                        report = uiState.currentReport,
                        isLoading = uiState.isLoading,
                        tempUnit = tempUnit,
                        onCityClick = {
                            scope.launch { pagerState.animateScrollToPage(5) }
                        },
                        onGoToForecast = {
                            scope.launch { pagerState.animateScrollToPage(1) }
                        }
                    )

                    1 -> ForecastScreen(
                        report = uiState.currentReport,
                        tempUnit = tempUnit
                    )

                    2 -> HourlyScreen(
                        report = uiState.currentReport,
                        tempUnit = tempUnit
                    )

                    3 -> DetailsScreen(
                        report = uiState.currentReport,
                        tempUnit = tempUnit
                    )

                    4 -> LiveTilesScreen(
                        currentReport = uiState.currentReport,
                        savedCities = uiState.savedCities,
                        pinnedReports = uiState.pinnedReports,
                        tempUnit = tempUnit,
                        onSelectCity = { city ->
                            viewModel.selectCity(city)
                            scope.launch { pagerState.animateScrollToPage(0) }
                        },
                        onManageCities = {
                            scope.launch { pagerState.animateScrollToPage(5) }
                        }
                    )

                    5 -> PlacesScreen(
                        currentCityId = uiState.currentReport?.city?.id,
                        savedCities = uiState.savedCities,
                        pinnedCityIds = pinnedCityIds,
                        searchQuery = uiState.searchQuery,
                        isSearching = uiState.isSearching,
                        searchResults = uiState.searchResults,
                        tempUnit = tempUnit,
                        pinnedReports = uiState.pinnedReports,
                        onQueryChange = { q -> viewModel.searchLocations(q) },
                        onSelectCity = { city ->
                            viewModel.selectCity(city)
                            scope.launch { pagerState.animateScrollToPage(0) }
                        },
                        onAddCity = { city ->
                            viewModel.addCity(city)
                            scope.launch { pagerState.animateScrollToPage(0) }
                        },
                        onRemoveCity = { id -> viewModel.removeCity(id) },
                        onTogglePin = { id -> viewModel.togglePin(id) },
                        onRequestGps = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    // Windows Phone Accent Color Picker Dialog
    if (uiState.isAccentPickerOpen) {
        AccentPickerDialog(
            currentAccent = accent,
            isDark = isDark,
            onAccentSelected = { newAccent ->
                viewModel.setAccent(newAccent)
            },
            onToggleDark = { viewModel.toggleDarkTheme() },
            onDismiss = { viewModel.closeAccentPicker() }
        )
    }
}
