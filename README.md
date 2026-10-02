
A small Android app.
You enter an amount, choose a currency and installments, and get a receipt.
It is a mock payment. No real money is used.

Built with Kotlin and Jetpack Compose.

## How to run

1. Open the project folder in Android Studio.
2. Wait until Gradle Sync is done.
3. Press Run. Use an emulator or a phone with Android 8.0 or newer.

### Exchange rates (bonus)

The Convert screen needs a free API key.

1. Make a free account on https://freecurrencyapi.com and copy your key.
2. Open the file `local.properties` in the main project folder.
3. Add this line:

```
CURRENCY_API_KEY=your_key_here
```

4. Sync Gradle and run the app again.

The key is not uploaded to Git.
Without a key the app still works. Only the Convert screen shows an error.

## What is done

Payment screen:
- Enter an amount. It can't be empty, it must be more than 0, and it can have only 2 digits after the dot.
- Choose ILS or USD. ILS is the default.
- A live clock with the phone time.
- Installments on or off. Choose 2 to 12. The picker is off when installments are off.
- A short summary.
- Continue opens the receipt. Cancel clears the form.
- A settings icon. The form keeps its values when you go to settings and back, and when you rotate the phone.

Settings screen:
- Show installments and Show currency. Both are on at the start.
- Changes show right away on the payment screen.
- Hidden installments = one payment. Hidden currency = ILS, and the receipt does not show it.
- A Back button at the top.
- Settings stay while the app is open, also after Finish.

Receipt screen:
- Shows the amount, the currency (if shown) and the installments (if on).
- Finish opens a new, clean form.
- The phone Back button goes back to the form with the same values.

Bonus - Exchange rates:
- A Convert button on the receipt.
- Live rates for 5 currencies: currency, rate and converted amount.
- Loading, error and Retry.
- Going back keeps the receipt data.

Bonus - Design:
- Same spacing on all screens, light and dark mode.
- Clear error messages.
- Help for screen readers (TalkBack).

## Architecture decision

The task asked that the settings stay after Finish, and that the form is cleaned.

Instead of one big ViewModel with a reset function, I put the settings in a shared repository, and I gave each screen its own ViewModel.

So Finish just opens a new payment screen with a new ViewModel.
The form is clean because it is new.
The settings stay because they were never there.

## Folders

```
com.example.hometask_liadsalhi
 ├─ data
 │   ├─ remote          CurrencyApi, LatestRatesResponse
 │   └─ repository      InMemoryPaymentRepository, RemoteExchangeRateRepository
 ├─ di                  AppContainer
 ├─ domain
 │   ├─ model           Currency, AppSettings, Payment, AmountValidation, ConvertedAmount
 │   ├─ repository      PaymentRepository, ExchangeRateRepository
 │   └─ usecase         ValidateAmountUseCase, CreatePaymentUseCase, ConvertAmountUseCase
 ├─ presentation
 │   ├─ convert         ConvertUiState, ConvertViewModel, ConvertScreen
 │   ├─ navigation      AppNavHost
 │   ├─ payment         PaymentUiState, PaymentViewModel, PaymentComponents, LiveClock, PaymentScreen
 │   ├─ receipt         ReceiptUiState, ReceiptViewModel, ReceiptScreen
 │   └─ settings        SettingsViewModel, SettingsScreen
 ├─ ui.theme            Color, Theme, Type
 ├─ MainActivity.kt
 └─ PaymentApp.kt
```
