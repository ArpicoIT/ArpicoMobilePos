package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Models.ItemsModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataDbFileSync {
    private String HOST = "";
    private int PORT = 0;
    private String USER = "";
    private String PASS = "";
    private String FTP_FILE_PATH = "";
    private String LOCAL_INPUT_FILE_PATH = "", LOCAL_OUTPUT_FILE_PATH = "";
    private String LOCAL_ROOT_PATH = "/BackupSF/";
    private String LOCAL_SUB_DB_PATH = "/BackupDB/";
    private String LOCAL_SUB_FILE_PATH = "/BackupFILE/";
    private String LOCAL_SUB_RST_FILE_PATH = "/BackupRST/";
    private String SUPER_CENTER_PATH = "/home/superc/MobilePos";

    private Context mContext;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;

    private FTPUtility ftpUtility;
    private FTPClient ftpClient = null;
    private OutputStream outputStream = null;
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private SharedPreferences sp;
    private ArrayList<String> fileList = null;

    public DataDbFileSync(Context mContext, SharedPreferences sp) {
        this.mContext = mContext;
        this.sp = sp;

        comHelper = new ComHelper(mContext, sp);
        dbHelper = new DbHelper(mContext);
        try {
            if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                HOST = sp.getString(ConfigMP.SP_FTP_IP, "");
                PORT = Integer.parseInt(sp.getString(ConfigMP.SP_FTP_PORT, ""));
                USER = sp.getString(ConfigMP.SP_FTP_USER, "");
                PASS = sp.getString(ConfigMP.SP_FTP_PASS, "");
            } else {
                HOST = "192.168.1.28";
                PORT = 64000;
                USER = "mobile";
                PASS = "Mobile#456";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        comHelper.addSP(ConfigMP.SP_FTP_IP, HOST, true);
        comHelper.addSP(ConfigMP.SP_FTP_PORT, String.valueOf(PORT), true);
        comHelper.addSP(ConfigMP.SP_FTP_USER, USER, true);
        comHelper.addSP(ConfigMP.SP_FTP_PASS, PASS, true);

//        ftpUtility = new FTPUtility(HOST, PORT, USER, PASS);

        ftpUtility = new FTPUtility();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Downloading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void viewUploadedFiles(final String strDay, final boolean isAllFiles, final boolean isBackup) {
        new Thread(new Runnable() {
            String details = "", strDate = "";
            int rownum = 1;

            @Override
            public void run() {
                try {
                    ftpClient = ftpUtility.connect(sp);

                    fileList = new ArrayList<>();

                    String day[] = strDay.split("-");
                    String folderName = day[2] + day[1] + day[0];

                    String FTPFilePath = "";
                    if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                        FTPFilePath = SUPER_CENTER_PATH + "/Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + (isBackup ? "/BACKUP/" : "/MOB/") + folderName;
                    } else {
                        FTPFilePath = "Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + (isBackup ? "/BACKUP/" : "/MOB/") + folderName;
                    }

                    FTPFile[] files = ftpClient.listFiles(FTPFilePath);

                    for (FTPFile file : files) {
                        if (!isAllFiles) {
                            String selectFile[] = file.getName().split("_");
                            if (selectFile[1].equals(sp.getString(ConfigMP.SP_USERID, ""))) {
                                details = rownum + " --> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getTimestamp().getTime()) + " - "
                                        + String.format("%.2f", ((double) file.getSize() / (1024 * 1024))) + "\n" + file.getName();

                                fileList.add(details);
                                rownum++;
                            }
                        } else {
                            details = rownum + " --> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getTimestamp().getTime()) + " - "
                                    + String.format("%.2f", ((double) file.getSize() / (1024 * 1024))) + "\n" + file.getName();

                            fileList.add(details);
                            rownum++;
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext.getResources().getString(R.string.broadcast_view_upload_files));
                            intent.putExtra("list", fileList.toString());
                            mContext.sendBroadcast(intent);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public void conformationDialogForFTP(final int intType) {
        if (sp.getString(ConfigMP.SP_LOCCOD, "").trim().isEmpty()) {
            comHelper.alert("Please Enter Loc Code");

        } else if (sp.getString(ConfigMP.SP_INPFIL, "").trim().isEmpty()) {
            comHelper.alert("Please Enter Input File Type");

        } else if (sp.getString(ConfigMP.SP_OUTFIL, "").trim().isEmpty()) {
            comHelper.alert("Please Enter Output File Type");

        } else if (sp.getString(ConfigMP.SP_USERID, "").trim().isEmpty()) {
            comHelper.alert("Please Enter User ID");

        } else {
            if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                FTP_FILE_PATH = SUPER_CENTER_PATH + "/Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + "/SYS/";
            } else {
                FTP_FILE_PATH = "/Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + "/SYS/";
            }
            LOCAL_INPUT_FILE_PATH = Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + (sp.getString(ConfigMP.SP_INPFIL, "").equals(".DB") ? LOCAL_SUB_DB_PATH : LOCAL_SUB_FILE_PATH);
//            FTP_FILE_PATH = Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + (sp.getString(ConfigCC.SP_OUTFIL, "").equals(".DB") ? LOCAL_SUB_DB_PATH : LOCAL_SUB_FILE_PATH);

            String title = "", message = "";
            int icon = 0;
            switch (intType) {
                case 1:
                    title = "Download";
                    message = "Item Downloading";
                    icon = R.drawable.ic_save;
                    break;
                case 2:
                    title = "Upload";
                    message = "Item Uploading";
                    icon = R.drawable.ic_upload;
                    break;
                case 3:
                    title = "Reset";
                    message = "Reset Database";
                    icon = R.drawable.ic_reset;
                    break;
                case 4:
                    title = "Backup";
                    message = "Getting Backup";
                    icon = R.drawable.ic_backup_files;
                    break;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Do you want to " + title + " ? ");
            builder.setMessage("\n" + message + "");
            builder.setIcon(icon);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    switch (intType) {
                        case 1://Download
                            new validateMasterFile().execute();
                            break;
                        case 2://Upload
                            new SyncData().execute(false);
                            break;
                        case 3://Reset
                            new SyncData().execute(true);
                            break;
                        case 4://Backup
                            new BackupData().execute();
                            break;
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.RED);
            alert.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.GRAY);
        }
    }

    private class validateMasterFile extends AsyncTask<String, Void, Void> {
        String fileName = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Downloading Master Data.\nPlease wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... voids) {
            try {
                ftpClient = ftpUtility.connect(sp);

                FTPFile[] files = ftpClient.listFiles(FTP_FILE_PATH);
                for (FTPFile file : files) {
                    if (file.getName().equalsIgnoreCase("MobilePos.db")) {
                        fileName = file.getName();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (fileName.equals("")) {
                progressDialog.dismiss();
                comHelper.alert("File Not Found");
            } else {
                downloadItems(fileName, ftpClient);
            }
        }
    }

    private void downloadItems(final String fileName, final FTPClient ftpClient) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = false;

                    createLOCALFolder(LOCAL_ROOT_PATH);
                    createLOCALFolder(LOCAL_ROOT_PATH + LOCAL_SUB_DB_PATH);
                    createLOCALFolder(LOCAL_ROOT_PATH + LOCAL_SUB_RST_FILE_PATH);

                    outputStream = new BufferedOutputStream(new FileOutputStream(new File(LOCAL_INPUT_FILE_PATH + fileName)));
                    ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    success = ftpClient.retrieveFile(FTP_FILE_PATH + "/" + fileName, outputStream);
                    outputStream.close();

                    if (success) {
                        progressDialog.dismiss();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "ّData Download Successfully", Toast.LENGTH_SHORT).show();
                                String file_name = fileName;
                                File file = new File(LOCAL_INPUT_FILE_PATH + file_name);

                                if (file.exists()) {
                                    dbHelper.Restore(comHelper);

                                    Intent intent = new Intent(mContext.getResources().getString(R.string.broadcast_dashboard));
                                    mContext.sendBroadcast(intent);

                                } else {
                                    Toast.makeText(mContext, "Data not found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mContext, "Cannot Connect with FTP Server", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                } finally {
                    progressDialog.dismiss();
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (ftpClient.isConnected()) {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private class SyncData extends AsyncTask<Boolean, Void, Void> {
        int count = 1;
        boolean isReset;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Updating Data.\nPlease wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Boolean... voids) {
            isReset = voids[0];
            File file = null;

            if (sp.getString(ConfigMP.SP_OUTFIL, "").equalsIgnoreCase(".DAT")) {
                String textFile = "";
                try {
                    List<ItemsModel> findItemList = new Gson().fromJson(dbHelper.getCountItems("", " WHERE " + ConfigMP.C_ICOUNT + " > '0'").toString(), new TypeToken<ArrayList<ItemsModel>>() {
                    }.getType());
                    if (findItemList.size() > 0) {
                        for (ItemsModel selectItem : findItemList) {
                            if (textFile.equals("")) {
                                textFile += sp.getString(ConfigMP.SP_TBCODE, "") + "," + selectItem.getPLUCOD().trim() + "," + selectItem.getUNIPRI().trim() + "," + selectItem.getICOUNT().trim() + "";
                            } else {
                                textFile += "\n" + sp.getString(ConfigMP.SP_TBCODE, "") + "," + selectItem.getPLUCOD().trim() + "," + selectItem.getUNIPRI().trim() + "," + selectItem.getICOUNT().trim() + "";
                            }
                        }
                    }
                    createLOCALFolder(LOCAL_ROOT_PATH);
                    createLOCALFolder(LOCAL_ROOT_PATH + LOCAL_SUB_FILE_PATH);
                    createLOCALFolder(LOCAL_ROOT_PATH + LOCAL_SUB_RST_FILE_PATH);

                    if (isReset) {
                        String RESET_OUTPUT_FILE_PATH = Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + LOCAL_SUB_RST_FILE_PATH;
                        file = new File(RESET_OUTPUT_FILE_PATH, "MobilePos.dat");
                    } else {
                        LOCAL_OUTPUT_FILE_PATH = Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + LOCAL_SUB_FILE_PATH;
                        file = new File(LOCAL_OUTPUT_FILE_PATH, "MobilePos.dat");
                    }

                    FileWriter writer = new FileWriter(file);
                    writer.append(textFile);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (sp.getString(ConfigMP.SP_OUTFIL, "").equalsIgnoreCase(".DB")) {
                dbHelper.BackupAsDB(comHelper, isReset);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            comHelper.alert("File Created.\nCount: " + count);
            uploadFTP(isReset ? "Reset" : "Upload");
        }
    }

    private class BackupData extends AsyncTask<Boolean, Void, Void> {
        int count = 1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Updating Data.\nPlease wait...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Boolean... voids) {
            dbHelper.BackupAsDB(comHelper, false);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            comHelper.alert("File Created.\nCount: " + count);
            uploadFTP("Backup");
        }
    }

    private void uploadFTP(final String exeType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String FTPFilePath = "";
                File localFile = null;
                try {
                    ftpClient = ftpUtility.connect(sp);

                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                    String file_name = "MobilePos" + sp.getString(ConfigMP.SP_OUTFIL, "").toLowerCase();
                    String zip_file_name = "";

                    if (exeType.equalsIgnoreCase("Reset")) {//Reset Data
                        String RESET_OUTPUT_FILE_PATH = Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + LOCAL_SUB_RST_FILE_PATH;
                        localFile = new File((exeType.equalsIgnoreCase("Reset") ? RESET_OUTPUT_FILE_PATH : LOCAL_OUTPUT_FILE_PATH) + file_name);

                        if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                            FTPFilePath = SUPER_CENTER_PATH + "/Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + "/BACKUP/";
                        } else {
                            FTPFilePath = "Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + "/BACKUP/";
                        }

                    } else {//Upload Data
                        if (exeType.equalsIgnoreCase("Backup")) {
                            String backupDBPath = Environment.getExternalStorageDirectory().getPath() + LOCAL_ROOT_PATH + LOCAL_SUB_DB_PATH;
                            final File backupDBFolder = new File(backupDBPath);
                            backupDBFolder.mkdirs();
                            final File backupDB = new File(backupDBFolder, "/MobilePos.db");
                            String[] s = new String[1];
                            s[0] = backupDB.getAbsolutePath();

                            zip_file_name = dbHelper.getTime(3).replace(":", "") + ".zip";

                            ZipManager.zip(s, backupDBPath + "/" + zip_file_name);

                            localFile = new File(backupDBPath + zip_file_name);

                        } else {
                            localFile = new File(LOCAL_OUTPUT_FILE_PATH + file_name);
                        }

                        if (sp.getString(ConfigMP.SP_FTP, "").equalsIgnoreCase("N")) {
                            FTPFilePath = SUPER_CENTER_PATH + "/Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + (exeType.equalsIgnoreCase("Backup") ? "/BACKUP/" : "/MOB/");
                        } else {
                            FTPFilePath = "Files/MobilePos/" + sp.getString(ConfigMP.SP_LOCCOD, "") + (exeType.equalsIgnoreCase("Backup") ? "/BACKUP/" : "/MOB/");
                        }
                    }
                    createFTPFolder(FTPFilePath);

                    FTPFilePath += new SimpleDateFormat("ddMMyyyy").format(new Date()) + "/";
                    createFTPFolder(FTPFilePath);

                    if (exeType.equalsIgnoreCase("Reset")) {//Reset Data
                        file_name = sp.getString(ConfigMP.SP_TBCODE, "") + "_" + sp.getString(ConfigMP.SP_USERID, "") + "_"
                                + sp.getString(ConfigMP.SP_AUTHID, "") + "_" + dbHelper.getTime(3).replace(":", "") + "_MobilePos"
                                + sp.getString(ConfigMP.SP_OUTFIL, "").toLowerCase();

                    } else {//Upload Data
                        if (exeType.equalsIgnoreCase("Backup")) {
                            file_name = sp.getString(ConfigMP.SP_TBCODE, "") + "_" + sp.getString(ConfigMP.SP_USERID, "") + "_" + zip_file_name;

                        } else {
                            File currentFile = new File(Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + LOCAL_SUB_DB_PATH + file_name);
                            File newFile = new File(Environment.getExternalStorageDirectory() + LOCAL_ROOT_PATH + LOCAL_SUB_DB_PATH + sp.getString(ConfigMP.SP_TBCODE, "") + "_" + sp.getString(ConfigMP.SP_USERID, "") + "_MobilePos"
                                    + sp.getString(ConfigMP.SP_OUTFIL, "").toLowerCase());

                            if (dbHelper.renameFile(currentFile, newFile)) {
                                file_name = sp.getString(ConfigMP.SP_TBCODE, "") + "_" + sp.getString(ConfigMP.SP_USERID, "") + "_MobilePos"
                                        + sp.getString(ConfigMP.SP_OUTFIL, "").toLowerCase();

                                localFile = newFile;
                            } else {
                                Log.i("SSSS", "Fail");
                            }
                        }

                    }
                    String remoteFile = FTPFilePath + file_name;

                    InputStream inputStream = new FileInputStream(localFile);
                    System.out.println("Start uploading file");

                    boolean stored = ftpClient.storeFile(remoteFile, inputStream);
                    if (stored) {
                        progressDialog.dismiss();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "ّData " + exeType + " Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = null;

                                if (exeType.equalsIgnoreCase("Reset")) {
                                    if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase("dashboardReset")) {
                                        intent = new Intent(mContext.getResources().getString(R.string.broadcast_dashboard));
                                        mContext.sendBroadcast(intent);
                                    } else if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase("uploadReset")) {
                                        intent = new Intent(mContext.getResources().getString(R.string.broadcast_update_item));
                                        mContext.sendBroadcast(intent);
                                    }
                                }
                            }
                        });
                    }
                    inputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void createFTPFolder(String prm_path) {
        try {
            boolean boolFolder = ftpClient.makeDirectory(prm_path);
            if (boolFolder) {
                System.out.println("The " + prm_path + " directory is created");
            } else {
                System.out.println("Something unexpected happened from " + prm_path + " directory is create...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLOCALFolder(String prm_path) {
        File sd = Environment.getExternalStorageDirectory();
        if (sd.canWrite()) {
            File directory = new File(sd + prm_path);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("The " + prm_path + " directory is created");
            } else {
                System.out.println("Something unexpected happened from " + prm_path + " directory is create...");
            }
        } else {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
                    comHelper.alert("Permission Denied...");
                }
            });
        }
    }
}