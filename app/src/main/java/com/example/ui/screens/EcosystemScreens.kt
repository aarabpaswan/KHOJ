package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.KhojAccentOrange
import com.example.ui.theme.KhojPrimaryBlue
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.KhojScreen

@Composable
fun KhojAiScreen(viewModel: BrowserViewModel) {
    val messages by viewModel.aiMessages.collectAsState()
    var inputPrompt by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(KhojAccentOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = KhojAccentOrange, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("KHOJ AI Companion", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Research, summarize & explore", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("Ask KHOJ AI anything...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input"),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputPrompt.isNotBlank()) {
                            val p = inputPrompt
                            inputPrompt = ""
                            viewModel.sendAiPrompt(p)
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(KhojPrimaryBlue)
                        .testTag("ai_send_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.khoj_launcher_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Separate from traditional web search. Use KHOJ AI to analyze articles, query Nepalese cultural heritage, or generate code solutions.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(messages) { (sender, content) ->
                val isAi = sender == "KHOJ AI"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 300.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isAi) 4.dp else 16.dp,
                            bottomEnd = if (isAi) 16.dp else 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAi) MaterialTheme.colorScheme.surface else KhojPrimaryBlue
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = sender,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAi) KhojAccentOrange else Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = content,
                                fontSize = 14.sp,
                                color = if (isAi) MaterialTheme.colorScheme.onSurface else Color.White,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KhojDomainsScreen(viewModel: BrowserViewModel) {
    val domainQuery by viewModel.domainQuery.collectAsState()
    val domainResults by viewModel.domainResults.collectAsState()
    var input by remember { mutableStateOf(domainQuery) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("KHOJ Domain Registry", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Official Nepal .np & Global TLDs", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        ".np domains are officially free for Nepalese citizens and registered companies under Mercantile Communications. Manage DNS, nameservers, and renewals here.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Search domain (e.g. myhimalaya)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("domain_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.searchDomain(input) },
                    modifier = Modifier.height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KhojPrimaryBlue)
                ) {
                    Text("Check")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (domainResults.isNotEmpty()) {
                Text("Availability Results", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                Spacer(modifier = Modifier.height(8.dp))

                domainResults.forEach { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.domain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(res.pricePerYear, fontSize = 12.sp, color = if (res.isAvailable) Color(0xFF10B981) else Color.Red)
                                Text(res.registrarPartner, fontSize = 10.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    viewModel.openNewTab("https://register.com.np")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KhojPrimaryBlue)
                            ) {
                                Text(if (res.extension == ".np") "Register .np" else "Register")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AnnouncementsScreen(viewModel: BrowserViewModel) {
    val announcements = remember { viewModel.repository.getAnnouncements() }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(KhojScreen.HOME) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("KHOJ News & Updates", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(announcements) { ann ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text(ann.category, fontSize = 11.sp) }
                            )
                            Text(ann.date, fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(ann.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = KhojPrimaryBlue)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ann.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}
