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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.BodyRegion;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.AbstractPersistentObject;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DrugIntakeInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.PainDescriptionInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;
import org.secuso.privacyfriendlypaindiary.tutorial.PrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Susanne Felsen
 * @version 20180207
 */
public class PdfCreator {

    private static final int WIDTH_A4 = 595;
    private static final int HEIGHT_A4 = 842;
    private static final int TEXT_SIZE = 12;
    private static final int PADDING = 20;
    private static final int PERSON_WIDTH = 80;
    private static final int DX_SECOND_COLUMN = 200;

    private Context context;
    private SimpleDateFormat dateFormat;
    private Date startDate;
    private Date endDate;
    private TextPaint normalTextPaint = new TextPaint();
    private TextPaint accentedTextPaint;
    private Paint blackPaint = new Paint();

    private List<DiaryEntryInterface> diaryEntries;
    private UserInterface user;

    public PdfCreator(Context context, Date startDate, Date endDate) {
        this.context = context;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.startDate = startDate;
        this.endDate = endDate;

        normalTextPaint.setTextSize(TEXT_SIZE);
        normalTextPaint.setColor(Color.BLACK);
        normalTextPaint.setTypeface(Typeface.SANS_SERIF);
        normalTextPaint.setAntiAlias(true);

        accentedTextPaint = new TextPaint(normalTextPaint);
        accentedTextPaint.setColor(context.getResources().getColor(R.color.colorAccent));
        accentedTextPaint.setFakeBoldText(true);

        DBServiceInterface service = DBService.getInstance(context);
        diaryEntries = service.getDiaryEntriesByTimeSpan(startDate, endDate);

        long userID = new PrefManager(context).getUserID();
        if(userID == AbstractPersistentObject.INVALID_OBJECT_ID) {
            user = new User();
        } else {
            user = service.getUserByID(userID);
        }
    }

