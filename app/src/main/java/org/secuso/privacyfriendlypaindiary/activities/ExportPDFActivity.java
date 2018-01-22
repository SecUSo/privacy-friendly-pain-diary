/*
 This file is part of Privacy Friendly App Example.

 Privacy Friendly App Example is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly App Example is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly App Example. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlypaindiary.activities;

import android.app.FragmentTransaction;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.widget.Toast;

import org.secuso.privacyfriendlypaindiary.R;
import org.secuso.privacyfriendlypaindiary.helpers.DateDialog;
import org.secuso.privacyfriendlypaindiary.helpers.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportPDFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_pdf);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fromButton:
            case R.id.untilButton:
            case R.id.fromDate:
            case R.id.untilDate:
                //DateDialog dialog=new DateDialog(myView);
                //FragmentTransaction ft =getFragmentManager().beginTransaction();
                //dialog.show(ft, "DatePicker");
                break;
            case R.id.create:
                createPdf();
                break;
            default:
                break;
        }
    }

    public void onStart(){
        super.onStart();
//        EditText txtDate=(EditText)findViewById(R.id.fromDate);
//
//        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            public void onFocusChange(View view, boolean hasfocus){
//                if(hasfocus){
//                    DateDialog dialog=new DateDialog(view);
//                    FragmentTransaction ft =getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DatePicker");
//                }
//            }
//        });
    }

    private void createPdf(){
        // create a new document
        PdfDocument document = new PdfDocument();
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // crate a page description
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(width, height, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        canvas.drawText("Vorname: Max       Name: Mustermann",100, height/2, paint);

        // finish the page
        document.finishPage(page);

        // create pain diary entry pages
        for (View v : Helper.pdfViewList){
            page = document.startPage(pageInfo);
            // draw view on the page
            View content = v;//this.findViewById(android.R.id.content);
            content.draw(page.getCanvas());
            // finish the page
            document.finishPage(page);
        }

        // write the document content
        String targetPdf = "/sdcard/PainDiary.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "PainDiary.pfd created", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

}
