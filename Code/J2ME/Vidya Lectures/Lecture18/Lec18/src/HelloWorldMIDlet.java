import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CommandListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.m3g.*;

public class HelloWorldMIDlet extends MIDlet implements CommandListener {
	private Display myDisplay = null;
	private MyCanvas myCanvas = null;
	private TimerTask myRefreshTask = null;
	private Camera camera = null;	
	private Timer myRefreshTimer = new Timer();	
	private Command exitCommand = new Command("Exit", Command.ITEM, 1);
	
	Graphics3D myGraphics3D = Graphics3D.getInstance();
	
	// Contains our entire scene graph
	World myWorld = null;

	int viewport_x;
	int viewport_y;
	int viewport_width;
	int viewport_height;

	/**
	 * HelloWorldMIDlet - default constructor.
	 */
	public HelloWorldMIDlet() {
		super();
		// Set up the user interface.
		myDisplay = Display.getDisplay(this);
		myCanvas = new MyCanvas(this);
		myCanvas.setCommandListener(this);
		myCanvas.addCommand(exitCommand);
	}

	/**
	 * startApp()
	 */
	public void startApp() throws MIDletStateChangeException {
		myDisplay.setCurrent(myCanvas);

		try {
			// Load the world from the m3g file							
		    myWorld = (World) Loader.load("/pogoroo.m3g")[0];
		    setupAspectRatio();
		} catch (Exception e) {
			e.printStackTrace();
		}

		myRefreshTask = new RefreshTask();

		// Schedule a repeating timer with a frame rate of 20fps.
		myRefreshTimer.schedule(myRefreshTask, 0, 50);
	}

	
	/**
	 * Make sure that the content is rendered with the correct aspect ratio.
	 */
	void setupAspectRatio() {
		viewport_x = 0;
		viewport_y = 0;
		viewport_width = myCanvas.getWidth();
		viewport_height = myCanvas.getHeight();

		Camera cam = myWorld.getActiveCamera();

		float[] params = new float[4];
		int type = cam.getProjection(params);
		if (type != Camera.GENERIC) {
			// Calculate the window aspect ratio
			float aspectratio = viewport_width / viewport_height;

			if (aspectratio < params[1]) {
				float height = viewport_width / params[1];
				viewport_height = (int) height;
				viewport_y = (myCanvas.getHeight() - viewport_height) / 2;
			} else {
				float width = viewport_height * params[1];
				viewport_width = (int) width;
				viewport_x = (myCanvas.getWidth() - viewport_width) / 2;
			}
		}
	}

	/**
	 * pauseApp()
	 */
	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
		myRefreshTimer.cancel();
		myRefreshTimer = null;
		myRefreshTask = null;
	}

	/**
	 * MIDlet paint method.
	 */
	public void paint(Graphics g) {
		if (g.getClipWidth() != viewport_width
				|| g.getClipHeight() != viewport_height
				|| g.getClipX() != viewport_x || g.getClipY() != viewport_y) {
			g.setColor(0x00);
			g.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
		}

		if ((myGraphics3D != null) && (myWorld != null)) {
			myGraphics3D.bindTarget(g);
			myGraphics3D.setViewport(viewport_x, viewport_y, viewport_width,
					viewport_height);
			myGraphics3D.render(myWorld);
			myGraphics3D.releaseTarget();
		}
	}

	/**
	 * Handle commands.
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == exitCommand) {
			try {
				destroyApp(false);
				notifyDestroyed();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inner class for refreshing the view.
	 */
	private class RefreshTask extends TimerTask {
		public void run() {
			if (myCanvas != null && myGraphics3D != null && myWorld != null) {
				int startTime = (int) System.currentTimeMillis();
				int validity = myWorld.animate(startTime);
				myCanvas.repaint(viewport_x, viewport_y, viewport_width,
						viewport_height);
			}
		}
	}

	/**
	 * Inner class for handling the canvas.
	 */
	class MyCanvas extends Canvas {
		HelloWorldMIDlet myMIDlet;

		/**
		 * Construct a new canvas
		 */
		MyCanvas(HelloWorldMIDlet midlet) {
			myMIDlet = midlet;
		}

		/**
		 * Initialize self.
		 */
		void init() { 
		}
		
		/**
		 * Cleanup and destroy.
		 */
		void destroy() { 
		}
			
		/*
		 * Ask myMIDlet to paint itself
		 */				
		protected void paint(Graphics g) {
			myMIDlet.paint(g);
		}
	}
}