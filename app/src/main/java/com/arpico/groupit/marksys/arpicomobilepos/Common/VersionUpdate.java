package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.zxing.client.android.BuildConfig;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class VersionUpdate {
    private String HOST = "192.168.1.28";
    private int PORT = 64000;
    private String USER = "mobile";
    private String PASS = "Mobile#456";
    private String FILE_DOWNLOAD_PATH = "/MobileAppRelease/MobilePos";
    private String FILE_SAVE_PATH = Environment.getExternalStorageDirectory() + "/BackupSF/Apps/";

    private Context mContext;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;

    private FTPUtility ftpUtility;
    private FTPClient ftpClient = null;
    private OutputStream outputStream = null;

    public VersionUpdate(Context mContext) {
        this.mContext = mContext;

//        ftpUtility = new FTPUtility(HOST, PORT, USER, PASS);
        ftpUtility = new FTPUtility();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Downloading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void getVersion(final SharedPreferences sp) {
        new Thread(new Runnable() {
            String updateSize = "0";

            @Override
            public void run() {
                try {
                    ftpClient = ftpUtility.connect(sp);

                    FTPFile[] files = ftpClient.listFiles(FILE_DOWNLOAD_PATH);
                    for (FTPFile file : files) {
                        updateSize = String.format("%.2f", ((double) file.getSize() / (1024 * 1024)));

                        String details = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.getTimestamp().getTime());
                        details += "\t\t" + updateSize;
                        details += "\t\t" + file.getName();

                        if (file.getName().contains(".apk")) {
                            final Double curVersion = Double.parseDouble(BuildConfig.VERSION_NAME);
                            final Double newVersion = Double.parseDouble(file.getName().split(".apk")[0]);
                            final String lastUpdDate = new SimpleDateFormat("yyyy-MM-dd").format(file.getTimestamp().getTime());

                            final String finalUpdateSize = updateSize;
                            final String finalDetails = details;

                            if (curVersion < newVersion) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                                        alertDialog.setIcon(R.drawable.ic_version);
                                        alertDialog.setTitle(Html.fromHtml("<font color='#00BFFF'>Version Update</font>"));
                                        alertDialog.setCancelable(false);
                                        alertDialog.setMessage(Html.fromHtml("<b>Current Version : </b>" + BuildConfig.VERSION_NAME + "<br>"
                                                + "<b>New Version : </b>" + newVersion + "<br>"
                                                + "<b>Updated Date : </b>" + lastUpdDate + "<br>"
                                                + "<b>Size : </b>" + finalUpdateSize + "<br><br>"
                                                + "<font color=''>Please Update Before Login.</font>"));
                                        alertDialog.setButton("Update", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressDialog.show();
                                                downloadVersion(finalDetails.split("\t\t"), sp);
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ftpClient != null) {
                        ftpUtility.disconnect();
                    }
                }
            }
        }).start();
    }

    private void downloadVersion(final String file[], final SharedPreferences sp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ftpClient = ftpUtility.connect(sp);

                    boolean success = false;
                    File directory = new File(FILE_SAVE_PATH);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    outputStream = new BufferedOutputStream(new FileOutputStream(new File(FILE_SAVE_PATH + file[2])));
                    success = ftpClient.retrieveFile(FILE_DOWNLOAD_PATH + "/" + file[2], outputStream);
                    outputStream.close();

                    if (success) {
                        progressDialog.dismiss();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String file_name = file[2];
                                File file = new File(FILE_SAVE_PATH + file_name);

                                if (file.exists()) {
                                    Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                                    Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
                                    promptInstall.setDataAndType(uri, "application/vnd.android.package-archive");
                                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    mContext.startActivity(promptInstall);

                                } else {
                                    Toast.makeText(mContext, "ّFile not found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Cannot Connect with FTP Server", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (ftpClient != null) {
                            ftpUtility.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}