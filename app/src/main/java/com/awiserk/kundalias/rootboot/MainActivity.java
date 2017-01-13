package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

    Button reboot, shutdown, rebootRecovery;
    boolean suAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_list);

      /*  //assing Buttons from XML
        reboot = (Button) findViewById(R.id.reboot);
        shutdown = (Button) findViewById(R.id.shutdown);
        rebootRecovery = (Button) findViewById(R.id.rebootrecovery);
*/
        // Add data to a list
        final ArrayList<ButtonData> buttonDatas = new ArrayList<ButtonData>();
        buttonDatas.add(new ButtonData(R.drawable.ic_power_settings_new_black_48dp, R.string.shutdown, R.string.shutdowndesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_rotate_right_black_48dp, R.string.reboot, R.string.rebootdesc));
        buttonDatas.add(new ButtonData(R.drawable.ic_sync_problem_black_48dp, R.string.rebootrecovery, R.string.rebootrecdesc));

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

           /* listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Are you sure you want to " + getText(R.string.reboot) + " ?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RootBoot(MainActivity.this).execute("reboot");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });

            shutdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Are you sure you want to " + getText(R.string.shutdown) + " ?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.shutdown, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RootBoot(MainActivity.this).execute("shutdown");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });

            rebootRecovery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Are you sure you want to " + getText(R.string.rebootrecovery) + " ?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.rebootrecovery, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RootBoot(MainActivity.this).execute("rebootrecovery");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });*/


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
                case "Reboot Phone":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 1 && reboot");
                    break;

                case "Shutdown Phone":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 1 && reboot -p");
                    break;

                case "Reboot Recovery":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 1 && reboot recovery");
                    break;

                default:
                    break;
            }
            return null;
        }
    }
}
