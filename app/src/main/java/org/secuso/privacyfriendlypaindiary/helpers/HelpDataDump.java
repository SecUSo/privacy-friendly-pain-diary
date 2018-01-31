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

import android.content.Context;

import org.secuso.privacyfriendlypaindiary.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Karola Marky
 * @version 20171016
 * Class structure taken from tutorial at http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * last access 27th October 2016
 */

public class HelpDataDump {

    private Context context;

    public HelpDataDump(Context context) {
        this.context = context;
    }

    public LinkedHashMap<String, List<String>> getDataGeneral() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        List<String> general = new ArrayList<String>();
        general.add(context.getResources().getString(R.string.help_whatis_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_whatis), general);

        List<String> addEntry = new ArrayList<String>();
        addEntry.add(context.getResources().getString(R.string.help_add_entry_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_add_entry), addEntry);

        List<String> information = new ArrayList<String>();
        information.add(context.getResources().getString(R.string.help_entry_information_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_entry_information), information);

        List<String> personalInfo = new ArrayList<String>();
        personalInfo.add(context.getResources().getString(R.string.help_userdetails_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_userdetails), personalInfo);

        List<String> privacy = new ArrayList<String>();
        privacy.add(context.getResources().getString(R.string.help_privacy_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_privacy), privacy);

        List<String> permission = new ArrayList<String>();
        permission.add(context.getResources().getString(R.string.help_permission_answer));
        expandableListDetail.put(context.getResources().getString(R.string.help_permission), permission);



        return expandableListDetail;
    }

}
