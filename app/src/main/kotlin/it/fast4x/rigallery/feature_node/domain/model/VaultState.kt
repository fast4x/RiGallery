package it.fast4x.rigallery.feature_node.domain.model

import androidx.compose.runtime.Stable
import it.fast4x.rigallery.feature_node.presentation.vault.VaultScreens

@Stable
data class VaultState(
    val vaults: List<Vault> = emptyList(),
    val isLoading: Boolean = true
) {

    fun getStartScreen(): String {
        return (if (isLoading) VaultScreens.LoadingScreen else if (vaults.isEmpty()) {
            VaultScreens.VaultSetup
        } else {
            VaultScreens.VaultDisplay
        }).invoke()
    }
}