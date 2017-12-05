package org.secuso.privacyfriendlypaindiary.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Text Watcher adapted from <a href="https://stackoverflow.com/questions/16889502/how-to-mask-an-edittext-to-show-the-dd-mm-yyyy-date-format"/a>.
 *
 * @author Susanne Felsen
 * @version 20171129
 */
public class UserDetailsActivity extends BaseActivity {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private UserInterface user;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;

    private TextInputLayout firstNameWrapper;
    private TextInputLayout lastNameWrapper;
    private TextInputLayout dateWrapper;

    private String pattern = "^[a-zA-Z\\- ]{0,35}$";

    private TextWatcher dateWatcher = new TextWatcher() {
        private Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY"; //TODO adapt to different date patterns

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean valid = true;
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", ""); //all non-digits are replaced
                clean = clean.replaceAll("\\.", ""); //all periods are replaced
                String cleanC = current.replaceAll("[^\\d.]", "");
                cleanC = cleanC.replaceAll("\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + ddmmyyyy.substring(clean.length());
                } else {
                    //this part makes sure that when we finish entering numbers, the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(0, 2));
                    int mon = Integer.parseInt(clean.substring(2, 4));
                    int year = Integer.parseInt(clean.substring(4, 8));

                    if (mon > 12) {
                        valid = false;
                        mon = 12;
                    }
                    cal.set(Calendar.MONTH, mon - 1);
                    if (year < 1900 || year > currentYear) {
                        valid = false;
                        year = currentYear;
                    }
                    //first set year for the line below to work correctly with leap years
                    cal.set(Calendar.YEAR, year);
                    if (day > cal.getActualMaximum(Calendar.DATE)) {
                        valid = false;
                        day = cal.getActualMaximum(Calendar.DATE);
                    }
                    clean = String.format("%02d%02d%02d", day, mon, year);
                }
                clean = String.format("%s.%s.%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                dateWrapper.getEditText().setText(current);
                dateWrapper.getEditText().setSelection(sel < current.length() ? sel : current.length());
            }
            if (!valid) {
                dateWrapper.setErrorEnabled(true);
                dateWrapper.setError(getString(R.string.date_invalid));
            } else {
                dateWrapper.setErrorEnabled(false);
                dateWrapper.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            String date = dateWrapper.getEditText().getText().toString();
            if (date != null) {
                if(date.equals("DD.MM.YYYY")) {
                    dateWrapper.setError(null);
                    dateWrapper.setErrorEnabled(false);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    try {
                        dateOfBirth = dateFormat.parse(date);
                    } catch (ParseException e) {
                        dateWrapper.setErrorEnabled(true);
                        dateWrapper.setError(getString(R.string.date_invalid));
                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);
        overridePendingTransition(0, 0);

        DBServiceInterface service = DBService.getInstance(this);
        List<UserInterface> users = service.getAllUsers();
        if (users.isEmpty()) {
            user = new User();
        } else {
            //TODO: alternative: save user ID and getUserByID
            user = users.get(0); //users should only contain one element
        }
        dateWrapper = (TextInputLayout) findViewById(R.id.date_of_birth_wrapper);
        dateWrapper.getEditText().addTextChangedListener(dateWatcher);

        firstNameWrapper = (TextInputLayout) findViewById(R.id.first_name_wrapper);
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

        lastNameWrapper = (TextInputLayout) findViewById(R.id.last_name_wrapper);
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

        initFields();
    }

    private void initFields() {
        firstName = user.getFirstName();
        lastName = user.getLastName();
        dateOfBirth = user.getDateOfBirth();
        gender = user.getGender();

        if (dateOfBirth != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); //TODO
            dateWrapper.getEditText().setText(dateFormat.format(dateOfBirth));
        }
        firstNameWrapper.getEditText().setText(firstName);
        lastNameWrapper.getEditText().setText(lastName);
    }

    private void saveChanges() {
        if (firstNameWrapper.getError() == null && lastNameWrapper.getError() == null && dateWrapper.getError() == null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateOfBirth(dateOfBirth);
            DBServiceInterface service = DBService.getInstance(this);
            long userID;
            if(user.getObjectID() == PersistentObject.INVALID_OBJECT_ID) {
                 userID = service.storeUser(user);
            } else {
                service.updateUser(user);
                userID = user.getObjectID();
            }
            user = service.getUserByID(userID);
            initFields();
            //TODO: display success message
        } else {
            cancel();
            //TODO: display message
        }
    }

    private void cancel() {
        initFields();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveChanges();
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_user_details;
    }

}


