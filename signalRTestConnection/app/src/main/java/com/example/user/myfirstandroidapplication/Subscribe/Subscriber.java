package com.example.user.myfirstandroidapplication.Subscribe;

import java.util.concurrent.ExecutionException;


import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;



import android.os.AsyncTask;



public class Subscriber {
    public SubscribeMessage messager;
    public String connectionStr;
    HubConnection connection;
    HubProxy mainHubProxy;
    Logger logger = new Logger() {
        @Override
        public void log(String message, LogLevel level) {
            System.out.println(message);
        }
    };

    public Subscriber(String connectionString, String hubProxy, final SubscribeMessage messager) {
        this.messager = messager;
        this.connectionStr = connectionString;
        System.err.println("//*Connection: " + connectionStr);


        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        connection = new HubConnection(this.connectionStr, "", true, logger);
        mainHubProxy = connection.createHubProxy(hubProxy);

        System.err.println("/Subscribe addMessage");


       mainHubProxy.on("AddMessage", new SubscriptionHandler2<String, String>() {
            @Override
            public void run(String e1, String e2) {
                System.out.println(e1.toString() + " -> " + e2.toString());
            }
        }, String.class, String.class);



        connection.closed(new Runnable() {
            @Override
            public void run() {
                if (messager != null)
                    messager.message("/close " + connectionStr);
                //connectSignalr();
            }
        });
        connection.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState connectionState, ConnectionState connectionState2) {
                if (messager != null)
                    messager.message("SignalR: " + connectionStr + ": " + connectionState.name() + "->" + connectionState2.name());
            }
        });
        connectSignalr();
        System.err.println("/Connection append!");
    }

    private void connectSignalr() {
        try {
            System.err.println("/ConnectSignalr: " + connectionStr);
            SignalRConnectTask signalRConnectTask = new SignalRConnectTask();
            signalRConnectTask.execute(connection);

        } catch (Exception ex) {
            System.err.println("*/Exception ConnectSignalr: " + connectionStr);
            ex.printStackTrace();
        }
    }

    public class SignalRConnectTask extends AsyncTask {
        String outMessage = "";
        String outConnstr = "";

        @Override
        protected Object doInBackground(Object[] objects) {
            HubConnection connection = (HubConnection) objects[0];
            try {

                SignalRFuture<Void> con = connection.start(new ServerSentEventsTransport(logger)); //Or LongPollingTransport
                System.err.println("con get");
                con.get();
                System.err.println("con get stop");

            } catch (InterruptedException e) {
                System.err.println("*/InterruptedException");
                e.printStackTrace();
                //messager.message("InterruptedException");
                outMessage = e.getMessage();
                outConnstr = connectionStr;

            } catch (ExecutionException e) {
                e.printStackTrace();
                //messager.message("ExecutionException");
                System.err.println("*/ExecutionException");
                outMessage = e.getMessage();
                outConnstr = connectionStr;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (outMessage != "") {
                messager.reload(outConnstr);
                messager.reload(outMessage);
            }
            System.err.println("onPostExecute");
        }
    }
}
