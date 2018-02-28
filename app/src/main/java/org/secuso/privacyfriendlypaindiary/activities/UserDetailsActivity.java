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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * This activity allows a user to enter and save personal data such as name,
 * date of birth and gender.
 *
 * @author Susanne Felsen
 * @version 201802227
 */
public class UserDetailsActivity extends AppCompatActivity {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private PrefManager prefManager;
    private UserInterface user;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;

    private boolean tutorial = false;
    private boolean maleSelected = false;
    private boolean femaleSelected = false;

    private TextInputLayout firstNameWrapper;
    private TextInputLayout lastNameWrapper;
    private TextInputLayout dateWrapper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private String pattern = "^[a-zA-Z\\- ]{0,35}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        tutorial = getIntent().getBooleanExtra("TUTORIAL", false);
        if (tutorial) {
            ((Button) findViewById(R.id.btn_cancel)).setText(getString(R.string.skip));
        }

        prefManager = new PrefManager(this);
        long userID = prefManager.getUserID();
        if (userID == AbstractPersistentObject.INVALID_OBJECT_ID) {
            user = new User();
        } else {
            DBServiceInterface service = DBService.getInstance(this);
            user = service.getUserByID(userID);
            if (user == null) user = new User(); //this is an error case
        }

        dateWrapper = findViewById(R.id.date_of_birth_wrapper);
//        dateWrapper.getEditText().addTextChangedListener(dateWatcher);

