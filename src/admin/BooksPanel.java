package admin;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import ui.Theme;

public class BooksPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, removeBtn, refreshBtn, editBtn;

    public BooksPanel() {
        setLayout(new BorderLayout(8, 8));

        // Table model
        model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Total Copies", "Available Copies", "Category"}, 0);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        Theme.styleTable(table);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(6, 182, 212));
        table.setSelectionForeground(Color.white);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground((Color) UIManager.get("Panel.background"));

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        addBtn = new JButton("Add Book");
        editBtn = new JButton("Edit Book");
        removeBtn = new JButton("Remove Book");
        refreshBtn = new JButton("Refresh");

        Theme.styleButton(addBtn);
        Theme.styleButton(editBtn);
        Theme.styleButton(removeBtn);
        Theme.styleButton(refreshBtn);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(refreshBtn);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setOpaque(false);
        center.add(scroll, BorderLayout.CENTER);
        center.add(btnPanel, BorderLayout.SOUTH);

        add(Theme.createCard("Books Management", center, new Color(40, 20, 120), new Color(120, 60, 220)), BorderLayout.CENTER);

        // Load data
        SwingUtilities.invokeLater(this::loadBooks);

        // Button actions
        addBtn.addActionListener(e -> addBook());
        removeBtn.addActionListener(e -> removeBook());
        refreshBtn.addActionListener(e -> loadBooks());
        editBtn.addActionListener(e -> editBook());
    }

    private void loadBooks() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT b.book_id, b.title, b.author, b.total_copies, b.available_copies, c.c_name FROM books b JOIN category c ON b.cid=c.cid")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"),
                        rs.getString("c_name")
                });
            }
        } catch (SQLException ex) {
            Theme.showMessage(this, "Error loading books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setOpaque(false);
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField totalField = new JTextField();
        JTextField availField = new JTextField();
        JTextField cidField = new JTextField();
        form.add(new JLabel("Title:")); form.add(titleField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Total Copies:")); form.add(totalField);
        form.add(new JLabel("Available Copies:")); form.add(availField);
        form.add(new JLabel("Category ID:")); form.add(cidField);

        int option = Theme.showFormDialog(this, "Add Book", form);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO books(title, author, total_copies, available_copies, cid) VALUES(?,?,?,?,?)")) {
                ps.setString(1, titleField.getText());
                ps.setString(2, authorField.getText());
                ps.setInt(3, Integer.parseInt(totalField.getText()));
                ps.setInt(4, Integer.parseInt(availField.getText()));
                ps.setInt(5, Integer.parseInt(cidField.getText()));
                ps.executeUpdate();
                Theme.showMessage(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int bookId = (int) model.getValueAt(selectedRow, 0);
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE book_id=?")) {
                ps.setInt(1, bookId);
                ps.executeUpdate();
                Theme.showMessage(this, "Book removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error removing book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Theme.showMessage(this, "Please select a book to remove.", "Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            Theme.showMessage(this, "Please select a book to edit.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int bookId = (int) model.getValueAt(selectedRow, 0);
        String title = (String) model.getValueAt(selectedRow, 1);
        String author = (String) model.getValueAt(selectedRow, 2);
        int totalCopies = (int) model.getValueAt(selectedRow, 3);
        int availableCopies = (int) model.getValueAt(selectedRow, 4);

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setOpaque(false);
        JTextField titleField = new JTextField(title);
        JTextField authorField = new JTextField(author);
        JTextField totalField = new JTextField(String.valueOf(totalCopies));
        JTextField availField = new JTextField(String.valueOf(availableCopies));
        form.add(new JLabel("Title:")); form.add(titleField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Total Copies:")); form.add(totalField);
        form.add(new JLabel("Available Copies:")); form.add(availField);

        int option = Theme.showFormDialog(this, "Edit Book", form);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE books SET title=?, author=?, total_copies=?, available_copies=? WHERE book_id=?")) {
                ps.setString(1, titleField.getText());
                ps.setString(2, authorField.getText());
                ps.setInt(3, Integer.parseInt(totalField.getText()));
                ps.setInt(4, Integer.parseInt(availField.getText()));
                ps.setInt(5, bookId);
                ps.executeUpdate();
                Theme.showMessage(this, "Book details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks();
            } catch (SQLException ex) {
                Theme.showMessage(this, "Error updating book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Books Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(new BooksPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
