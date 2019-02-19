package info.firzen.cubemaster2.other;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.cube.CubeColor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Useful {
	public static void refreshBackendStickers() {
		Sticker.ONE.setColor(CubeColor.WHITE.getColor());
		Sticker.TWO.setColor(CubeColor.RED.getColor());
		Sticker.THREE.setColor(CubeColor.GREEN.getColor());
		Sticker.FOUR.setColor(CubeColor.BLUE.getColor());
		Sticker.FIVE.setColor(CubeColor.YELLOW.getColor());
		Sticker.SIX.setColor(CubeColor.ORANGE.getColor());
	}
	
	public static boolean isThereCamera(Context context) {
		PackageManager pm = context.getPackageManager();

		int count = Camera.getNumberOfCameras();
		return count > 0 && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
	
	public static String getStackTrace(Throwable e) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return result.toString();
	}
	
	public static void showToast(Bitmap bitmap, Context context) {
		Toast toast = new Toast(context);
	    ImageView view = new ImageView(context); 
	    view.setImageBitmap(bitmap);
	    toast.setView(view);
	    toast.setDuration(Toast.LENGTH_LONG);
	    toast.show();
	}

	public static void showToast(String text, Context context) {
	    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void showToast(String text, Context context, boolean isShort) {
		if(isShort) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
		else {
			showToast(text, context);
		}
	}
	
	public static void showInfo(String text, Context context) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.message));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		dlgAlert.create().show();
	}
	
	public static void showWarning(String text, Context context) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.warning));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		dlgAlert.create().show();
	}
	
	public static void showWarning(String text, Context context,
			final Action afterAction) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.warning));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(afterAction != null) {
					afterAction.run();
				}
			}
		});

		dlgAlert.create().show();
	}
	
	public static void showError(final String text, final Context context) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.error));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);

		dlgAlert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		dlgAlert.setNeutralButton(context.getString(R.string.error_report),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL,
						new String[] {context.getString(R.string.email)});
				intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report");
				intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.error_describe_behaviour)
						+ "\n\n\n" + text);
				context.startActivity(Intent.createChooser(intent, ""));
			}
		});

		dlgAlert.create().show();
	}
	
	public static void showError(String text, Context context, Throwable e) {
		showError(text + "\n" + getStackTrace(e), context);
	}
	
	public static void showQuestion(String text, Context context,
			final Action yesAction) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.question));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.yes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(yesAction != null) {
					yesAction.run();
				}
			}
		});

		dlgAlert.setNegativeButton(context.getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			
			}
		});

		dlgAlert.create().show();
	}
	
	public static void loadPreviousCube(String text, Context context,
			final Action revert, final Action repair) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.warning));
		dlgAlert.setMessage(text);
		dlgAlert.setCancelable(true);
		dlgAlert.setNegativeButton(context.getString(R.string.revert_changes),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(revert != null) {
					revert.run();
				}
			}
		});
		dlgAlert.setPositiveButton(context.getString(R.string.repair),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(repair != null) {
					repair.run();
				}
			}
		});

		dlgAlert.create().show();
	}
	
	public static void showError(String text, Context context, String exception) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo));
		dlgAlert.setTitle(context.getString(R.string.error));
		dlgAlert.setMessage(text + "\n\n" + exception);
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		dlgAlert.create().show();
	}

	public static void showCustomDialog(int layoutId, String title,
			Context context) {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(
				new ContextThemeWrapper(context, android.R.style.Theme_Holo_Light));
		dlgAlert.setTitle(title);
		
		LayoutInflater li = LayoutInflater.from(context);
		View v = li.inflate(layoutId, null);
		dlgAlert.setView(v);
		
		dlgAlert.setCancelable(true);
		dlgAlert.setPositiveButton(context.getString(R.string.confirm),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DataHolder.getInstance().setHelpShown(false);
			}
		});
		
		dlgAlert.setCancelable(false);
		dlgAlert.create().show();
	}
	
	public static ProgressDialog showProgressDialog(String title, String message,
			Context context) {
		final ProgressDialog dialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}
}
