package admin;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import db.DBConnection;
import ui.Theme;

public class AdminLogin extends JFrame {

    public AdminLogin() {
        Theme.apply();

        setTitle("Admin Login - Library Management");
        setSize(480, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLbl = new JLabel("Username:");
        JTextField userField = new JTextField(16);
        JLabel passLbl = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(16);
        JButton loginButton = new JButton("Login");
        ui.Theme.styleButton(loginButton);

        gbc.gridx = 0; gbc.gridy = 0; form.add(userLbl, gbc);
        gbc.gridx = 1; form.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(passLbl, gbc);
        gbc.gridx = 1; form.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; form.add(loginButton, gbc);

        JPanel card = ui.Theme.createCard("Admin Sign In", form, new Color(8, 30, 52), new Color(6, 42, 85));
        card.setPreferredSize(new Dimension(420, 220));
        add(card);

        loginButton.addActionListener(e -> handleLogin(userField.getText(), String.valueOf(passField.getPassword())));

        setVisible(true);
    }

    private void handleLogin(String username, String password) {
            if(username.isEmpty() || password.isEmpty()) {
                Theme.showMessage(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    Theme.showMessage(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new AdminDashboard();
                } else {
                    Theme.showMessage(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
        } catch(Exception ex) {
                Theme.showMessage(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::new);
    }
}
