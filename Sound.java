import javax.swing.*;

public class Sound extends Thread {

    String message;

    Sound (String m) {
        message = m;
    }
    public void run() {
        JOptionPane.showMessageDialog(null, message);
    }
}
