package com.example.studiobook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studiobook.data.model.Booking
import com.example.studiobook.data.model.Equipment
import com.example.studiobook.data.model.Studio
import com.example.studiobook.ui.component.EquipmentItemCard
import com.example.studiobook.ui.component.StatSummaryCard
import com.example.studiobook.ui.component.StudioItemCard
import com.example.studiobook.ui.viewmodel.StudioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioSimulationScreen(
    viewModel: StudioViewModel
) {
    val studios by viewModel.studios.collectAsStateWithLifecycle()
    val equipments by viewModel.equipments.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val hasSeenWelcome by viewModel.hasSeenWelcome.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var studioToBook by remember { mutableStateOf<Studio?>(null) }
    var showAddStudioDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (!hasSeenWelcome) {
        Surface(modifier = Modifier.fillMaxSize()) {
            WelcomeScreen(
                onGetStarted = { viewModel.continueFromWelcome() }
            )
        }
        return
    }

    if (!isLoggedIn) {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginScreen(
                initialEmail = userEmail,
                onBackToWelcome = { viewModel.backToWelcome() },
                onLoginSuccess = { inputEmail, inputPass ->
                    val res = viewModel.login(inputEmail, inputPass)
                    if (res.isSuccess) {
                        selectedTab = 0
                    }
                    res
                }
            )
        }
        return
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000L)
            viewModel.refreshAndPurgeExpiredBookings()
        }
    }

    val filteredStudios = remember(studios, searchQuery) {
        if (searchQuery.isBlank()) studios
        else studios.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.facilities.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "StudioBook",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAdmin) "Mode Admin • ACC Transfer & Kelola Studio" else "Rental Studio Musik & Tracking Slot Jadwal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Studio & Alat") },
                    label = { Text("Studio & Alat") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Jadwal") },
                    label = { Text("Jadwal") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeTab(
                modifier = Modifier.padding(innerPadding),
                userName = viewModel.currentUser.name,
                isAdmin = isAdmin,
                studios = studios,
                bookings = bookings,
                onGoToStudios = { selectedTab = 1 },
                onGoToBookings = { selectedTab = 2 }
            )
            1 -> StudiosTab(
                modifier = Modifier.padding(innerPadding),
                studios = filteredStudios,
                equipments = equipments,
                searchQuery = searchQuery,
                isAdmin = isAdmin,
                onSearchChange = viewModel::onSearchQueryChange,
                onOpenBookingDialog = { studio -> studioToBook = studio },
                onOpenAddStudio = { showAddStudioDialog = true },
                onToggleStudio = viewModel::toggleStudioAvailability,
                onDeleteStudio = { id ->
                    viewModel.deleteStudio(id)
                    scope.launch { snackbarHostState.showSnackbar("Studio dihapus") }
                }
            )
            2 -> BookingsTab(
                modifier = Modifier.padding(innerPadding),
                bookings = bookings,
                isAdmin = isAdmin,
                onConfirm = { id ->
                    viewModel.confirmBooking(id)
                    scope.launch {
                        snackbarHostState.showSnackbar("Transfer di-ACC admin (tidak bisa dibatalkan)")
                    }
                },
                onDeleteBooking = { id ->
                    viewModel.deleteBooking(id)
                    scope.launch {
                        snackbarHostState.showSnackbar("Jadwal dihapus oleh admin")
                    }
                }
            )
            else -> ProfileTab(
                modifier = Modifier.padding(innerPadding),
                name = viewModel.currentUser.name,
                email = userEmail,
                isAdmin = isAdmin,
                onLogout = {
                    selectedTab = 0
                    viewModel.logout()
                }
            )
        }

        studioToBook?.let { targetStudio ->
            BookingFormDialog(
                studio = targetStudio,
                equipments = equipments,
                onDismiss = { studioToBook = null },
                onConfirmBooking = { bandName, bookerName, date, slot, duration, selectedEquipment ->
                    val result = viewModel.createBooking(
                        studio = targetStudio,
                        equipment = selectedEquipment,
                        bandName = bandName,
                        bookerName = bookerName,
                        date = date,
                        timeSlot = slot,
                        durationHours = duration
                    )
                    result.onSuccess {
                        studioToBook = null
                        selectedTab = 2
                        scope.launch {
                            snackbarHostState.showSnackbar("Booking tercatat • Menunggu ACC transfer admin")
                        }
                    }.onFailure { error ->
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                error.message ?: "Booking gagal!"
                            )
                        }
                    }
                }
            )
        }

        if (showAddStudioDialog) {
            AddStudioDialog(
                onDismiss = { showAddStudioDialog = false },
                onSave = { name, capacity, price, facilities ->
                    viewModel.addStudio(name, capacity, price, facilities)
                    showAddStudioDialog = false
                    scope.launch { snackbarHostState.showSnackbar("Studio baru ditambahkan") }
                }
            )
        }
    }
}

