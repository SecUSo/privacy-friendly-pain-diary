package org.secuso.privacyfriendlypaindiary;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Susanne Felsen
 * @version 20171128
 * @see <a href="http://www.singhajit.com/testing-android-database/"></a>
 */
@RunWith(AndroidJUnit4.class)
public class DBServiceTest {

    DBService service;

    @Before
    public void setUp() {
        service = DBService.getInstance(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = service.getWritableDatabase();
        service.onUpgrade(db, 1, 1);
    }

    @After
    public void tearDown() throws Exception {
        service.close();
    }

    @Test
    public void testUser() {
        String firstName = "Max";
        String lastName = "Mustermann";
        Gender gender = Gender.MALE;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DBService.DATE_PATTERN);
        Date dateOfBirth = null;
        try {
            dateOfBirth = dateFormat.parse("01.01.1965");
        } catch (ParseException e) {
            fail("Error parsing date.");
        }
        long id = service.createAndStoreUser(firstName, lastName, gender, dateOfBirth);
        UserInterface user = service.getUserByID(id);
        assertEquals("First name incorrect.", firstName, user.getFirstName());
        assertEquals("Last name incorrect.", lastName, user.getLastName());
        assertEquals("Gender incorrect.", gender, user.getGender());
        assertEquals("Date of birth incorrect.", dateOfBirth, user.getDateOfBirth());

        service.deleteUser(user);
        user = service.getUserByID(id);
        assertNull(user);
    }

}
