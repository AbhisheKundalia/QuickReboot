package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    private static final String SHUTDOWN = "reboot -p";
    private static final String REBOOT_CMD = "reboot";
    private static final String REBOOT_QUICK_REBOOT_CMD = "setprop ctl.restart zygote";
    private static final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private static final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private static final String[] REBOOT_SAFE_MODE
            = new String[]{"setprop persist.sys.safemode 1", REBOOT_QUICK_REBOOT_CMD};
    private static final String PLAY_STORE_RATE_US
            = "https://play.google.com/store/apps/details?id=com.awiserk.kundalias.rootboot";
    private static final int RUNNABLE_DELAY_MS = 1000;
    private static boolean suAvailable = false;
    private Handler mHandler;

    // just for safe measure, we don't want any data corruption, right?
    private static String[] SHUTDOWN_BROADCAST() {
        return new String[]{
                // we announce the device is going down so apps that listen for
                // this broadcast can do whatever
                "am broadcast android.intent.action.ACTION_SHUTDOWN",
                // we tell the file system to write any data buffered in memory out to disk
                "sync",
                // we also instruct the kernel to drop clean caches, as well as
                // reclaimable slab objects like dentries and inodes
                "echo 3 > /proc/sys/vm/drop_caches",
                // and sync buffered data as before
                "sync",
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_list);

        // Add data to a list
        final ArrayList<ButtonData> buttonDatas = new ArrayList<ButtonData>();

        buttonDatas.add(new ButtonData(R.drawable.ic_shutdown_new_black_48dp, R.string.shutdown, R.string.shutdowndesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_reboot_phone_black_48dp, R.string.reboot, R.string.rebootdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_quick_reboot_black_48dp, R.string.quickreboot, R.string.quickrebootdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_safemode_black_48dp, R.string.safemode, R.string.safemodedesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_reboot_recovery_black_48dp, R.string.rebootrecovery, R.string.rebootrecdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_bootloader_black_48dp, R.string.rebootbootloader, R.string.rebootbootloaderdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_grade_black_24dp, R.string.rateus, R.string.rateusdesc));


        //Call listViewAdapter and set values of the arraylist in the list view
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, buttonDatas);

        //Create a Listview object pointing to Button_List.xml
        ListView listView = (ListView) findViewById(R.id.list);

        //Set adapter to the listview
        listView.setAdapter(listViewAdapter);

        //Check if Root access is available or not
        suAvailable = Shell.SU.available();
        if (suAvailable) {
            //Assigning listener to each Button
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("Clicked", " button clicked");
                    final String itemTitle = getText(buttonDatas.get(position).getTitleID()).toString();
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Are you sure you want to " + itemTitle + " ?")
                            .setCancelable(false)
                            .setPositiveButton(itemTitle, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RootBoot(MainActivity.this).execute(itemTitle);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Phone not Rooted!", Toast.LENGTH_SHORT).show();
            //Alert user with dialog to provide permission or quit app
            new AlertDialog.Builder(this)
                    .setMessage("Your application does not have root access. So now the application will Quit!").setCancelable(false)
                    .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    }).setNegativeButton("Restart App", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }).show();
        }
    }

    private void runCmd(long timeout, @NonNull final String... cmd) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run(cmd);
                mHandler.removeCallbacks(this);
            }
        }, timeout);
    }

    /**
     * Closes current app and thus provides user friendly interface
     */
    private void closeCurrentActivity()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.finish();
            }
        });
    }

    /*
* Start with rating the app
* Determine if the Play Store is installed on the device
*
* */
    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    //Async task for executing operation in background thread
    public class RootBoot extends AsyncTask<String, Void, Void> {
        Context context = null;

        public RootBoot(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            String result = null;
            switch (params[0]) {
                case "Shutdown Phone":
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN);
                    break;

                case "Reboot Phone":
                    closeCurrentActivity();
                    runCmd(0, REBOOT_CMD);
                    break;

                case "Quick Reboot":
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_QUICK_REBOOT_CMD);
                    break;

                case "Reboot in Safe Mode":
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_SAFE_MODE);
                    break;

                case "Reboot Recovery":
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_RECOVERY_CMD);
                    break;

                case "Reboot Bootloader":
                    closeCurrentActivity();
                    runCmd(0, REBOOT_BOOTLOADER_CMD);
                    break;

                case "Rate Us":
                    rateApp();
                    break;

                default:
                    break;
            }
            return null;
        }
    }
}