    public PdfDocument createPdfDocument() {
        PdfDocument document = new PdfDocument();

        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(WIDTH_A4, HEIGHT_A4, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.translate(PADDING, PADDING);
        canvas.save();
        int height = drawHeader(canvas);
        height += 10;
        canvas.restore();

        canvas.save();
        canvas.translate(0, height);
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.diary_entries)).append(" ");
        sb.append(dateFormat.format(startDate)).append(" - ").append(dateFormat.format(endDate));
        StaticLayout layout = new StaticLayout(sb.toString(), accentedTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        height += layout.getHeight() + 10;
        canvas.restore();

        canvas.save();
        canvas.translate(0, height);
        canvas.drawLine(0, 0, WIDTH_A4 - 2 * PADDING, 0, blackPaint);
        height += 10;
        canvas.restore();

        int numberOfEntries = 0;
        if(diaryEntries.isEmpty()) {
            canvas.save();
            canvas.translate(0, height);
            sb.setLength(0);
            sb.append(context.getResources().getString(R.string.diary_entries_none));
            layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
            layout.draw(canvas);
            canvas.restore();
        }
        for (Iterator<DiaryEntryInterface> iter = diaryEntries.iterator(); iter.hasNext(); ) {
            DiaryEntryInterface diaryEntry = iter.next();
            canvas.save();
            canvas.translate(0, height);
            height += drawDiaryEntry(canvas, height, diaryEntry);
            canvas.restore();

            canvas.save();
            height += 10;
            canvas.translate(0, height);
            canvas.drawLine(0, 0, WIDTH_A4 - 2 * PADDING, 0, blackPaint);
            height += 10;
            canvas.restore();

            numberOfEntries += 1;
            if(numberOfEntries == 2 && iter.hasNext()) {
                document.finishPage(page);
                pageNumber += 1;
                pageInfo = new PdfDocument.PageInfo.Builder(WIDTH_A4, HEIGHT_A4, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                canvas.translate(PADDING, PADDING);

                height = 0;
                numberOfEntries = 0;
            }
        }

        document.finishPage(page);

        return document;
    }

    private int drawHeader(Canvas canvas) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.patient_details));
        StaticLayout layout = new StaticLayout(sb.toString(), accentedTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        int height = layout.getHeight() + 5;
        canvas.translate(0, height);

        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.first_name)).append(": ");
        if(user.getFirstName() != null) {
            sb.append(user.getFirstName());
        }
        sb.append("\n").append(context.getResources().getString(R.string.last_name)).append(": ");
        if(user.getLastName() != null) {
            sb.append(user.getLastName());
        }
        sb.append("\n").append(context.getResources().getString(R.string.date_of_birth)).append(": ");
        if(user.getDateOfBirth() != null) {
            sb.append(dateFormat.format(user.getDateOfBirth()));
        }
        sb.append("\n").append(context.getResources().getString(R.string.gender)).append(": ");
        if(user.getGender() != null) {
            String gender = context.getResources().getString(R.string.female);
            if(user.getGender() == Gender.MALE) {
                gender = context.getResources().getString(R.string.male);
            }
            sb.append(gender);
        }
        sb.append("\n");

        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        height += layout.getHeight();
        return height;
    }

    private int drawDiaryEntry(Canvas canvas, int totalHeight, DiaryEntryInterface diaryEntry) {
        PainDescriptionInterface painDescription = diaryEntry.getPainDescription();

        StringBuilder sb = new StringBuilder();
        sb.append(dateFormat.format(diaryEntry.getDate()));
        StaticLayout layout = new StaticLayout(sb.toString(), accentedTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        int height = layout.getHeight() + 5;
        canvas.restore();

        canvas.save();
        canvas.translate(0, totalHeight + height);
        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.condition));
        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        height += layout.getHeight();
        canvas.restore();

        canvas.save();
        canvas.translate(0, totalHeight + height);
        int resourceID = R.drawable.ic_menu_help;
        if(diaryEntry.getCondition() != null) {
            resourceID = Helper.getResourceIDForCondition(diaryEntry.getCondition());
        }
        Drawable drawable = ContextCompat.getDrawable(context, resourceID);
        Bitmap condition = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(condition);
        drawable.setBounds(0, 0, bitmapCanvas.getWidth(), bitmapCanvas.getHeight());
        drawable.draw(bitmapCanvas);
        canvas.drawBitmap(condition, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        condition.recycle();
        height += 35;
        canvas.restore();

        canvas.save();
        canvas.translate(0, totalHeight + height);
        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.painlevel)).append(" ");
        if(painDescription != null) {
            sb.append(painDescription.getPainLevel()).append("/10");
        }
        sb.append("\n");
        sb.append(context.getResources().getString(R.string.bodyregion));
        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        height += layout.getHeight();
        canvas.restore();

        canvas.save();
        canvas.translate(0, totalHeight + height);
        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.body_front));
        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(PERSON_WIDTH + 10, totalHeight + height);
        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.body_back));
        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.translate(DX_SECOND_COLUMN, totalHeight + height);
        height += layout.getHeight() + 5;
        sb.setLength(0);
        sb.append(context.getResources().getString(R.string.timeofpain)).append(" ");
        if(painDescription != null) {
            String timesOfPain = Helper.convertTimeEnumSetToString(context, painDescription.getTimesOfPain());
            if(timesOfPain == null) {
                timesOfPain = context.getResources().getString(R.string.none);
            }
            sb.append(timesOfPain).append("\n");
        }
        sb.append(context.getResources().getString(R.string.paindescription)).append(" ");
        if(painDescription != null) {
            String painQualities = Helper.convertPainQualityEnumSetToString(context, painDescription.getPainQualities());
            if(painQualities == null) {
                painQualities = context.getResources().getString(R.string.none);
            }
            sb.append(painQualities).append("\n");
        }
        sb.append(context.getResources().getString(R.string.notes)).append(" ");
        String notes = diaryEntry.getNotes();
        if(notes == null) {
            notes = context.getResources().getString(R.string.none);
        }
        sb.append(notes).append("\n");
        String medication = "";
        sb.append(context.getResources().getString(R.string.medication_taken)).append("\n");
        for(DrugIntakeInterface drugIntake : diaryEntry.getDrugIntakes()) {
            if(drugIntake.getDrug().getName() != null) {
                medication += Html.fromHtml("&#8226;") + " " + drugIntake.getDrug().getName();
            }
            if(drugIntake.getDrug().getDose() != null) {
                medication += " (" + drugIntake.getDrug().getDose() + ") ";
            }
            medication += ": " + drugIntake.getQuantityMorning() + " " + drugIntake.getQuantityNoon() + " " + drugIntake.getQuantityEvening() + " " + drugIntake.getQuantityNight() + "\n";
        }
        if(medication.isEmpty()) {
            medication = context.getResources().getString(R.string.none);
        }
        sb.append(medication);
        layout = new StaticLayout(sb.toString(), normalTextPaint, WIDTH_A4 / 5 * 3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);
        canvas.restore();

        if(painDescription != null) {
            canvas.save();
            canvas.translate(0, totalHeight + height);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.pdf_person, null);
            EnumSet<BodyRegion> bodyRegions = painDescription.getBodyRegions();
            EnumSet<BodyRegion> bodyRegionsFront = EnumSet.noneOf(BodyRegion.class);
            EnumSet<BodyRegion> bodyRegionsBack = EnumSet.noneOf(BodyRegion.class);
            // body regions are split up into two separate sets (front and back)
            for(BodyRegion region : bodyRegions) {
                if(region.getValue() < BodyRegion.LOWEST_BACK_INDEX) {
                    bodyRegionsFront.add(region);
                } else {
                    bodyRegionsBack.add(region);
                }
            }
            if(!bodyRegionsFront.isEmpty()) {
                Bitmap[] images = Helper.getBitmapArrayForBodyRegions(context, bodyRegionsFront);
                ((ImageView) view.findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
                view.findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
            }

            Bitmap bitmap = createBitmapFromView(view.findViewById(R.id.bodyregion));
            bitmap = Bitmap.createScaledBitmap(bitmap, PERSON_WIDTH, 160, true);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
//            height += bitmap.getHeight() + 5;
            canvas.restore();
            bitmap.recycle();

            canvas.save();
            canvas.translate(PERSON_WIDTH + 10, totalHeight + height);
            if(!bodyRegionsBack.isEmpty()) {
                Bitmap[] images = Helper.getBitmapArrayForBodyRegions(context, bodyRegionsBack);
                ((ImageView) view.findViewById(R.id.bodyregion_value)).setImageBitmap(Helper.overlay(images));
                view.findViewById(R.id.bodyregion_value).setVisibility(View.VISIBLE);
            }

            bitmap = createBitmapFromView(view.findViewById(R.id.bodyregion));
            bitmap = Bitmap.createScaledBitmap(bitmap, PERSON_WIDTH, 160, true);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            height += bitmap.getHeight() + 5;
            canvas.restore();
            bitmap.recycle();
        }
        canvas.save();
        return height;
    }

    private Bitmap createBitmapFromView(View view) {
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }

}
