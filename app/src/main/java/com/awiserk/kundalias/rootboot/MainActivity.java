package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    public static final String RATE_US = "Rate Us";
    public static final String DONATE_US = "Donate Us";
    public static final String REBOOT_BOOTLOADER = "Reboot Bootloader";
    public static final String REBOOT_RECOVERY = "Reboot Recovery";
    public static final String REBOOT_IN_SAFE_MODE = "Reboot in Safe Mode";
    public static final String QUICK_REBOOT = "Quick Reboot";
    public static final String REBOOT_PHONE = "Reboot Phone";
    public static final String SHUTDOWN_PHONE = "Shutdown Phone";
    private static final String BACKGROUND_THREAD = "BackgroundThread";
    private static final String SHUTDOWN = "reboot -p";
    private static final String REBOOT_CMD = "reboot";
    private static final String REBOOT_QUICK_REBOOT_CMD = "setprop ctl.restart zygote";
    private static final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private static final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private static final String[] REBOOT_SAFE_MODE
            = new String[]{"setprop persist.sys.safemode 1", REBOOT_QUICK_REBOOT_CMD};
    private static final int RUNNABLE_DELAY_MS = 700;
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

        HandlerThread mHandlerThread = new HandlerThread(BACKGROUND_THREAD);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        // Add data to a list
        final ArrayList<ButtonData> buttonDatas = new ArrayList<>();

        buttonDatas.add(new ButtonData(R.drawable.ic_shutdown_new_black_48dp, R.string.shutdown, R.string.shutdowndesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_reboot_phone_black_48dp, R.string.reboot, R.string.rebootdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_quick_reboot_black_48dp, R.string.quickreboot, R.string.quickrebootdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_safemode_black_48dp, R.string.safemode, R.string.safemodedesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_reboot_recovery_black_48dp, R.string.rebootrecovery, R.string.rebootrecdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_bootloader_black_48dp, R.string.rebootbootloader, R.string.rebootbootloaderdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_grade_black_24dp, R.string.rateus, R.string.rateusdesc));
        buttonDatas.add(new ButtonData(R.drawable.outline_tag_faces_black_48, R.string.donateus, R.string.donateusdesc));


        //Call listViewAdapter and set values of the arraylist in the list view
        ListViewAdapter listViewAdapter = new ListViewAdapter(this, buttonDatas);

        //Create a Listview object pointing to Button_List.xml
        ListView listView = findViewById(R.id.list);

        //Set adapter to the listview
        listView.setAdapter(listViewAdapter);

        //Check if Root access is available or not
        boolean suAvailable = Shell.SU.available();
        if (suAvailable) {
            // List to avoid confirmation dialogue box
            final List<String> skipDialogueList = new ArrayList<>();
            skipDialogueList.add(RATE_US);
            skipDialogueList.add(DONATE_US);
            //Assigning listener to each Button
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Log.i("Clicked", " button clicked");
                final String itemTitle = getText(buttonDatas.get(position).getTitleID()).toString();
                if (skipDialogueList.contains(itemTitle)) {
                    new RootBoot(MainActivity.this).execute(itemTitle);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Are you sure you want to " + itemTitle + " ?")
                            .setCancelable(false)
                            .setPositiveButton(itemTitle, (dialog, which) -> new RootBoot(MainActivity.this).execute(itemTitle))
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Phone not Rooted!", Toast.LENGTH_SHORT).show();
            //Alert user with dialog to provide permission or quit app
            new AlertDialog.Builder(this)
                    .setMessage("Your application does not have root access. So now the application will Quit!").setCancelable(false)
                    .setPositiveButton("Quit", (dialog, which) -> MainActivity.this.finish()).setNegativeButton("Restart App", (dialog, which) -> {
                MainActivity.this.finish();
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }).show();
        }
    }

    private void runCmd(long timeout, @NonNull final String... cmd) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, cmd.toString(), Toast.LENGTH_SHORT).show();
                Shell.SU.run(cmd);
                mHandler.removeCallbacks(this);
            }
        }, timeout);
    }

    /**
     * Closes current app and thus provides user friendly interface
     */
    private void closeCurrentActivity() {
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
    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    public void donateUs() {
        Intent rateIntent = rateIntentForUrl("https://www.buymeacoffee.com/awiserk");
        startActivity(rateIntent);
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    //Async task for executing operation in background thread
    public class RootBoot extends AsyncTask<String, Void, Void> {
        Context context;

        RootBoot(Context context) {
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
            switch (params[0]) {
                case SHUTDOWN_PHONE:
                    closeCurrentActivity();
                    runCmd(RUNNABLE_DELAY_MS, SHUTDOWN);
                    break;

                case REBOOT_PHONE:
                    closeCurrentActivity();
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_CMD);
                    break;

                case QUICK_REBOOT:
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_QUICK_REBOOT_CMD);
                    break;

                case REBOOT_IN_SAFE_MODE:
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_SAFE_MODE);
                    break;

                case REBOOT_RECOVERY:
                    closeCurrentActivity();
                    runCmd(0, SHUTDOWN_BROADCAST());
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_RECOVERY_CMD);
                    break;

                case REBOOT_BOOTLOADER:
                    closeCurrentActivity();
                    runCmd(RUNNABLE_DELAY_MS, REBOOT_BOOTLOADER_CMD);
                    break;

                case RATE_US:
                    rateApp();
                    break;

                case DONATE_US:
                    donateUs();
                    break;

                default:
                    break;
            }
            return null;
        }
    }
}
