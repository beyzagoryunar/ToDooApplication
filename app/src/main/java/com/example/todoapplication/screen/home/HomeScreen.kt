package com.example.todoapplication.screen.home

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.todoapplication.screen.settings.ThemeSelectionDialog
import com.example.todoapplication.task.Task
import com.example.todoapplication.ui.theme.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var isSearchMode by remember { mutableStateOf(false) }
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var tasktoEdit by remember { mutableStateOf<Task?>(null) }
    var taskToShowDetails by remember { mutableStateOf<Task?>(null) }
    val hasLoggedOut by homeViewModel.logoutState.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }

    homeState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(message = errorMessage)
        }
    }
    LaunchedEffect(Unit) {
        homeViewModel.loadUserName()
        homeViewModel.getTasks()
        // snackbarHostState.showSnackbar("Giriş başarılı!") // İsteğe bağlı, kaldırılabilir
    }
    LaunchedEffect(hasLoggedOut) {
        if (hasLoggedOut) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationDrawerContent(
                    fullName = homeState.fullName,
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        homeViewModel.logout()
                        scope.launch {
                            snackbarHostState.showSnackbar("Çıkış başarılı!")
                        }
                    },
                    onThemeClick = { showThemeDialog = true },
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        },
        gesturesEnabled = drawerState.isOpen,
        content = {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (isSearchMode) {
                        SearchTopBar(
                            searchQuery = homeState.searchQuery,
                            onSearchQueryChange = { newQuery ->
                                homeViewModel.onSearchQueryChange(newQuery)
                            },
                            onBackClick = {
                                isSearchMode = false
                                homeViewModel.onSearchQueryChange("")
                            }
                        )
                    } else {
                        HomeTopBar(
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onSearchClick = { isSearchMode = true },
                            onThemeClick = { showThemeDialog = true }
                        )
                    }
                },
                floatingActionButton = {
                    HomeFAB(onClick = {
                        showDialog = true
                        tasktoEdit = null
                    })
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            homeState.fullName?.let { WelcomeSection(name = it) }
                            Spacer(modifier = Modifier.height(24.dp))
                            TasksSection(
                                tasks = homeState.tasks,
                                onTaskClick = { task ->
                                    homeViewModel.toggleDone(task)
                                },
                                onDeleteClick = { task ->
                                    homeViewModel.deleteTask(task.id)
                                },
                                onEditClick = { task ->
                                    tasktoEdit = task
                                },
                                onShowDetailsClick = { task ->
                                    taskToShowDetails = task
                                }
                            )
                        }
                        if (homeState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary // <<< DEĞİŞİKLİK
                            )
                        }
                    }
                }
            )

            if (showDialog) {
                AddEditTaskDialog(
                    task = tasktoEdit,
                    onDismiss = {
                        showDialog = false
                        tasktoEdit = null
                    },
                    onSave = { title, description ->
                        if (tasktoEdit == null) {
                            homeViewModel.addTask(title, description)
                        } else {
                            homeViewModel.updateTask(tasktoEdit!!, title, description)
                        }
                        showDialog = false
                        tasktoEdit = null
                    }
                )
            }
            if (tasktoEdit != null) {
                AddEditTaskDialog(
                    task = tasktoEdit,
                    onDismiss = { tasktoEdit = null },
                    onSave = { title, description ->
                        homeViewModel.updateTask(tasktoEdit!!, title, description)
                        tasktoEdit = null
                    }
                )
            }
            taskToShowDetails?.let { task ->
                TaskDetailsDialog(
                    task = task,
                    onDismiss = { taskToShowDetails = null }
                )
            }
            if (showThemeDialog) {
                ThemeSelectionDialog(onDismiss = { showThemeDialog = false })
            }
        }
    )
}

