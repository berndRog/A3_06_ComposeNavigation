package de.rogallab.mobile.ui.people.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.rogallab.mobile.R
import de.rogallab.mobile.ui.errors.validatePhone

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputPhone(
   phone: String?,                           // State ↓
   onPhoneChange: (String) -> Unit,          // Event ↑
) {
   val label = stringResource(R.string.phone)

   // Immediate Feedback: error messages from string resources
   // Cache the string resource to avoid redundant lookups.
   var resourceError = stringResource(R.string.errorPhone)
   val errorPhone = remember { resourceError }
   var isError by rememberSaveable { mutableStateOf(false) }
   var errorText by rememberSaveable { mutableStateOf("") }

   var isFocus by rememberSaveable { mutableStateOf(false) }
   val focusManager = LocalFocusManager.current
   val keyboardController = LocalSoftwareKeyboardController.current

   // Reusable Validation Functions: Validate the input when it changes
   val validate: (String?) -> Unit = { input ->
      val (e, t) = validatePhone(input, errorPhone)
      isError = e
      errorText = t
   }

   OutlinedTextField(
      modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth()
         .onFocusChanged { focusState ->
            if (!focusState.isFocused && isFocus) validate(phone)
            isFocus = focusState.isFocused
         },
      value = phone ?: "",
      onValueChange = { onPhoneChange(it) }, // Event ↑
      label = { Text(text = label) },
      textStyle = MaterialTheme.typography.bodyLarge,
      leadingIcon = {
         Icon(imageVector = Icons.Outlined.Phone,
              contentDescription = label)
      },
      singleLine = true,
      keyboardOptions = KeyboardOptions(
         keyboardType = KeyboardType.Phone,
         imeAction = ImeAction.Done
      ),
      // check when keyboard action is clicked
      keyboardActions = KeyboardActions(
         onDone = {
            keyboardController?.hide()
            validate(phone)
            if(!isError) {
               keyboardController?.hide()
               focusManager.clearFocus()
            }
         }
      ),
      isError = isError,
      supportingText = {
         if (isError) {
            Text(
               text = errorText,
               color = MaterialTheme.colorScheme.error
            )
         }
      },
      trailingIcon = {
         if (isError)
            Icon(
               imageVector = Icons.Filled.Error,
               contentDescription = errorText,
               tint = MaterialTheme.colorScheme.error
            )
      },
   )
}