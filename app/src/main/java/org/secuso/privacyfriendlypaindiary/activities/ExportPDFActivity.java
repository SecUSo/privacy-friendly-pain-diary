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

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.helpers.PdfCreator;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;
import org.secuso.privacyfriendlypaindiary.viewmodel.DatabaseViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This activity allows to export and/or share a pdf document of the diary entries made
 * within a specific period of time marked by a start date and an end date.
 * Exporting the pdf file requires the permission <code>WRITE_EXTERNAL_STORAGE</code>.
 *
 * @author Susanne Felsen
 * @version 20180228
 */
public class ExportPDFActivity extends AppCompatActivity {

    private static final String TAG = ExportPDFActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 42;

    private TextInputLayout startDateWrapper;
    private TextInputLayout endDateWrapper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    private Date startDate;
    private Date endDate;

    private DatabaseViewModel database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);

        database = new ViewModelProvider(this).get(DatabaseViewModel.class);

        startDateWrapper = findViewById(R.id.start_date_wrapper);
        endDateWrapper = findViewById(R.id.end_date_wrapper);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (startDate != null) {
            outState.putString("startDate", dateFormat.format(startDate));
        }
        if (endDate != null) {
            outState.putString("endDate", dateFormat.format(endDate));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String startDateAsString = savedInstanceState.getString("startDate");
        if (startDateAsString != null) {
            try {
                startDate = dateFormat.parse(startDateAsString);
            } catch (ParseException e) {
            }
        }
        String endDateAsString = savedInstanceState.getString("endDate");
        if (endDateAsString != null) {
            try {
                endDate = dateFormat.parse(endDateAsString);
            } catch (ParseException e) {
            }
        }
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
                LiveData<File> fileLive = exportAsPDF();
                final ExportPDFActivity activity = this;
                fileLive.observe(this, new Observer<File>() {
                    @Override
                    public void onChanged(File file) {
                        if (file != null) {
                            Toast.makeText(activity, getString(R.string.export_success), Toast.LENGTH_LONG).show();
                        }
                        fileLive.removeObserver(this);
                    }
                });
                break;
            case R.id.btn_share:
                exportAndShare();
                break;
            default:
                break;
        }
    }

    private void showDatePickerDialog(final int callerID, String dateText) {
        Date date;
        if (dateText != null || !dateText.equals("")) {
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
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(year, month, day);
                if (callerID == R.id.start_date) {
                    startDate = cal.getTime();
                    startDateWrapper.getEditText().setText(dateFormat.format(startDate));
                    startDateWrapper.setError(null);
                } else if (callerID == R.id.end_date) {
                    endDate = cal.getTime();
                    endDateWrapper.getEditText().setText(dateFormat.format(endDate));
                    endDateWrapper.setError(null);
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        if (callerID == R.id.start_date && endDate != null) {
            dialog.getDatePicker().setMaxDate(endDate.getTime());
        } else if (callerID == R.id.end_date && startDate != null) {
            dialog.getDatePicker().setMinDate(startDate.getTime());
        }
        dialog.show();
    }

    private LiveData<File> exportAsPDF() {
        final MutableLiveData<File>[] file = new MutableLiveData[]{new MutableLiveData<>()};
        if (startDate == null) {
            startDateWrapper.setError(getString(R.string.start_date_error));
        } else if (endDate == null) {
            endDateWrapper.setError(getString(R.string.end_date_error));
        } else if (startDate.compareTo(endDate) <= 0) {
            LiveData<List<DiaryEntryInterface>> diaryEntriesLive = database.getDiaryEntriesByTimeSpan(startDate, endDate);
            diaryEntriesLive.observe(this, diaryEntryInterfaces -> {
                long userID = new PrefManager(this).getUserID();
                if (userID == AbstractPersistentObject.INVALID_OBJECT_ID) {
                    file[0].setValue(exportAsPDF(new PdfCreator(this, startDate, endDate, diaryEntryInterfaces, new User()).createPdfDocument()));
                } else {
                    LiveData<UserInterface> userLive = database.getUserByID(userID);
                    userLive.observe(this, userInterface -> {
                        file[0].setValue(exportAsPDF(new PdfCreator(this, startDate, endDate, diaryEntryInterfaces, userInterface).createPdfDocument()));
                    });
                }
            });
        }
        return file[0];
    }

    private File exportAsPDF(PdfDocument doc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ExportPDFActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ExportPDFActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                return null;
            }
        }

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Toast.makeText(this, getString(R.string.export_failure), Toast.LENGTH_LONG).show();
                return null;
            }
        }

        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy");
        String filename = s.format(startDate) + "-" + s.format(endDate);
        File file = new File(directory, filename + ".pdf");
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            doc.writeTo(out);
            doc.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.export_failure), Toast.LENGTH_LONG).show();
        }

        return file;
    }

    private void exportAndShare() {
        LiveData<File> fileLive = exportAsPDF();
        final ExportPDFActivity activity = this;
        fileLive.observe(this, new Observer<File>() {
            @Override
            public void onChanged(File file) {
                if (file != null) {
                    Uri attachment = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, attachment);
                    sendIntent.setType("application/pdf");
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_caution)));
                }
                fileLive.removeObserver(this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.permission_write_granted), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.permission_write_denied), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
