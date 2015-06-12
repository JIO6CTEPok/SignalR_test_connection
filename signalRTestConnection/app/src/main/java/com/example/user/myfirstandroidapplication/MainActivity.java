package com.example.user.myfirstandroidapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.myfirstandroidapplication.Subscribe.SubscribeMessage;
import com.example.user.myfirstandroidapplication.Subscribe.Subscriber;

public class MainActivity extends ActionBarActivity {
    Subscriber[] s;
    Subscriber subscriber;
    TextView statusTextView;
    EditText ipConnectionTextField;
    EditText hubTextField;
    TextView logTextView;
    int pos = 0;
    String[][] connectionMass = {
            {"10.0.2.2:8089", "MyHub"}
            //  {"http://localhost:8089","MyHub"},
            // {"http://photomsk.by","MessageHub"}
    };

    public MainActivity() {
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void onClickConnectionButton() {
        createCon();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // checkConnection();
        statusTextView = (TextView) findViewById(R.id.textout1);
        Button massConnectButton = (Button) findViewById(R.id.connectButton);
        Button OneConnectButton = (Button) findViewById(R.id.button);
        logTextView = (TextView) findViewById(R.id.textView);
        ipConnectionTextField = (EditText) findViewById(R.id.editText);
        hubTextField = (EditText) findViewById(R.id.editText2);


        if (isOnline())
            statusTextView.setText("Step One:online");
        else
            statusTextView.setText("Step One:offline");

        massConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCon();
            }
        });


        logTextView.setMovementMethod(new ScrollingMovementMethod());

        OneConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.println("edit text=" + ipConnectionTextField.getText().toString());
                subscriber = createSubscriber(ipConnectionTextField.getText().toString(), hubTextField.getText().toString());
            }
        });
    }


    void createCon() {

        System.err.println("Create con");

        s = new Subscriber[connectionMass.length];
        for (int i = 0; i < connectionMass.length; i++) {
            System.err.println("Create con" + i + " :" + connectionMass[i][0]);
            logTextView.append("Create con" + i + " :" + connectionMass[i][0] + "\r\n");

            s[i] = createSubscriber(connectionMass[i][0], connectionMass[i][1]);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected Subscriber createSubscriber(String connectionString, String HubString) {
        System.err.println("Create subscriber:" + connectionString + " " + HubString);
        Subscriber res = new Subscriber(connectionString, HubString, new SubscribeMessage() {
            @Override
            public void message(String mess) {
                super.message(mess);
                System.err.println("Message: " + mess);
                statusTextView.setText(mess);
                logTextView.append("Message:" + mess + " \r\n ");
            }

            @Override
            public void reload(String text) {
                System.err.println("Bad: " + text);
                logTextView.append("Bad:" + text + " \r\n ");
            }
        });
        return res;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
