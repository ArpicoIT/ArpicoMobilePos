package com.arpico.groupit.marksys.arpicomobilepos.Models;

public class ItemsModel {

    private String ROWNUM;
    private String PLUCOD;
    private String ITMCOD;
    private String ITMDES;
    private String BARCOD;
    private String UNIPRI;
    private String ENCODE;
    private String ICOUNT;
    private String TBCODE;
    private String CREABY;
    private String CREADT;
    private String ROWSUM;

    public ItemsModel() {
    }

    public ItemsModel(String ROWNUM, String PLUCOD, String ITMCOD, String ITMDES, String BARCOD, String UNIPRI, String ENCODE, String ICOUNT, String ROWSUM, String TBCODE, String CREABY, String CREADT) {
        this.ROWNUM = ROWNUM;
        this.PLUCOD = PLUCOD;
        this.ITMCOD = ITMCOD;
        this.ITMDES = ITMDES;
        this.BARCOD = BARCOD;
        this.UNIPRI = UNIPRI;
        this.ENCODE = ENCODE;
        this.ICOUNT = ICOUNT;
        this.ROWSUM = ROWSUM;
        this.TBCODE = TBCODE;
        this.CREABY = CREABY;
        this.CREADT = CREADT;
    }

    public String getROWNUM() {
        return ROWNUM;
    }

    public String getPLUCOD() {
        return PLUCOD;
    }

    public String getITMCOD() {
        return ITMCOD;
    }

    public String getITMDES() {
        return ITMDES;
    }

    public String getBARCOD() {
        return BARCOD;
    }

    public String getUNIPRI() {
        return UNIPRI;
    }

    public String getENCODE() {
        return ENCODE;
    }

    public String getICOUNT() {
        return ICOUNT;
    }

    public String getROWSUM() {
        return ROWSUM;
    }

    public String getTBCODE() {
        return TBCODE;
    }

    public String getCREABY() {
        return CREABY;
    }

    public String getCREADT() {
        return CREADT;
    }

    public void setROWNUM(String ROWNUM) {
        this.ICOUNT = ICOUNT;

    }

    public void setROWSUM(String ROWSUM) {
        this.ROWSUM = ROWSUM;
    }

    public void setICOUNT(String ICOUNT) {
        this.ICOUNT = ICOUNT;
    }


}