@Composable
fun HomeTopBar(onMenuClick: () -> Unit,
               onSearchClick: () -> Unit,
               onThemeClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background) // <<< DEĞİŞİKLİK
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
        }
        Row(verticalAlignment = Alignment.CenterVertically) { // <<< DEĞİŞİKLİK
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(onClick = { /* TODO: Bildirimler */ }) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onBackground // <<< DEĞİŞİKLİK
                )
            }
            IconButton(onClick = onThemeClick) {
                Icon(Icons.Default.Create, contentDescription = "Change Theme", tint = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground) // <<< DEĞİŞİKLİK
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Görevlerde ara...") }, // <<< DEĞİŞİKLİK
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors( // <<< DEĞİŞİKLİK
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun WelcomeSection(name: String) {
    Column {
        Text(
            text = "Merhaba, $name!", // <<< DEĞİŞİKLİK
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TasksSection(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onEditClick: (Task) -> Unit,
    onShowDetailsClick: (Task) -> Unit
) {
    Text(text = "BUGÜNÜN GÖREVLERİ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // <<< DEĞİŞİKLİK
    Spacer(modifier = Modifier.height(8.dp))
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onToggleComplete = { onTaskClick(task) },
                onDeleteClick = onDeleteClick,
                onEditClick = { onEditClick(task) },
                onShowDetailsClick = { onShowDetailsClick(task) }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onDeleteClick: (Task) -> Unit,
    onEditClick: () -> Unit,
    onShowDetailsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowDetailsClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp // <<< DEĞİŞİKLİK
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isComplete,
                onCheckedChange = { onToggleComplete(task) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    modifier = Modifier.alpha(if (task.isComplete) 0.6f else 1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!task.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        modifier = Modifier.alpha(if (task.isComplete) 0.6f else 1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Task",
                    tint = MaterialTheme.colorScheme.secondary // <<< DEĞİŞİKLİK
                )
            }
            IconButton(onClick = { onDeleteClick(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.secondary // <<< DEĞİŞİKLİK
                )
            }
        }
    }
}

@Composable
fun HomeFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary, // <<< DEĞİŞİKLİK
        contentColor = MaterialTheme.colorScheme.onPrimary, // <<< DEĞİŞİKLİK
        shape = CircleShape
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskDialog(
    task: Task? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var taskTitle by remember(task) { mutableStateOf(task?.name ?: "") }
    var taskDescription by remember(task) { mutableStateOf(task?.description ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Text(text = if (task != null) "Görevi Düzenle" else "Yeni Görev Ekle")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Görev Başlığı") },
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        if (taskTitle.isNotEmpty()) {
                            IconButton(onClick = { taskTitle = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Açıklama (İsteğe Bağlı)") },
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        if (taskDescription.isNotEmpty()) {
                            IconButton(onClick = { taskDescription = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (taskTitle.isNotBlank()) onSave(
                            taskTitle,
                            taskDescription
                        )
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(taskTitle, taskDescription) },
                enabled = taskTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // <<< DEĞİŞİKLİK
            ) {
                Text(if (task != null) "Güncelle" else "Ekle")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("İptal", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun NavigationDrawerContent(
    fullName: String?,
    onLogoutClick: () -> Unit,
    onThemeClick: () -> Unit,
    closeDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // <<< DEĞİŞİKLİK
            .padding(16.dp)
    ) {
        UserProfileSection(name = fullName ?: "User")
        Spacer(modifier = Modifier.height(24.dp))

        DrawerMenuItem(
            label = "Tema",
            icon = Icons.Default.Create, // <<< DEĞİŞİKLİK
            onClick = {
                onThemeClick()
                closeDrawer()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        DrawerMenuItem(
            label = "ÇIKIŞ YAP", // <<< DEĞİŞİKLİK
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            onClick = onLogoutClick
        )
    }
}

@Composable
fun UserProfileSection(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer), // <<< DEĞİŞİKLİK
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "?",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer // <<< DEĞİŞİKLİK
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.medium, // <<< DEĞİŞİKLİK
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant, // <<< DEĞİŞİKLİK
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TaskDetailsDialog(
    task: Task,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = {
            Text(
                text = task.name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        text = {
            Text(
                text = task.description ?: "Açıklama yok.",
                fontSize = 16.sp,
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // <<< DEĞİŞİKLİK
            ) {
                Text("Kapat")
            }
        }
    )
}