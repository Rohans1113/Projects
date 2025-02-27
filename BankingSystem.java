import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BankingSystem {
    private static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Banking Management System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to Banking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        frame.add(buttonPanel, BorderLayout.CENTER);
        
        frame.getContentPane().setBackground(new Color(173, 216, 230));
        buttonPanel.setBackground(new Color(224, 255, 255));
        registerButton.setBackground(new Color(60, 179, 113));
        loginButton.setBackground(new Color(30, 144, 255));
        registerButton.setForeground(Color.WHITE);
        loginButton.setForeground(Color.WHITE);
        
        registerButton.addActionListener(e -> showRegisterForm());
        loginButton.addActionListener(e -> showLoginForm());
        
        frame.setVisible(true);
    }

    private static void showRegisterForm() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setSize(350, 250);
        registerFrame.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");

        registerFrame.add(new JLabel("Username:"));
        registerFrame.add(usernameField);
        registerFrame.add(new JLabel("Password:"));
        registerFrame.add(passwordField);
        registerFrame.add(registerButton);
        
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try (Connection conn = connectDB()) {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, balance) VALUES (?, ?, 0)");
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(registerFrame, "Registration Successful!");
                registerFrame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        registerFrame.setVisible(true);
    }

    private static void showLoginForm() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(350, 250);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(new JLabel("Username:"));
        loginFrame.add(usernameField);
        loginFrame.add(new JLabel("Password:"));
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try (Connection conn = connectDB()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(loginFrame, "Login Successful!");
                    showAccountDashboard(username);
                    loginFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid Credentials!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        loginFrame.setVisible(true);
    }

    private static void showAccountDashboard(String username) {
        JFrame dashboard = new JFrame("Dashboard");
        dashboard.setSize(350, 300);
        dashboard.setLayout(new GridLayout(4, 1, 10, 10));

        JButton balanceButton = new JButton("Check Balance");
        JButton depositButton = new JButton("Deposit Money");
        JButton withdrawButton = new JButton("Withdraw Money");
        
        balanceButton.setFont(new Font("Arial", Font.BOLD, 14));
        depositButton.setFont(new Font("Arial", Font.BOLD, 14));
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        dashboard.add(balanceButton);
        dashboard.add(depositButton);
        dashboard.add(withdrawButton);
        
        balanceButton.addActionListener(e -> {
            try (Connection conn = connectDB()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM users WHERE username = ?");
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(dashboard, "Balance: " + rs.getInt("balance"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        depositButton.addActionListener(e -> updateBalance(username, true));
        withdrawButton.addActionListener(e -> updateBalance(username, false));
        
        dashboard.setVisible(true);
    }
}