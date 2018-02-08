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

import android.app.DatePickerDialog;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.helpers.PdfCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExportPDFActivity extends AppCompatActivity {

    private static final String TAG = ExportPDFActivity.class.getSimpleName();

    private TextInputLayout startDateWrapper;
    private TextInputLayout endDateWrapper;

    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);
        startDateWrapper = findViewById(R.id.start_date_wrapper);
        endDateWrapper = findViewById(R.id.end_date_wrapper);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_date:
                String dateText = startDateWrapper.getEditText().getText().toString();
                showDatePickerDialog(R.id.start_date, dateText);
                break;
            case R.id.end_date:
                dateText = endDateWrapper.getEditText().getText().toString();
                showDatePickerDialog(R.id.end_date, dateText);
                break;
            case R.id.btn_export:
//                createPdf();
                saveFile();
                break;
            default:
                break;
        }
    }

    public void showDatePickerDialog(final int callerID, String dateText) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        Date date = null;

        if(dateText != null || !dateText.equals("")) {
            try {
                date = dateFormat.parse(dateText);
            } catch (Exception exp) {
                date = new Date();
            }
        } else {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // calendar month 0-11
        int day = calendar.get(Calendar.DATE);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                if(callerID == R.id.start_date) {
                    startDate = cal.getTime();
                    startDateWrapper.getEditText().setText(dateFormat.format(startDate));
                } else if (callerID == R.id.end_date) {
                    endDate = cal.getTime();
                    endDateWrapper.getEditText().setText(dateFormat.format(endDate));
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
//        dialog.getDatePicker().setMinDate( ).getTime()); day of first entry?
        dialog.show();
    }

    private void saveFile() {
        DBServiceInterface service = DBService.getInstance(this);
        Calendar c = Calendar.getInstance();
        Date endDate = c.getTime();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
        Date startDate = c.getTime();
        saveFile(new PdfCreator(this, startDate, endDate).createPdfDocument());
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
