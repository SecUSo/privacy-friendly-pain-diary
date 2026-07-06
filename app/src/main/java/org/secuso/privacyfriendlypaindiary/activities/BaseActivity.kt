/*
    This file is part of Privacy Friendly Pain Diary.

    Privacy Friendly Pain Diary is free software: you can redistribute it
    and/or modify it under the terms of the GNU General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
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
