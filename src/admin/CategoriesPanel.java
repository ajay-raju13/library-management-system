package admin;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import ui.Theme;

public class CategoriesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, removeBtn, refreshBtn;

    public CategoriesPanel() {
        setLayout(new BorderLayout(8, 8));

        model = new DefaultTableModel(new String[]{"Category ID", "Category Name"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        Theme.styleTable(table);
        table.setSelectionBackground(new Color(6, 182, 212));
        table.setSelectionForeground(Color.white);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground((Color) UIManager.get("Panel.background"));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        addBtn = new JButton("Add Category");
        removeBtn = new JButton("Remove Category");
        refreshBtn = new JButton("Refresh");

        Theme.styleButton(addBtn);
        Theme.styleButton(removeBtn);
        Theme.styleButton(refreshBtn);

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(refreshBtn);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setOpaque(false);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add(Theme.createCard("Categories", center, new Color(20, 60, 200), new Color(40, 120, 245)), BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadCategories);

        addBtn.addActionListener(e -> addCategory());
        removeBtn.addActionListener(e -> removeCategory());
        refreshBtn.addActionListener(e -> loadCategories());
    }

    private void loadCategories() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM category")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("cid"),
                        rs.getString("c_name")
                });
            }
        } catch (SQLException ex) {
            Theme.showMessage(this, "Error loading categories: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCategory() {
        String name = Theme.showInputDialog(this, "Enter category name:", "");
        if (name != null && !name.isEmpty()) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO category(c_name) VALUES(?)")) {
                ps.setString(1, name);
                ps.executeUpdate();
                Theme.showMessage(this, "Category added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCategories();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error adding category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeCategory() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) model.getValueAt(row, 0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement psCheck = con.prepareStatement("SELECT * FROM books WHERE cid=?");
                 PreparedStatement psDel = con.prepareStatement("DELETE FROM category WHERE cid=?")) {
                psCheck.setInt(1, id);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    Theme.showMessage(this, "Cannot remove category with books.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                psDel.setInt(1, id);
                psDel.executeUpdate();
                Theme.showMessage(this, "Category removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCategories();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error removing category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Theme.showMessage(this, "Select a category first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
