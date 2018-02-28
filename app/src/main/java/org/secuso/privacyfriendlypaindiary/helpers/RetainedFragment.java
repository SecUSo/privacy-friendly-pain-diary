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
package org.secuso.privacyfriendlypaindiary.helpers;

import android.app.Fragment;
import android.os.Bundle;

import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;

/**
 * Fragment that is retained across configuration changes such as screen
 * orientation changes. Holds a diary entry object encapsulating all information
 * already entered by the user when creating a new diary entry (see also:
 * {@link org.secuso.privacyfriendlypaindiary.activities.DiaryEntryActivity}).
 *
 * @author Susanne Felsen
 * @version 20180228
 */
public class RetainedFragment extends Fragment {

    // diaryEntry object we want to retain
    private DiaryEntryInterface diaryEntry;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setDiaryEntry(DiaryEntryInterface diaryEntry) {
        this.diaryEntry = diaryEntry;
    }

    public DiaryEntryInterface getDiaryEntry() {
        return diaryEntry;
    }

}