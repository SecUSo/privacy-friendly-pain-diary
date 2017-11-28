package org.secuso.privacyfriendlypaindiary;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Condition;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.Drug;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.DrugIntake;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        //create
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
        UserInterface user = new User(firstName, lastName, gender, dateOfBirth);
        long id = service.storeUser(user);

        //get
        user = service.getUserByID(id);
        assertEquals("First name incorrect.", firstName, user.getFirstName());
        assertEquals("Last name incorrect.", lastName, user.getLastName());
        assertEquals("Gender incorrect.", gender, user.getGender());
        assertEquals("Date of birth incorrect.", dateOfBirth, user.getDateOfBirth());

        //update
        String newFirstName = "Moritz";
        user.setFirstName(newFirstName);
        service.updateUser(user);
        user = service.getUserByID(id);
        assertEquals("First name incorrect.", newFirstName, user.getFirstName());
        assertEquals("Last name incorrect.", lastName, user.getLastName());

        //delete
        service.deleteUser(user);
        user = service.getUserByID(id);
        assertNull(user);
    }

    @Test
    public void testDiaryEntry() {
        //create
        SimpleDateFormat dateFormat = new SimpleDateFormat(DBService.DATE_PATTERN);
        Date date = null;
        try {
            date = dateFormat.parse("28.11.2017");
        } catch (ParseException e) {
            fail("Error parsing date.");
        }
        Condition condition = Condition.OKAY;
        String notes = "Additional notes.";
        DiaryEntryInterface entry = new DiaryEntry(date);
        entry.setCondition(condition);
        entry.setNotes(notes);

        int painLevel = 0;
        BodyRegion bodyRegion = BodyRegion.HEAD;
        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegion);
        entry.setPainDescription(painDescription);

        String drugName = "Ibuprofen";
        String dose = "400mg";
        DrugInterface drug = new Drug(drugName, dose);

        int quantityMorning = 0;
        int quantityNoon = 0;
        int quantityEvening = 1;
        int quantityNight = 0;
        DrugIntakeInterface drugIntake = new DrugIntake(drug, quantityMorning, quantityNoon, quantityEvening, quantityNight);

        entry.addDrugIntake(drugIntake);
        long entryID = service.storeDiaryEntryAndAssociatedObjects(entry);

        //get
        entry = service.getDiaryEntryByID(entryID);
        assertEquals("Date was incorrect.", date, entry.getDate());
        assertEquals("Condition was incorrect.", condition, entry.getCondition());
        assertEquals("Notes were incorrect.", notes, entry.getNotes());

        painDescription = entry.getPainDescription();
        assertEquals("Pain Level was incorrect.", painLevel, painDescription.getPainLevel());
        assertEquals("Body Region was incorrect.", bodyRegion, painDescription.getBodyRegion());

        Set<DrugIntakeInterface> intakes = entry.getDrugIntakes();
        assertEquals("Number of Drug Intakes was incorrect.", 1,  intakes.size());
        drugIntake = intakes.iterator().next();
        assertEquals("Quantity Morning was incorrect.", quantityMorning, drugIntake.getQuantityMorning());
        assertEquals("Quantity Noon was incorrect.", quantityNoon, drugIntake.getQuantityNoon());
        assertEquals("Quantity Evening was incorrect.", quantityEvening, drugIntake.getQuantityEvening());
        assertEquals("Quantity Night was incorrect.", quantityNight, drugIntake.getQuantityNight());

        drug = drugIntake.getDrug();
        assertEquals("Drug Name was incorrect.", drugName, drug.getName());
        assertEquals("Drug Dose was incorrect.", dose, drug.getDose());

        //update
        Condition newCondition = Condition.BAD;
        entry.setCondition(newCondition);
        int newPainLevel = 5;
        painDescription.setPainLevel(newPainLevel);
        int newQuantityMorning = 1;
        drugIntake.setQuantityMorning(newQuantityMorning);
//        entry.addDrugIntake(new DrugIntake(drug, quantityMorning, quantityNoon, quantityEvening, quantityNight));
        service.updateDiaryEntryAndAssociatedObjects(entry);
        entry = service.getDiaryEntryByID(entryID);
        assertEquals("Condition was incorrect.", newCondition, entry.getCondition());
        painDescription = entry.getPainDescription();
        assertEquals("Pain Level was incorrect.", newPainLevel, painDescription.getPainLevel());
        intakes = entry.getDrugIntakes();
        assertEquals("Number of Drug Intakes was incorrect.", 1, intakes.size());
        drugIntake = intakes.iterator().next();
        assertEquals("Quantity Morning was incorrect.", newQuantityMorning, drugIntake.getQuantityMorning());

    }

}
