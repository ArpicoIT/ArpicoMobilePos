package com.arpico.groupit.marksys.arpicomobilepos.Common;


import android.content.SharedPreferences;
import android.util.Log;

import com.arpico.groupit.marksys.arpicomobilepos.DatabaseHandler.ConfigMP;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FTPUtility {
    String TAG = "FTPUtility";

    // FTP server information
    private String host;
    private int port;
    private String username;
    private String password;

    private FTPClient ftpClient = null;
    private int replyCode;

    public FTPUtility(){}

//    public FTPUtility(String host, int port, String user, String pass) {
//        this.host = host;
//        this.port = port;
//        this.username = user;
//        this.password = pass;
//    }

    /**
     * Connect and login to the server.
     * <p>
     * //     * @throws FTPException
     *
     * @return
     */
    public FTPClient connect(SharedPreferences sp) {
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(sp.getString(ConfigMP.SP_FTP_IP,""), Integer.parseInt(sp.getString(ConfigMP.SP_FTP_PORT,"")));
            replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                Log.e(TAG, "FTP serve refused connection.");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            boolean logged = ftpClient.login(sp.getString(ConfigMP.SP_FTP_USER,""), sp.getString(ConfigMP.SP_FTP_PASS,""));
            if (!logged) {
                // failed to login
                ftpClient.disconnect();
                Log.e(TAG, "Could not login to the server.");
            }



        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "I/O error: " + ex.getMessage());
        }
        return ftpClient;
    }

    /**
     * Gets size (in bytes) of the file on the server.
     *
     * @param filePath Path of the file on server
     *                 //     * @return file size in bytes
     *                 //     * @throws FTPException
     */
    public long getFileSize(String filePath) {
        FTPFile file = null;
        try {
            file = ftpClient.mlistFile(filePath);
            if (file == null) {
                Log.e(TAG, "The file may not exist on the server!");
            }
        } catch (IOException ex) {
            Log.e(TAG, "Could not determine size of the file: " + ex.getMessage());
        }
        return file.getSize();
    }

    /**
     * Complete the download operation.
     */
    public void finish() throws IOException {
        ftpClient.completePendingCommand();
    }

    /**
     * Log out and disconnect from the server
     *
     * @return
     */
    public FTPClient disconnect() {
        if (ftpClient.isConnected()) {
            try {
                if (!ftpClient.logout()) {
                    Log.e(TAG, "Could not log out from the server");
                }
                ftpClient.disconnect();
            } catch (IOException ex) {
                Log.e(TAG, "Error disconnect from the server: " + ex.getMessage());
            }
        }
        return ftpClient;
    }
}