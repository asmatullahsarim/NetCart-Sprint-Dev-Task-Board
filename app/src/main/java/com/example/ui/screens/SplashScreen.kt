package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.R
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.MutedSlate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
  val scale = remember { Animatable(0.78f) }
  val alpha = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    launch {
      scale.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
      )
    }
    launch {
      alpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
      )
    }
    delay(1800L)
    onTimeout()
  }

  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .background(MidnightNavy)
        .statusBarsPadding()
        .navigationBarsPadding()
        .testTag("splash_screen"),
  ) {
    Column(
      modifier =
        Modifier
          .align(Alignment.Center)
          .scale(scale.value)
          .alpha(alpha.value)
          .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_netcart_foreground),
        contentDescription = stringResource(R.string.cd_app_logo),
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(110.dp).clip(RoundedCornerShape(24.dp)),
      )

      Spacer(modifier = Modifier.height(22.dp))

      Text(
        text = stringResource(R.string.app_name),
        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
        color = CrispWhite,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = stringResource(R.string.app_subtitle),
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
        color = ElectricCyan,
        textAlign = TextAlign.Center,
      )
    }

    Row(
      modifier =
        Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 28.dp)
          .alpha(alpha.value),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = Icons.Filled.Lock,
        contentDescription = null,
        tint = MutedSlate,
        modifier = Modifier.size(13.dp),
      )
      Text(
        text = stringResource(R.string.splash_footer),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        color = MutedSlate,
      )
    }
  }
}
