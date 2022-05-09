package com.arpico.groupit.marksys.arpicomobilepos.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Sampath-IT on 2017-09-28.
 */

public class CreatePDF {

    public File createReportPDF(List<ItemsModel> list_recent_item, String prm_doc_no, String prm_date, SharedPreferences sp, Context mContext) {
        File myFile = null;
        double sumAmount = 0.00;
        try {
            Font hedFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL);
            Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.RED);
            Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            Font subFontB = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
            Font smallFontB = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLD);

            File pdfFolder = new File(Environment.getExternalStorageDirectory(), "BackupSF/" + "Reports");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
            }

            pdfFolder = new File("/sdcard/BackupSF/Reports");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }

            pdfFolder = new File("/sdcard/BackupSF/Reports/" + prm_date);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }

            String fileName = new SimpleDateFormat("HHmmss").format(new Date()) + "_" + prm_doc_no + ".pdf";

            System.out.println("fileName : " + fileName);
            myFile = new File(pdfFolder.getAbsolutePath(), fileName);

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(myFile));
            document.open();

            float[] columnWidths = {10, 10, 50, 10, 10, 10};
            PdfPTable table = new PdfPTable(columnWidths);

            Drawable d = mContext.getResources().getDrawable(R.drawable.logo);
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

            Image image = Image.getInstance(getBytes(bitmap));   // byte[] sign
            image.scaleAbsolute(120f,80f);

            PdfPCell cell_image = new PdfPCell(image);
            cell_image.setPadding(10.0f);
            cell_image.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell_image).setBorder(Rectangle.NO_BORDER);

//            PdfPCell cell_header = new PdfPCell(new Phrase("ARPICO SUPER CENTER\n" + "Better For You", hedFont));
//            cell_header.setColspan(10);
//            cell_header.setHorizontalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell_header).setBorder(Rectangle.NO_BORDER);

            PdfPCell cell_sub_header = new PdfPCell(new Phrase("RICHARD PIERIS DISTRIBUTORS LTD.\n"
                    + sp.getString(ConfigMP.SP_LOCATION, "") + "\n"
                    + "Tel : " + sp.getString(ConfigMP.SP_LOC_TEL_NO, "") + " Fax : " + sp.getString(ConfigMP.SP_LOC_FAX_NO, "") + "\n"
                    + prm_date + " Rec No : " + prm_doc_no + " " + sp.getString(ConfigMP.SP_USERID, "") + "/" + sp.getString(ConfigMP.SP_TBCODE, ""), catFont));
            cell_sub_header.setColspan(10);
            cell_sub_header.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_sub_header.setPadding(10.0f);
            table.addCell(cell_sub_header).setBorder(Rectangle.NO_BORDER);

            table.addCell("Line No");
            table.addCell("PLU");
            table.addCell("DESCRIPTION");
            table.addCell("QTY");
            table.addCell("GROSS");
            table.addCell("NETAMT");

            for (int i = 0; i < list_recent_item.size(); i++) {
                ItemsModel itemsModel = list_recent_item.get(i);

                sumAmount += new Double(itemsModel.getROWSUM());

                PdfPCell cell_count = new PdfPCell(new Phrase(String.valueOf(i + 1)));
                cell_count.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell_count);

                table.addCell(itemsModel.getPLUCOD());
                table.addCell(itemsModel.getITMDES());

                PdfPCell cell_qty = new PdfPCell(new Phrase(itemsModel.getICOUNT()));
                cell_qty.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell_qty);

                PdfPCell cell_unit_price = new PdfPCell(new Phrase(itemsModel.getUNIPRI()));
                cell_unit_price.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell_unit_price);

                PdfPCell cell_row_sum = new PdfPCell(new Phrase(itemsModel.getROWSUM()));
                cell_row_sum.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell_row_sum);
            }

            PdfPCell cell_time = new PdfPCell(new Phrase("Start Time : " + sp.getString(ConfigMP.SP_START_TIME, "") + " End Time : " + sp.getString(ConfigMP.SP_END_TIME, "") + ""));
            cell_time.setColspan(3);
            cell_time.setPaddingTop(10.0f);
            cell_time.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell_time).setBorder(Rectangle.NO_BORDER);

            PdfPCell cell_bill_val = new PdfPCell(new Phrase("Total Bill Val : " + sumAmount));
            cell_bill_val.setColspan(3);
            cell_bill_val.setPaddingTop(10.0f);
            cell_bill_val.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell_bill_val).setBorder(Rectangle.NO_BORDER);

            PdfPCell DD = new PdfPCell(new Phrase("Items with proof of purchase will be exchanged within five days.\n"
                    + "Undergarments, Pharmaceuticals, Liquor, Baby  products, Sale Items and all accessories are non exchangeable.\n"
                    + "In case of price discrepancy, return the item & bill within 7 days for refund of difference.\n\n"
                    + "********* Thank You Come Again *********", subFont));
            DD.setColspan(10);
            DD.setPaddingTop(10.0f);
            DD.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(DD).setBorder(Rectangle.NO_BORDER);

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myFile;
    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
//    private static void addEmptyLine(Paragraph paragraph, int number) {
//        for (int i = 0; i < number; i++) {
//            paragraph.add(new Paragraph(" "));
//        }
//    }

//    public File GeneratePDF(List<ItemsModel> list_recent_item, String prm_doc_no, String prm_date, SharedPreferences sp) {
//        File myFile = null;
//
//        try {
//            File pdfFolder = new File(Environment.getExternalStorageDirectory(), "BackupSF/" + "Reports");
//            boolean isPresent = true;
//            if (!pdfFolder.exists()) {
//                isPresent = pdfFolder.mkdir();
//            }
//
//            if (!isPresent) {
//                pdfFolder = new File("/sdcard/BackupSF/Reports");
//                pdfFolder.mkdirs();
//            }
//
//            Date date = new Date();
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
//
//            String fileName = timeStamp + ".pdf";
//
//            System.out.println("fileName : " + fileName);
//            myFile = new File(pdfFolder.getAbsolutePath(), fileName);
//
//            OutputStream file = new FileOutputStream(myFile);
//            Document document = new Document(PageSize.A4);
//            PdfWriter writer = PdfWriter.getInstance(document, file);
//            document.open();
//
//            String htmlFile = htmlFormat.htmlFormat(list_recent_item, prm_doc_no, prm_date, sp);
//
//            InputStream is = new ByteArrayInputStream(htmlFile.getBytes());
//            XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
//            document.close();
//            file.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return myFile;
//    }

}
