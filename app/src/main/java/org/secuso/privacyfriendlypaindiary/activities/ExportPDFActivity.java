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
package org.secuso.privacyfriendlypaindiary.activities;

import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.helpers.PdfCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExportPDFActivity extends AppCompatActivity {

    private static final String TAG = ExportPDFActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fromButton:
            case R.id.untilButton:
            case R.id.fromDate:
            case R.id.untilDate:
                //DateDialog dialog=new DateDialog(myView);
                //FragmentTransaction ft =getFragmentManager().beginTransaction();
                //dialog.show(ft, "DatePicker");
                break;
            case R.id.create:
//                createPdf();
                saveFile();
                break;
            default:
                break;
        }
    }

    public void onStart(){
        super.onStart();
//        EditText txtDate=(EditText)findViewById(R.id.fromDate);
//
//        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            public void onFocusChange(View view, boolean hasfocus){
//                if(hasfocus){
//                    DateDialog dialog=new DateDialog(view);
//                    FragmentTransaction ft =getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DatePicker");
//                }
//            }
//        });
    }

    private void saveFile() {
        DBServiceInterface service = DBService.getInstance(this);
        Calendar c = Calendar.getInstance();
        Date endDate = c.getTime();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
        Date startDate = c.getTime();
        List<DiaryEntryInterface> diaryEntries = service.getDiaryEntriesByTimeSpan(startDate, endDate);
        saveFile(PdfCreator.createPdfDocument(this, diaryEntries));
    }

    private void saveFile(PdfDocument doc){
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (!directory.mkdirs()) {
                Log.d(TAG, "Directory not created");
            }

            File file = new File(directory, "test.pdf");
            if (file.exists()) {
                file.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                doc.writeTo(out);
                doc.close();
                out.flush();
                out.close();
                Toast.makeText(this, "PDF created.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String path = file.getAbsolutePath();
//            Log.d(TAG, "path: " + path);
//        } else {
//            Log.d(TAG, "Permission denied.");
//        }
    }

}
