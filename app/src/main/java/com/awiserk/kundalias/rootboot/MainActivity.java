package com.awiserk.kundalias.rootboot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button reboot, shutdown, ping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assing Buttons from XML
        reboot = (Button) findViewById(R.id.reboot);
        shutdown = (Button) findViewById(R.id.shutdown);
        ping = (Button) findViewById(R.id.ping);

        //Assigning listener to each Button
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RootBoot(v.getContext()).execute("reboot");
            }
        });

        shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RootBoot(v.getContext()).execute("shutdown");
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
            Toast.makeText(context, params, Toast.LENGTH_LONG).show();

        }

        @Override
        protected String doInBackground(String... params) {


            return params[0];
        }
    }
}
