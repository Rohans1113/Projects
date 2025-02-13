import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BankingSystem {
    private static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "your_mysql_password_here");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Banking Management System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        
        frame.add(new JLabel("Welcome to Banking System", SwingConstants.CENTER));
        frame.add(registerButton);
        frame.add(loginButton);
        
        registerButton.addActionListener(e -> showRegisterForm());
        loginButton.addActionListener(e -> showLoginForm());
        
        frame.setVisible(true);
    }

    private static void showRegisterForm() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setSize(300, 200);
        registerFrame.setLayout(new GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JButton registerButton = new JButton("Register");

        registerFrame.add(new JLabel("Username:"));
        registerFrame.add(usernameField);
        registerFrame.add(new JLabel("Password:"));
        registerFrame.add(passwordField);
        registerFrame.add(registerButton);
        
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
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
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JButton loginButton = new JButton("Login");

        loginFrame.add(new JLabel("Username:"));
        loginFrame.add(usernameField);
        loginFrame.add(new JLabel("Password:"));
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
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
        dashboard.setSize(300, 200);
        dashboard.setLayout(new GridLayout(4, 1));

        JButton balanceButton = new JButton("Check Balance");
        JButton depositButton = new JButton("Deposit Money");
        JButton withdrawButton = new JButton("Withdraw Money");

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
    
    private static void updateBalance(String username, boolean isDeposit) {
        String action = isDeposit ? "Deposit" : "Withdraw";
        String input = JOptionPane.showInputDialog("Enter amount to " + action);
        if (input != null) {
            try (Connection conn = connectDB()) {
                int amount = Integer.parseInt(input);
                String sql = isDeposit ? "UPDATE users SET balance = balance + ? WHERE username = ?"
                                       : "UPDATE users SET balance = balance - ? WHERE username = ? AND balance >= ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, amount);
                stmt.setString(2, username);
                if (!isDeposit) stmt.setInt(3, amount);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, action + " successful!");
                } else {
                    JOptionPane.showMessageDialog(null, "Insufficient funds!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
