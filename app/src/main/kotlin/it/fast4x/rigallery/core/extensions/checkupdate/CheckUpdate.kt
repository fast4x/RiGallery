package it.fast4x.rigallery.core.extensions.checkupdate

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.fast4x.rigallery.R
import it.fast4x.rigallery.core.extensions.components.DefaultDialog
import it.fast4x.rigallery.core.util.getVersionCode
import java.io.File

@Composable
fun CheckAvailableNewVersion(
    onDismiss: () -> Unit,
    updateAvailable: (Boolean) -> Unit
) {
    var updatedProductName = ""
    var updatedVersionName = ""
    var updatedVersionCode = 0
    val file = File(LocalContext.current.filesDir, "UpdatedVersionCode.ver")
    if (file.exists()) {
        val dataText = file.readText().substring(0, file.readText().length - 1).split("-")
        updatedVersionCode =
            try {
                dataText.first().toInt()
            } catch (e: Exception) {
                0
            }
        updatedVersionName = if(dataText.size == 3) dataText[1] else ""
        updatedProductName =  if(dataText.size == 3) dataText[2] else ""
    }

    //println("CheckAvailableNewVersion UpdatedVersionCode: $updatedVersionCode versionCode: ${getVersionCode()}")

    if (updatedVersionCode > getVersionCode()) {
        //if (updatedVersionCode > BuildConfig.VERSION_CODE)
        NewVersionDialog(
            updatedVersionName = updatedVersionName,
            updatedVersionCode = updatedVersionCode,
            updatedProductName = updatedProductName,
            onDismiss = onDismiss
        )
        updateAvailable(true)
    } else {
        updateAvailable(false)
          onDismiss()
    }
}

@Composable
fun NewVersionDialog (
    updatedProductName: String,
    updatedVersionName: String,
    updatedVersionCode: Int,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    DefaultDialog(
        onDismiss = { onDismiss() },
        content = {
            BasicText(
                text = stringResource(R.string.update_available),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(10.dp))
            BasicText(
                text = String.format(stringResource(R.string.app_update_dialog_new),updatedVersionName),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(10.dp))
            BasicText(
                text = stringResource(R.string.actions_you_can_do),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = stringResource(R.string.open_the_github_releases_web_page_and_download_latest_version),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Image(
                    painter = painterResource(R.drawable.globe),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            onDismiss()
                            uriHandler.openUri("https://github.com/fast4x/RiGallery/releases/latest")
                        }
                )
            }
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .padding(bottom = 20.dp)
//                    .fillMaxWidth()
//            ) {
//                BasicText(
//                    text = stringResource(R.string.download_latest_version_from_github_you_will_find_the_file_in_the_notification_area_and_you_can_install_by_clicking_on_it),
//                    style = MaterialTheme.typography.titleSmall,
//                    maxLines = 4,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.fillMaxWidth(0.8f)
//                )
//                Image(
//                    painter = painterResource(R.drawable.downloaded),
//                    contentDescription = null,
//                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
//                    modifier = Modifier
//                        .size(30.dp)
//                        .clickable {
//                            onDismiss()
//                            //TODO Download from github specific cpu version
//                            //uriHandler.openUri("https://github.com/fast4x/RiGallery/releases/download/$updatedVersionName/rimusic-full-release.apk")
//                        }
//                )
//            }
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .padding(bottom = 20.dp)
//                    .fillMaxWidth()
//            ) {
//                BasicText(
//                    text = stringResource(R.string.f_droid_users_can_wait_for_the_update_info),
//                    style = typography().xxs.semiBold.copy(color = colorPalette().textSecondary),
//                    maxLines = 4,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }
        }

    )
}