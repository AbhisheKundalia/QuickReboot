package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    Button reboot, shutdown, ping;
    boolean suAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assing Buttons from XML
        reboot = (Button) findViewById(R.id.reboot);
        shutdown = (Button) findViewById(R.id.shutdown);
        ping = (Button) findViewById(R.id.ping);

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

            ping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setMessage("Are you sure you want to " + getText(R.string.ping) + " ?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.ping, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RootBoot(MainActivity.this).execute("ping");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Phone not Rooted!", Toast.LENGTH_SHORT).show();
        }

    }

    public class RootBoot extends AsyncTask<String, Void, String> {
        Context context = null;

        public RootBoot(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String params) {
            if (params == null) {
                Toast.makeText(context, "Null returned", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, params, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            switch (params[0]) {
                case "reboot":
                    Shell.SU.run("reboot");
                    result = null;
                    break;

                case "shutdown":
                    Shell.SU.run("reboot -p");
                    result = null;
                    break;

                case "ping":
                    List<String> suResult;

                    if (suAvailable) {
                        suResult = Shell.SU.run(new String[]{"su; "});
                        Log.i("sysUI: ", suResult.toString());
                        result = suResult.toString();
                    }
                    break;

                default:
                    break;
            }
            return result;
        }
    }
}
