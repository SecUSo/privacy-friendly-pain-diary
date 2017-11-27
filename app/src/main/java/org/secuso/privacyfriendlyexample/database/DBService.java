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
import org.secuso.privacyfriendlyexample.database.entities.impl.PainDescription;
import org.secuso.privacyfriendlyexample.database.entities.impl.User;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.DrugInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlyexample.database.entities.interfaces.UserInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        db.execSQL(DiaryEntry.TABLE_ASSOCIATIVE_CREATE);
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
        entry.addDrug(drug);
        storeDiaryEntryAndAssociatedObjects(entry);

    }

    private void dropAll(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Drug.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PainDescription.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DiaryEntry.TABLE_ASSOCIATIVE_NAME);
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
                String date = cursor.getString(cursor.getColumnIndex(User.COLUMN_DATE_OF_BIRTH));
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                try {
                    dateOfBirth = dateFormat.parse(date);
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date of birth." + e);
                }
            }

            user = new User(firstName, lastName, gender, dateOfBirth);
            user.setObjectID(objectID);
            cursor.close();
        }
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
    public long createAndStoreDrug(String name, String dose) {
        DrugInterface drug = new Drug(name, dose);
        return createAndStoreDrug(drug);
    }

    @Override
    public long createAndStoreDrug(DrugInterface drug) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Drug.COLUMN_NAME, drug.getName());
        values.put(Drug.COLUMN_DOSE, drug.getDose());
        values.put(Drug.COLUMN_CURRENTLY_TAKEN, drug.isCurrentlyTaken());

        long id = db.insert(Drug.TABLE_NAME, null, values);
        Log.d(TAG, "Created drug.");

        return id;
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

        //TODO: check whether drug is already persistent -> get from database instead of create
        for(DrugInterface drug : diaryEntry.getDrugs()) {
            long drugID = createAndStoreDrug(drug);

            ContentValues associativeValues = new ContentValues();
            associativeValues.put(DiaryEntry.TABLE_NAME + "_id", diaryEntryID);
            associativeValues.put(Drug.TABLE_NAME + "_id", drugID);
            //TODO: insert information when drug is taken (morning, noon, evening, night)
            db.insert(DiaryEntry.TABLE_ASSOCIATIVE_NAME, null, associativeValues);
        }

        return diaryEntryID;
    }

}
