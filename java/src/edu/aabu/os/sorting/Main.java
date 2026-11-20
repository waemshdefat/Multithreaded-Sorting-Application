package edu.aabu.os.sorting;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. */
public final class Main {

    private Main() {
        // No instances required.
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // The custom dark theme does not depend on a specific Look and Feel.
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
