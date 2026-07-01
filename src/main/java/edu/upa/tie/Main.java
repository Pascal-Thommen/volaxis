package edu.upa.tie;

import edu.upa.tie.db.Database;
import edu.upa.tie.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Database.init();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
