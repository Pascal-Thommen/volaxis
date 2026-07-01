package edu.upa.tie.ui.panels.admin;

import edu.upa.tie.dao.OfertaDAO;
import edu.upa.tie.dao.UsuarioDAO;
import edu.upa.tie.model.Oferta;
import edu.upa.tie.model.Usuario;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ModerarPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final OfertaDAO ofertaDAO = new OfertaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private final DefaultTableModel ofertasModel;
    private final JTable ofertasTable;
    private List<Oferta> ofertas;

    private final DefaultTableModel usuariosModel;
    private final JTable usuariosTable;
    private List<Usuario> usuarios;

    public ModerarPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 247, 255));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Moderación");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 35, 90));
        title.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.6);
        split.setBorder(null);

        // Ofertas panel
        String[] ofertaCols = {"ID", "Libro", "Precio", "Condición", "Estado", "Destacada", "Vendedor"};
        ofertasModel = new DefaultTableModel(ofertaCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 5 ? Boolean.class : (c == 0 ? Integer.class : String.class); }
        };
        ofertasTable = new JTable(ofertasModel);
        styleTable(ofertasTable);

        JPanel ofertasPanel = new JPanel(new BorderLayout());
        ofertasPanel.setOpaque(false);
        JLabel ofLabel = new JLabel("Todas las ofertas");
        ofLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        ofLabel.setForeground(new Color(70, 60, 120));
        ofLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
        ofertasPanel.add(ofLabel, BorderLayout.NORTH);
        ofertasPanel.add(new JScrollPane(ofertasTable), BorderLayout.CENTER);

        JPanel ofertasBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        ofertasBtns.setOpaque(false);
        JButton toggleDestBtn = plainButton("Alternar destacada");
        JButton deleteOfertaBtn = plainButton("Eliminar oferta");
        toggleDestBtn.addActionListener(e -> toggleDestacada());
        deleteOfertaBtn.addActionListener(e -> deleteOferta());
        ofertasBtns.add(toggleDestBtn);
        ofertasBtns.add(deleteOfertaBtn);
        ofertasPanel.add(ofertasBtns, BorderLayout.SOUTH);
        split.setTopComponent(ofertasPanel);

        // Usuarios panel
        String[] usuarioCols = {"ID", "Nombre", "Correo", "WhatsApp", "Admin"};
        usuariosModel = new DefaultTableModel(usuarioCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 4 ? Boolean.class : (c == 0 ? Integer.class : String.class); }
        };
        usuariosTable = new JTable(usuariosModel);
        styleTable(usuariosTable);

        JPanel usuariosPanel = new JPanel(new BorderLayout());
        usuariosPanel.setOpaque(false);
        JLabel usLabel = new JLabel("Gestión de usuarios");
        usLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        usLabel.setForeground(new Color(70, 60, 120));
        usLabel.setBorder(new EmptyBorder(8, 0, 6, 0));
        usuariosPanel.add(usLabel, BorderLayout.NORTH);
        usuariosPanel.add(new JScrollPane(usuariosTable), BorderLayout.CENTER);

        JPanel usuariosBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        usuariosBtns.setOpaque(false);
        JButton deleteUserBtn = plainButton("Eliminar usuario");
        deleteUserBtn.addActionListener(e -> deleteUsuario());
        usuariosBtns.add(deleteUserBtn);
        usuariosPanel.add(usuariosBtns, BorderLayout.SOUTH);
        split.setBottomComponent(usuariosPanel);

        add(split, BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        ofertas = ofertaDAO.getAll();
        ofertasModel.setRowCount(0);
        for (Oferta o : ofertas) {
            ofertasModel.addRow(new Object[]{
                o.getId(), o.getLibroTitulo(),
                String.format("%.0f", o.getPrecio()),
                o.getCondicion(), o.getEstado(),
                o.isDestacada(), o.getUsuarioNombre()
            });
        }

        usuarios = usuarioDAO.getAll();
        usuariosModel.setRowCount(0);
        for (Usuario u : usuarios) {
            usuariosModel.addRow(new Object[]{
                u.getId(), u.getNombre(), u.getEmail(),
                u.getWhatsapp() != null ? u.getWhatsapp() : "",
                u.isAdmin()
            });
        }
    }

    private void toggleDestacada() {
        int row = ofertasTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona una oferta.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        Oferta o = ofertas.get(row);
        ofertaDAO.toggleDestacada(o.getId(), !o.isDestacada());
        refresh();
    }

    private void deleteOferta() {
        int row = ofertasTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona una oferta.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        Oferta o = ofertas.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la oferta de \"" + o.getLibroTitulo() + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { ofertaDAO.delete(o.getId()); refresh(); }
    }

    private void deleteUsuario() {
        int row = usuariosTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecciona un usuario.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        Usuario u = usuarios.get(row);
        if (u.isAdmin()) { JOptionPane.showMessageDialog(this, "No se puede eliminar un administrador.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al usuario \"" + u.getNombre() + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) { usuarioDAO.delete(u.getId()); refresh(); }
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setRowHeight(26);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(243, 240, 253));
        t.getTableHeader().setForeground(new Color(60, 50, 130));
        t.setSelectionBackground(new Color(220, 215, 255));
        t.setGridColor(new Color(230, 228, 250));
        t.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    private JButton plainButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
