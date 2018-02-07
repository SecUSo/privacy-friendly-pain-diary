package org.secuso.privacyfriendlypaindiary.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.database.DBService;
import org.secuso.privacyfriendlypaindiary.database.DBServiceInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.enums.Gender;
import org.secuso.privacyfriendlypaindiary.database.entities.impl.User;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.DiaryEntryInterface;
import org.secuso.privacyfriendlypaindiary.database.entities.interfaces.UserInterface;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Susanne Felsen
 * @version 20180207
 */
public class PdfCreator {

    private static final int WIDTH_A4 = 595;
    private static final int HEIGHT_A4 = 842;

    public static PdfDocument createPdfDocument(Context context, List<DiaryEntryInterface> diaryEntries) {
        DBServiceInterface service = DBService.getInstance(context);
        List<UserInterface> users = service.getAllUsers();
        UserInterface user = null;
        if (!users.isEmpty()) {
            user = users.get(0);
        } else {
            user = new User();
        }
        PdfDocument document = new PdfDocument();

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(10);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setAntiAlias(true);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(WIDTH_A4, HEIGHT_A4, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
//        int padding = context.getResources().getInteger(R.integer.export_pdf_page_padding);
        int padding = 20;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append(context.getResources().getString(R.string.first_name)).append(": ");
        if(user.getFirstName() != null) {
            sb.append(user.getFirstName()).append("\n");
        }
        sb.append(context.getResources().getString(R.string.last_name)).append(": ");
        if(user.getLastName() != null) {
            sb.append(user.getLastName()).append("\n");
        }
        sb.append(context.getResources().getString(R.string.date_of_birth)).append(": ");
        if(user.getDateOfBirth() != null) {
            sb.append(dateFormat.format(user.getDateOfBirth())).append("\n");
        }
        sb.append(context.getResources().getString(R.string.gender)).append(": ");
        if(user.getGender() != null) {
            String gender = context.getResources().getString(R.string.female);
            if(user.getGender() == Gender.MALE) {
                gender = context.getResources().getString(R.string.male);
            }
            sb.append(gender).append("\n");
        }

//        sb.setLength(0);
//        for (DiaryEntryInterface diaryEntry : diaryEntries) {
//            sb.append(dateFormat.format(diaryEntry.getDate())).append("\n");
//        }

        // write entries - TODO care about pagination at some point
        canvas.save();
        canvas.translate(padding, padding);
        StaticLayout layout = new StaticLayout(sb.toString(), textPaint, WIDTH_A4 / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        layout.draw(canvas);


        document.finishPage(page);

        return document;
    }

}
