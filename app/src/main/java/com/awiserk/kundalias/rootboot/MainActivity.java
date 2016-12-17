package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    Button reboot, shutdown, rebootRecovery;
    boolean suAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assing Buttons from XML
        reboot = (Button) findViewById(R.id.reboot);
        shutdown = (Button) findViewById(R.id.shutdown);
        rebootRecovery = (Button) findViewById(R.id.rebootrecovery);

        suAvailable = Shell.SU.available();
        if (suAvailable) {
            //Assigning listener to each Button
            reboot.setOnClickListener(new View.OnClickListener() {
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
            });
        } else {
            Toast.makeText(MainActivity.this, "Phone not Rooted!", Toast.LENGTH_SHORT).show();
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
                case "reboot":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 2 && reboot");
                    break;

                case "shutdown":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 2 && reboot -p");
                    break;

                case "rebootrecovery":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                    Shell.SU.run("sleep 2 && reboot recovery");
                    break;

                default:
                    break;
            }
            return null;
        }
    }
}
