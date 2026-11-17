package com.moonwave.music.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.moonwave.music.R
import com.moonwave.music.ui.components.GlassmorphicCard
import com.moonwave.music.ui.theme.CardBorder
import com.moonwave.music.ui.theme.CosmicViolet
import com.moonwave.music.ui.theme.ElectricBlue
import com.moonwave.music.ui.theme.MidnightBlack
import com.moonwave.music.ui.theme.MoonlightMuted
import com.moonwave.music.ui.theme.MoonlightSilver
import com.moonwave.music.ui.theme.MoonlightSubtext
import com.moonwave.music.ui.theme.MoonlightWhite
import com.moonwave.music.ui.theme.SurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val developerName = "Mahfuz Rahman"
    val developerGithub = "https://github.com/rmdmahfuz727-sudo"
    val developerAvatar = "https://avatars.githubusercontent.com/u/289979958?v=4"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "About MOONWAVE",
                        style = MaterialTheme.typography.titleLarge,
                        color = MoonlightWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MoonlightWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // App Identity Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceCard)
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_moonwave_logo),
                        contentDescription = "MOONWAVE Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(72.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "MOONWAVE",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MoonlightWhite,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Version 1.0.0 (Release Edition)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricBlue
                )
            }

            // Lead Developer Card
            Text(
                text = "LEAD DEVELOPER",
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(developerGithub))
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .border(2.dp, ElectricBlue, CircleShape)
                            .background(SurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(developerAvatar)
                                .crossfade(true)
                                .placeholder(R.drawable.ic_moonwave_logo)
                                .error(R.drawable.ic_moonwave_logo)
                                .build(),
                            contentDescription = developerName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = developerName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MoonlightWhite
                        )
                        Text(
                            text = "@rmdmahfuz727-sudo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "GitHub Profile & Projects",
                            style = MaterialTheme.typography.labelSmall,
                            color = MoonlightSubtext
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Profile",
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tech Stack & Architecture
            Text(
                text = "ENGINEERING & ARCHITECTURE",
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TechRow(name = "Framework", value = "Kotlin & Jetpack Compose (Material 3)")
                    TechRow(name = "Audio Engine", value = "AndroidX Media3 ExoPlayer & Session")
                    TechRow(name = "Audio DSP", value = "Hardware 5/10-Band Equalizer & BassBoost")
                    TechRow(name = "Database", value = "Android SQLite Engine & Preferences DataStore")
                    TechRow(name = "Image Pipeline", value = "Coil 2.6.0 Asynchronous Loader")
                    TechRow(name = "Lyrics Provider", value = "LRCLIB Synced API & Engine")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Open-Source Notices & Attribution
            Text(
                text = "OPEN-SOURCE LICENSES & ATTRIBUTION",
                style = MaterialTheme.typography.labelSmall,
                color = MoonlightSubtext,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Reference Application Attribution",
                        style = MaterialTheme.typography.titleMedium,
                        color = MoonlightWhite
                    )
                    Text(
                        text = "MOONWAVE respects and acknowledges Mochy Music by @realidkroo (licensed under GNU General Public License v3.0). MOONWAVE is distributed in full compliance with GNU GPL v3.0.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoonlightSilver
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E283A))
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/realidkroo/Mochy-Music")
                                )
                                context.startActivity(intent)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Mochy Music Repository",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricBlue
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun TechRow(name: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = MoonlightSubtext
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MoonlightWhite
        )
    }
}
