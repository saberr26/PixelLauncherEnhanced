package com.drdisagree.pixellauncherenhanced.xposed.mods

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_CUSTOM_COLOR_DARK
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_CUSTOM_COLOR_LIGHT
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_BG_COLOR_DARK
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_BG_COLOR_LIGHT
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_COLOR
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_FG_COLOR_DARK
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_CUSTOM_FG_COLOR_LIGHT
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_FG_LIGHT_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_FG_LIGHT_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_BG_LIGHT_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_BG_LIGHT_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_FG_DARK_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_FG_DARK_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_BG_DARK_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.THEMED_ICON_BG_DARK_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_LIGHT_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_LIGHT_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_DARK_USE_MONET
import com.drdisagree.pixellauncherenhanced.data.common.Constants.FOLDER_DARK_MONET_ROLE
import com.drdisagree.pixellauncherenhanced.xposed.HookRes.Companion.resParams
import com.drdisagree.pixellauncherenhanced.xposed.ModPack
import com.drdisagree.pixellauncherenhanced.xposed.mods.LauncherUtils.Companion.reloadIcons
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.XposedHook.Companion.findClass
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.hookMethod
import com.drdisagree.pixellauncherenhanced.xposed.utils.XPrefs.Xprefs
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class ThemedIconsColor(context: Context) : ModPack(context) {

    private var mCustomThemedIconColor = false
    private var mIconFgColorLight = Color.BLACK
    private var mIconBgColorLight = Color.WHITE
    private var mIconFgColorDark = Color.WHITE
    private var mIconBgColorDark = Color.BLACK
    private var mFolderColorLight = Color.WHITE
    private var mFolderColorDark = Color.BLACK

    // Material You prefs
    private var mIconFgLightUseMonet = false
    private var mIconFgLightMonetRole = "system_accent1_500"
    private var mIconBgLightUseMonet = false
    private var mIconBgLightMonetRole = "system_accent2_100"
    private var mIconFgDarkUseMonet = false
    private var mIconFgDarkMonetRole = "system_accent1_200"
    private var mIconBgDarkUseMonet = false
    private var mIconBgDarkMonetRole = "system_accent2_700"
    private var mFolderLightUseMonet = false
    private var mFolderLightMonetRole = "system_accent2_100"
    private var mFolderDarkUseMonet = false
    private var mFolderDarkMonetRole = "system_accent2_700"

    private var packageName: String? = null

    override fun updatePrefs(vararg key: String) {
        Xprefs.apply {
            mCustomThemedIconColor = getBoolean(THEMED_ICON_CUSTOM_COLOR, false)
            mIconFgColorLight = getInt(THEMED_ICON_CUSTOM_FG_COLOR_LIGHT, Color.BLACK)
            mIconBgColorLight = getInt(THEMED_ICON_CUSTOM_BG_COLOR_LIGHT, Color.WHITE)
            mIconFgColorDark = getInt(THEMED_ICON_CUSTOM_FG_COLOR_DARK, Color.WHITE)
            mIconBgColorDark = getInt(THEMED_ICON_CUSTOM_BG_COLOR_DARK, Color.BLACK)
            mFolderColorLight = getInt(FOLDER_CUSTOM_COLOR_LIGHT, Color.WHITE)
            mFolderColorDark = getInt(FOLDER_CUSTOM_COLOR_DARK, Color.BLACK)

            // Material You role prefs
            mIconFgLightUseMonet = getBoolean(THEMED_ICON_FG_LIGHT_USE_MONET, false)
            mIconFgLightMonetRole = getString(THEMED_ICON_FG_LIGHT_MONET_ROLE, "system_accent1_500") ?: "system_accent1_500"
            mIconBgLightUseMonet = getBoolean(THEMED_ICON_BG_LIGHT_USE_MONET, false)
            mIconBgLightMonetRole = getString(THEMED_ICON_BG_LIGHT_MONET_ROLE, "system_accent2_100") ?: "system_accent2_100"
            mIconFgDarkUseMonet = getBoolean(THEMED_ICON_FG_DARK_USE_MONET, false)
            mIconFgDarkMonetRole = getString(THEMED_ICON_FG_DARK_MONET_ROLE, "system_accent1_200") ?: "system_accent1_200"
            mIconBgDarkUseMonet = getBoolean(THEMED_ICON_BG_DARK_USE_MONET, false)
            mIconBgDarkMonetRole = getString(THEMED_ICON_BG_DARK_MONET_ROLE, "system_accent2_700") ?: "system_accent2_700"
            mFolderLightUseMonet = getBoolean(FOLDER_LIGHT_USE_MONET, false)
            mFolderLightMonetRole = getString(FOLDER_LIGHT_MONET_ROLE, "system_accent2_100") ?: "system_accent2_100"
            mFolderDarkUseMonet = getBoolean(FOLDER_DARK_USE_MONET, false)
            mFolderDarkMonetRole = getString(FOLDER_DARK_MONET_ROLE, "system_accent2_700") ?: "system_accent2_700"
        }

        when (key.firstOrNull()) {
            THEMED_ICON_CUSTOM_COLOR,
            THEMED_ICON_CUSTOM_FG_COLOR_LIGHT,
            THEMED_ICON_CUSTOM_BG_COLOR_LIGHT,
            THEMED_ICON_CUSTOM_FG_COLOR_DARK,
            THEMED_ICON_CUSTOM_BG_COLOR_DARK,
            FOLDER_CUSTOM_COLOR_LIGHT,
            FOLDER_CUSTOM_COLOR_DARK,
            THEMED_ICON_FG_LIGHT_USE_MONET,
            THEMED_ICON_FG_LIGHT_MONET_ROLE,
            THEMED_ICON_BG_LIGHT_USE_MONET,
            THEMED_ICON_BG_LIGHT_MONET_ROLE,
            THEMED_ICON_FG_DARK_USE_MONET,
            THEMED_ICON_FG_DARK_MONET_ROLE,
            THEMED_ICON_BG_DARK_USE_MONET,
            THEMED_ICON_BG_DARK_MONET_ROLE,
            FOLDER_LIGHT_USE_MONET,
            FOLDER_LIGHT_MONET_ROLE,
            FOLDER_DARK_USE_MONET,
            FOLDER_DARK_MONET_ROLE -> {
                replaceResources(packageName)
                reloadIcons()
            }
        }
    }

    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        packageName = loadPackageParam.packageName
        replaceResources(packageName)

        val sdCardAvailableReceiverClass =
            findClass("com.android.launcher3.model.SdCardAvailableReceiver")

        sdCardAvailableReceiverClass
            .hookMethod("onReceive")
            .parameters(
                Context::class.java,
                Intent::class.java
            )
            .runAfter { param ->
                val intent = param.args[1] as Intent
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    replaceResources(packageName)
                    reloadIcons()
                }
            }
    }
    
    @SuppressLint("DiscouragedApi")
    private fun resolveMonetColor(roleName: String): Int? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
        val resId = mContext.resources.getIdentifier(roleName, "color", "android")
        if (resId == 0) return null
        return mContext.resources.getColor(resId, mContext.theme)
    }
    
    private fun effectiveColor(useMonet: Boolean, monetRole: String, staticColor: Int): Int {
        return if (useMonet) resolveMonetColor(monetRole) ?: staticColor else staticColor
    }

    @SuppressLint("DiscouragedApi")
    private fun replaceResources(packageName: String?) {
        if (!mCustomThemedIconColor || packageName == null) return

        val resParam = resParams[packageName] ?: return

        val isDarkTheme = (mContext.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val iconFgColor = if (isDarkTheme)
            effectiveColor(mIconFgDarkUseMonet, mIconFgDarkMonetRole, mIconFgColorDark)
        else
            effectiveColor(mIconFgLightUseMonet, mIconFgLightMonetRole, mIconFgColorLight)

        val iconBgColor = if (isDarkTheme)
            effectiveColor(mIconBgDarkUseMonet, mIconBgDarkMonetRole, mIconBgColorDark)
        else
            effectiveColor(mIconBgLightUseMonet, mIconBgLightMonetRole, mIconBgColorLight)

        val folderColor = if (isDarkTheme)
            effectiveColor(mFolderDarkUseMonet, mFolderDarkMonetRole, mFolderColorDark)
        else
            effectiveColor(mFolderLightUseMonet, mFolderLightMonetRole, mFolderColorLight)

        mContext.resources.getIdentifier(
            "themed_icon_background_color", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, iconBgColor) }

        mContext.resources.getIdentifier(
            "qsb_icon_tint_quaternary_mono", "color", packageName
        ).takeIf { it != 0 } ?: mContext.resources.getIdentifier(
            "themed_icon_color", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, iconFgColor) }

        mContext.resources.getIdentifier(
            "themed_badge_icon_background_color", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, iconBgColor) }

        mContext.resources.getIdentifier(
            "themed_badge_icon_color", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, iconFgColor) }

        mContext.resources.getIdentifier(
            "folder_preview_light", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, folderColor) }

        mContext.resources.getIdentifier(
            "folder_preview_dark", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, folderColor) }

        mContext.resources.getIdentifier(
            "folder_background_light", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, folderColor) }

        mContext.resources.getIdentifier(
            "folder_background_dark", "color", packageName
        ).takeIf { it != 0 }?.let { resParam.res.setReplacement(it, folderColor) }
    }
}
