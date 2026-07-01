package edu.upa.tie.ui;

import edu.upa.tie.Session;
import edu.upa.tie.dao.UsuarioDAO;
import edu.upa.tie.model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private static final Color PRIMARY = new Color(127, 119, 221);
    private static final Color PRIMARY_LIGHT = new Color(243, 240, 253);

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        setTitle("Volaxis – Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 450);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel();
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(30, 20, 30, 20));
        JLabel title = new JLabel("VOLAXIS");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Mercado de libros universitarios");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(220, 215, 255));
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        title.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitle);
        header.add(titlePanel);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 40, 24, 40));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);

        c.gridx = 0; c.gridy = 0;
        form.add(label("Correo electrónico"), c);
        c.gridy = 1;
        styleField(emailField);
        addPlaceholder(emailField, "tu@email.com");
        form.add(emailField, c);
        c.gridy = 2;
        form.add(label("Contraseña"), c);
        c.gridy = 3;
        styleField(passField);
        addPlaceholder(passField, "Tu contraseña");
        form.add(passField, c);

        c.gridy = 4;
        c.insets = new Insets(16, 0, 5, 0);
        JButton loginBtn = primaryButton("Iniciar sesión");
        form.add(loginBtn, c);

        c.gridy = 5;
        c.insets = new Insets(4, 0, 0, 0);
        JButton registerBtn = linkButton("¿No tienes cuenta? Registrarse");
        form.add(registerBtn, c);

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> openRegister());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword());
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario user = usuarioDAO.login(email, pass);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Session.set(user);
        new MainFrame().setVisible(true);
        dispose();
    }

    private void openRegister() {
        new RegisterFrame(this).setVisible(true);
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
            new EmptyBorder(6, 10, 6, 10)
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
