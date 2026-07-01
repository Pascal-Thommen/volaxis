package edu.upa.tie.ui.panels.admin;

import edu.upa.tie.dao.LibroDAO;
import edu.upa.tie.model.Libro;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionLibrosPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final LibroDAO libroDAO = new LibroDAO();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Libro> libros;

    public GestionLibrosPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 247, 255));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildToolbar(), BorderLayout.NORTH);

        String[] cols = {"ID", "Título", "Materia", "ISBN", "Estandarizado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 4 ? Boolean.class : (c == 0 ? Integer.class : String.class); }
        };
        table = new JTable(tableModel);
        styleTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActionBar(), BorderLayout.SOUTH);
        refresh();
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel title = new JLabel("Gestionar libros");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 35, 90));
        bar.add(title);
        return bar;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setOpaque(false);
        JButton newBtn = accentButton("+ Nuevo libro");
        JButton editBtn = plainButton("Editar");
        JButton deleteBtn = plainButton("Eliminar");
        newBtn.addActionListener(e -> showDialog(null));
        editBtn.addActionListener(e -> { Libro l = getSelected(); if (l != null) showDialog(l); });
        deleteBtn.addActionListener(e -> deleteSelected());
        bar.add(newBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        return bar;
    }

    @Override
    public void refresh() {
        libros = libroDAO.getAll();
        tableModel.setRowCount(0);
        for (Libro l : libros) {
            tableModel.addRow(new Object[]{l.getId(), l.getTitulo(), l.getFach(), l.getIsbn(), l.isEstandarizado()});
        }
    }

    private Libro getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return libros.get(row);
    }

    private void deleteSelected() {
        Libro l = getSelected();
        if (l == null) return;
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar \"" + l.getTitulo() + "\"? (Solo si no tiene ofertas asociadas)",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                libroDAO.delete(l.getId());
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "No se puede eliminar: el libro tiene ofertas asociadas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDialog(Libro libro) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            libro == null ? "Nuevo libro" : "Editar libro", true);
        dlg.setSize(400, 310);
        dlg.setLocationRelativeTo(this);

        JTextField tituloF = new JTextField(libro != null ? libro.getTitulo() : "");
        JTextField descripF = new JTextField(libro != null && libro.getDescripcion() != null ? libro.getDescripcion() : "");
        JTextField isbnF = new JTextField(libro != null && libro.getIsbn() != null ? libro.getIsbn() : "");
        JTextField fachF = new JTextField(libro != null && libro.getFach() != null ? libro.getFach() : "");
        JCheckBox estandarizadoCheck = new JCheckBox("Estandarizado", libro != null && libro.isEstandarizado());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 4, 5, 4);

        Object[][] rows = {
            {"Título *", tituloF}, {"Descripción", descripF},
            {"ISBN", isbnF}, {"Materia", fachF}, {null, estandarizadoCheck}
        };
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0] != null) {
                c.gridx = 0; c.gridy = i; c.weightx = 0.35;
                form.add(new JLabel((String) rows[i][0]), c);
            }
            c.gridx = 1; c.gridy = i; c.weightx = 0.65;
            form.add((Component) rows[i][1], c);
        }

        JButton saveBtn = accentButton(libro == null ? "Crear" : "Guardar");
        saveBtn.addActionListener(e -> {
            if (tituloF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El título es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Libro l = libro != null ? libro : new Libro();
            l.setTitulo(tituloF.getText().trim());
            l.setDescripcion(descripF.getText().trim());
            l.setIsbn(isbnF.getText().trim());
            l.setFach(fachF.getText().trim());
            l.setEstandarizado(estandarizadoCheck.isSelected());
            if (libro == null) libroDAO.insert(l); else libroDAO.update(l);
            refresh();
            dlg.dispose();
        });

        JButton cancelBtn = plainButton("Cancelar");
        cancelBtn.addActionListener(ev -> dlg.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);
        bottom.add(cancelBtn);

        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void styleTable() {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(243, 240, 253));
        table.getTableHeader().setForeground(new Color(60, 50, 130));
        table.setSelectionBackground(new Color(220, 215, 255));
        table.setGridColor(new Color(230, 228, 250));
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(4).setMaxWidth(100);
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
