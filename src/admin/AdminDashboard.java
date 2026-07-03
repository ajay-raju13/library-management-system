package admin;

import javax.swing.*;
import java.awt.*;

import ui.Theme;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        Theme.apply();

        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JLabel header = new JLabel("Library Management — Admin");
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));

        add(header, BorderLayout.NORTH);

        JPanel gridWrap = new JPanel(new GridBagLayout());
        gridWrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 24, 12));

        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(760, 320));

        JButton booksBtn = new JButton("Manage Books");
        JButton borrowersBtn = new JButton("Manage Borrowers");
        JButton categoriesBtn = new JButton("Manage Categories");
        JButton checkoutsBtn = new JButton("Manage Checkouts");

        Theme.styleButton(booksBtn);
        booksBtn.setPreferredSize(new Dimension(220, 44));
        Theme.styleButton(borrowersBtn);
        borrowersBtn.setPreferredSize(new Dimension(220, 44));
        Theme.styleButton(categoriesBtn);
        categoriesBtn.setPreferredSize(new Dimension(220, 44));
        Theme.styleButton(checkoutsBtn);
        checkoutsBtn.setPreferredSize(new Dimension(220, 44));

        panel.add(Theme.createCard("Books", booksBtn, new Color(128, 64, 224), new Color(96, 32, 200)));
        panel.add(Theme.createCard("Borrowers", borrowersBtn, new Color(16, 185, 129), new Color(4, 120, 87)));
        panel.add(Theme.createCard("Categories", categoriesBtn, new Color(37, 99, 235), new Color(30, 64, 175)));
        panel.add(Theme.createCard("Checkouts", checkoutsBtn, new Color(249, 115, 22), new Color(234, 88, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gridWrap.add(panel, gbc);
        add(gridWrap, BorderLayout.CENTER);

        // Button actions
        booksBtn.addActionListener(e -> showPanel(new BooksPanel()));
        borrowersBtn.addActionListener(e -> showPanel(new BorrowersPanel()));
        categoriesBtn.addActionListener(e -> showPanel(new CategoriesPanel()));
        checkoutsBtn.addActionListener(e -> showPanel(new CheckoutsPanel()));

        setVisible(true);
    }

    

    private void showPanel(JPanel panel) {
        JFrame frame = new JFrame();
        frame.setTitle(panel.getClass().getSimpleName());
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
