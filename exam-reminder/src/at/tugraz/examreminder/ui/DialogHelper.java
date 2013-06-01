package at.tugraz.examreminder.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import at.tugraz.examreminder.R;

public class DialogHelper {

    public static void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
