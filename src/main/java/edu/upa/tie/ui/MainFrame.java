package edu.upa.tie.ui;

import edu.upa.tie.Session;
import edu.upa.tie.ui.panels.CatalogoPanel;
import edu.upa.tie.ui.panels.MisOfertasPanel;
import edu.upa.tie.ui.panels.PerfilPanel;
import edu.upa.tie.ui.panels.admin.GestionLibrosPanel;
import edu.upa.tie.ui.panels.admin.ModerarPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private static final Color PRIMARY = new Color(127, 119, 221);
    private static final Color SIDEBAR_BG = new Color(55, 48, 130);
    private static final Color SIDEBAR_HOVER = new Color(90, 82, 180);
    private static final Color SIDEBAR_SELECTED = PRIMARY;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(cardLayout);
    private JButton activeButton;

    public MainFrame() {
        setTitle("Volaxis");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);

        contentArea.setBackground(new Color(248, 247, 255));
        add(contentArea, BorderLayout.CENTER);

        // Register panels
        contentArea.add(new CatalogoPanel(), "catalogo");
        contentArea.add(new MisOfertasPanel(), "misOfertas");
        contentArea.add(new PerfilPanel(this), "perfil");
        if (Session.isAdmin()) {
            contentArea.add(new GestionLibrosPanel(), "libros");
            contentArea.add(new ModerarPanel(), "moderar");
        }

        cardLayout.show(contentArea, "catalogo");
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        header.setPreferredSize(new Dimension(0, 55));

        JLabel logo = new JLabel("VOLAXIS");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        header.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel userLabel = new JLabel("Bienvenido/a, " + Session.get().getNombre());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userLabel.setForeground(new Color(220, 215, 255));
        right.add(userLabel);

        JButton logoutBtn = new JButton("Cerrar sesión");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(90, 82, 170));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        right.add(logoutBtn);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        sidebar.add(navButton("Catálogo", "catalogo", sidebar));
        sidebar.add(navButton("Mis ofertas", "misOfertas", sidebar));
        sidebar.add(navButton("Mi perfil", "perfil", sidebar));

        if (Session.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(12));
            JLabel sep = new JLabel("  ADMINISTRACIÓN");
            sep.setFont(new Font("SansSerif", Font.BOLD, 10));
            sep.setForeground(new Color(160, 150, 220));
            sep.setAlignmentX(LEFT_ALIGNMENT);
            sep.setBorder(new EmptyBorder(8, 16, 4, 0));
            sidebar.add(sep);
            sidebar.add(navButton("Gestionar libros", "libros", sidebar));
            sidebar.add(navButton("Moderar", "moderar", sidebar));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton navButton(String text, String card, JPanel sidebar) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(new Color(210, 205, 255));
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(SIDEBAR_BG);
            }
        });

        btn.addActionListener(e -> {
            setActive(btn);
            cardLayout.show(contentArea, card);
            // Refresh panel on switch
            Component shown = getVisiblePanel();
            if (shown instanceof Refreshable r) r.refresh();
        });

        if (activeButton == null) {
            setActive(btn);
        }

        return btn;
    }

    private void setActive(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_BG);
            activeButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        activeButton = btn;
        btn.setBackground(SIDEBAR_SELECTED);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    private Component getVisiblePanel() {
        for (Component c : contentArea.getComponents()) {
            if (c.isVisible()) return c;
        }
        return null;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Session.clear();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    public interface Refreshable {
        void refresh();
    }
}