        firstNameWrapper = findViewById(R.id.first_name_wrapper);
        firstNameWrapper.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                boolean valid = true;
                String firstName = firstNameWrapper.getEditText().getText().toString();
                if (firstName != null && !firstName.isEmpty()) {
                    valid = Pattern.matches(pattern, firstName);
                }
                if (!valid) {
                    firstNameWrapper.setErrorEnabled(true);
                    firstNameWrapper.setError(getString(R.string.name_invalid));
                } else {
                    firstNameWrapper.setError(null);
                    firstNameWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                firstName = firstNameWrapper.getEditText().getText().toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        lastNameWrapper = findViewById(R.id.last_name_wrapper);
        lastNameWrapper.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                boolean valid = true;
                String lastName = lastNameWrapper.getEditText().getText().toString();
                if (lastName != null && !lastName.isEmpty()) {
                    valid = Pattern.matches(pattern, lastName);
                }
                if (!valid) {
                    lastNameWrapper.setErrorEnabled(true);
                    lastNameWrapper.setError(getString(R.string.name_invalid));
                } else {
                    lastNameWrapper.setError(null);
                    lastNameWrapper.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                lastName = lastNameWrapper.getEditText().getText().toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        TextView genderLabel = findViewById(R.id.gender_label);
        EditText editText = firstNameWrapper.getEditText();
//        genderLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, editText.getTextSize())
        genderLabel.setTextColor(editText.getHintTextColors().getDefaultColor());
        genderLabel.setPadding(editText.getPaddingLeft(), 0, editText.getPaddingRight(), 0);
        initFields();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("firstName", firstName);
        outState.putString("lastName", lastName);
        if (dateOfBirth != null) outState.putString("dateOfBirth", dateFormat.format(dateOfBirth));
        if (gender != null) outState.putString("gender", gender.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        firstName = savedInstanceState.getString("firstName");
        lastName = savedInstanceState.getString("lastName");
        String dateOfBirthAsString = savedInstanceState.getString("dateOfBirth");
        if (dateOfBirthAsString != null) {
            try {
                dateOfBirth = dateFormat.parse(dateOfBirthAsString);
            } catch (ParseException e) {
            }
        }
        String genderAsString = savedInstanceState.getString("gender");
        if (genderAsString != null) gender = Gender.valueOf(genderAsString);
    }

    private void initFields() {
        firstName = user.getFirstName();
        lastName = user.getLastName();
        dateOfBirth = user.getDateOfBirth();
        gender = user.getGender();
        RadioGroup genderGroup = findViewById(R.id.gender);
        if (gender == Gender.MALE) {
            genderGroup.check(R.id.gender_male);
            maleSelected = true;
            femaleSelected = false;
        } else if (gender == Gender.FEMALE) {
            genderGroup.check(R.id.gender_female);
            maleSelected = false;
            femaleSelected = true;
        } else {
            genderGroup.clearCheck();
            maleSelected = false;
            femaleSelected = false;
        }

        if (dateOfBirth != null) {
            dateWrapper.getEditText().setText(dateFormat.format(dateOfBirth));
        } else {
            dateWrapper.getEditText().setText(null);
        }
        firstNameWrapper.getEditText().setText(firstName);
        lastNameWrapper.getEditText().setText(lastName);
    }

    private void showDatePickerDialog(String dateText) {
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
                dateOfBirth = cal.getTime();
                dateWrapper.getEditText().setText(dateFormat.format(dateOfBirth));
//                dateWrapper.setError(null);

            }
        }, year, month, day);
        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        Calendar min = Calendar.getInstance();
        min.set(1970, 0, 0, 0, 0);
        min.setTimeInMillis(0);
        dialog.getDatePicker().setMinDate(min.getTimeInMillis());
        dialog.show();
    }

    private void saveChanges() {
        if (firstNameWrapper.getError() == null && lastNameWrapper.getError() == null) { //&& dateWrapper.getError() == null
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateOfBirth(dateOfBirth);
            user.setGender(gender);
            DBServiceInterface service = DBService.getInstance(this);
            long userID;
            if (user.getObjectID() == PersistentObject.INVALID_OBJECT_ID) {
                userID = service.storeUser(user);
                prefManager.setUserID(userID);
            } else {
                service.updateUser(user);
                userID = user.getObjectID();
            }
            user = service.getUserByID(userID);
            initFields();
            Toast.makeText(getApplicationContext(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
            launchHomeScreen();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.save_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancel() {
        initFields();
        if (!tutorial) {
            Toast.makeText(getApplicationContext(), getString(R.string.changes_discarded), Toast.LENGTH_SHORT).show();
        } else {
            launchHomeScreen();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_of_birth:
                String dateText = dateWrapper.getEditText().getText().toString();
                showDatePickerDialog(dateText);
                break;
            case R.id.gender_male:
                if (maleSelected) {
                    ((RadioGroup) findViewById(R.id.gender)).clearCheck();
                    gender = null;
                    maleSelected = false;
                } else {
                    gender = Gender.MALE;
                    maleSelected = true;
                    femaleSelected = false;
                }
                break;
            case R.id.gender_female:
                if (femaleSelected) {
                    ((RadioGroup) findViewById(R.id.gender)).clearCheck();
                    gender = null;
                    femaleSelected = false;
                } else {
                    gender = Gender.FEMALE;
                    maleSelected = false;
                    femaleSelected = true;
                }
                break;
            case R.id.btn_save:
                saveChanges();
                break;
            case R.id.btn_cancel:
//                cancel();
                launchHomeScreen();
                break;
            default:
                break;
        }
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (tutorial) {
                    launchHomeScreen();
                } else {
                    super.onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

// Text Watcher adapted from <a href="https://stackoverflow.com/questions/16889502/how-to-mask-an-edittext-to-show-the-dd-mm-yyyy-date-format"/>.
//    private TextWatcher dateWatcher = new TextWatcher() {
//        private Calendar cal = Calendar.getInstance();
//        int currentYear = cal.get(Calendar.YEAR);
//        private String current = "";
//        private String ddmmyyyy = "DDMMYYYY"; //TODO adapt to different date patterns
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            boolean valid = true;
//            if (!s.toString().equals("") && !s.toString().equals(current)) {
//                String clean = s.toString().replaceAll("[^\\d.]", ""); //all non-digits are replaced
//                clean = clean.replaceAll("\\.", ""); //all periods are replaced
//                String cleanC = current.replaceAll("[^\\d.]", "");
//                cleanC = cleanC.replaceAll("\\.", "");
//
//                int cl = clean.length();
//                int sel = cl;
//                for (int i = 2; i <= cl && i < 6; i += 2) {
//                    sel++;
//                }
//                //fix for pressing delete next to a forward slash
//                if (clean.equals(cleanC)) sel--;
//
//                if (clean.length() < 8) {
//                    clean = clean + ddmmyyyy.substring(clean.length());
//                } else {
//                    //this part makes sure that when we finish entering numbers, the date is correct, fixing it otherwise
//                    int day = Integer.parseInt(clean.substring(0, 2));
//                    int mon = Integer.parseInt(clean.substring(2, 4));
//                    int year = Integer.parseInt(clean.substring(4, 8));
//
//                    if (mon > 12) {
//                        valid = false;
//                        mon = 12;
//                    }
//                    cal.set(Calendar.MONTH, mon - 1);
//                    if (year < 1900 || year > currentYear) {
//                        valid = false;
//                        year = currentYear;
//                    }
//                    //first set year for the line below to work correctly with leap years
//                    cal.set(Calendar.YEAR, year);
//                    if (day > cal.getActualMaximum(Calendar.DATE)) {
//                        valid = false;
//                        day = cal.getActualMaximum(Calendar.DATE);
//                    }
//                    clean = String.format("%02d%02d%02d", day, mon, year);
//                }
//                clean = String.format("%s.%s.%s", clean.substring(0, 2),
//                        clean.substring(2, 4),
//                        clean.substring(4, 8));
//
//                sel = sel < 0 ? 0 : sel;
//                current = clean;
//                dateWrapper.getEditText().setText(current);
//                dateWrapper.getEditText().setSelection(sel < current.length() ? sel : current.length());
//            }
//            if (!valid) {
//                dateWrapper.setErrorEnabled(true);
//                dateWrapper.setError(getString(R.string.date_invalid));
//            } else {
//                dateWrapper.setErrorEnabled(false);
//                dateWrapper.setError(null);
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            Editable date = dateWrapper.getEditText().getText();
//            if (date != null) {
//                if (date.toString().isEmpty() || date.toString().equals("DD.MM.YYYY")) {
//                    dateOfBirth = null;
//                    dateWrapper.setError(null);
//                    dateWrapper.setErrorEnabled(false);
//                } else {
//                    try {
//                        dateOfBirth = dateFormat.parse(date.toString());
//                    } catch (ParseException e) {
//                        dateWrapper.setErrorEnabled(true);
//                        dateWrapper.setError(getString(R.string.date_invalid));
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//    };