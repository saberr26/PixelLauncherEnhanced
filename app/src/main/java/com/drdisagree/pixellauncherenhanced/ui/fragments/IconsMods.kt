package com.drdisagree.pixellauncherenhanced.ui.fragments

import androidx.preference.ListPreference
import androidx.preference.SwitchPreference
import com.drdisagree.pixellauncherenhanced.R
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_COLOR
import com.drdisagree.pixellauncherenhanced.data.config.RPrefs
import com.drdisagree.pixellauncherenhanced.ui.base.ControlledPreferenceFragmentCompat
import com.drdisagree.pixellauncherenhanced.utils.LauncherUtils.restartLauncher

class IconsMods : ControlledPreferenceFragmentCompat() {

    override val title: String
        get() = getString(R.string.fragment_icons_title)

    override val backButtonEnabled: Boolean
        get() = true

    override val layoutResource: Int
        get() = R.xml.icons_mods

    override val hasMenu: Boolean
        get() = false

    override val themeResource: Int
        get() = R.style.PrefsThemeCollapsingToolbar

    override fun updateScreen(key: String?) {
        super.updateScreen(key)

        when (key) {
            THEMED_ICON_CUSTOM_COLOR -> {
                if (!RPrefs.getBoolean(THEMED_ICON_CUSTOM_COLOR)) {
                    context?.restartLauncher()
                }
            }
        }
        
        val monetPairs = listOf(
            "themed_icon_fg_light_use_monet"  to "themed_icon_fg_light_monet_role",
            "themed_icon_bg_light_use_monet"  to "themed_icon_bg_light_monet_role",
            "folder_light_use_monet"          to "folder_light_monet_role",
            "themed_icon_fg_dark_use_monet"   to "themed_icon_fg_dark_monet_role",
            "themed_icon_bg_dark_use_monet"   to "themed_icon_bg_dark_monet_role",
            "folder_dark_use_monet"           to "folder_dark_monet_role",
        )

        monetPairs.forEach { (toggleKey, listKey) ->
            val toggle = findPreference<SwitchPreference>(toggleKey)
            val list   = findPreference<ListPreference>(listKey)
            list?.isEnabled = toggle?.isChecked == true
        }
    }
}
