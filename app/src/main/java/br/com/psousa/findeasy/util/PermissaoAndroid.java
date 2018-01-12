package br.com.psousa.findeasy.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by Desenvolvimento on 08/05/2017.
 */

public class PermissaoAndroid extends ActivityCompat {

    public static final int REQUEST_PERMISSIONS_CODE = 128;

    public static String[] permissoes = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };


    public static boolean validate(Activity activity, int requestCode, Context context, String... permissions) {
        List list = new ArrayList();
        boolean retorno = false;
        for (String permission : permissions) {
            // Valida permissão
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
        }

        if (list.isEmpty()) {
            // Tudo ok, retorna true
            retorno = true;
        } else {

            for (String permission : permissions) {
                // Valida permissão
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    list.add(permission);
                }
            }

            if (list.isEmpty()) {
                String[] newPermissions = new String[list.size()];
                list.toArray(newPermissions);
                ActivityCompat.requestPermissions(activity, newPermissions, REQUEST_PERMISSIONS_CODE);

            } else {
                String[] newPermissions = new String[list.size()];
                list.toArray(newPermissions);
                callDialog("É preciso habilitar as permissões para utilizar as funções.", newPermissions, context, activity);
            }

        }

        return retorno;
    }


    public static void showMessage(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Atenção");
        //                    builder.setIcon(R.drawable.alert_octagon);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public static boolean checkSinglePermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static void callDialog(String message, final String[] permissions, Context context, final Activity activity) {
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setTitle("Permissão");
        mMaterialDialog.setMessage(message);
        mMaterialDialog.setPositiveButton("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS_CODE);
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();

            }
        });
        mMaterialDialog.show();
    }

}
