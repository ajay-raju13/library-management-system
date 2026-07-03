package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {

    // Apply a dark, modern theme using UIManager defaults.
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Color bg = new Color(12, 18, 28);           // dark background
        Color panelBg = new Color(15, 23, 34);
        Color cardBg = new Color(22, 28, 40);
        Color primary = new Color(6, 182, 212);     // cyan accent
        Color muted = new Color(148, 163, 184);
        Color text = new Color(235, 238, 241);

        Font base = new Font("Segoe UI", Font.PLAIN, 14);
        Font strong = base.deriveFont(Font.BOLD, 14f);

        UIManager.put("Panel.background", panelBg);
        UIManager.put("Viewport.background", panelBg);
        UIManager.put("Button.background", primary);
        UIManager.put("Button.foreground", Color.white);
        UIManager.put("Label.foreground", text);
        UIManager.put("TextField.background", cardBg);
        UIManager.put("TextField.foreground", text);
        UIManager.put("PasswordField.background", cardBg);
        UIManager.put("PasswordField.foreground", text);
        UIManager.put("Table.selectionBackground", primary);
        UIManager.put("Table.selectionForeground", Color.white);

        UIManager.put("Label.font", base);
        UIManager.put("Button.font", strong);
        UIManager.put("TextField.font", base);
        UIManager.put("PasswordField.font", base);
        UIManager.put("Table.font", base);
    }

    public static void styleButton(AbstractButton b) {
        b.setOpaque(true);
        b.setBackground((Color) UIManager.get("Button.background"));
        b.setForeground((Color) UIManager.get("Button.foreground"));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);

        // Hover and press effects
        Color base = (Color) UIManager.get("Button.background");
        Color hover = base.brighter();
        Color pressed = base.darker();

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                b.setBackground(base);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                b.setBackground(pressed);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                b.setBackground(hover);
            }
        });
    }

    // Create a rounded gradient card panel suitable for dashboard tiles.
    public static JPanel createCard(String title, JComponent center, Color start, Color end) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, start, w, h, end);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel t = new JLabel(title);
        t.setForeground(Color.white);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(t, BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(center, BorderLayout.CENTER);
        card.add(centerWrap, BorderLayout.CENTER);

        return card;
    }

    // Style JTable for dark theme visibility
    public static void styleTable(JTable table) {
        table.setBackground(new Color(18, 24, 34));
        table.setForeground(new Color(230, 235, 238));
        table.setGridColor(new Color(40, 48, 60));
        table.setShowGrid(true);
        table.setRowHeight(26);
        if (table.getTableHeader() != null) {
            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(10, 20, 30));
            header.setForeground(new Color(180, 200, 220));
            header.setReorderingAllowed(false);
            header.setOpaque(true);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 48, 60)));

            DefaultTableCellRenderer hdr = new DefaultTableCellRenderer();
            hdr.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
            hdr.setBackground(header.getBackground());
            hdr.setForeground(header.getForeground());
            hdr.setFont(header.getFont().deriveFont(Font.BOLD));
            hdr.setBorder(new EmptyBorder(6, 12, 6, 12));
            header.setDefaultRenderer(hdr);
        }
    }

    // Themed message dialog (info/error)
    public static void showMessage(Component parent, String message, String title, int messageType) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(parent), title == null ? "Message" : title, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBackground((Color) UIManager.get("Panel.background"));
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // Icon selection
        Icon icon = null;
        try {
            switch (messageType) {
                case JOptionPane.ERROR_MESSAGE:
                    icon = UIManager.getIcon("OptionPane.errorIcon"); break;
                case JOptionPane.INFORMATION_MESSAGE:
                    icon = UIManager.getIcon("OptionPane.informationIcon"); break;
                case JOptionPane.WARNING_MESSAGE:
                    icon = UIManager.getIcon("OptionPane.warningIcon"); break;
                case JOptionPane.QUESTION_MESSAGE:
                    icon = UIManager.getIcon("OptionPane.questionIcon"); break;
                default:
                    icon = UIManager.getIcon("OptionPane.informationIcon");
            }
        } catch (Exception ignored) { }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JLabel msg = new JLabel("<html>" + message.replaceAll("\n", "<br>") + "</html>");
        msg.setForeground((Color) UIManager.get("Label.foreground"));
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(msg, BorderLayout.CENTER);

        JPanel mid = new JPanel(new BorderLayout(12,12));
        mid.setOpaque(false);
        mid.add(iconLabel, BorderLayout.WEST);
        mid.add(center, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        styleButton(ok);
        ok.setPreferredSize(new Dimension(90, 34));
        ok.addActionListener(e -> d.dispose());

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(ok);

        content.add(mid, BorderLayout.CENTER);
        content.add(south, BorderLayout.SOUTH);

        d.setContentPane(content);
        d.getRootPane().setDefaultButton(ok);
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }

    public static void showMessage(Component parent, String message) {
        showMessage(parent, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    // Show a themed form dialog with OK/Cancel buttons. Returns JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
    public static int showFormDialog(Component parent, String title, JComponent form) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(parent), title == null ? "" : title, Dialog.ModalityType.APPLICATION_MODAL);
        JPanel content = new JPanel(new BorderLayout(12,12));
        content.setBackground((Color) UIManager.get("Panel.background"));
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(form, BorderLayout.CENTER);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        styleButton(ok);
        styleButton(cancel);
        final int[] result = {JOptionPane.CANCEL_OPTION};
        ok.addActionListener(e -> { result[0] = JOptionPane.OK_OPTION; d.dispose(); });
        cancel.addActionListener(e -> { result[0] = JOptionPane.CANCEL_OPTION; d.dispose(); });

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(ok);
        south.add(cancel);

        content.add(center, BorderLayout.CENTER);
        content.add(south, BorderLayout.SOUTH);

        d.setContentPane(content);
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
        return result[0];
    }

    // Simple themed input dialog that returns null on cancel
    public static String showInputDialog(Component parent, String title, String initial) {
        JPanel p = new JPanel(new BorderLayout(6,6));
        p.setOpaque(false);
        JTextField tf = new JTextField(initial == null ? "" : initial, 24);
        p.add(tf, BorderLayout.CENTER);
        int res = showFormDialog(parent, title, p);
        if (res == JOptionPane.OK_OPTION) return tf.getText();
        return null;
    }

}
