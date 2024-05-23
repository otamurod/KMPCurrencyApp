package presentation.screen.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.model.CurrencyCode
import domain.model.CurrencyItemResponse
import domain.model.CurrencyType
import domain.util.DisplayResult
import domain.util.RequestState
import kmpcurrencyapp.composeapp.generated.resources.Res
import kmpcurrencyapp.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CurrencyInputs(
    source: RequestState<CurrencyItemResponse>,
    target: RequestState<CurrencyItemResponse>,
    onSwitchClick: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrencyView(
            placeholder = "From",
            currency = source,
            onClick = {
                if (source.isSuccess()) {
                    onCurrencyTypeSelect(
                        CurrencyType.Source(
                            currencyCode = CurrencyCode.valueOf(
                                source.getSuccessData().code
                            )
                        )
                    )
                }
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        IconButton(
            modifier = Modifier.padding(top = 24.dp)
                .graphicsLayer {
                    rotationY = animatedRotation
                },
            onClick = {
                animationStarted = !animationStarted
                onSwitchClick()
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch Icon",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        CurrencyView(
            placeholder = "To",
            currency = target,
            onClick = {
                if (target.isSuccess()) {
                    onCurrencyTypeSelect(
                        CurrencyType.Target(
                            currencyCode = CurrencyCode.valueOf(
                                target.getSuccessData().code
                            )
                        )
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<CurrencyItemResponse>,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeholder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .height(54.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            currency.DisplayResult(
                onSuccess = { data ->
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(CurrencyCode.valueOf(data.code).flag),
                        tint = Color.Unspecified,
                        contentDescription = "County Flag",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CurrencyCode.valueOf(data.code).name,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = Color.White
                    )
                }
            )
        }
    }
}