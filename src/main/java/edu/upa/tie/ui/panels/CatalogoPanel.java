package edu.upa.tie.ui.panels;

import edu.upa.tie.dao.LibroDAO;
import edu.upa.tie.dao.OfertaDAO;
import edu.upa.tie.model.Libro;
import edu.upa.tie.model.Oferta;
import edu.upa.tie.ui.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogoPanel extends JPanel implements MainFrame.Refreshable {

    private static final Color PRIMARY = new Color(127, 119, 221);

    private final OfertaDAO ofertaDAO = new OfertaDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    private final CardLayout pagesLayout = new CardLayout();
    private final JPanel pages = new JPanel(pagesLayout);

    private final JPanel booksPage = new JPanel(new BorderLayout(0, 12));
    private final JPanel bookGrid = new JPanel(new GridLayout(0, 3, 12, 12));
    private final JScrollPane bookScroll = new JScrollPane(bookGrid);

    private final JPanel offersPage = new JPanel(new BorderLayout(0, 12));
    private final JPanel offersListPanel = new JPanel();
    private final JScrollPane offersScroll = new JScrollPane(offersListPanel);
    private final JLabel offersTitle = new JLabel();

    private final JComboBox<String> estadoFilter = new JComboBox<>(new String[]{"Todos", "Disponible", "Reservado"});
    private final JComboBox<String> condicionFilter = new JComboBox<>(new String[]{"Todas", "Nuevo", "Bueno", "Regular", "Deteriorado"});
    private final JComboBox<String> precioFilter = new JComboBox<>(new String[]{"Sin ordenar", "Precio ↑", "Precio ↓"});

    private List<Oferta> currentOfertas = new ArrayList<>();
    private Libro currentLibro;

    public CatalogoPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(new Color(248, 247, 255));
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Catálogo de libros");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(40, 35, 90));
        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JPanel buildContent() {
        pages.setOpaque(false);

        booksPage.setOpaque(false);
        bookGrid.setOpaque(false);
        bookScroll.setBorder(BorderFactory.createEmptyBorder());
        bookScroll.getViewport().setBackground(new Color(248, 247, 255));
        booksPage.add(bookScroll, BorderLayout.CENTER);

        offersPage.setOpaque(false);
        offersListPanel.setOpaque(false);
        offersListPanel.setLayout(new BoxLayout(offersListPanel, BoxLayout.Y_AXIS));
        offersScroll.setBorder(BorderFactory.createEmptyBorder());
        offersScroll.getViewport().setBackground(new Color(248, 247, 255));
        offersPage.add(buildOffersBar(), BorderLayout.NORTH);
        offersPage.add(offersScroll, BorderLayout.CENTER);

        pages.add(booksPage, "books");
        pages.add(offersPage, "offers");
        pagesLayout.show(pages, "books");

        return pages;
    }

    private JPanel buildOffersBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setOpaque(false);

        JButton backBtn = plainButton("← Volver a libros");
        backBtn.addActionListener(e -> showBooksPage());
        bar.add(backBtn, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filters.setOpaque(false);
        filters.add(smallLabel("Estado:"));
        estadoFilter.addActionListener(e -> refreshOffers());
        filters.add(estadoFilter);
        filters.add(smallLabel("Condición:"));
        condicionFilter.addActionListener(e -> refreshOffers());
        filters.add(condicionFilter);
        filters.add(smallLabel("Precio:"));
        precioFilter.addActionListener(e -> refreshOffers());
        filters.add(precioFilter);

        bar.add(filters, BorderLayout.EAST);
        return bar;
    }

    @Override
    public void refresh() {
        renderBookGrid();
        showBooksPage();
    }

    private void renderBookGrid() {
        bookGrid.removeAll();

        Map<Integer, List<Oferta>> ofertasPorLibro = new LinkedHashMap<>();
        Map<Integer, Libro> librosPorId = new LinkedHashMap<>();

        List<Oferta> visible = ofertaDAO.getVisibleOffers("Todos", "Todas", "Sin ordenar");
        for (Oferta oferta : visible) {
            ofertasPorLibro.computeIfAbsent(oferta.getLibroId(), k -> new ArrayList<>()).add(oferta);
            if (!librosPorId.containsKey(oferta.getLibroId())) {
                Libro libro = libroDAO.getById(oferta.getLibroId());
                if (libro != null) {
                    librosPorId.put(libro.getId(), libro);
                }
            }
        }

        List<Libro> libros = new ArrayList<>(librosPorId.values());
        libros.sort((a, b) -> {
            int cmp = Boolean.compare(b.isEstandarizado(), a.isEstandarizado());
            if (cmp != 0) {
                return cmp;
            }
            return a.getTitulo().compareToIgnoreCase(b.getTitulo());
        });

        for (Libro libro : libros) {
            bookGrid.add(buildBookCard(libro, ofertasPorLibro.getOrDefault(libro.getId(), new ArrayList<>())));
        }

        if (libros.isEmpty()) {
            bookGrid.add(emptyLabel("No hay libros con ofertas visibles."));
        }

        int columns = 3;
        int rows = Math.max(1, (libros.size() + columns - 1) / columns);
        bookGrid.setLayout(new GridLayout(rows, columns, 12, 12));
        bookGrid.revalidate();
        bookGrid.repaint();
    }

    private JPanel buildBookCard(Libro libro, List<Oferta> libroOfertas) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 240), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel title = new JLabel(libro.getTitulo());
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(35, 30, 90));

        JLabel fach = new JLabel("Materia: " + libro.getFach());
        fach.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fach.setForeground(new Color(100, 95, 130));

        String desc = libro.getDescripcion() != null && !libro.getDescripcion().isBlank()
            ? libro.getDescripcion() : "Sin descripción.";
        JLabel description = new JLabel(String.format("<html><body style='width:220px'>%s</body></html>", desc));
        description.setFont(new Font("SansSerif", Font.PLAIN, 12));
        description.setForeground(new Color(90, 85, 120));

        JLabel isbn = new JLabel("ISBN: " + (libro.getIsbn() != null ? libro.getIsbn() : "--"));
        isbn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        isbn.setForeground(new Color(90, 85, 120));

        JLabel stats = new JLabel(String.format("Ofertas: %d", libroOfertas.size()));
        stats.setFont(new Font("SansSerif", Font.BOLD, 12));
        stats.setForeground(new Color(70, 55, 110));

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        meta.add(fach);
        meta.add(stats);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(title);
        center.add(Box.createVerticalStrut(6));
        center.add(meta);
        center.add(Box.createVerticalStrut(8));
        center.add(description);
        center.add(Box.createVerticalStrut(8));
        center.add(isbn);

        card.add(center, BorderLayout.CENTER);

        JButton viewBtn = accentButton("Ver ofertas");
        viewBtn.addActionListener(e -> openOffersPage(libro));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(viewBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private void openOffersPage(Libro libro) {
        currentLibro = libro;
        offersTitle.setText(String.format("Ofertas para: %s", libro.getTitulo()));
        refreshOffers();
        pagesLayout.show(pages, "offers");
    }

    private void showBooksPage() {
        currentLibro = null;
        pagesLayout.show(pages, "books");
    }

    private void refreshOffers() {
        if (currentLibro == null) {
            showBooksPage();
            return;
        }
        currentOfertas = ofertaDAO.getVisibleOffersByLibro(
            currentLibro.getId(),
            (String) estadoFilter.getSelectedItem(),
            (String) condicionFilter.getSelectedItem(),
            (String) precioFilter.getSelectedItem()
        );
        renderOffersList();
    }

    private void renderOffersList() {
        offersListPanel.removeAll();

        offersTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        offersTitle.setForeground(new Color(35, 30, 90));
        offersTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        offersListPanel.add(offersTitle);

        if (currentOfertas.isEmpty()) {
            offersListPanel.add(emptyLabel("No hay ofertas visibles para este libro."));
        } else {
            for (Oferta oferta : currentOfertas) {
                offersListPanel.add(buildOfferLine(oferta));
                offersListPanel.add(Box.createVerticalStrut(10));
            }
        }

        offersListPanel.revalidate();
        offersListPanel.repaint();
    }

    private JPanel buildOfferLine(Oferta oferta) {
        JPanel line = new JPanel(new BorderLayout(10, 10));
        line.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 240), 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        line.setBackground("Reservado".equalsIgnoreCase(oferta.getEstado()) ? new Color(245, 245, 245) : Color.WHITE);

        JLabel info = new JLabel(String.format("<html><b>Precio:</b> %.0f<br/><b>Condición:</b> %s<br/><b>Estado:</b> %s<br/><b>Vendedor:</b> %s</html>",
            oferta.getPrecio(), oferta.getCondicion(), oferta.getEstado(), oferta.getUsuarioNombre()));
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setForeground(new Color(50, 45, 100));

        JButton detailsBtn = accentButton("Ver detalles");
        detailsBtn.addActionListener(e -> openOfferPopup(oferta));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(detailsBtn);

        line.add(info, BorderLayout.CENTER);
        line.add(right, BorderLayout.EAST);

        return line;
    }

    private void openOfferPopup(Oferta oferta) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Detalle de oferta", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(Color.WHITE);

        String whatsapp = oferta.getUsuarioWhatsapp() != null && !oferta.getUsuarioWhatsapp().isBlank()
            ? oferta.getUsuarioWhatsapp() : "No disponible";
        String descripcion = currentLibro.getDescripcion() != null && !currentLibro.getDescripcion().isBlank()
            ? currentLibro.getDescripcion() : "Sin descripción.";

        content.add(new JLabel("Título: " + currentLibro.getTitulo()));
        content.add(Box.createVerticalStrut(8));
        content.add(new JLabel("Materia: " + currentLibro.getFach()));
        content.add(Box.createVerticalStrut(8));
        content.add(new JLabel("ISBN: " + (currentLibro.getIsbn() != null ? currentLibro.getIsbn() : "--")));
        content.add(Box.createVerticalStrut(8));
        content.add(new JLabel("Descripción:"));
        JLabel descLabel = new JLabel(String.format("<html><body style='width:360px'>%s</body></html>", descripcion));
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(85, 80, 110));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(new JLabel(String.format("Precio: %.0f", oferta.getPrecio())));
        content.add(Box.createVerticalStrut(6));
        content.add(new JLabel("Condición: " + oferta.getCondicion()));
        content.add(Box.createVerticalStrut(6));
        content.add(new JLabel("Estado: " + oferta.getEstado()));
        content.add(Box.createVerticalStrut(6));
        content.add(new JLabel("Vendedor: " + oferta.getUsuarioNombre()));
        content.add(Box.createVerticalStrut(6));
        content.add(new JLabel("WhatsApp: " + whatsapp));
        content.add(Box.createVerticalStrut(16));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton reserveBtn = accentButton("Reservar");
        reserveBtn.setEnabled("Disponible".equalsIgnoreCase(oferta.getEstado()));
        reserveBtn.addActionListener(e -> {
            if ("Disponible".equalsIgnoreCase(oferta.getEstado())) {
                ofertaDAO.reservar(oferta.getId());
                JOptionPane.showMessageDialog(dlg, "La oferta fue reservada.", "Reserva", JOptionPane.INFORMATION_MESSAGE);
                refreshOffers();
                dlg.dispose();
            }
        });
        JButton contactBtn = accentButton("Contactar vendedor");
        contactBtn.addActionListener(e -> {
            if (oferta.getUsuarioWhatsapp() == null || oferta.getUsuarioWhatsapp().isBlank()) {
                JOptionPane.showMessageDialog(dlg, "El vendedor no registró un número de WhatsApp.", "Sin contacto", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            try {
                String numero = oferta.getUsuarioWhatsapp().replaceAll("[^0-9]", "");
                String texto = "Hola, me interesa tu oferta del libro: " + currentLibro.getTitulo();
                String url = "https://wa.me/" + numero + "?text=" + java.net.URLEncoder.encode(texto, "UTF-8");
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "No se pudo abrir WhatsApp: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttons.add(reserveBtn);
        buttons.add(contactBtn);

        content.add(buttons);

        dlg.setContentPane(content);
        dlg.setVisible(true);
    }

    private JLabel emptyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(new Color(110, 110, 140));
        return label;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(new Color(80, 70, 120));
        return l;
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
