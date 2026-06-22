package org.secuso.privacyfriendlypaindiary.activities

import android.content.Intent
import org.secuso.pfacore.model.DrawerElement
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.pfacore.ui.activities.DrawerActivity
import org.secuso.privacyfriendlypaindiary.R

/**
 * Shared base for the activities that show the navigation drawer. The drawer itself,
 * the toolbar and the back handling are provided by the PFA-Core [DrawerActivity];
 * here we only declare the app-specific drawer entries. The Tutorial, Help, Settings,
 * About and error-report entries are added by [defaultDrawerSection] from the library.
 */
abstract class BaseActivity : DrawerActivity() {

    override fun drawer(): DrawerMenu = DrawerMenu.build {
        name = getString(R.string.app_name)
        icon = R.mipmap.ic_launcher

        section {
            activity {
                name = getString(R.string.action_main)
                icon = R.drawable.ic_menu_home
                clazz = MainActivity::class.java
                extras = { it.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP } }
            }
            activity {
                name = getString(R.string.user_details)
                icon = R.drawable.ic_user
                clazz = UserDetailsActivity::class.java
            }
            activity {
                name = getString(R.string.export_pdf)
                icon = R.drawable.ic_export_pdf
                clazz = ExportPDFActivity::class.java
            }
        }

        defaultDrawerSection(this)
    }

    override fun isActiveDrawerElement(element: DrawerElement): Boolean = false
}
