package com.example.composecurrencyconverter.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.ui.theme.PayPayTestTheme
import com.example.composecurrencyconverter.ui.viewmodels.MainViewModel
import com.example.composecurrencyconverter.utils.AppUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyConverterActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PayPayTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
                    LaunchedEffect(Unit) {
                        viewModel.exchangeRates.collect {
                            when (it) {
                                is Result.Success -> rates = it.data?.rates ?: emptyMap()
                                is Result.Empty -> Unit
                                is Result.Failure -> AppUtils.ToastMsg.showMsg(
                                    this@CurrencyConverterActivity,
                                    it.message
                                )

                                is Result.Loading -> Unit
                            }
                        }
                    }
                    CurrencyConvertUI(rates, onDropDownItemClick = {
                        viewModel.getExchangeRates()
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConvertUI(
    rates: Map<String, Double>,
    showDropDown: Boolean = false,
    inputText: String = "",
    onDropDownItemClick: (() -> Unit)?
) {

    var userInputText by remember { mutableStateOf(inputText) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var isDropdownShown by remember { mutableStateOf(showDropDown) }
    var dropdownTextSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // EditText on top
        OutlinedTextField(
            value = userInputText,
            onValueChange = { newText -> userInputText = newText },
            label = { Text("Enter Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true
        )

        // Spinner (Dropdown) bottom right of EditText
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.BottomEnd)
        ) {
            OutlinedButton(
                onClick = { isDropdownShown = !isDropdownShown },
                modifier = Modifier
                    .background(Color.Transparent)
                    .onGloballyPositioned {
                        dropdownTextSize = it.size.toSize()
                    }
            ) {
                Text(text = selectedCurrency, modifier = Modifier.padding(start = 10.dp))
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = isDropdownShown,
                onDismissRequest = {
                    isDropdownShown = false
                },
                modifier = Modifier
                    .width(with(LocalDensity.current) { dropdownTextSize.width.toDp() })
                    .height(600.dp)
            ) {
                rates.keys.forEach { currencyCode ->
                    DropdownMenuItem(text = {
                        Text(
                            text = currencyCode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.Center)
                        )
                    }, onClick = {
                        selectedCurrency = currencyCode
                        isDropdownShown = false
                        onDropDownItemClick?.invoke()
                    })
                }
            }
        }

        if (userInputText.isEmpty().not())
            LazyVerticalGrid(
                GridCells.Fixed(3),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                items(rates.size) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        ),
                        shape = CardDefaults.elevatedShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray,
                            contentColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                                .wrapContentSize(align = Alignment.Center)
                        ) {
                            Text(
                                text = (rates.values.toMutableList()[index] * userInputText.toDouble()).toString(),
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                maxLines = 1
                            )
                            Text(
                                text = rates.keys.toMutableList()[index],
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
    }
}


@Preview(showBackground = true)
@Composable
fun CurrencyConvertPreview() {
    PayPayTestTheme {
        val rates = mutableMapOf<String, Double>()
        rates["AED"] = 100.1
        rates["AEK"] = 100.1
        rates["AEL"] = 100.1
        rates["AEM"] = 100.1
        rates["USD"] = 100.1
        rates["PKR"] = 100.1
        rates["INR"] = 100.1
        rates["INK"] = 100.1
        rates["INS"] = 100.1
        rates["INT"] = 100.1
        rates["INP"] = 100.1
        rates.putAll(
            mapOf(
                "AED" to 10.2
            )
        )
        CurrencyConvertUI(
            rates = rates.toMap(),
            showDropDown = false,
            inputText = "1",
            onDropDownItemClick = null
        )
    }
}

