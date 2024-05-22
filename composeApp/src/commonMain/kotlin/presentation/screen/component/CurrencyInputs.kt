package presentation.screen.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.model.CurrencyCode
import domain.model.CurrencyItemResponse
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
    onSwitchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrencyView(
            placeholder = "From",
            currency = source,
            onClick = {}
        )

        Spacer(modifier = Modifier.width(14.dp))

        IconButton(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onSwitchClick
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
            onClick = {}
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
            if (currency.isSuccess()) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(CurrencyCode.valueOf(currency.getSuccessData().code).flag),
                    tint = Color.Unspecified,
                    contentDescription = "County Flag",
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = CurrencyCode.valueOf(currency.getSuccessData().code).name,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color.White
                )
            }
        }
    }
}