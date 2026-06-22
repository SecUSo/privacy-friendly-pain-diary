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
package org.secuso.privacyfriendlypaindiary.helpers

import android.content.Context
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.ui.dialog.show
import org.secuso.privacyfriendlypaindiary.R

/**
 * Small bridge so the Java activities can show a yes/no dialog through PFA-Core's dialog DSL
 * instead of building a Google [com.google.android.material.dialog.MaterialAlertDialogBuilder]
 * directly. The library still uses Material under the hood, but the call goes through PFA-Core.
 */
object Dialogs {

    /**
     * Shows a confirm dialog with the given [message]. [onConfirm] runs when the user accepts;
     * [onCancel] (if given) runs when the user taps cancel. Dismissing the dialog does nothing.
     */
    @JvmStatic
    @JvmOverloads
    fun confirm(context: Context, message: String, onConfirm: Runnable, onCancel: Runnable? = null) {
        AbortElseDialog.build(context) {
            title = { "" }
            content = { message }
            acceptLabel = context.getString(R.string.confirm)
            abortLabel = context.getString(R.string.cancel)
            onElse = { onConfirm.run() }
            onAbort = { onCancel?.run() }
            handleDismiss = false
        }.show()
    }
}
