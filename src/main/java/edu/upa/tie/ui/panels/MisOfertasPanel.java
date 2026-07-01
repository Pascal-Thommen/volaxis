package edu.upa.tie.ui.panels;

import edu.upa.tie.Session;
import edu.upa.tie.dao.*;
import edu.upa.tie.model.*;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MisOfertasPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final OfertaDAO ofertaDAO = new OfertaDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();
    private final CondicionDAO condicionDAO = new CondicionDAO();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Oferta> ofertas;

    public MisOfertasPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 247, 255));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildToolbar(), BorderLayout.NORTH);

        String[] cols = {"ID", "Libro", "Materia", "Precio", "Condición", "Estado", "Destacada"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 6 ? Boolean.class : (c == 0 ? Integer.class : String.class); }
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
        JLabel title = new JLabel("Mis ofertas");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 35, 90));
        bar.add(title);
        return bar;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setOpaque(false);
        JButton newBtn = accentButton("+ Nueva oferta");
        JButton editBtn = plainButton("Editar");
        JButton deleteBtn = plainButton("Eliminar");

        newBtn.addActionListener(e -> showOfertaDialog(null));
        editBtn.addActionListener(e -> {
            Oferta o = getSelected();
            if (o != null) showOfertaDialog(o);
        });
        deleteBtn.addActionListener(e -> deleteSelected());

        bar.add(newBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        return bar;
    }

    @Override
    public void refresh() {
        ofertas = ofertaDAO.getByUsuario(Session.get().getId());
        tableModel.setRowCount(0);
        for (Oferta o : ofertas) {
            tableModel.addRow(new Object[]{
                o.getId(),
                o.getLibroTitulo(),
                o.getLibroFach(),
                String.format("%.0f", o.getPrecio()),
                o.getCondicion(),
                o.getEstado(),
                o.isDestacada()
            });
        }
    }

    private Oferta getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una oferta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return ofertas.get(row);
    }

    private void deleteSelected() {
        Oferta o = getSelected();
        if (o == null) return;
        int ok = JOptionPane.showConfirmDialog(this,
            "¿Eliminar la oferta de \"" + o.getLibroTitulo() + "\"?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            ofertaDAO.delete(o.getId());
            refresh();
        }
    }

    private void showOfertaDialog(Oferta oferta) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            oferta == null ? "Nueva oferta" : "Editar oferta", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);

        List<Libro> libros = libroDAO.getAll();
        List<Estado> estados = estadoDAO.getAll();
        List<Condicion> condiciones = condicionDAO.getAll();

        JComboBox<Libro> libroBox = new JComboBox<>(libros.toArray(new Libro[0]));
        JTextField precioField = new JTextField();
        JComboBox<Estado> estadoBox = new JComboBox<>(estados.toArray(new Estado[0]));
        JComboBox<Condicion> condicionBox = new JComboBox<>(condiciones.toArray(new Condicion[0]));
        JCheckBox destacadaCheck = new JCheckBox("Marcar como destacada");
        JTextField fotoField = new JTextField();

        if (oferta != null) {
            libros.stream().filter(l -> l.getId() == oferta.getLibroId()).findFirst().ifPresent(libroBox::setSelectedItem);
            precioField.setText(String.format("%.0f", oferta.getPrecio()));
            estados.stream().filter(e -> e.getId() == oferta.getEstadoId()).findFirst().ifPresent(estadoBox::setSelectedItem);
            condiciones.stream().filter(c -> c.getId() == oferta.getCondicionId()).findFirst().ifPresent(condicionBox::setSelectedItem);
            destacadaCheck.setSelected(oferta.isDestacada());
            fotoField.setText(oferta.getFoto() != null ? oferta.getFoto() : "");
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);

        Object[][] rows = {
            {"Libro", libroBox},
            {"Precio", precioField},
            {"Estado", estadoBox},
            {"Condición", condicionBox},
            {"Foto (ruta)", fotoField},
            {null, destacadaCheck}
        };
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0] != null) {
                c.gridx = 0; c.gridy = i; c.weightx = 0.3;
                form.add(new JLabel((String) rows[i][0]), c);
            }
            c.gridx = 1; c.gridy = i; c.weightx = 0.7;
            form.add((Component) rows[i][1], c);
        }

        JButton nuevoLibroBtn = new JButton("+ Crear nuevo libro");
        nuevoLibroBtn.setForeground(PRIMARY);
        nuevoLibroBtn.setBorderPainted(false);
        nuevoLibroBtn.setContentAreaFilled(false);
        nuevoLibroBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nuevoLibroBtn.addActionListener(e -> {
            Libro nuevo = showLibroDialog(dlg);
            if (nuevo != null) {
                libroBox.addItem(nuevo);
                libroBox.setSelectedItem(nuevo);
            }
        });
        c.gridx = 1; c.gridy = rows.length; c.weightx = 0.7;
        form.add(nuevoLibroBtn, c);

        JButton saveBtn = accentButton(oferta == null ? "Publicar oferta" : "Guardar cambios");
        saveBtn.addActionListener(e -> {
            Libro libroSel = (Libro) libroBox.getSelectedItem();
            String precioTxt = precioField.getText().trim();
            if (libroSel == null || precioTxt.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Libro y precio son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            float precio;
            try { precio = Float.parseFloat(precioTxt); } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "El precio debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Oferta o = oferta != null ? oferta : new Oferta();
            o.setLibroId(libroSel.getId());
            o.setPrecio(precio);
            o.setEstadoId(((Estado) estadoBox.getSelectedItem()).getId());
            o.setCondicionId(((Condicion) condicionBox.getSelectedItem()).getId());
            o.setDestacada(destacadaCheck.isSelected());
            String foto = fotoField.getText().trim();
            o.setFoto(foto.isEmpty() ? null : foto);
            if (oferta == null) {
                o.setUsuarioId(Session.get().getId());
                ofertaDAO.insert(o);
            } else {
                ofertaDAO.update(o);
            }
            refresh();
            dlg.dispose();
        });

        JButton cancelBtn1 = plainButton("Cancelar");
        cancelBtn1.addActionListener(e -> dlg.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);
        bottom.add(cancelBtn1);

        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private Libro showLibroDialog(JDialog parent) {
        JDialog dlg = new JDialog(parent, "Nuevo libro", true);
        dlg.setSize(400, 320);
        dlg.setLocationRelativeTo(parent);

        JTextField tituloF = new JTextField();
        JTextField descripF = new JTextField();
        JTextField isbnF = new JTextField();
        JTextField fachF = new JTextField();
        JCheckBox estandarizadoCheck = new JCheckBox("Libro estandarizado");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);

        Object[][] rows = {
            {"Título *", tituloF},
            {"Descripción", descripF},
            {"ISBN", isbnF},
            {"Materia", fachF},
            {null, estandarizadoCheck}
        };
        for (int i = 0; i < rows.length; i++) {
            if (rows[i][0] != null) {
                c.gridx = 0; c.gridy = i; c.weightx = 0.35;
                form.add(new JLabel((String) rows[i][0]), c);
            }
            c.gridx = 1; c.gridy = i; c.weightx = 0.65;
            form.add((Component) rows[i][1], c);
        }

        Libro[] result = {null};
        JButton saveBtn = accentButton("Crear libro");
        saveBtn.addActionListener(e -> {
            if (tituloF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El título es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Libro l = new Libro();
            l.setTitulo(tituloF.getText().trim());
            l.setDescripcion(descripF.getText().trim());
            l.setIsbn(isbnF.getText().trim());
            l.setFach(fachF.getText().trim());
            l.setEstandarizado(estandarizadoCheck.isSelected());
            int id = libroDAO.insert(l);
            l.setId(id);
            result[0] = l;
            dlg.dispose();
        });

        JButton cancelBtn2 = plainButton("Cancelar");
        cancelBtn2.addActionListener(e -> dlg.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(saveBtn);
        bottom.add(cancelBtn2);

        dlg.setLayout(new BorderLayout());
        dlg.add(form, BorderLayout.CENTER);
        dlg.add(bottom, BorderLayout.SOUTH);
        dlg.setVisible(true);
        return result[0];
    }

    private void styleTable() {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(243, 240, 253));
        table.getTableHeader().setForeground(new Color(60, 50, 130));
        table.setSelectionBackground(new Color(220, 215, 255));
        table.setGridColor(new Color(230, 228, 250));
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(6).setMaxWidth(80);
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
