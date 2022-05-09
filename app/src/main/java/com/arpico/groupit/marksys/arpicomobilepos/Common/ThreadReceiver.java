package com.arpico.groupit.marksys.arpicomobilepos.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;
import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.DbHelper;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.DashboardFragment;
import com.arpico.groupit.marksys.arpicomobilepos.Fragments.UpdateItemFragment;
import com.arpico.groupit.marksys.arpicomobilepos.Models.AuthModel;
import com.arpico.groupit.marksys.arpicomobilepos.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadReceiver implements Runnable {
    private Socket socket;
    private ServerSocket serverSocket;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private String downData;
    private String host = "";
    private ComHelper comHelper;
    private DbHelper dbHelper;
    private Handler handler = new Handler();
    private DashboardFragment dashboardFragment;
    private UpdateItemFragment updateItemFragment;
    private SharedPreferences sp;
    private Context mContext;
    private DataDbFileSync dataDbFileSync;

    public ThreadReceiver(Context mContext, SharedPreferences shpref) {
        this.mContext = mContext;
        sp = shpref;
        host = sp.getString(ConfigMP.SP_HOST, "");
        comHelper = new ComHelper(mContext, sp);
        dbHelper = new DbHelper(mContext);
        dashboardFragment = new DashboardFragment();
        updateItemFragment = new UpdateItemFragment();

        dataDbFileSync = new DataDbFileSync(mContext, sp);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(ConfigMP.PORT);
            while (true) {
                socket = serverSocket.accept();
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                downData = bufferedReader.readLine();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JsonObject obj = (JsonObject) new JsonParser().parse(downData);

                        String result = obj.get("result").toString().replace("\"", "");
                        String type = obj.get("type").toString().replace("\"", "");

                        if (result.equalsIgnoreCase("success")) {
                            if (type.equalsIgnoreCase("Download")) {
                                String arrObject = obj.get("arrData").toString();

                                if (arrObject == null) {
                                    comHelper.alert("Data not found....!");
                                } else {
                                    AuthModel[] auth = new Gson().fromJson(arrObject, AuthModel[].class);
                                    if (auth.length > 0) {

                                        if (!((Activity) mContext).isFinishing()) {
                                            if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_download))) {
                                                dataDbFileSync.conformationDialogForFTP(1);

                                            } else if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_reset)) ||
                                                    sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_upload_reset))) {
                                                dataDbFileSync.conformationDialogForFTP(3);

                                            } else if (sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_find_item)) ||
                                                    sp.getString(ConfigMP.SP_CLICK, "").equalsIgnoreCase(mContext.getResources().getString(R.string.thread_dashboard_find_item))) {

                                                Intent intent = new Intent(mContext.getResources().getString(R.string.thread_dashboard_find_item));
//                                                intent.putExtra("message", mContext.getResources().getString(R.string.msg_successfully_updated));
                                                mContext.sendBroadcast(intent);
                                            }
                                        }
                                    } else {
                                        comHelper.alert("Authentication failed....!");
                                    }
                                }
                            }
                        } else {
                            comHelper.alert("Authentication failed....!");
                        }
                    }


                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}