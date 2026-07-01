package edu.upa.tie.ui.panels;

import edu.upa.tie.dao.OfertaDAO;
import edu.upa.tie.model.Oferta;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;
import java.util.List;

public class CatalogoPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final OfertaDAO ofertaDAO = new OfertaDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField = new JTextField(20);
    private final JTextField fachField = new JTextField(15);
    private List<Oferta> ofertas;

    public CatalogoPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 247, 255));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildToolbar(), BorderLayout.NORTH);

        String[] cols = {"★", "Título", "Materia", "Precio", "Condición", "Estado", "Vendedor", "WhatsApp"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        };
        table = new JTable(tableModel);
        styleTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(buildContactBar(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Catálogo de ofertas");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 35, 90));
        bar.add(title);

        bar.add(Box.createHorizontalStrut(20));
        bar.add(smallLabel("Buscar:"));
        styleField(searchField);
        bar.add(searchField);
        bar.add(smallLabel("Materia:"));
        styleField(fachField);
        bar.add(fachField);

        JButton searchBtn = accentButton("Buscar");
        searchBtn.addActionListener(e -> refresh());
        bar.add(searchBtn);

        JButton clearBtn = plainButton("Limpiar");
        clearBtn.addActionListener(e -> { searchField.setText(""); fachField.setText(""); refresh(); });
        bar.add(clearBtn);

        return bar;
    }

    private JPanel buildContactBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setOpaque(false);
        JButton waBtn = accentButton("Contactar por WhatsApp");
        waBtn.addActionListener(e -> contactWhatsApp());
        bar.add(waBtn);
        bar.add(smallLabel("  Selecciona una oferta y haz clic para contactar al vendedor."));
        return bar;
    }

    @Override
    public void refresh() {
        String titulo = searchField.getText().trim();
        String fach = fachField.getText().trim();
        ofertas = ofertaDAO.search(titulo, fach);
        tableModel.setRowCount(0);
        for (Oferta o : ofertas) {
            tableModel.addRow(new Object[]{
                o.isDestacada(),
                o.getLibroTitulo(),
                o.getLibroFach(),
                String.format("%.0f", o.getPrecio()),
                o.getCondicion(),
                o.getEstado(),
                o.getUsuarioNombre(),
                o.getUsuarioWhatsapp() != null ? o.getUsuarioWhatsapp() : "(sin WhatsApp)"
            });
        }
    }

    private void contactWhatsApp() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una oferta primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Oferta o = ofertas.get(row);
        if (o.getUsuarioWhatsapp() == null || o.getUsuarioWhatsapp().isBlank()) {
            JOptionPane.showMessageDialog(this, "El vendedor no registró número de WhatsApp.", "Sin contacto", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String numero = o.getUsuarioWhatsapp().replaceAll("[^0-9]", "");
        String texto = "Hola, me interesa tu oferta del libro: " + o.getLibroTitulo();
        try {
            String url = "https://wa.me/" + numero + "?text=" + java.net.URLEncoder.encode(texto, "UTF-8");
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir WhatsApp: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable() {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(243, 240, 253));
        table.getTableHeader().setForeground(new Color(60, 50, 130));
        table.setSelectionBackground(new Color(220, 215, 255));
        table.setGridColor(new Color(230, 228, 250));
        table.getColumnModel().getColumn(0).setMaxWidth(32);
        table.getColumnModel().getColumn(3).setMaxWidth(80);
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(new Color(80, 70, 120));
        return l;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 196, 240), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton plainButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
