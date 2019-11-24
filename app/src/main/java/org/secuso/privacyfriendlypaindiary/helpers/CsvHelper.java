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

import com.opencsv.CSVWriter;

import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Responsible for writing CSV.
 *
 * @author Pelmato
 * @version 20191111
 */
public class CsvHelper {

    private Context context;
    private SimpleDateFormat dateFormat;
    private Date startDate;
    private Date endDate;

    private List<DiaryEntryInterface> diaryEntries;
    private UserInterface user;

    public CsvHelper(Context context, Date startDate, Date endDate) {
        this.context = context;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.startDate = startDate;
        this.endDate = endDate;

        DBServiceInterface service = DBService.getInstance(context);
        diaryEntries = service.getDiaryEntriesByTimeSpan(startDate, endDate);

        long userID = new PrefManager(context).getUserID();
        if (userID == AbstractPersistentObject.INVALID_OBJECT_ID) {
            user = new User();
        } else {
            user = service.getUserByID(userID);
        }
    }

    public void writeCsv(File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file);
             CSVWriter writer = new CSVWriter(fileWriter)) {
            String[] headers = new String[]{"Date", "Condition", "Pain description", "Drug Intakes"};
            writer.writeNext(headers);

            for (Iterator<DiaryEntryInterface> iter = diaryEntries.iterator(); iter.hasNext(); ) {
                DiaryEntryInterface diaryEntry = iter.next();
                String[] line = new String[4];
                line[0] = dateFormat.format(diaryEntry.getDate());
                line[1] = diaryEntry.getCondition().toString();
                line[2] = diaryEntry.getPainDescription().toString();
                line[3] = diaryEntry.getDrugIntakes().toString();
                writer.writeNext(line);
            }
        }
    }
}