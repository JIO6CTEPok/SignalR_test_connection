package by.example.subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;

public class MyTestApp {
   public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException 
   {
	 
	  List<String> checkPorts=new ArrayList<String>();
        Logger logger = new Logger() {
	        @Override
	        public void log(String message, LogLevel level) {
	            System.out.println(message);
	        }
	    };
	  
	    String ServerURI = "http://localhost:8089";
	   HubConnection Connection = new HubConnection(ServerURI);
       HubProxy HubProxy = Connection.createHubProxy("MyHub");

        HubProxy.on("Heartbeat",new SubscriptionHandler() {
			
			@Override
			public void run() {
				System.out.println("Heartbeat");
				
			}
		});
        HubProxy.on("AddMessage", new SubscriptionHandler2<String, String>() {
          @Override
          public void run(String e1, String e2) {
             System.out.println(e1.toString()+  " -> " +e2.toString());   
          }
        }, String.class, String.class);
        try
        {
        SignalRFuture<Void> con  =Connection.start(new ServerSentEventsTransport(logger)); //Or LongPollingTransport
        con.get(); 
        
       }
        catch(InterruptedException e)
        {
        	System.err.println("message:"+e.getMessage());
        }
        catch(ExecutionException e)
        {
        	System.err.println("message:"+e.getMessage());
        }
        Scanner inputReader = new Scanner(System.in);
        String line = inputReader.nextLine();
        while (!"exit".equals(line)) {
              HubProxy.invoke("send", "Console", line);
              line = inputReader.next();
         }

        inputReader.close();

        Connection.stop();
	    System.out.println("------------");
	    System.out.println("------------");
   }
}