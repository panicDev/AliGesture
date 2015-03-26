package com.way.util;

import java.util.ArrayList;
import java.util.List;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;

/**
 * This class uploads a gesture Exception Report to oops.gesture.org or sends it
 * as a TEXT intent, if IS_SILENT is true
 */
public class GestureExceptionReporter implements ReportSender {

    private static final String TAG = GestureExceptionReporter.class
            .getSimpleName();

    /**
     * Construct a new GestureExceptionReporter
     */
    public GestureExceptionReporter() {
    }

    /**
     * Pull information from the given {@link CrashReportData} and send it via
     * HTTP to oops.gesture or sends it as a TEXT intent, if IS_SILENT is true
     */
    @Override
    public void send(Context context, CrashReportData errorContent)
            throws ReportSenderException {
        StringBuilder body = new StringBuilder();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("Version", errorContent
                .getProperty(ReportField.APP_VERSION_NAME)));

        nameValuePairs.add(new BasicNameValuePair("BuildID", errorContent
                .getProperty(ReportField.BUILD)));
        nameValuePairs.add(new BasicNameValuePair("ProductName", "Gesture"));
        nameValuePairs.add(new BasicNameValuePair("Vendor", "Gesture"));
        nameValuePairs.add(new BasicNameValuePair("timestamp", errorContent
                .getProperty(ReportField.USER_CRASH_DATE)));

        for (NameValuePair pair : nameValuePairs) {
            body.append("--gesture\r\n");
            body.append("Content-Disposition: form-data; name=\"");
            body.append(pair.getName()).append("\"\r\n\r\n")
                    .append(pair.getValue()).append("\r\n");
        }

        body.append("--gesture\r\n");
        body.append(
                "Content-Disposition: form-data; name=\"upload_file_minidump\"; filename=\"")
                .append(errorContent.getProperty(ReportField.REPORT_ID))
                .append("\"\r\n");
        body.append("Content-Type: application/octet-stream\r\n\r\n");

        body.append("============== gesture Exception Report ==============\r\n\r\n");
        body.append("Report ID: ")
                .append(errorContent.getProperty(ReportField.REPORT_ID))
                .append("\r\n");
        body.append("App Start Date: ")
                .append(errorContent
                        .getProperty(ReportField.USER_APP_START_DATE))
                .append("\r\n");
        body.append("Crash Date: ")
                .append(errorContent.getProperty(ReportField.USER_CRASH_DATE))
                .append("\r\n\r\n");

        body.append("--------- Phone Details  ----------\r\n");
        body.append("Phone Model: ")
                .append(errorContent.getProperty(ReportField.PHONE_MODEL))
                .append("\r\n");
        body.append("Brand: ")
                .append(errorContent.getProperty(ReportField.BRAND))
                .append("\r\n");
        body.append("Product: ")
                .append(errorContent.getProperty(ReportField.PRODUCT))
                .append("\r\n");
        body.append("Display: ")
                .append(errorContent.getProperty(ReportField.DISPLAY))
                .append("\r\n");
        body.append("-----------------------------------\r\n\r\n");

        body.append("----------- Stack Trace -----------\r\n");
        body.append(errorContent.getProperty(ReportField.STACK_TRACE)).append(
                "\r\n");
        body.append("-----------------------------------\r\n\r\n");

        body.append("------- Operating System  ---------\r\n");
        body.append("App Version Name: ")
                .append(errorContent.getProperty(ReportField.APP_VERSION_NAME))
                .append("\r\n");
        body.append("Total Mem Size: ")
                .append(errorContent.getProperty(ReportField.TOTAL_MEM_SIZE))
                .append("\r\n");
        body.append("Available Mem Size: ")
                .append(errorContent
                        .getProperty(ReportField.AVAILABLE_MEM_SIZE))
                .append("\r\n");
        body.append("Dumpsys Meminfo: ")
                .append(errorContent.getProperty(ReportField.DUMPSYS_MEMINFO))
                .append("\r\n");
        body.append("-----------------------------------\r\n\r\n");

        body.append("-------------- Misc ---------------\r\n");
        body.append("Package Name: ")
                .append(errorContent.getProperty(ReportField.PACKAGE_NAME))
                .append("\r\n");
        body.append("File Path: ")
                .append(errorContent.getProperty(ReportField.FILE_PATH))
                .append("\r\n");

        body.append("Android Version: ")
                .append(errorContent.getProperty(ReportField.ANDROID_VERSION))
                .append("\r\n");
        body.append("Build: ")
                .append(errorContent.getProperty(ReportField.BUILD))
                .append("\r\n");
        body.append("Initial Configuration:  ")
                .append(errorContent
                        .getProperty(ReportField.INITIAL_CONFIGURATION))
                .append("\r\n");
        body.append("Crash Configuration: ")
                .append(errorContent
                        .getProperty(ReportField.CRASH_CONFIGURATION))
                .append("\r\n");
        body.append("Settings Secure: ")
                .append(errorContent.getProperty(ReportField.SETTINGS_SECURE))
                .append("\r\n");
        body.append("User Email: ")
                .append(errorContent.getProperty(ReportField.USER_EMAIL))
                .append("\r\n");
        body.append("User Comment: ")
                .append(errorContent.getProperty(ReportField.USER_COMMENT))
                .append("\r\n");
        body.append("-----------------------------------\r\n\r\n");

        body.append("---------------- Logs -------------\r\n");
        body.append("Logcat: ")
                .append(errorContent.getProperty(ReportField.LOGCAT))
                .append("\r\n\r\n");
        body.append("Events Log: ")
                .append(errorContent.getProperty(ReportField.EVENTSLOG))
                .append("\r\n\r\n");
        body.append("Radio Log: ")
                .append(errorContent.getProperty(ReportField.RADIOLOG))
                .append("\r\n");
        body.append("-----------------------------------\r\n\r\n");

        body.append("=======================================================\r\n\r\n");
        body.append("--gesture\r\n");
        body.append("Content-Disposition: form-data; name=\"upload_file_gesturelog\"; filename=\"Gesture.log\"\r\n");
        body.append("Content-Type: text/plain\r\n\r\n");
        body.append(errorContent.getProperty(ReportField.LOGCAT));
        body.append("\r\n--gesture--\r\n");

        //if ("true".equals(errorContent.getProperty(ReportField.IS_SILENT))) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        body.insert(0,
                "Please tell us why you're sending us this log:\n\n\n\n\n");
        intent.putExtra(Intent.EXTRA_TEXT, body.toString());
        intent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"way.ping.li@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Gesture Android Log");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//		} else {
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpPost httppost = new HttpPost(
//					"http://oops.gesture-player.org/addreport.php");
//			httppost.setHeader("Content-type",
//					"multipart/form-data; boundary=gesture");
//			try {
//				httppost.setEntity(new StringEntity(body.toString()));
//				httpclient.execute(httppost);
//			} catch (ClientProtocolException e) {
//				MyLog.e(TAG,
//						"send: " + e.getClass() + ": "
//								+ e.getLocalizedMessage());
//			} catch (IOException e) {
//				MyLog.e(TAG,
//						"send: " + e.getClass() + ": "
//								+ e.getLocalizedMessage());
//			}
//		}

    }
}
