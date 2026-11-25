package com.shinewhite.weave.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shinewhite.weave.localization.LocalStrings
import com.shinewhite.weave.localization.PersianStrings
import com.shinewhite.weave.settings.AppLanguage
import com.shinewhite.weave.settings.AppSettings
import com.shinewhite.weave.ui.theme.AppTheme
import com.shinewhite.weave.ui.theme.Dimens
import com.shinewhite.weave.ui.theme.LocalDarkTheme

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

    SettingsScreenContent(
        settings = settings,
        isDarkTheme = LocalDarkTheme.current,
        onBack = onBack,
        onLanguageChange = viewModel::setLanguage,
        onDarkThemeChange = viewModel::setDarkTheme
    )
}

@Composable
fun SettingsScreenContent(
    settings: AppSettings,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkThemeChange: (Boolean) -> Unit
) {
    val strings = LocalStrings.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = Dimens.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(Dimens.minTouchTarget)) {
                    Icon(
                        imageVector = WeaveIcons.Back,
                        contentDescription = strings.back,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = strings.settings,
                    modifier = Modifier.padding(start = Dimens.paddingSmall),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            SectionTitle(strings.language)
            SectionCard {
                Column {
                    LanguageRow(
                        label = "English",
                        selected = settings.language == AppLanguage.ENGLISH,
                        onSelect = { onLanguageChange(AppLanguage.ENGLISH) }
                    )
                    LanguageRow(
                        label = "فارسی",
                        selected = settings.language == AppLanguage.PERSIAN,
                        onSelect = { onLanguageChange(AppLanguage.PERSIAN) }
                    )
                }
            }

            SectionTitle(strings.appearance)
            SectionCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.buttonHeight)
                        .toggleable(
                            value = isDarkTheme,
                            onValueChange = onDarkThemeChange,
                            role = Role.Switch
                        )
                        .padding(horizontal = Dimens.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.darkMode,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(checked = isDarkTheme, onCheckedChange = null)
                }
            }
        }
    }
}

@Composable
private fun LanguageRow(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.buttonHeight)
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(horizontal = Dimens.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(Dimens.paddingMedium))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreenContent(
            settings = AppSettings(),
            isDarkTheme = LocalDarkTheme.current,
            onBack = {},
            onLanguageChange = {},
            onDarkThemeChange = {}
        )
    }
}

@Preview(name = "Persian RTL", showBackground = true)
@Composable
private fun SettingsScreenPersianPreview() {
    AppTheme {
        CompositionLocalProvider(
            LocalStrings provides PersianStrings,
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            SettingsScreenContent(
                settings = AppSettings(language = AppLanguage.PERSIAN),
                isDarkTheme = LocalDarkTheme.current,
                onBack = {},
                onLanguageChange = {},
                onDarkThemeChange = {}
            )
        }
    }
}