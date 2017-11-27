package org.secuso.privacyfriendlyexample.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.secuso.privacyfriendlyexample.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlyexample.database.entities.enums.Condition;
import org.secuso.privacyfriendlyexample.database.entities.enums.Gender;
import org.secuso.privacyfriendlyexample.database.entities.impl.DiaryEntry;
import org.secuso.privacyfriendlyexample.database.entities.impl.Drug;
import org.secuso.privacyfriendlyexample.database.entities.impl.DrugIntake;
import org.secuso.privacyfriendlyexample.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlyexample.database.entities.impl.User;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Karola Marky, Susanne Felsen
 * @version 20171121
 * Structure based on http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/ (last access: 18.11.17)
 *
 * This class defines the structure of our database.
 */
public class DBService extends SQLiteOpenHelper implements DBServiceInterface {

    private static DBService instance = null;

    private static final String TAG = DBService.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "paindiary";

    private static final String DATE_PATTERN = "dd.MM.yyyy";

    private DBService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBService getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new DBService(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAll(db);
        test();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAll(db);
        onCreate(db);
    }

    private void createAll(SQLiteDatabase db) {
        db.execSQL(User.TABLE_CREATE);
        db.execSQL(Drug.TABLE_CREATE);
        db.execSQL(PainDescription.TABLE_CREATE);
        db.execSQL(DiaryEntry.TABLE_CREATE);
        db.execSQL(DrugIntake.TABLE_CREATE);
        Log.i(TAG,"Created database.");
     }

