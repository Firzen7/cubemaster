// Copied from https://trivedihardik.wordpress.com/2011/08/20/how-to-avoid-force-close-error-in-android/

package info.firzen.cubemaster2.other;

import info.firzen.cubemaster2.activities.MainActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

public class ErrorsCatcher implements java.lang.Thread.UncaughtExceptionHandler {
	private final Activity myContext;
	private final String LINE_SEPARATOR = "\n";

	public ErrorsCatcher(Activity context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

		// TMP
		System.err.println(errorReport.toString());

		Intent intent = new Intent(myContext, MainActivity.class);
		intent.putExtra("error", errorReport.toString());
		myContext.startActivity(intent);

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

}