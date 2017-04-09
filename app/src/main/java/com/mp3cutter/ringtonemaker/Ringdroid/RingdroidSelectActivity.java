/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mp3cutter.ringtonemaker.Ringdroid;

/**
 * Main screen that shows up when you launch Ringdroid. Handles selecting
 * an audio file or using an intent to record a new one, and then
 * launches RingdroidEditActivity from here.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mp3cutter.ringtonemaker.R;
import com.mp3cutter.ringtonemaker.Ringdroid.soundfile.SoundFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main screen that shows up when you launch Ringdroid. Handles selecting
 * an audio file or using an intent to record a new one, and then
 * launches RingdroidEditActivity from here.
 */
public class RingdroidSelectActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private SimpleCursorAdapter mAdapter;
    private boolean mWasGetContentIntent;


    // Result codes
    private static final int REQUEST_CODE_EDIT = 1;
    private static final int REQUEST_CODE_CHOOSE_CONTACT = 2;

    // Context menu
    private static final int CMD_EDIT = 4;
    private static final int CMD_DELETE = 5;
    private static final int CMD_SET_AS_DEFAULT = 6;
    private static final int CMD_SET_AS_CONTACT = 7;

    /**
     * Called when the activity is first created.
     */
    private RecyclerView mRecyclerView;
    private SongsAdapter mSongsAdapter;
    private ArrayList<SongsModel> mData;
    private Context mContext;

    private Toolbar mToolbar;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = getApplicationContext();
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            showFinalAlert(getResources().getText(R.string.sdcard_readonly));
            return;
        }
        if (status.equals(Environment.MEDIA_SHARED)) {
            showFinalAlert(getResources().getText(R.string.sdcard_shared));
            return;
        }
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            showFinalAlert(getResources().getText(R.string.no_sdcard));
            return;
        }
        Intent intent = getIntent();
        mWasGetContentIntent = intent.getAction().equals(Intent.ACTION_GET_CONTENT);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.media_select);

        mData = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mData.addAll(Utils.getSongList(getApplicationContext(), true));
        mData.addAll(Utils.getSongList(getApplicationContext(), false));


        mSongsAdapter = new SongsAdapter(this, mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mSongsAdapter);

        /*try {
            mAdapter = new SimpleCursorAdapter(
                    this,
                    // Use a template that displays a text view
                    R.layout.media_select_row,
                    null,
                    // Map from database columns...
                    new String[]{
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media._ID,
                            MediaStore.Audio.Media._ID},
                    // To widget ids in the row layout...
                    new int[]{
                            R.id.row_artist,
                            R.id.row_album,
                            R.id.row_title,
                            R.id.row_icon,
                            R.id.row_options_button},
                    0);
            list_view = (ListView) findViewById(R.id.list_view);
            list_view.setAdapter(mAdapter);

            list_view.setItemsCanFocus(true);


            // Normal click - open the editor
            list_view.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent,
                                        View view,
                                        int position,
                                        long id) {
                    startRingdroidEditor();
                }
            });

            mInternalCursor = null;
            mExternalCursor = null;
            getLoaderManager().initLoader(INTERNAL_CURSOR_ID, null, this);
            getLoaderManager().initLoader(EXTERNAL_CURSOR_ID, null, this);

        } catch (SecurityException e) {
            // No permission to retrieve audio?
            Log.e("Ringdroid", e.toString());

        } catch (IllegalArgumentException e) {
            // No permission to retrieve audio?
            Log.e("Ringdroid", e.toString());
            // TODO error 2
        }

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.row_options_button) {
                    // Get the arrow ImageView and set the onClickListener to open the context menu.
                    ImageView iv = (ImageView) view;
                    iv.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            openContextMenu(v);
                        }
                    });
                    return true;
                } else if (view.getId() == R.id.row_icon) {
                    setSoundIconFromCursor((ImageView) view, cursor);
                    return true;
                }

                return false;
            }
        });

        registerForContextMenu(list_view);*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Utils.initImageLoader(mContext);
    }

    private void setSoundIconFromCursor(ImageView view, Cursor cursor) {
        if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_RINGTONE))) {
            view.setImageResource(R.drawable.type_ringtone);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.color.type_bkgnd_ringtone));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_ALARM))) {
            view.setImageResource(R.drawable.bottom_dragger);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.color.type_bkgnd_alarm));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_NOTIFICATION))) {
            view.setImageResource(R.drawable.bottom_dragger);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.color.type_bkgnd_notification));
        } else if (0 != cursor.getInt(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.IS_MUSIC))) {
            view.setImageResource(R.drawable.bottom_dragger);
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.color.type_bkgnd_music));
        }

        String filename = cursor.getString(cursor.getColumnIndexOrThrow(
                MediaStore.Audio.Media.DATA));
        if (!SoundFile.isFilenameSupported(filename)) {
            ((View) view.getParent()).setBackgroundColor(
                    getResources().getColor(R.color.type_bkgnd_unsupported));
        }
    }

    /**
     * Called with an Activity we started with an Intent returns.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent dataIntent) {
        if (requestCode != REQUEST_CODE_EDIT) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        setResult(RESULT_OK, dataIntent);
        //finish();  // TODO(nfaralli): why would we want to quit the app here?
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_options, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                RingdroidEditActivity.onAbout(this);
                return true;
            case R.id.action_record:
                onRecord();
                return true;
            default:
                return false;
        }
    }

    public void onPopUpMenuClickListener(View v, final int position) {
        final PopupMenu menu = new PopupMenu(this, v);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_song_edit:
                        startEditor(position);
                        break;
                    case R.id.popup_song_delete:
                        confirmDelete(position);
                        break;
                    case R.id.popup_song_assign_to_contact:
                        chooseContactForRingtone(position);
                        break;
                    case R.id.popup_song_set_default_notification:
                        setAsDefaultRingtoneOrNotification(position);
                        break;
                    case R.id.popup_song_set_default_ringtone:
                        setAsDefaultRingtoneOrNotification(position);
                        break;

                }
                return false;
            }
        });
        menu.inflate(R.menu.popup_song);

        if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {
            menu.getMenu().findItem(R.id.popup_song_set_default_notification).setVisible(false);
        } else if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_NOTIFICATION)) {
            menu.getMenu().findItem(R.id.popup_song_set_default_ringtone).setVisible(false);
            menu.getMenu().findItem(R.id.popup_song_assign_to_contact).setVisible(false);
        } else if (mData.get(position).mFileType.equalsIgnoreCase(Constants.IS_MUSIC)) {
            menu.getMenu().findItem(R.id.popup_song_set_default_notification).setVisible(false);
        }

        menu.show();
    }


    private void setAsDefaultRingtoneOrNotification(int pos) {
        // If the item is a ringtone then set the default ringtone,
        // otherwise it has to be a notification so set the default notification sound
        if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {
            RingtoneManager.setActualDefaultRingtoneUri(
                    RingdroidSelectActivity.this,
                    RingtoneManager.TYPE_RINGTONE, getUri(pos));
            Toast.makeText(
                    RingdroidSelectActivity.this,
                    R.string.default_ringtone_success_message,
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            RingtoneManager.setActualDefaultRingtoneUri(
                    RingdroidSelectActivity.this,
                    RingtoneManager.TYPE_NOTIFICATION,
                    getUri(pos));
            Toast.makeText(
                    RingdroidSelectActivity.this,
                    R.string.default_notification_success_message,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private int getUriIndex(Cursor c) {
        int uriIndex;
        String[] columnNames = {
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI.toString(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
        };

        for (String columnName : Arrays.asList(columnNames)) {
            uriIndex = c.getColumnIndex(columnName);
            if (uriIndex >= 0) {
                return uriIndex;
            }
            // On some phones and/or Android versions, the column name includes the double quotes.
            uriIndex = c.getColumnIndex("\"" + columnName + "\"");
            if (uriIndex >= 0) {
                return uriIndex;
            }
        }
        return -1;
    }

    private Uri getUri(int pos) {
        /*//Get the uri of the item that is in the row
        Cursor c = mAdapter.getCursor();
        int uriIndex = getUriIndex(c);
        if (uriIndex == -1) {
            return null;
        }
        String itemUri = c.getString(uriIndex) + "/" +
                c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));*/
        return MediaStore.Audio.Media.getContentUriForPath(mData.get(pos).mPath);
    }

    private boolean chooseContactForRingtone(int pos) {
        try {
            //Go to the choose contact activity
            Intent intent = new Intent(RingdroidSelectActivity.this, ChooseContactActivity.class);
            intent.putExtra(Constants.FILE_NAME, String.valueOf(getUri(pos)));
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CONTACT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't open Choose Contact window" + e);
        }
        return true;
    }

    private void confirmDelete(int pos) {
        // See if the selected list item was created by Ringdroid to
        // determine which alert message to show

        String artist = mData.get(pos).mArtistName;
        CharSequence ringdroidArtist = getResources().getText(R.string.artist_name);

        CharSequence message;
        if (artist.equals(ringdroidArtist)) {
            message = getResources().getText(
                    R.string.confirm_delete_ringdroid);
        } else {
            message = getResources().getText(
                    R.string.confirm_delete_non_ringdroid);
        }

        CharSequence title;
        if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_RINGTONE)) {
            title = getResources().getText(R.string.delete_ringtone);
        } else if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_ALARM)) {
            title = getResources().getText(R.string.delete_alarm);
        } else if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_NOTIFICATION)) {
            title = getResources().getText(R.string.delete_notification);
        } else if (mData.get(pos).mFileType.equalsIgnoreCase(Constants.IS_MUSIC)) {
            title = getResources().getText(R.string.delete_music);
        } else {
            title = getResources().getText(R.string.delete_audio);
        }

        new AlertDialog.Builder(RingdroidSelectActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        R.string.delete_ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                onDelete();
                            }
                        })
                .setNegativeButton(
                        R.string.delete_cancel_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .setCancelable(true)
                .show();
    }

    private void onDelete() {
        Cursor c = mAdapter.getCursor();
        int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        String filename = c.getString(dataIndex);

        int uriIndex = getUriIndex(c);
        if (uriIndex == -1) {
            showFinalAlert(getResources().getText(R.string.delete_failed));
            return;
        }

        if (!new File(filename).delete()) {
            showFinalAlert(getResources().getText(R.string.delete_failed));
        }

        String itemUri = c.getString(uriIndex) + "/" + c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        getContentResolver().delete(Uri.parse(itemUri), null, null);
    }

    private void showFinalAlert(CharSequence message) {
        new AlertDialog.Builder(RingdroidSelectActivity.this)
                .setTitle(getResources().getText(R.string.alert_title_failure))
                .setMessage(message)
                .setPositiveButton(
                        R.string.alert_ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                finish();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    private void onRecord() {
        try {
            Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("record"));
            intent.putExtra("was_get_content_intent", mWasGetContentIntent);
            intent.setClassName("com.ringdroid", "com.ringdroid.RingdroidEditActivity");
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }
    }


    public void onItemClicked(int adapterPosition) {
        startEditor(adapterPosition);
    }

    private void startEditor(int pos) {
        Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(mData.get(pos).mPath));
        intent.putExtra("was_get_content_intent", mWasGetContentIntent);
        intent.setClassName("com.ringdroid", "com.ringdroid.RingdroidEditActivity");
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }
}