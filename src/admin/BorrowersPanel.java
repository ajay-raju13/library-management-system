package admin;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import ui.Theme;

public class BorrowersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, removeBtn, editBtn, refreshBtn;

    public BorrowersPanel() {
        setLayout(new BorderLayout(8, 8));

        // Table model
        model = new DefaultTableModel(
                new String[]{"Borrower ID", "Name", "Current Fine", "Contact"}, 0
        );
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        Theme.styleTable(table);
        table.setSelectionBackground(new Color(6, 182, 212));
        table.setSelectionForeground(Color.white);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground((Color) UIManager.get("Panel.background"));

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        addBtn = new JButton("Add Borrower");
        removeBtn = new JButton("Remove Borrower");
        editBtn = new JButton("Edit Borrower");
        refreshBtn = new JButton("Refresh");

        Theme.styleButton(addBtn);
        Theme.styleButton(removeBtn);
        Theme.styleButton(editBtn);
        Theme.styleButton(refreshBtn);

        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(editBtn);
        btnPanel.add(refreshBtn);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setOpaque(false);
        center.add(scrollPane, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add(Theme.createCard("Borrowers", center, new Color(3, 105, 161), new Color(6, 182, 129)), BorderLayout.CENTER);

        // Load table
        SwingUtilities.invokeLater(this::loadBorrowers);

        // Button actions
        addBtn.addActionListener(e -> addBorrower());
        removeBtn.addActionListener(e -> removeBorrower());
        editBtn.addActionListener(e -> editBorrower());
        refreshBtn.addActionListener(e -> loadBorrowers());
    }

    private void loadBorrowers() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT borrow_id, name, current_fine, contact FROM borrower")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("borrow_id"),
                        rs.getString("name"),
                        rs.getFloat("current_fine"),
                        rs.getLong("contact")
                });
            }
        } catch (SQLException ex) {
            Theme.showMessage(this, "Error loading borrowers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBorrower() {
        JPanel form = new JPanel(new GridLayout(3,2,8,8));
        form.setOpaque(false);
        JTextField nameField = new JTextField();
        JTextField fineField = new JTextField("0");
        JTextField contactField = new JTextField();
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Current Fine:")); form.add(fineField);
        form.add(new JLabel("Contact:")); form.add(contactField);

        int option = Theme.showFormDialog(this, "Add Borrower", form);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO borrower(name, current_fine, contact) VALUES(?,?,?)"
                 )) {
                ps.setString(1, nameField.getText());
                ps.setFloat(2, Float.parseFloat(fineField.getText()));
                ps.setLong(3, Long.parseLong(contactField.getText()));
                ps.executeUpdate();
                Theme.showMessage(this, "Borrower added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowers();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error adding borrower: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeBorrower() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int borrowId = (int) model.getValueAt(selectedRow, 0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM borrower WHERE borrow_id=?")) {
                ps.setInt(1, borrowId);
                ps.executeUpdate();
                Theme.showMessage(this, "Borrower removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowers();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error removing borrower: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Theme.showMessage(this, "Please select a borrower to remove.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editBorrower() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int borrowId = (int) model.getValueAt(selectedRow, 0);
            String currentName = (String) model.getValueAt(selectedRow, 1);
            float currentFine = (float) model.getValueAt(selectedRow, 2);
            long currentContact = (long) model.getValueAt(selectedRow, 3);

                JPanel form = new JPanel(new GridLayout(3,2,8,8));
                form.setOpaque(false);
                JTextField nameField = new JTextField(currentName);
                JTextField fineField = new JTextField(String.valueOf(currentFine));
                JTextField contactField = new JTextField(String.valueOf(currentContact));
                form.add(new JLabel("Name:")); form.add(nameField);
                form.add(new JLabel("Current Fine:")); form.add(fineField);
                form.add(new JLabel("Contact:")); form.add(contactField);

                int option = Theme.showFormDialog(this, "Edit Borrower", form);
                if (option == JOptionPane.OK_OPTION) {
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "UPDATE borrower SET name=?, current_fine=?, contact=? WHERE borrow_id=?"
                     )) {
                    ps.setString(1, nameField.getText());
                    ps.setFloat(2, Float.parseFloat(fineField.getText()));
                    ps.setLong(3, Long.parseLong(contactField.getText()));
                    ps.setInt(4, borrowId);
                    ps.executeUpdate();
                    Theme.showMessage(this, "Borrower updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBorrowers();
                } catch (SQLException ex) {
                    Theme.showMessage(this, "Error updating borrower: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            Theme.showMessage(this, "Please select a borrower to edit.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Test standalone
    public static void main(String[] args) {
        JFrame frame = new JFrame("Borrowers Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new BorrowersPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
