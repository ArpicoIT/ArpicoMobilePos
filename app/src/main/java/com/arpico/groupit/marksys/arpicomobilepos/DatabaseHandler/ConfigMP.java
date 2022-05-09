package com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler;

public class ConfigMP {
    public static final String TABLE_ITEMS = "FIND_ITEMS";
    public static final String TABLE_POS_TXN_MAS = "FIND_POS_TXN_MAS";
    public static final String TABLE_POS_TXN_DET = "FIND_POS_TXN_DET";
    public static final String TABLE_SMPROGRMPARA = "SMPROGRMPARA";
    public static final String TABLE_SEQUENCE = "SEQUENCE";

    //FIND_POS_TXN_MAS
    public static final String C_SBUCOD = "SBUCOD";
    public static final String C_LOCCOD = "LOCCOD";
    public static final String C_TBCODE = "TBCODE";
    public static final String C_DOC_NO = "DOC_NO";
    public static final String C_TXNDAT = "TXNDAT";//Txn Date
    public static final String C_USERID = "USERID";
    public static final String C_NETAMT = "NETAMT";//Net Amount
    public static final String C_CSHAMT = "CSHAMT";//Cash Amount
    public static final String C_CHGAMT = "CHGAMT";//Change Amount
    public static final String C_TOTDIS = "TOTDIS";//Total Discount
    public static final String C_STTIME = "STTIME";//Start Time
    public static final String C_ENTIME = "ENTIME";//ENd Time
    public static final String C_INVSTS = "INVSTS";//INV Status
    public static final String C_REMARK = "REMARK";
    public static final String C_CREABY = "CREABY";
    public static final String C_CREADT = "CREADT";

    //FIND_POS_TXN_DET
    public static final String C_SEQ_ID = "SEQ_ID";
    public static final String C_PLUCOD = "PLUCOD";
    public static final String C_ITMCOD = "ITMCOD";
    public static final String C_ITMDES = "ITMDES";
    public static final String C_ITMQTY = "ITMQTY";
    public static final String C_UNIPRI = "UNIPRI";
    public static final String C_DISAMT = "DISAMT";
    public static final String C_DISPER = "DISPER";
    public static final String C_BARCOD = "BARCOD";
    public static final String C_ENCODE = "ENCODE";
    public static final String C_ICOUNT = "ICOUNT";
    public static final String C_ROWSUM = "ROWSUM";

    //SMPROGRMPARA
    public static final String C_PRGNAM = "PRGNAM";
    public static final String C_PARCOD = "PARCOD";
    public static final String C_COMENT = "COMENT";

    public static final String C_INCRBY = "INCRBY";
    public static final String C_MINVAL = "MINVAL";
    public static final String C_MAXVAL = "MAXVAL";
    public static final String C_CURVAL = "CURVAL";

    public static final String SQ_POSBILL = "POSBILLSQ";

    public static final String SP_IMEINO = "IMEINO";
    public static final String SP_LANGUA = "LANGUA";
    public static final String SP_INTROW = "INTROW";
    public static final String SP_INPFIL = "INPFIL";//input file type
    public static final String SP_OUTFIL = "OUTFIL";//output file type
    public static final String SP_TBCODE = "TBCODE";
    public static final String SP_LOCCOD = "LOCCOD";
    public static final String SP_USERID = "USERID";
    public static final String SP_AUTHID = "AUTHID";
    public static final String SP_HOST = "HOST";
    public static final String SP_CLICK = "CLICK";
    public static final String SP_DECIMAL = "DECIMAL";
    public static final String SP_COUNT_TYPE = "COUNT_TYPE";
    public static final String SP_BILLNO = "SP_BILLNO";

    public static final String SP_LOCATION = "LOCATION";
    public static final String SP_LOC_TEL_NO = "LOC_TEL_NO";
    public static final String SP_LOC_FAX_NO = "LOC_FAX_NO";

    public static final String SP_START_TIME = "START_TIME";
    public static final String SP_END_TIME = "END_TIME";

    public static final String SP_FTP = "FTP";
    public static final String SP_FTP_IP = "FTP_IP";
    public static final String SP_FTP_PORT = "FTP_PORT";
    public static final String SP_FTP_USER = "FTP_USER";
    public static final String SP_FTP_PASS = "FTP_PASS";

    public static final String column = "column";
    public static final String count = "count";
    public static final String TITLE = "TITLE";
    public static final String SYNC_SUCCESS = "Y";
    public static final String SYNC_FAIL = "N";
    public static final int PORT = 9876;

    public static String LOCAL_SUB_BACKUP_RST_PATH = "BackupSF/BackupRST";
    public static String LOCAL_SUB_BACKUP_DB_PATH = "BackupSF/BackupDB";


    public static final String C_USERPW = "PASSWD";
    public static final String C_ROWNUM = "ROWNUM";


}
