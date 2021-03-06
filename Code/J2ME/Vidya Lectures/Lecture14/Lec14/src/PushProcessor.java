import javax.microedition.io.*;
import java.io.*;

    /**
     *  PushProcessor - Thread of execution responsible of
     *  receiving and processing push events.
     */
    class PushProcessor implements Runnable {

	Thread th = new Thread(this);
	String url;
	boolean done = false;
	String midletClassName;

	/** Constructor */
	public PushProcessor(String url) {
	    this.url = url;
	    th.start();
	}

	public void notifyDone() {
	    done = true;
	}

	/**
	 *  Thread's run method to wait for and process
	 *  received messages.
	 */
	public void run() {
	    ServerSocketConnection ssc = null;
	    SocketConnection sc = null;
	    InputStream is = null;
	    try {
		while(!done) {

		    if (url.startsWith("socket://")) {
			//  "Open" connection.
			ssc = (ServerSocketConnection)
			    Connector.open(url);
			//  Wait for (and accept) inbound connection.
			sc = (SocketConnection)
			    ssc.acceptAndOpen();
			is = sc.openInputStream();
			//  Read data from inbound connection.
			int ch;
			byte[] data = null;
			ByteArrayOutputStream tmp = new
			    ByteArrayOutputStream();
			while( ( ch = is.read() ) != -1 ) {
			    tmp.write( ch );
			}
			data = tmp.toByteArray();
			//--------------------------------------+
			//  Here do something with received data|
			//--------------------------------------+
			System.out.print(new String(data));
		    }

		}
	    }
	    catch (IOException e) {
		System.out.println("PushProcessor.run Exception" + e);
		e.printStackTrace();
	    }
	    finally {
		try {
		    if (is != null) is.close();
		    if (sc != null) sc.close();
		    if (ssc != null) ssc.close();
		}
		catch(Exception e) {
		}
	    }
	}

    } 
