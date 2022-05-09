package com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler;

import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_BARCOD;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_COMENT;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_CREABY;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_CREADT;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ENCODE;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ICOUNT;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ITMCOD;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ITMDES;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_PARCOD;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_PLUCOD;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ROWNUM;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_ROWSUM;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_TBCODE;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.C_UNIPRI;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.SP_TBCODE;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.SP_USERID;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.SQ_POSBILL;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.SYNC_FAIL;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.column;
import static com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP.count;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.arpico.groupit.marksys.arpicomobilepos.Common.ComHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hayaz on 27-05-2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    private static final String DATABASE = "MobilePos.db";
    private static final int VERSION = 1;

    private SQLiteDatabase db;
    private ArrayList<String> teams;
    private Context mContext;

    ContentValues contentPosMas;
    ContentValues contentPosDet;

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    public DbHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new DbTables().createDbTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop old version table
        //db.execSQL("Drop table if exists " + TABLE_rms_Sal_InqH);

        // Create New Version table
        onCreate(db);
    }

    @SuppressLint("Range")
    public JSONArray getViewItemsData(String code, String displayFilter) {
        JSONArray arr = new JSONArray();
        JSONObject obj = null;
        Cursor c = null;
        String filter = "";

        if (!code.isEmpty()) {
            if (code.length() < 7) {
                filter = " where " + C_PLUCOD + "='" + code + "' ";
            } else {
                filter = " where (" + C_BARCOD + "='" + code + "' or  " + C_ENCODE + "='" + code + "') ";
            }
        }

        c = getData("SELECT * FROM " + ConfigMP.TABLE_ITEMS + filter + displayFilter);//+" limit 100"
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    obj = new JSONObject();
                    try {
                        obj.put(C_PLUCOD, c.getString(c.getColumnIndex(C_PLUCOD)));
                        obj.put(C_ITMCOD, c.getString(c.getColumnIndex(C_ITMCOD)));
                        obj.put(C_ITMDES, c.getString(c.getColumnIndex(C_ITMDES)));
                        obj.put(C_BARCOD, c.getString(c.getColumnIndex(C_BARCOD)));
                        obj.put(C_UNIPRI, c.getString(c.getColumnIndex(C_UNIPRI)));
                        obj.put(C_ENCODE, c.getString(c.getColumnIndex(C_ENCODE)));
                        obj.put(C_ICOUNT, c.getString(c.getColumnIndex(C_ICOUNT)));
                        obj.put(C_ROWSUM, c.getString(c.getColumnIndex(C_ROWSUM)));
                        arr.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        }

        return arr;
    }

    @SuppressLint("Range")
    public JSONArray getCountItems(String code, String displayFilter) {
        JSONArray arr = new JSONArray();
        JSONObject obj = null;
        Cursor c = null;
        String filter = "";

        if (!code.isEmpty()) {
            if (code.length() < 7) {
                filter = " where " + C_PLUCOD + "='" + code + "' ";
            } else {
                filter = " where (" + C_BARCOD + "='" + code + "' or  " + C_ENCODE + "='" + code + "') ";
            }
        }

        c = getData("SELECT * FROM " + ConfigMP.TABLE_ITEMS + filter + displayFilter);//+" limit 100"
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    obj = new JSONObject();
                    try {
                        obj.put(C_PLUCOD, c.getString(c.getColumnIndex(C_PLUCOD)));
                        obj.put(C_ITMCOD, c.getString(c.getColumnIndex(C_ITMCOD)));
                        obj.put(C_ITMDES, c.getString(c.getColumnIndex(C_ITMDES)));
                        obj.put(C_BARCOD, c.getString(c.getColumnIndex(C_BARCOD)));
                        obj.put(C_UNIPRI, c.getString(c.getColumnIndex(C_UNIPRI)));
                        obj.put(C_ENCODE, c.getString(c.getColumnIndex(C_ENCODE)));
                        obj.put(C_ICOUNT, c.getString(c.getColumnIndex(C_ICOUNT)));
                        obj.put(C_ROWSUM, c.getString(c.getColumnIndex(C_ROWSUM)));
//                        obj.put(C_TBCODE, c.getString(c.getColumnIndex(C_TBCODE)));
//                        obj.put(C_CREABY, c.getString(c.getColumnIndex(C_CREABY)));
//                        obj.put(C_CREADT, c.getString(c.getColumnIndex(C_CREADT)));
                        arr.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } while (c.moveToNext());
            }
        }

        return arr;
    }

    public JSONObject updateCartItems(ItemsModel selectItem) {
        JSONObject retObj = new JSONObject();
        ContentValues values = new ContentValues();

        try {
            db = getWritableDatabase();

            if (selectItem == null) {
                values.put(C_ICOUNT, "0");
                values.put(C_ROWSUM, "0");
                db.update(ConfigMP.TABLE_ITEMS, values, null, null);

            } else {
                values.put(C_ICOUNT, selectItem.getICOUNT());
                values.put(C_ROWSUM, selectItem.getROWSUM());
                db.update(ConfigMP.TABLE_ITEMS, values, C_PLUCOD + "=? ", new String[]{selectItem.getPLUCOD()});
            }
            db.close();

            retObj.put("result", "success");
            retObj.put("message", "Successfully Update");

        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            try {
                retObj.put("result", "failed");
                retObj.put("message", e.toString());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return retObj;
    }


    public JSONObject updateItems(ItemsModel selectItem, boolean boolIsInsert) {
        JSONObject retObj = new JSONObject();
        ContentValues values = new ContentValues();

        try {
            values.put(C_ICOUNT, selectItem.getICOUNT());

            db = getWritableDatabase();

            if (boolIsInsert) {
                values.put(C_PLUCOD, selectItem.getPLUCOD());
                values.put(C_ITMCOD, selectItem.getITMCOD());
                values.put(C_ITMDES, selectItem.getITMDES());
                values.put(C_BARCOD, selectItem.getBARCOD());
                values.put(C_UNIPRI, selectItem.getUNIPRI());
                values.put(C_ENCODE, selectItem.getENCODE());

                db.insert(ConfigMP.TABLE_ITEMS, null, values);

            } else {
                db.update(ConfigMP.TABLE_ITEMS, values, C_PLUCOD + "=? ", new String[]{selectItem.getPLUCOD()});
            }
            db.close();

            retObj.put("result", "success");
            retObj.put("message", "Successfully Update");

        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            try {
                retObj.put("result", "failed");
                retObj.put("message", e.toString());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return retObj;
    }

    public JSONObject saveReceipt(List<ItemsModel> list_recent_item, SharedPreferences sp, String prm_net_amount, String prm_cashamt, String prm_changeamt, String prm_remark) {
        JSONObject retObj = new JSONObject();
        String number[] = new String[]{"0", ""};
        int ordnum = 0;
        try {
            if (list_recent_item.size() > 0) {
                contentPosMas = new ContentValues();
                contentPosDet = new ContentValues();

                for (int i = 0; i < list_recent_item.size(); i++) {
                    ItemsModel itemsModel = list_recent_item.get(i);

                    if (i == 0) {
                        number = getNewSerialNumber("830", sp.getString(ConfigMP.SP_LOCCOD, ""), SQ_POSBILL);
                        if (number[0].equalsIgnoreCase("0") || number[0].equalsIgnoreCase("Success")) {
                            if (!number[0].equalsIgnoreCase("0")) {
                                ordnum = Integer.parseInt(number[1]); // add the serail number
                            }
                        }

                        contentPosMas.put(ConfigMP.C_SBUCOD, "830");
                        contentPosMas.put(ConfigMP.C_LOCCOD, sp.getString(ConfigMP.SP_LOCCOD, ""));
                        contentPosMas.put(ConfigMP.C_TBCODE, sp.getString(ConfigMP.SP_TBCODE, ""));
                        contentPosMas.put(ConfigMP.C_DOC_NO, ordnum);
                        contentPosMas.put(ConfigMP.C_TXNDAT, getTime(2));
                        contentPosMas.put(ConfigMP.C_USERID, sp.getString(ConfigMP.SP_USERID, ""));
                        contentPosMas.put(ConfigMP.C_NETAMT, prm_net_amount);
                        contentPosMas.put(ConfigMP.C_CSHAMT, prm_cashamt);
                        contentPosMas.put(ConfigMP.C_CHGAMT, prm_changeamt);
                        contentPosMas.put(ConfigMP.C_TOTDIS, "0");
                        contentPosMas.put(ConfigMP.C_STTIME, sp.getString(ConfigMP.SP_START_TIME, ""));
                        contentPosMas.put(ConfigMP.C_ENTIME, sp.getString(ConfigMP.SP_END_TIME, ""));
                        contentPosMas.put(ConfigMP.C_INVSTS, "");
                        contentPosMas.put(ConfigMP.C_REMARK, prm_remark);
                        contentPosMas.put(ConfigMP.C_CREABY, sp.getString(ConfigMP.SP_USERID, ""));
                        contentPosMas.put(ConfigMP.C_CREADT, getTime(1));

                        db = this.getWritableDatabase();
                        db.insert(ConfigMP.TABLE_POS_TXN_MAS, null, contentPosMas);
                        db.close();

                    }

                    contentPosDet.put(ConfigMP.C_SBUCOD, "830");
                    contentPosDet.put(ConfigMP.C_LOCCOD, sp.getString(ConfigMP.SP_LOCCOD, ""));
                    contentPosDet.put(ConfigMP.C_TBCODE, sp.getString(ConfigMP.SP_TBCODE, ""));
                    contentPosDet.put(ConfigMP.C_DOC_NO, ordnum);
                    contentPosDet.put(ConfigMP.C_TXNDAT, getTime(2));
                    contentPosDet.put(ConfigMP.C_SEQ_ID, String.valueOf(i));
                    contentPosDet.put(ConfigMP.C_USERID, sp.getString(ConfigMP.SP_USERID, ""));
                    contentPosDet.put(ConfigMP.C_PLUCOD, itemsModel.getPLUCOD());
                    contentPosDet.put(ConfigMP.C_ITMCOD, itemsModel.getITMCOD());
                    contentPosDet.put(ConfigMP.C_ITMQTY, itemsModel.getICOUNT());
                    contentPosDet.put(ConfigMP.C_UNIPRI, itemsModel.getUNIPRI());
                    contentPosDet.put(ConfigMP.C_DISAMT, "0");
                    contentPosDet.put(ConfigMP.C_DISPER, "0");
                    contentPosDet.put(ConfigMP.C_BARCOD, itemsModel.getBARCOD());
                    contentPosDet.put(ConfigMP.C_ENCODE, itemsModel.getENCODE());
                    contentPosDet.put(ConfigMP.C_ENCODE, itemsModel.getENCODE());
                    contentPosDet.put(ConfigMP.C_INVSTS, "");
                    contentPosDet.put(ConfigMP.C_CREABY, sp.getString(ConfigMP.SP_USERID, ""));
                    contentPosDet.put(ConfigMP.C_CREADT, getTime(1));

                    db = this.getWritableDatabase();
                    db.insert(ConfigMP.TABLE_POS_TXN_DET, null, contentPosDet);
                    db.close();

                }

                retObj.put("result", "success");
                retObj.put("message", "Successfully Update");
                retObj.put("doc_no", ordnum);

            }

        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
            try {
                retObj.put("result", "failed");
                retObj.put("message", e.toString());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        return retObj;
    }

    @SuppressLint("Range")
    public JSONObject displayData() {
        JSONObject retObj = new JSONObject();
        Cursor c = getData("SELECT 'TOT_DOWN' column,count(*) count FROM " + ConfigMP.TABLE_ITEMS + " " +
                "union all SELECT 'TOT_UPLO' column,count(*) count FROM " + ConfigMP.TABLE_POS_TXN_MAS + " where " + ConfigMP.C_TXNDAT + "='2022-04-19' ");//
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    try {
                        retObj.put(c.getString(c.getColumnIndex(column)), c.getString(c.getColumnIndex(count)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        }
        return retObj;
    }

    @SuppressLint("Range")
    public JSONObject locConfig() {
        JSONObject retObj = new JSONObject();
        Cursor c = getData("SELECT PARCOD,COMENT FROM SMPROGRMPARA where PRGNAM = 'tb_settings'");
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    try {
                        retObj.put(c.getString(c.getColumnIndex(C_PARCOD)), c.getString(c.getColumnIndex(C_COMENT)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } while (c.moveToNext());
            }
        }
        return retObj;
    }

    public void resetData(){
        try{

            db = this.getWritableDatabase();
            db.delete(ConfigMP.TABLE_POS_TXN_MAS, null, null);
            db.close();

            db = this.getWritableDatabase();
            db.delete(ConfigMP.TABLE_POS_TXN_DET, null, null);
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Cursor getData(String query) {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "err->" + e.getMessage());
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }

    @SuppressLint("Range")
    public String[] getNewSerialNumber(String sbucod, String prm_tbcode, String serial_id) {
        int increment_by = 0;
        int minimum_number = 0;
        int maximum_number = 0;
        int current_number = 0;
        int result = 0;
        String responce[] = new String[2];

        try {
            db = this.getReadableDatabase();
            Cursor c = db.query(ConfigMP.TABLE_SEQUENCE, null,
                    ConfigMP.C_SBUCOD + "=? and " + ConfigMP.C_LOCCOD + "=? and " + ConfigMP.C_SEQ_ID + "=? ",
                    new String[]{sbucod, prm_tbcode, serial_id}, null, null, null);
            if (c.moveToFirst()) {
                increment_by = Integer.parseInt(c.getString(c.getColumnIndex(ConfigMP.C_INCRBY)));
                minimum_number = Integer.parseInt(c.getString(c.getColumnIndex(ConfigMP.C_MINVAL)));
                maximum_number = Integer.parseInt(c.getString(c.getColumnIndex(ConfigMP.C_MAXVAL)));
                current_number = Integer.parseInt(c.getString(c.getColumnIndex(ConfigMP.C_CURVAL)));
                current_number += increment_by;

                if (current_number < minimum_number) {
                    result = -2; // current number is lessthan minumum number;
                    responce[0] = "Error";
                    responce[1] = String.valueOf(result);

                } else if (current_number > maximum_number) {
                    result = -3; // current number is greater than the maximum number;
                    responce[0] = "Error";
                    responce[1] = String.valueOf(result);
                } else {
                    result = 1;
                }
            } else {
                result = -1; // squence object cannot be found
                responce[0] = "Error";
                responce[1] = String.valueOf(result);
            }

            if (result == 1) {
                db = this.getWritableDatabase();
                String getSQL1 = "Update " + ConfigMP.TABLE_SEQUENCE + " set "
                        + ConfigMP.C_CURVAL + " = '" + current_number + "' "
                        + " where "
                        + ConfigMP.C_SBUCOD + "='" + sbucod + "' and "
                        + ConfigMP.C_LOCCOD + "='" + prm_tbcode + "' and "
                        + ConfigMP.C_SEQ_ID + "='" + serial_id + "'";
                Cursor cursor = db.rawQuery(getSQL1, null);
                Log.d("getRecord()", getSQL1 + "##Count = " + cursor.getCount());
                db.close();

                responce[0] = "Success";
                responce[1] = String.valueOf(current_number);
            }
        } catch (Exception e) {
            Logger log = Logger.getLogger("");
            log.log(Level.WARNING, "App Error", e);
            result = -999;
            responce[0] = "Error";
            responce[1] = String.valueOf(result) + ". " + e.getLocalizedMessage();
        }
        return responce;
    }

    public void updateSequence(SharedPreferences sp, String prm_cur_val) {
        try {
            db = this.getReadableDatabase();
            db.delete(ConfigMP.TABLE_SEQUENCE, null, null);
            db.close();

            ContentValues values = new ContentValues();
            values.put(ConfigMP.C_SBUCOD, "830");
            values.put(ConfigMP.C_LOCCOD, sp.getString(ConfigMP.SP_LOCCOD, ""));
            values.put(ConfigMP.C_SEQ_ID, SQ_POSBILL);
            values.put(ConfigMP.C_INCRBY, "1");
            values.put(ConfigMP.C_MAXVAL, "999999999");
            values.put(ConfigMP.C_MINVAL, "1000");
            values.put(ConfigMP.C_CURVAL, prm_cur_val);

            db = this.getReadableDatabase();
            db.insert(ConfigMP.TABLE_SEQUENCE, null, values);
            db.close();
        } catch (Exception e) {

        }
    }

    public void BackupAsDB(ComHelper comHelper, boolean isReset) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File directory2 = null;
            String backupDBPath = "";

            if (sd.canWrite()) {
                File directory = new File(sd + "/BackupSF");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                if (isReset) {
                    directory2 = new File(sd + "/" + ConfigMP.LOCAL_SUB_BACKUP_RST_PATH);
                    if (!directory2.exists()) {
                        directory2.mkdirs();
                    }
                } else {
                    directory2 = new File(sd + "/" + ConfigMP.LOCAL_SUB_BACKUP_DB_PATH);
                    if (!directory2.exists()) {
                        directory2.mkdirs();
                    }
                }

                String currentDBPath = "//data//" + mContext.getApplicationContext().getPackageName() + "//databases//" + DATABASE;

                if (isReset) {
                    backupDBPath = ConfigMP.LOCAL_SUB_BACKUP_RST_PATH + "/" + DATABASE;
                } else {
                    backupDBPath = ConfigMP.LOCAL_SUB_BACKUP_DB_PATH + "/" + DATABASE;
                }

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } else {
                    comHelper.alert("Data Not Found");
                }
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public void Restore(ComHelper comHelper) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + mContext.getApplicationContext().getPackageName() + "//databases//" + DATABASE;
                String backupDBPath = ConfigMP.LOCAL_SUB_BACKUP_DB_PATH + "/" + DATABASE;

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    comHelper.alert("Data Restore Successfully");
                } else {
                    comHelper.alert("Data Not Found");
                }
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    public boolean renameFile(File from, File to) {
        return from.getParentFile().exists() && from.exists() && from.renameTo(to);
    }

    public String getTime(int intReqType) {
        SimpleDateFormat dateFormat;
        switch (intReqType) {
            case 1:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case 2:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                break;

            case 3:
                dateFormat = new SimpleDateFormat("HH:mm:ss");
                break;

            case 4:
                dateFormat = new SimpleDateFormat("yyyy");
                break;

            case 5:
                dateFormat = new SimpleDateFormat("MM");
                break;

            case 6:
                dateFormat = new SimpleDateFormat("dd");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + intReqType);
        }
        Date date = new Date();
        return dateFormat.format(date);
    }

}
