package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.content.SharedPreferences;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;

import java.util.List;

public class htmlFormat {

    public static String htmlFormat(List<ItemsModel> list_recent_item, String prm_doc_no, String prm_date, SharedPreferences sp) {
        String retHtml = "";
        double sumAmount = 0.00;

        retHtml = "<html>\n"
                + "    <style>\n"
                + "        table {\n"
                + "            table-layout: fixed;\n"
                + "            width: 100%;\n"
                + "            border-collapse: collapse;\n"
                + "        }\n"
                + "        thead th:nth-child(1) {\n"
                + "            width: 5%;\n"
                + "        }\n"
                + "        thead th:nth-child(2) {\n"
                + "            width: 10%;\n"
                + "        }\n"
                + "        thead th:nth-child(3) {\n"
                + "            width: 15%;\n"
                + "        }\n"
                + "        thead th:nth-child(4) {\n"
                + "            width: 30%;\n"
                + "        }\n"
                + "        thead th:nth-child(5) {\n"
                + "            width: 5%;\n"
                + "        }\n"
                + "        thead th:nth-child(6) {\n"
                + "            width: 10%;\n"
                + "        }\n"
                + "        \n"
                + "        thead th:nth-child(7) {\n"
                + "            width: 10%;\n"
                + "        }\n"
                + "        th, td {\n"
                + "            padding: 20px;\n"
                + "        }\n"
                + "    </style>\n"
                + "    <body class = \"center;\"> \n"
                + "        <table>\n"
                + "            <caption> \n"
                + "                <p>ARPICO SUPER CENTER</p>\n"
                + "                <p>Better For You</p><label></label>\n"
                + "                <p>RICHARD PIERIS DISTRIBUTORS LTD.</p>\n"
                + "                <p>" + sp.getString(ConfigMP.SP_LOCATION, "") + "</p>\n"
                + "                <p>Tel : " + sp.getString(ConfigMP.SP_LOC_TEL_NO, "") + " Fax : " + sp.getString(ConfigMP.SP_LOC_FAX_NO, "") + "</p>\n"
                + "                <p>" + prm_date + " Rec No : " + prm_doc_no + " " + sp.getString(ConfigMP.SP_USERID, "") + "/" + sp.getString(ConfigMP.SP_TBCODE, "") + "</p>\n"
                + "            </caption>\n"
                + "            <thead>\n"
                + "                <tr>\n"
                + "                    <th scope=\"col\" style=\"text-align: left\">Line No</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: left\">PLU</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: left\">ITEM CODE</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: left\">DESCRIPTION</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: right\">QTY</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: right\">GROSS</th>\n"
                + "                    <th scope=\"col\" style=\"text-align: right\">NETAMT</th>\n"
                + "                </tr>\n"
                + "            </thead>\n"
                + "            <tbody>\n";

        for (int i = 0; i < list_recent_item.size(); i++) {
            ItemsModel itemsModel = list_recent_item.get(i);

            sumAmount += new Double(itemsModel.getROWSUM());

            retHtml += "                <tr>\n"
                    + "                    <th scope=\"row\" style=\"text-align: left\">" + i + 1 + "</th>\n"
                    + "                    <td style=\"text-align: left\">" + itemsModel.getPLUCOD() + "</td>\n"
                    + "                    <td style=\"text-align: left\">" + itemsModel.getITMCOD() + "</td>\n"
                    + "                    <td style=\"text-align: left\">" + itemsModel.getITMDES() + "</td>\n"
                    + "                    <td style=\"text-align: right\">" + itemsModel.getICOUNT() + "</td>\n"
                    + "                    <td style=\"text-align: right\">" + itemsModel.getUNIPRI() + "</td>\n"
                    + "                    <td style=\"text-align: right\">" + itemsModel.getROWSUM() + "</td>\n"
                    + "                </tr>\n";
        }

        retHtml += "                <tr><th scope=\"row\" style=\"text-align: left\"></th></tr>\n"
                + "            </tbody>\n"
                + "            <tfoot>\n"
                + "                <tr>\n"
                + "                    <th scope=\"row\" colspan=\"6\" style=\"text-align: right\">Total Bill Val</th>\n"
                + "                    <td style=\"text-align: right\">" + sumAmount + "</td>\n"
                + "                </tr>\n"
                + "            </tfoot>\n"
                + "        </table>\n"
                + "                <p>Start Time : " + sp.getString(ConfigMP.SP_START_TIME, "") + " End Time : " + sp.getString(ConfigMP.SP_END_TIME, "") + "</p>\n"
                + "        <div style=\"text-align: center;\">\n"
                + "            <label></label>\n"
                + "            <p>Items with proof of purchase will be exchanged within five days.</p>\n"
                + "            <p>Undergarments, Pharmaceuticals, Liquor, Baby  products, Sale Items and all accessories are non exchangeable.</p>\n"
                + "            <p>In case of price discrepancy, return the item & bill within 7 days for refund of difference.</p>\n"
                + "            <p>********* Thank You Come Again *********</p>\n"
                + "        </div>\n"
                + "    </body>\n"
                + "</html>";

        return retHtml;

    }
}