private fun normalizeValidDate(rawDate: String): String? {
    val trimmed = rawDate.trim()
    if (!trimmed.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) return null
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        val parsed = sdf.parse(trimmed) ?: return null
        sdf.format(parsed)
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun StudioBookingCard(
    booking: Booking,
    isAdmin: Boolean,
    onConfirm: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val isConfirmed = booking.status.equals("Confirmed", ignoreCase = true)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.studioName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isConfirmed)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = if (isConfirmed) "Confirmed" else "Pending",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isConfirmed)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Band: ${booking.bandName} • Pemesan: ${booking.bookerName}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.timeSlot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!booking.equipmentName.isNullOrBlank()) {
                Text(
                    text = "Tambahan Alat: ${booking.equipmentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Transfer ke admin • Non-refundable",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: Rp${booking.totalPrice}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isAdmin) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isConfirmed) {
                            Button(
                                onClick = { onConfirm(booking.id) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACC")
                            }
                        }
                        IconButton(onClick = { onDelete(booking.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus Jadwal",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val stageBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0E12),
            Color(0xFF161822),
            Color(0xFF0B0C10)
        )
    )
    val accentGold = Color(0xFFFFB703)
    val accentCoral = Color(0xFFFF5A5F)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(stageBg)
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = null,
                            tint = accentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "STUDIOBOOK • LIVE SLOT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(10.dp, 20.dp, 14.dp, 24.dp, 12.dp)
                    heights.forEachIndexed { idx, h ->
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(h)
                                .background(
                                    color = if (idx == 3) accentCoral else accentGold.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "NO CLASH.\nJUST PURE\nVOLUME.",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight
                )

                Text(
                    text = "Booking jadwal latihan band, sewa gear tambahan, dan pantau slot real-time tanpa tabrakan jadwal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.72f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.06f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ATURAN MAIN STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentGold
                        )
                        Text(
                            text = "✓ Transfer ke admin dulu lalu di-ACC (non-refundable)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                        Text(
                            text = "✓ Jadwal yang lewat tanggal & jam otomatis terhapus",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                        Text(
                            text = "✓ Konfirmasi slot & kelola studio terpusat oleh Admin",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Masuk & Login",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Siap cek jadwal studio untuk sesi latihan berikutnya?",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(
    initialEmail: String,
    onBackToWelcome: () -> Unit,
    onLoginSuccess: (email: String, password: String) -> Result<Unit>
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackToWelcome) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali ke Welcome",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kembali ke Welcome")
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Masuk ke StudioBook",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Masukkan email dan password akun kamu untuk melanjutkan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email / Gmail") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        onLoginSuccess(email, password).onFailure {
                            errorMessage = it.message ?: "Gagal login"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}

@Composable
private fun BookingFormDialog(
    studio: Studio,
    equipments: List<Equipment>,
    onDismiss: () -> Unit,
    onConfirmBooking: (
        bandName: String,
        bookerName: String,
        date: String,
        slot: String,
        duration: Int,
        equipment: Equipment?
    ) -> Unit
) {
    val slots = listOf("10:00 - 12:00", "13:00 - 15:00", "16:00 - 18:00", "19:00 - 21:00")
    var bandName by remember { mutableStateOf("") }
    var bookerName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-11-25") }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
    var hasTransferred by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val estimatedTotal = (studio.pricePerHour * 2) + (selectedEquipment?.pricePerSession ?: 0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Booking ${studio.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bandName,
                    onValueChange = {
                        bandName = it
                        formError = null
                    },
                    label = { Text("Nama Band *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bookerName,
                    onValueChange = {
                        bookerName = it
                        formError = null
                    },
                    label = { Text("Nama Pemesan *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        formError = null
                    },
                    label = { Text("Tanggal Main (YYYY-MM-DD) *") },
                    placeholder = { Text("2026-11-25") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Pilih Jam / Slot (2 Jam) *:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                slots.chunked(2).forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = {
                                    selectedSlot = slot
                                    formError = null
                                },
                                label = { Text(slot, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Text(
                    text = "Tambahan Alat (Opsional):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                FilterChip(
                    selected = selectedEquipment == null,
                    onClick = { selectedEquipment = null },
                    label = { Text("Tanpa Alat Tambahan", style = MaterialTheme.typography.labelSmall) }
                )
                equipments.take(2).forEach { eq ->
                    FilterChip(
                        selected = selectedEquipment?.id == eq.id,
                        onClick = { selectedEquipment = eq },
                        label = {
                            Text(
                                "${eq.name} (+Rp${eq.pricePerSession})",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }

                HorizontalDivider()
                Text(
                    text = "Total Transfer ke Admin: Rp$estimatedTotal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Transfer ke rekening Admin dulu, lalu tunggu ACC admin. Tidak bisa dibatalkan/refund.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hasTransferred,
                        onCheckedChange = {
                            hasTransferred = it
                            formError = null
                        }
                    )
                    Text(
                        text = "Saya sudah transfer & setuju non-refundable",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (formError != null) {
                    Text(
                        text = formError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bandName.isBlank()) {
                        formError = "Nama band wajib diisi!"
                        return@Button
                    }
                    if (bookerName.isBlank()) {
                        formError = "Nama pemesan wajib diisi!"
                        return@Button
                    }
                    val cleanDate = normalizeValidDate(date)
                    if (cleanDate == null) {
                        formError = "Tanggal wajib diisi dengan format YYYY-MM-DD (misal: 2026-11-25)!"
                        return@Button
                    }
                    val chosenSlot = selectedSlot
                    if (chosenSlot == null) {
                        formError = "Jam / slot waktu wajib dipilih!"
                        return@Button
                    }
                    if (!hasTransferred) {
                        formError = "Centang konfirmasi transfer ke admin dulu!"
                        return@Button
                    }

                    onConfirmBooking(
                        bandName.trim(),
                        bookerName.trim(),
                        cleanDate,
                        chosenSlot,
                        2,
                        selectedEquipment
                    )
                }
            ) {
                Text("Kirim Booking")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun AddStudioDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, capacity: Int, price: Int, facilities: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("5") }
    var priceText by remember { mutableStateOf("75000") }
    var facilities by remember { mutableStateOf("Drum Set, Amp Gitar & Bass, Mixer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Ruang Studio (Admin)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Studio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Kapasitas (Orang)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Harga per Jam (Rp)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = facilities,
                    onValueChange = { facilities = it },
                    label = { Text("Fasilitas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name,
                            capacityText.toIntOrNull() ?: 4,
                            priceText.toIntOrNull() ?: 60000,
                            facilities
                        )
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun HomeTab(
    modifier: Modifier,
    userName: String,
    isAdmin: Boolean,
    studios: List<Studio>,
    bookings: List<Booking>,
    onGoToStudios: () -> Unit,
    onGoToBookings: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isAdmin) "Halo Admin, $userName!" else "Halo, $userName!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isAdmin)
                            "Cek transfer masuk, ACC jadwal band, dan kelola ketersediaan studio."
                        else
                            "Transfer ke admin dulu lalu tunggu ACC, dan pantau jadwal aktif band kamu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSummaryCard(
                    title = "Ruang Studio",
                    value = "${studios.size} Room",
                    subtitle = "${studios.count { it.isAvailable }} tersedia",
                    icon = Icons.Default.LibraryMusic,
                    modifier = Modifier.weight(1f)
                )
                StatSummaryCard(
                    title = "Jadwal Aktif",
                    value = "${bookings.size} Slot",
                    subtitle = "${bookings.count { it.status == "Confirmed" }} ter-ACC",
                    icon = Icons.Default.EventAvailable,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Akses Cepat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onGoToStudios,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Pilih Studio & Booking Sekarang")
                    }
                    OutlinedButton(
                        onClick = onGoToBookings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lihat Tracking Slot Jadwal")
                    }
                }
            }
        }
    }
}

@Composable
private fun StudiosTab(
    modifier: Modifier,
    studios: List<Studio>,
    equipments: List<Equipment>,
    searchQuery: String,
    isAdmin: Boolean,
    onSearchChange: (String) -> Unit,
    onOpenBookingDialog: (Studio) -> Unit,
    onOpenAddStudio: () -> Unit,
    onToggleStudio: (String) -> Unit,
    onDeleteStudio: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari studio atau fasilitas (drum, amp...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Ruang Studio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isAdmin) {
                    Button(
                        onClick = onOpenAddStudio,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Tambah")
                    }
                }
            }
        }
        items(studios, key = { it.id }) { studio ->
            Column {
                StudioItemCard(
                    studio = studio,
                    onBookClick = onOpenBookingDialog
                )
                if (isAdmin) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onToggleStudio(studio.id) }) {
                            Text(if (studio.isAvailable) "Set Perawatan" else "Set Tersedia")
                        }
                        IconButton(onClick = { onDeleteStudio(studio.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus Studio",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sewa Tambahan Alat Musik",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(equipments, key = { it.id }) { equipment ->
            EquipmentItemCard(equipment = equipment)
        }
    }
}

@Composable
private fun BookingsTab(
    modifier: Modifier,
    bookings: List<Booking>,
    isAdmin: Boolean,
    onConfirm: (String) -> Unit,
    onDeleteBooking: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Tracking Slot & Status Booking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Jadwal yang sudah melewati batas tanggal & jam otomatis terhapus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (bookings.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Belum ada jadwal aktif saat ini.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                StudioBookingCard(
                    booking = booking,
                    isAdmin = isAdmin,
                    onConfirm = onConfirm,
                    onDelete = onDeleteBooking
                )
            }
        }
    }
}

@Composable
private fun ProfileTab(
    modifier: Modifier,
    name: String,
    email: String,
    isAdmin: Boolean,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Peran Akun",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isAdmin) "ADMIN" else "USER",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider()
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}