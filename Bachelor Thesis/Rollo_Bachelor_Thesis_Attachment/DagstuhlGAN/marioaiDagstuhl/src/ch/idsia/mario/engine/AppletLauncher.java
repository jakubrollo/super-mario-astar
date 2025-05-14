package ch.idsia.mario.engine;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;

import javax.swing.*;

public class AppletLauncher extends JFrame {
    private static final long serialVersionUID = -2238077255106243788L;

    private MarioComponent mario;
    private boolean started = false;

    public AppletLauncher() {
        setTitle("MarioInfinite AI Framework by Sergey Karakovskiy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // You can adjust the size as needed
        setLocationRelativeTo(null); // Center the window
    }

    public void start() {
        if (!started) {
            started = true;
            IAgent hka = new HumanKeyboardAgent();
            hka.reset();
            mario = new MarioComponent(getWidth(), getHeight());
            setContentPane(mario);
            setFocusable(false);
            mario.setFocusCycleRoot(true);
            mario.start();
            setVisible(true);  // Show the JFrame
        }
    }

    public void stop() {
        if (started) {
            started = false;
            mario.stop();
            dispose();  // Close and dispose of the JFrame
        }
    }

    public static void main(String[] args) {
        AppletLauncher launcher = new AppletLauncher();
        launcher.start();
    }
}
