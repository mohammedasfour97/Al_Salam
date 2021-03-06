package com.alsalamegypt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import dagger.Module;
import dagger.Provides;

import com.alsalamegypt.Models.IDName;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

@Module
public class Utils {

    private Application application;

    public Utils(Application application) {
        this.application = application;
    }

    @Provides
    public Utils utilities(){

        return this;
    }
    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the empty string if the
     * input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == 0) {
            return str;
        }
        return str.substring(pos + separator.length());
    }


    public static void displayImageOriginal(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
        } catch (Exception e) {
        }
    }


    public static void setPref(Context context,String key,String value) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("app", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }


    // Fetches reg id from shared preferences
    // and displays on the scree
    public static String getPref(Context context,String key) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("app", 0);
        String result = pref.getString(key, "");

        if (result == null) {
            return result = "";
        }else{
            return result ;
        }
    }


    public static Typeface gettypeface(Context context){
    return ResourcesCompat.getFont(context, R.font.cairo_regular);
    }


    public static void setDefaultFont(TextView textView, Context context){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.cairo_regular);
        textView.setTypeface(typeface);
        textView.setTextSize(14f);
    }


    public static  void openSettings(Context context) {
        Toast.makeText(context, "Please open location Permission", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void goToNotificationSettings(String channel, Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (channel != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channel != null) {
                intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
            } else {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        context.startActivity(intent);
    }


    public String convertNullToEmpty(String s){

        String value = s;
        if (value == null)
            value = "";

        return value;
    }


    public static String chooseNonNull(String first, String second){

        if (first!=null)
            return first;
        else if (second!=null)
            return second;
        else return "";
    }


    public String chooseNonEmpty(String first, String second){

        if (first!=null)
            return first;
        else if (second!=null)
            return second;
        else return "";
    }


    public static String generateUniqueNumber(){

        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
    }


    public static String formatDate(String format, String date ){

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateFormat outputFormat = new SimpleDateFormat(format);
        try {
            return  outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {

            return "";
        }
    }


    public static String getCurrentDateTime(String format){

        DateFormat outputFormat = new SimpleDateFormat(format);
        try {
            return  outputFormat.format(Calendar.getInstance().getTime());
        } catch (Exception e) {

            return "";
        }
    }


    public static long getSubstractedDateTime(String firstDT, String secondDT, String format){

        DateFormat df = new SimpleDateFormat(format);
        Date date1 = null, date2 = null;
        try {
            date1 = df.parse(firstDT);
            date2 = df.parse(secondDT);
        } catch (ParseException e) {
            e.printStackTrace();
        }

            return date2.getTime() - date1.getTime();
        }



    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Drawable drwNewCopy = drawable.getConstantState().newDrawable().mutate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drwNewCopy = (DrawableCompat.wrap(drwNewCopy)).mutate();
        }


        Bitmap bitmap = Bitmap.createBitmap(drwNewCopy.getIntrinsicWidth(),
                drwNewCopy.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drwNewCopy.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drwNewCopy.draw(canvas);

        return bitmap;
    }


    public static Bitmap getBitmapFromVectorDrawable(Context context, Drawable drawable) {
        Drawable drwNewCopy = drawable.getConstantState().newDrawable().mutate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drwNewCopy = (DrawableCompat.wrap(drwNewCopy)).mutate();
        }


        Bitmap bitmap = Bitmap.createBitmap(drwNewCopy.getIntrinsicWidth(),
                drwNewCopy.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drwNewCopy.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drwNewCopy.draw(canvas);

        return bitmap;
    }




    public static class Hsl {
        public float h, s, l;

        public Hsl(float h, float s, float l) {
            this.h = h;
            this.s = s;
            this.l = l;
        }
    }


    public static Hsl rgbToHsl(float r, float g, float b) {
        r /= 255d; g /= 255d; b /= 255d;

        float max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b);
        float h, s, l = (max + min) / 2;

        if (max == min) {
            h = s = 0; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4; // if (max == b)

            h /= 6;
        }

        return new Hsl(h, s, l);
    }

    public static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color)
    {
        Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(),true);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }


    public static String getStringRandomCode(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
        return dateFormat.format(new Date());
    }

    public static int getIntRandomCode(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("mmssSSS", Locale.US);
        return Integer.parseInt(dateFormat.format(new Date()));
    }


    public static String getStringKeyValue(Context context, int key, String value){

        return context.getResources().getString(key) + " " + ":" + " " + value;
    }


    public static void hideIfEmpty(List<View> viewList, List<String> values){

        for (int a=0 ; a<viewList.size() ; a++){

            if (TextUtils.isEmpty(values.get(a)))
                viewList.get(a).setVisibility(View.GONE);
        }
    }


    public static IDName getSelectedIDNameItemInSpinner(List<IDName> list, String name){

        IDName returnedIDName = null;

        for (IDName idName : list){

            if (idName.getName().equals(name)){

                returnedIDName = idName;
                break;
            }

        }

        return returnedIDName;
    }


    public static void setAllEmpty(List<EditText> editTextList){

        for (EditText editText : editTextList)
            editText.setText("");
    }


    public static void setEnableOrNot(List<View> viewList, Boolean enable){

        for (View view : viewList)
            view.setEnabled(enable);
    }


    public static void animateViewVisibility(final View view, final int visibility)
    {
        // cancel runnning animations and remove and listeners
        view.animate().cancel();
        view.animate().setListener(null);

        // animate making view visible
        if (visibility == View.VISIBLE)
        {
            view.animate().alpha(1f).setDuration(2000).start();
            view.setVisibility(View.VISIBLE);
        }
        // animate making view hidden (HIDDEN or INVISIBLE)
        else
        {
            view.animate().setDuration(1000).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    view.setVisibility(visibility);
                }
            }).alpha(0f).start();
        }
    }

    public void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = this.application.getResources();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    @SuppressLint("Range")
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static double getImageSizeFromUriInMegaByte(Uri filePath, Context context) {
        String scheme = filePath.getScheme();
        double dataSize = 0;
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream = context.getContentResolver().openInputStream(filePath);
                if (fileInputStream != null) {
                    dataSize = fileInputStream.available();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            String path = filePath.getPath();
            File file = null;
            try {
                file = new File(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (file != null) {
                dataSize = file.length();
            }
        }
        return dataSize / (1024 * 1024);
    }


}
