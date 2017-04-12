package com.mp3cutter.ringtonemaker.Ringdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.TypedValue;

import com.mp3cutter.ringtonemaker.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by REYANSH on 4/8/2017.
 */

public class Utils {

    private static final String[] INTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\""
    };
    private static final String[] EXTERNAL_COLUMNS = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.IS_RINGTONE,
            MediaStore.Audio.Media.IS_ALARM,
            MediaStore.Audio.Media.IS_NOTIFICATION,
            MediaStore.Audio.Media.IS_MUSIC,
            MediaStore.Audio.Media.ALBUM_ID,
            "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\""
    };

    public static ArrayList<SongsModel> getSongList(Context context, boolean internal) {
        ArrayList<SongsModel> songsModels = new ArrayList<>();
        Uri CONTENT_URI;
        String[] COLUMNS;

        if (internal) {
            CONTENT_URI = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
            COLUMNS = INTERNAL_COLUMNS;
        } else {
            CONTENT_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            COLUMNS = EXTERNAL_COLUMNS;
        }
        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI,
                COLUMNS,
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String fileType;
                if (cursor.getString(6).equalsIgnoreCase("1")) {
                    fileType = Constants.IS_RINGTONE;
                } else if (cursor.getString(7).equalsIgnoreCase("1")) {
                    fileType = Constants.IS_ALARM;
                } else if (cursor.getString(8).equalsIgnoreCase("1")) {
                    fileType = Constants.IS_NOTIFICATION;
                } else {
                    fileType = Constants.IS_MUSIC;
                }

                SongsModel song = new SongsModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(10),
                        fileType);
                songsModels.add(song);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return songsModels;
    }

    public static Uri getAlbumArtUri(long paramInt) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }


    public static final String makeShortTimeString(final Context context, long secs) {
        long hours, mins;

        hours = secs / 3600;
        secs %= 3600;
        mins = secs / 60;
        secs %= 60;

        final String durationFormat = context.getResources().getString(
                hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
        return String.format(durationFormat, hours, mins, secs);
    }

    public static void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_art)
                .showImageForEmptyUri(R.drawable.default_art)
                .showImageOnFail(R.drawable.default_art)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(500))
                .handler(new Handler()).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new WeakMemoryCache()).defaultDisplayImageOptions(options).memoryCacheSizePercentage(13).build();
        ImageLoader.getInstance().init(config);
    }

    public static boolean checkSystemWritePermission(final Activity context) {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(context);
            if (retVal) {
            } else {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.set_ringtone)
                        .setMessage(context.getString(R.string.write_setting_text))
                        .setPositiveButton(R.string.alert_ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
        return retVal;
    }

    public static int getDimensionInPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(0, dp, context.getResources().getDisplayMetrics());
    }


}
