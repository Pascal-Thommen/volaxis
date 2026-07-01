package edu.upa.tie.ui;

import edu.upa.tie.dao.UsuarioDAO;
import edu.upa.tie.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JDialog {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final JTextField nombreField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);
    private final JTextField whatsappField = new JTextField(20);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public RegisterFrame(Frame parent) {
        super(parent, "Volaxis – Registrarse", true);
        setResizable(false);
        setSize(440, 540);
        setLocationRelativeTo(parent);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel();
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Crear cuenta");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 4, 0);
        c.gridx = 0;

        String[] labels = {"Nombre completo", "Correo electrónico", "Contraseña", "WhatsApp (ej: 595981000000)"};
        String[] placeholders = {"Juan Pérez", "tu@email.com", "Tu contraseña", "5959810000000"};
        JTextField[] fields = {nombreField, emailField, passField, whatsappField};

        for (int i = 0; i < labels.length; i++) {
            c.gridy = i * 2;
            form.add(label(labels[i]), c);
            c.gridy = i * 2 + 1;
            styleField(fields[i]);
            addPlaceholder(fields[i], placeholders[i]);
            form.add(fields[i], c);
        }

        c.gridy = 8;
        c.insets = new Insets(16, 0, 5, 0);
        JButton registerBtn = primaryButton("Registrarse");
        form.add(registerBtn, c);

        c.gridy = 9;
        c.insets = new Insets(4, 0, 0, 0);
        JButton backBtn = linkButton("¿Ya tienes cuenta? Iniciar sesión");
        form.add(backBtn, c);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(registerBtn);
    }

    private void doRegister() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        String whatsapp = whatsappField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, correo y contraseña son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (usuarioDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Ya existe una cuenta con ese correo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(pass);
        u.setWhatsapp(whatsapp.isEmpty() ? null : whatsapp);
        usuarioDAO.insert(u);
        JOptionPane.showMessageDialog(this, "Cuenta creada exitosamente. Ahora puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(new Color(60, 60, 80));
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 196, 240), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        Color placeholderColor = new Color(170, 170, 190);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(placeholderColor);
                }
            }
        });
        field.setText(placeholder);
        field.setForeground(placeholderColor);
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 46));
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        return b;
    }

    private JButton linkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setForeground(PRIMARY);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
