package com.sayler666.gina.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(
    visibleMonth: CalendarMonth,
    onSelectDate: (LocalDate) -> Unit,
    onTodayClick: () -> Unit
) {
    val showPopup = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Row(
                modifier = Modifier.clickable {
                    showPopup.value = true
                },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = visibleMonth.yearMonth.displayText())
                Icon(
                    Icons.Filled.ArrowDropDown,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                YearMonthSwitcherDialog(
                    showPopup = showPopup.value,
                    currentYearMonth = visibleMonth.yearMonth,
                    onDismiss = {
                        scope.launch {
                            delay(100)
                            showPopup.value = false
                        }
                    },
                    onSelectDate = {
                        onSelectDate(it)
                    }
                )
            }
        }, actions = {
            IconButton(onClick = {
                onTodayClick()
            }) {
                Icon(Icons.Filled.CalendarToday, null)
                Text(
                    text = LocalDate.now().dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        })
}
