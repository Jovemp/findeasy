package br.com.psousa.findeasy.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.com.psousa.findeasy.R;

/**
 * Created by Paulo on 07/03/2017.
 */

public class AndroidUtils {

    protected static final String TAG = "findeasy";
    public static final String URL_PADRAO = "http://192.168.1.28:3001/api";
    public static final String URL_USUARIO = URL_PADRAO+"/usuario";
    public static final String URL_USUARIOS = URL_PADRAO+"/usuarios";
    public static final String URL_CATEGORIA_LOCAL = URL_PADRAO + "/categoria/local";
    public static final String URL_LOCAL = URL_PADRAO + "/local";
    public static final String URL_USUARIO_RECUPERA_SENHA = URL_USUARIO + "/recupera_senha";
    public static final String URL_USUARIO_RECUPERA_SENHA_USUARIO = URL_USUARIO_RECUPERA_SENHA + "/usuario";
    public static final String URL_USUARIO_RECUPERA_SENHA_ALTERA_SENHA = URL_USUARIO_RECUPERA_SENHA + "/alterasenha";
    public static final String URL_TOKEN = URL_PADRAO+"/token";




    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                return false;
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            alertDialog(context, e.getClass().getSimpleName(), e.getMessage());
        }
        return false;
    }

    public static String numEmeiCel(Context context) {
        String IMEI = "";
        // <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        return IMEI;
    }

    public static String crypt(String acao, String src) {
        int KeyLen, KeyPos, OffSet, SrcPos, SrcAsc, TmpSrcAsc, Range;
        String Dest, Key;

        if (src.equals("")) {
            return "";
        }

        Key = "#2004$";
        Dest = "";
        KeyLen = Key.length();
        KeyPos = -1;
        SrcPos = -1;
        SrcAsc = -1;
        Range = 256;
        if (acao.equals("C")) {
            OffSet = 10;
            Dest = String.format("%1$02X", OffSet);
            for (SrcPos = 0; SrcPos < src.length(); SrcPos++) {
                SrcAsc = ((int) (src.charAt(SrcPos)) + OffSet) % 255;
                if (KeyPos < KeyLen - 1) {
                    KeyPos = KeyPos + 1;
                } else {
                    KeyPos = 0;
                }
                SrcAsc = SrcAsc ^ (int) (Key.charAt(KeyPos));
                Dest = Dest + String.format("%1$02X", SrcAsc);
                OffSet = SrcAsc;
            }
        } else if (acao.equals("D")) {

            OffSet = (int) Long.parseLong(src.substring(0, 2), 16);
            SrcPos = 2;
            do {
                String aux = src.substring(SrcPos, SrcPos + 2);
                SrcAsc = (int) Long.parseLong(aux, 16);
                if (KeyPos < KeyLen - 1) {
                    KeyPos = KeyPos + 1;
                } else {
                    KeyPos = 0;
                }
                TmpSrcAsc = SrcAsc ^ (int) (Key.charAt(KeyPos));
                if (TmpSrcAsc <= OffSet) {
                    TmpSrcAsc = 255 + TmpSrcAsc - OffSet;
                } else {
                    TmpSrcAsc = TmpSrcAsc - OffSet;
                }
                Dest = Dest + (char) (TmpSrcAsc);
                OffSet = SrcAsc;
                SrcPos = SrcPos + 2;
            } while (SrcPos < src.length());
        }
        return Dest;
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatar(String pTexto, int pTamanhoDesejado) {
        return formatar(pTexto, pTamanhoDesejado, true);
    }

    public static String formatar(String pTexto, int pTamanhoDesejado, boolean pAcrescentarADireita) {
        return formatar(pTexto, pTamanhoDesejado, pAcrescentarADireita, " ");
    }

    public static String formatar(String pTexto, int pTamanhoDesejado, boolean pAcrescentarADireita, String pCaracterAcrescentar) {
        /*
            OBJETIVO:
            Eliminar caracteres inválidos e acrescentar caracteres à esquerda ou à direita
            do texto original para que a string resultante fique com o tamanho desejado

            Texto:
            Texto original
            TamanhoDesejado:
            Tamanho que a string resultante deverá ter
            AcrescentarADireita:
            Indica se o carácter será acrescentado à direita ou à esquerda
            TRUE - Se o tamanho do texto for MENOR que o desejado, acrescentar carácter à direita
            Se o tamanho do texto for MAIOR que o desejado, eliminar últimos caracteres do texto
            FALSE - Se o tamanho do texto for MENOR que o desejado, acrescentar carácter à esquerda
            Se o tamanho do texto for MAIOR que o desejado, eliminar primeiros caracteres do texto
            CaracterAcrescentar:
            Carácter que deverá ser acrescentado
        */
        int quantidadeAcrescentar,
                tamanhoTexto,
                posicaoInicial,
                i;

        if (pCaracterAcrescentar.matches("[0-9]+") ||
                pCaracterAcrescentar.matches("[A-Z]+") ||
                pCaracterAcrescentar.matches("[a-z]+")) {
            // Não faz nada
        } else {
            pCaracterAcrescentar = " ";
        }

        pTexto = pTexto.toUpperCase().trim();
        tamanhoTexto = pTexto.length();
        pTexto = RemoveAcentos.semAcento(pTexto);

        quantidadeAcrescentar = pTamanhoDesejado - tamanhoTexto;
        if (quantidadeAcrescentar < 0) {
            quantidadeAcrescentar = 0;
        }
        if (pCaracterAcrescentar == "") {
            pCaracterAcrescentar = " ";
        }
        if (tamanhoTexto >= pTamanhoDesejado) {
            posicaoInicial = tamanhoTexto - pTamanhoDesejado + 1;
        }
        posicaoInicial = 0;

        if (pAcrescentarADireita) {
            for (i = 0; i < quantidadeAcrescentar; i++) {
                pTexto = pTexto + pCaracterAcrescentar;
            }
        } else {
            for (i = 0; i < quantidadeAcrescentar; i++) {
                pTexto = pCaracterAcrescentar + pTexto;
            }
        }

        return pTexto.toUpperCase();
    }

    public static void alertDialog(final Context context, final int title, final int mensagem) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(mensagem).create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void alertDialog(final Context context, final int title, final int mensagem, int icon) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle(title).setMessage(mensagem).setIcon(icon).create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void alertDialog(final Context context, final String title, final String mensagem) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle(
                    title).setMessage(mensagem)
                    .create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public interface OnClickAlertDialod{
        public void onOkClick();
    }

    public static void alertDialog(final Context context, final String title, final String mensagem, final OnClickAlertDialod onclick) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle(
                    title).setMessage(mensagem)
                    .create();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    onclick.onOkClick();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    // Fecha o teclado virtual se aberto (view com foque)
    public static boolean closeVirtualKeyboard(Context context, View view) {
        // Fecha o teclado virtual
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            boolean b = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return b;
        }
        return false;
    }

    public static void closeVirtualKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * Converte de DIP para Pixels
     */
    public static float toPixels(Context context, float dip) {
        Resources r = context.getResources();
        //int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());

        float scale = r.getDisplayMetrics().density;
        int px = (int) (dip * scale + 0.5f);

        return px;
    }

    /**
     * Converte de Pixels para DIP
     */
    public static float toDip(Context context, float pixels) {
        Resources r = context.getResources();

        float scale = r.getDisplayMetrics().density;

        int dip = (int) (pixels / scale + 0.5f);
        return dip;
    }

    /**
     * Para fazer android:foreground="?android:attr/selectableItemBackground"
     *
     * @param context getActionBar().getThemedContext()
     * @param attrId  android.R.attr.selectableItemBackground
     */
    public Drawable getDrawableAttr(Context context, int attrId) {
        int[] attrs = new int[]{attrId};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawable = ta.getDrawable(0 /* index */);
        ta.recycle();
        return drawable;
    }

    public static boolean isAndroid3Honeycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isAndroid4ICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isAndroid5Lollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    // Retorna se a tela é large ou xlarge
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    // Retona se é um tablet com Android 3.x
    public static boolean isAndroid_3_Tablet(Context context) {
        return isAndroid3Honeycomb() && isTablet(context);
    }

    /* Lê a versão do app */
    public static String getVersionName(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        String packageName = activity.getPackageName();
        String versionName;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "N/A";
        }
        return versionName;
    }

    public static void toastPerson(Context context, String mensagem, int gravity, int xOffset, int yOffset, int duracao) {
        Toast toast = Toast.makeText(context, mensagem, duracao);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    public static void Vibrar(int tempoMs, Context context) {
        Vibrator rr = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long milliseconds = tempoMs;
        rr.vibrate(milliseconds);
    }

    public static void telefonar(Context context, String telefone) {
        Uri u = Uri.parse("tel:" + (telefone.replace("-", "").replaceAll("[()]", "").replaceAll(" ", "")));
        Intent i = new Intent(Intent.ACTION_DIAL, u);
        context.startActivity(i);
    }


    public static void email(Context context, String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + email));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            alertDialog(context, "Erro", "Nenhum gerenciador de e-mail instalado!");
        }
    }

    public static String removeSimboloDinheiro(String texto) {
        String textoResp = texto;
        textoResp = textoResp.replaceAll("R|\\$| ", "");
        return textoResp;
    }

    // CAPTURANDO NÚMERO DE SÉRIE DO TABLET
    public static String numSerie(Context context) {
        return Build.SERIAL;
    }


    // VERIFICA SE GPS ESTÁ ATIVADO
    public static boolean gpsIsAtivo(Context context, String mensagem){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            final AlertDialog.Builder dialogAtivarGPS = new AlertDialog.Builder(context);
            dialogAtivarGPS.setTitle("Ativar GPS");
            dialogAtivarGPS.setIcon(R.mipmap.ic_gps);
            dialogAtivarGPS.setCancelable(false);
            dialogAtivarGPS.setMessage(mensagem+"\nDeseja ativar-lo agora?");
            dialogAtivarGPS.setPositiveButton(context.getString(R.string.btn_sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogAtivarGPS.getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    AndroidUtils.toastPerson(dialogAtivarGPS.getContext(), "Ative o GPS, tente executar o recurso novamente!", Gravity.BOTTOM | Gravity.CENTER, 0, 0, Toast.LENGTH_LONG);
                }
            });
            dialogAtivarGPS.setNegativeButton(context.getString(R.string.btn_nao), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialogAtivarGPS.show();
        }
        return isGPSEnabled;
    }

}
