package com.arpico.groupit.marksys.arpicomobilepos.Common;

public class mailBody {

    public static  String mailBody(String prm_ord_no, String prm_dealer, String prm_ord_date, String prm_ord_tot, String prm_cre_by) {
        String retRes = "";

        retRes = "<html>\n"
                + "<head>\n"
                + "<title></title>\n"
                + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "<style>\n"
                + ".labelFormat{\n"
                + "float:left;\n"
                + "display:block;\n"
                + "margin-right: 5px;\n"
                + "margin-left: 30px;\n"
                + "width:175px;"
                + "}\n"
                + ".startX2{\n"
                + "margin-left: 30px;\n"
                + "}\n"
                + ".start{\n"
                + "margin-left: 15px;\n"
                + "}\n"
                + ".highlight{\n"
                + "color: blue;\n"
                + "}\n"
                + "p{\n"
                + "margin-top: 2px;\n"
                + "margin-bottom: 2px;\n"
                + "}\n"
                + "\n"
                + "u{\n"
                + "color: blue;\n"
                + "}\n"
                + "</STYLE>\n"
                + "</head>\n"
                + "<body>\n";

        try {
            retRes += "<div class=\"start\">\n"
                    + "<label class=\"labelFormat\">Bill No</label><p>: " + prm_ord_no + "</p>\n"
                    + "<label class=\"labelFormat\">Date</label><p>: " + prm_dealer + "</p>\n"
                    + "<br/>\n"
                    + "<div class=\"start\"><b>Item Details</b><br/>\n"
                    + "<label class=\"labelFormat\" >Ordered Date</label><p>: " + prm_ord_date + "</p>\n"
                    + "<label class=\"labelFormat\" >Order Total Value</label><p>: " + prm_ord_tot + "</p>\n"
                    + "<label class=\"labelFormat\" >Created by</label><p>: " + prm_cre_by + "</p>\n"
                    + "</div>\n"
                    + "<br/>\n"
                    + "<p class=\"start\">This is System Generated email.For any queries please contact the sender of this mail.</p>\n"
                    + "</div><br/>\n"
                    + "</body>\n"
                    + "</html>";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retRes;
    }
}