    //TODO: remove - just for testing purposes
    private void test() {
        Calendar c = Calendar.getInstance();
        c.set(2010, 2, 7);
        long id = createAndStoreUser("Max", "Mustermann", Gender.MALE, c.getTime());
        UserInterface user = getUserByID(id);
        Log.d(TAG, user.getFirstName() + ", " + user.getLastName() + ", " + user.getGender() + ", " + user.getDateOfBirth().toString());
//        deleteUser(user);

        DiaryEntryInterface entry = new DiaryEntry(new Date());
        PainDescriptionInterface painDescription = new PainDescription(0, BodyRegion.HEAD);
        entry.setPainDescription(painDescription);
        entry.setCondition(Condition.OKAY);
        DrugInterface drug = new Drug("Ibuprofen", "400mg");
        DrugIntakeInterface drugIntake = new DrugIntake(drug, 0,0, 1,0);
        entry.addDrugIntake(drugIntake);
        storeDiaryEntryAndAssociatedObjects(entry);

    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Drug.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PainDescription.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DrugIntake.TABLE_NAME);
    }

    @Override
    public long createAndStoreUser(String firstName, String lastName, Gender gender, Date dateOfBirth) {
        SQLiteDatabase db = this.getWritableDatabase();

        UserInterface user = new User(firstName, lastName, gender, dateOfBirth);
        ContentValues values = getUserContentValues(user);

        long id = db.insert(User.TABLE_NAME, null, values);
        Log.d(TAG, "Created user.");

        return id;
    }

    @Override
    public void updateUser(UserInterface user) {
//        if(!user.isPersistent()) {
//            throw new IllegalArgumentException();
//        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getUserContentValues(user);
        db.update(User.TABLE_NAME, values, User.COLUMN_ID + " = ?",
                new String[] { String.valueOf(user.getObjectID()) });
    }

    private ContentValues getUserContentValues(UserInterface user) {
        ContentValues values = new ContentValues();
        values.put(User.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(User.COLUMN_LAST_NAME, user.getLastName());
        values.put(User.COLUMN_GENDER, user.getGender().getValue());
        Date dateOfBirth = user.getDateOfBirth();
        if(dateOfBirth != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            values.put(User.COLUMN_DATE_OF_BIRTH, dateFormat.format(dateOfBirth));
        }
        return values;
    }

    @Override
    public UserInterface getUserByID(long id) {
//        Log.d("DATABASE", Integer.toString(id));
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User.TABLE_NAME, null, User.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        UserInterface user = null;
        if( cursor != null && cursor.moveToFirst() ){
            user = instantiateUserFromCursor(cursor);
        }
        cursor.close();

        return user;
    }

    private UserInterface instantiateUserFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID));
        int indexFirstName = cursor.getColumnIndex(User.COLUMN_FIRST_NAME);
        String firstName = null;
        if(!cursor.isNull(indexFirstName)) {
            firstName = cursor.getString(indexFirstName);
        }
        int indexLastName = cursor.getColumnIndex(User.COLUMN_LAST_NAME);
        String lastName = null;
        if(!cursor.isNull(indexLastName)) {
            lastName = cursor.getString(indexLastName);
        }
        int indexGender = cursor.getColumnIndex(User.COLUMN_GENDER);
        Gender gender = null;
        if(!cursor.isNull(indexGender)) {
            gender = Gender.valueOf(cursor.getInt(indexGender));
        }
        int indexDate = cursor.getColumnIndex(User.COLUMN_DATE_OF_BIRTH);
        Date dateOfBirth = null;
        if(!cursor.isNull(indexDate)) {
            String date = cursor.getString(indexDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            try {
                dateOfBirth = dateFormat.parse(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing user date of birth." + e);
            }
        }
        UserInterface user = new User(firstName, lastName, gender, dateOfBirth);
        user.setObjectID(objectID);

        return user;
    }

    @Override
    public void deleteUser(UserInterface user) {
        SQLiteDatabase database = this.getWritableDatabase();
        Log.d(TAG, "Deleted user with name '" + user.getFirstName() + " " + user.getLastName() + "'.");
        database.delete(User.TABLE_NAME, User.COLUMN_ID + " = ?",
                new String[] { Long.toString(user.getObjectID()) });
    }

    @Override
    public long storeDrugIntakeAndAssociatedDrug(DrugIntakeInterface intake) {
        SQLiteDatabase db = this.getWritableDatabase();

        DrugInterface drug = intake.getDrug();
        long drugID;
        if(!drug.isPersistent()) {
            drugID = storeDrug(drug);
        } else {
            drugID = drug.getObjectID();
        }
        ContentValues values = new ContentValues();
        values.put(DrugIntake.COLUMN_MORNING, intake.getQuantityMorning());
        values.put(DrugIntake.COLUMN_NOON, intake.getQuantityNoon());
        values.put(DrugIntake.COLUMN_EVENING, intake.getQuantityEvening());
        values.put(DrugIntake.COLUMN_NIGHT, intake.getQuantityNight());
        values.put(Drug.TABLE_NAME + "_id", drugID);
        values.put(DiaryEntry.TABLE_NAME + "_id", intake.getDiaryEntry().getObjectID());

        return db.insert(DrugIntake.TABLE_NAME, null, values);
    }

    //TODO update method, getAllDrugIntakes for one diary entry

    @Override
    public long createAndStoreDrug(String name, String dose) {
        DrugInterface drug = new Drug(name, dose);
        return storeDrug(drug);
    }

    @Override
    public long storeDrug(DrugInterface drug) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drug.COLUMN_NAME, drug.getName());
        values.put(Drug.COLUMN_DOSE, drug.getDose());
        values.put(Drug.COLUMN_CURRENTLY_TAKEN, drug.isCurrentlyTaken());

        long id = db.insert(Drug.TABLE_NAME, null, values);
        Log.d(TAG, "Created drug.");

        return id;
    }

    @Override
    public void updateDrug(DrugInterface drug) {
//        if(!drug.isPersistent()) {
//            throw new IllegalArgumentException();
//        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drug.COLUMN_NAME, drug.getName());
        values.put(Drug.COLUMN_DOSE, drug.getDose());
        values.put(Drug.COLUMN_CURRENTLY_TAKEN, drug.isCurrentlyTaken());

        db.update(Drug.TABLE_NAME, values, Drug.COLUMN_ID + " = ?",
                new String[] { String.valueOf(drug.getObjectID()) });
    }

    @Override
    public DrugInterface getDrugByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Drug.TABLE_NAME, null, Drug.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        DrugInterface drug = null;
        if( cursor != null && cursor.moveToFirst() ){
            drug = instantiateDrugFromCursor(cursor);
        }
        cursor.close();

        return drug;
    }

    @Override
    public List<DrugInterface> getAllDrugs() {
        List<DrugInterface> drugs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Drug.TABLE_NAME;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        DrugInterface drug = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                drug = instantiateDrugFromCursor(cursor);
                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        return drugs;
    }

    @Override
    public List<DrugInterface> getAllCurrentlyTakenDrugs() {
        List<DrugInterface> drugs = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Drug.TABLE_NAME + "WHERE " + Drug.COLUMN_CURRENTLY_TAKEN + " = 1";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        DrugInterface drug = null;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                drug = instantiateDrugFromCursor(cursor);
                drugs.add(drug);
            } while (cursor.moveToNext());
        }

        return drugs;
    }

    private DrugInterface instantiateDrugFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(Drug.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(Drug.COLUMN_NAME));
        int indexDose = cursor.getColumnIndex(Drug.COLUMN_DOSE);
        String dose = null;
        if(!cursor.isNull(indexDose)) {
            dose = cursor.getString(indexDose);
        }
        int currentlyTakenInt = cursor.getInt(cursor.getColumnIndex(Drug.COLUMN_CURRENTLY_TAKEN));
        boolean currentlyTaken = true;
        DrugInterface drug = new Drug(name, dose);
        drug.setObjectID(objectID);
        if(currentlyTakenInt == 0) currentlyTaken = false;
        drug.setCurrentlyTaken(currentlyTaken);

        return drug;
    }

    public long storeDiaryEntryAndAssociatedObjects(DiaryEntryInterface diaryEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();
        ContentValues painDescriptionValues = new ContentValues();
        painDescriptionValues.put(PainDescription.COLUMN_PAIN_LEVEL, painDescription.getPainLevel());
        painDescriptionValues.put(PainDescription.COLUMN_BODY_REGION, painDescription.getBodyRegion().getValue());
        //TODO
        long painDescriptionID = db.insert(PainDescription.TABLE_NAME, null, painDescriptionValues);

        ContentValues diaryEntryValues = new ContentValues();
        diaryEntryValues.put(PainDescription.TABLE_NAME + "_id", painDescriptionID); //foreign key
        Date date = diaryEntry.getDate();
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            diaryEntryValues.put(DiaryEntry.COLUMN_DATE, dateFormat.format(date));
        }
        diaryEntryValues.put(DiaryEntry.COLUMN_CONDITION, diaryEntry.getCondition().getValue());
        diaryEntryValues.put(DiaryEntry.COLUMN_NOTES, diaryEntry.getNotes());
        long diaryEntryID = db.insert(DiaryEntry.TABLE_NAME, null, diaryEntryValues);

        for(DrugIntakeInterface intake : diaryEntry.getDrugIntakes()) {
            intake.getDiaryEntry().setObjectID(diaryEntryID); //maybe get diary entry from database instead
            storeDrugIntakeAndAssociatedDrug(intake);
        }

        return diaryEntryID;
    }

    /**
     * Fetches the list of diary entries for the given date from the database.
     * @param date
     * @return list of diary entries for the given date --> list should only contain one element (one entry per day)
     */
    public List<DiaryEntryInterface> getDiaryEntriesByDate(Date date) {
        List<DiaryEntryInterface> diaryEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

        Cursor cursor = db.query(DiaryEntry.TABLE_NAME, null, DiaryEntry.COLUMN_DATE + "=?",
                new String[]{dateFormat.format(date)}, null, null, null, null);

        DiaryEntryInterface diaryEntry = null;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                diaryEntry = instantiateDiaryEntryFromCursor(cursor);
                diaryEntries.add(diaryEntry);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return diaryEntries;
    }

    private PainDescriptionInterface getPainDescriptionByID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PainDescription.TABLE_NAME, null, PainDescription.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        PainDescriptionInterface painDescription = null;
        if(cursor != null && cursor.moveToFirst() ){
            painDescription = instantiatePainDescriptionFromCursor(cursor);
        }
        cursor.close();

        return painDescription;
    }

    /**
     * Instantiates a diary entry object from the given cursor.
     * @param cursor
     * @return diary entry object with associated pain description
     */
    private DiaryEntryInterface instantiateDiaryEntryFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(DiaryEntry.COLUMN_ID));
        int indexDate = cursor.getColumnIndex(DiaryEntry.COLUMN_DATE);
        Date dateOfEntry = null;
        if(!cursor.isNull(indexDate)) {
            String date = cursor.getString(indexDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
            try {
                dateOfEntry = dateFormat.parse(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing diary entry date." + e);
            }
        }
        DiaryEntryInterface diaryEntry = new DiaryEntry(dateOfEntry);
        diaryEntry.setObjectID(objectID);

        int indexNotes = cursor.getColumnIndex(DiaryEntry.COLUMN_NOTES);
        if(!cursor.isNull(indexNotes)) {
            diaryEntry.setNotes(cursor.getString(indexNotes));
        }
        diaryEntry.setCondition(Condition.valueOf(cursor.getInt(cursor.getColumnIndex(DiaryEntry.COLUMN_CONDITION))));

        long painDescriptionID = cursor.getLong(cursor.getColumnIndex(PainDescription.TABLE_NAME + "_id"));
        PainDescriptionInterface painDescription = getPainDescriptionByID(painDescriptionID);
        diaryEntry.setPainDescription(painDescription);

        //TODO: get drug intakes by diary entry ID and attach to diary entry object

        return diaryEntry;
    }

    private PainDescriptionInterface instantiatePainDescriptionFromCursor(Cursor cursor) {
        long objectID = cursor.getLong(cursor.getColumnIndex(PainDescription.COLUMN_ID));
        int painLevel = cursor.getInt(cursor.getColumnIndex(PainDescription.COLUMN_PAIN_LEVEL));
        BodyRegion bodyRegion = BodyRegion.valueOf(cursor.getInt(cursor.getColumnIndex(PainDescription.COLUMN_BODY_REGION)));
        PainDescriptionInterface painDescription = new PainDescription(painLevel, bodyRegion);
        painDescription.setObjectID(objectID);
        //TODO
        return painDescription;
    }

}
