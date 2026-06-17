package org.secuso.privacyfriendlypaindiary.activities

import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.secuso.pfacore.model.DrawerElement
import org.secuso.pfacore.model.DrawerMenu
import org.secuso.pfacore.ui.activities.DrawerActivity
import org.secuso.privacyfriendlypaindiary.R
import org.secuso.privacyfriendlypaindiary.database.PainDiaryDatabaseService
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager

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

        section {
            action {
                name = getString(R.string.pref_reset)
                icon = R.drawable.ic_delete
                onClick = { confirmReset() }
            }
        }

        defaultDrawerSection(this)
    }

    override fun isActiveDrawerElement(element: DrawerElement): Boolean = false

    /**
     * Asks the user to confirm before wiping all diary entries and user details. This used to live
     * in the old settings screen, which the PFA-Core migration removed, so it is offered here as a
     * drawer action instead.
     */
    private fun confirmReset() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.pref_reset_warning)
            .setPositiveButton(R.string.confirm) { _, _ -> performReset() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun performReset() {
        val context = applicationContext
        // The database reset touches disk, so keep it off the main thread.
        Thread {
            PainDiaryDatabaseService.getInstance(context).reinitializeDatabase(context)
            PrefManager(context).userID = AbstractPersistentObject.INVALID_OBJECT_ID
            runOnUiThread {
                // Restart from a clean main screen so every screen rebinds to the empty database.
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }.start()
    }
}
