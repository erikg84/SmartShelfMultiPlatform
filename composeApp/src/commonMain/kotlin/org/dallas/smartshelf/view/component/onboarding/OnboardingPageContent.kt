package org.dallas.smartshelf.view.component.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.view.component.SmartSpacer

@Composable
fun OnboardingPageContent(page: Int) {
    when (page) {
        0 -> {
            Column {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "Welcome to SmartShelf!",
                    style = MaterialTheme.typography.headlineSmall
                )
                SmartSpacer(MaterialTheme.dimens.dp16)
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "We need some permissions to give you the best experience.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        1 -> {
            Column {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "Grant Camera Permission",
                    style = MaterialTheme.typography.headlineSmall
                )
                SmartSpacer(MaterialTheme.dimens.dp16)
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "By allowing these permissions you will gain access to critical app functionality.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        2 -> {
            Column {
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "Thank you for using SmartShelf!",
                    style = MaterialTheme.typography.headlineSmall
                )
                SmartSpacer(MaterialTheme.dimens.dp16)
                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = "You're all set to use the app.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
