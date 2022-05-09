package com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler;

import android.database.sqlite.SQLiteDatabase;

public class DbTables {

    public void createDbTables(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ConfigMP.TABLE_ITEMS + " ( "
                + ConfigMP.C_PLUCOD + " VARCHAR(6),"
                + ConfigMP.C_ITMCOD + " VARCHAR(16), "
                + ConfigMP.C_ITMDES + " VARCHAR(50), "
                + ConfigMP.C_BARCOD + " VARCHAR(50), "
                + ConfigMP.C_UNIPRI + " DECIMAL(14,4), "
                + ConfigMP.C_ENCODE + " VARCHAR(50), "
                + ConfigMP.C_ICOUNT + " DECIMAL(12,4), "
                + ConfigMP.C_ROWSUM + " DECIMAL(12,4), "
                + " PRIMARY KEY ("
                + ConfigMP.C_PLUCOD + ", " + ConfigMP.C_ITMCOD + ") "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ConfigMP.TABLE_POS_TXN_MAS + " ( "
                + ConfigMP.C_SBUCOD + " VARCHAR(6),"
                + ConfigMP.C_LOCCOD + " VARCHAR(6),"
                + ConfigMP.C_TBCODE + " VARCHAR(30), "
                + ConfigMP.C_DOC_NO + " VARCHAR(10),"
                + ConfigMP.C_TXNDAT + " VARCHAR(10),"
                + ConfigMP.C_USERID + " VARCHAR(10),"
                + ConfigMP.C_NETAMT + " DECIMAL(14,4),"
                + ConfigMP.C_CSHAMT + " DECIMAL(14,4),"
                + ConfigMP.C_CHGAMT + " DECIMAL(14,4),"
                + ConfigMP.C_TOTDIS + " DECIMAL(14,4),"
                + ConfigMP.C_STTIME + " TIME ,"
                + ConfigMP.C_ENTIME + " TIME ,"
                + ConfigMP.C_INVSTS + " VARCHAR(10),"
                + ConfigMP.C_REMARK + " TEXT,"
                + ConfigMP.C_CREABY + " VARCHAR(30), "
                + ConfigMP.C_CREADT + " DATETIME, "
                + " PRIMARY KEY ("
                + ConfigMP.C_SBUCOD + ", " + ConfigMP.C_LOCCOD + ", " + ConfigMP.C_TBCODE + ", " + ConfigMP.C_DOC_NO + ", " + ConfigMP.C_TXNDAT + ", " + ConfigMP.C_USERID + ") "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ConfigMP.TABLE_POS_TXN_DET + " ( "
                + ConfigMP.C_SBUCOD + " VARCHAR(6),"
                + ConfigMP.C_LOCCOD + " VARCHAR(6),"
                + ConfigMP.C_TBCODE + " VARCHAR(30), "
                + ConfigMP.C_DOC_NO + " VARCHAR(10),"
                + ConfigMP.C_TXNDAT + " VARCHAR(10),"
                + ConfigMP.C_SEQ_ID + " VARCHAR(6),"
                + ConfigMP.C_USERID + " VARCHAR(10),"
                + ConfigMP.C_PLUCOD + " VARCHAR(20),"
                + ConfigMP.C_ITMCOD + " VARCHAR(50), "
                + ConfigMP.C_ITMQTY + " VARCHAR(10), "
                + ConfigMP.C_UNIPRI + " DECIMAL(14,4), "
                + ConfigMP.C_DISAMT + " DECIMAL(14,4), "
                + ConfigMP.C_DISPER + " DECIMAL(14,4), "
                + ConfigMP.C_BARCOD + " VARCHAR(50), "
                + ConfigMP.C_ENCODE + " VARCHAR(50), "
                + ConfigMP.C_INVSTS + " VARCHAR(10),"
                + ConfigMP.C_CREABY + " VARCHAR(30), "
                + ConfigMP.C_CREADT + " DATETIME, "
                + " PRIMARY KEY ("
                + ConfigMP.C_SBUCOD + ", " + ConfigMP.C_LOCCOD + ", " + ConfigMP.C_TBCODE + ", " + ConfigMP.C_DOC_NO + ", " + ConfigMP.C_TXNDAT + ", " + ConfigMP.C_SEQ_ID + ", " + ConfigMP.C_USERID + ") "
                + " )");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + ConfigMP.TABLE_SMPROGRMPARA + " ( "
                + ConfigMP.C_PRGNAM + " VARCHAR(20), "
                + ConfigMP.C_PARCOD + " VARCHAR(30), "
                + ConfigMP.C_COMENT + " VARCHAR(300), "
                + " PRIMARY KEY ("
                + ConfigMP.C_PRGNAM + ", " + ConfigMP.C_PARCOD + ") "
                + " )");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ConfigMP.TABLE_SEQUENCE + " ( "
                + ConfigMP.C_SBUCOD + " VARCHAR(5), "
                + ConfigMP.C_LOCCOD + " VARCHAR(5), "
                + ConfigMP.C_SEQ_ID + " INT(5), "
                + ConfigMP.C_INCRBY + " VARCHAR(10), "
                + ConfigMP.C_MINVAL + " VARCHAR(10), "
                + ConfigMP.C_MAXVAL + " VARCHAR(12), "
                + ConfigMP.C_CURVAL + " VARCHAR(10), "
                + " PRIMARY KEY ("
                + ConfigMP.C_SBUCOD + ", " + ConfigMP.C_LOCCOD + ", " + ConfigMP.C_SEQ_ID + ") "
                + " )");

    }
}
