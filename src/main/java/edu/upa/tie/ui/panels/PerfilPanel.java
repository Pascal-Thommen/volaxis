package edu.upa.tie.ui.panels;

import edu.upa.tie.Session;
import edu.upa.tie.dao.UsuarioDAO;
import edu.upa.tie.model.Usuario;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PerfilPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private final JTextField nombreField = new JTextField(22);
    private final JTextField emailField = new JTextField(22);
    private final JPasswordField passField = new JPasswordField(22);
    private final JTextField whatsappField = new JTextField(22);

    public PerfilPanel(MainFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 247, 255));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 215, 255), 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 4, 6, 4);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel title = new JLabel("Mi perfil");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(40, 35, 90));
        card.add(title, c);

        c.gridwidth = 1;
        String[] labels = {"Nombre", "Correo electrónico", "Nueva contraseña", "WhatsApp"};
        JTextField[] fields = {nombreField, emailField, passField, whatsappField};

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0; c.gridy = i + 1; c.weightx = 0.3;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            card.add(lbl, c);

            c.gridx = 1; c.weightx = 0.7;
            styleField(fields[i]);
            card.add(fields[i], c);
        }

        c.gridx = 0; c.gridy = labels.length + 1; c.gridwidth = 2;
        c.insets = new Insets(16, 4, 4, 4);
        JButton saveBtn = accentButton("Guardar cambios");
        saveBtn.addActionListener(e -> save());
        card.add(saveBtn, c);

        add(card);
    }

    @Override
    public void refresh() {
        Usuario u = Session.get();
        nombreField.setText(u.getNombre());
        emailField.setText(u.getEmail());
        passField.setText("");
        whatsappField.setText(u.getWhatsapp() != null ? u.getWhatsapp() : "");
    }

    private void save() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String whatsapp = whatsappField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y correo son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = Session.get();
        u.setNombre(nombre);
        u.setEmail(email);
        if (!pass.isEmpty()) u.setPassword(pass);
        u.setWhatsapp(whatsapp.isEmpty() ? null : whatsapp);

        usuarioDAO.update(u);
        JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 196, 240), 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 38));
        return b;
    }
}
