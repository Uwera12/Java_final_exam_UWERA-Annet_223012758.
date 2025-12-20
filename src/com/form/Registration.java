package com.form;

import com.test.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Registration extends JFrame implements ActionListener {

    JLabel header = new JLabel(" Ecommerce | SignUp");
    JLabel username = new JLabel("Username");
    JTextField usernameTxt = new JTextField();

    JLabel lblPassword = new JLabel("Password:");
    JPasswordField passwordTxt = new JPasswordField();

    JLabel email = new JLabel("Email");
    JTextField emailTxt = new JTextField();

    JLabel fullname = new JLabel("Full name:");
    JTextField fullnameTxt = new JTextField();

    JLabel role = new JLabel("Select Role:");
    String[] roles = {"Seller", "Customer"};
    JComboBox<String> roleCombo = new JComboBox<>(roles);

    JButton login = new JButton("Login");
    JButton register = new JButton("Register");
    JLabel footer = new JLabel(" @ GET-ONE| Register Now!");

    public Registration() {
        setTitle(" GET-ONE ");
        setBounds(100, 100, 450, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        header.setBounds(100, 10, 450, 50);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));

        username.setBounds(50, 70, 100, 30);
        usernameTxt.setBounds(170, 70, 150, 30);

        lblPassword.setBounds(50, 120, 100, 30);
        passwordTxt.setBounds(170, 120, 150, 30);

        email.setBounds(50, 190, 100, 30);
        emailTxt.setBounds(170, 190, 150, 30);

        fullname.setBounds(50, 230, 100, 30);
        fullnameTxt.setBounds(170, 230, 150, 30);

        role.setBounds(50, 270, 100, 30);
        roleCombo.setBounds(170, 270, 150, 30);

        login.setBounds(50, 320, 100, 30);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.setBounds(170, 320, 100, 30);
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footer.setBounds(60, 360, 400, 20);
        footer.setForeground(new Color(190, 117, 2));

        add(header);
        add(username);
        add(usernameTxt);
        add(lblPassword);
        add(passwordTxt);
        add(email);
        add(emailTxt);
        add(fullname);
        add(fullnameTxt);
        add(role);
        add(roleCombo);
        add(login);
        add(register);
        add(footer);

        login.addActionListener(this);
        register.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == login) {
            dispose();

        } else if (e.getSource() == register) {
            String user = usernameTxt.getText().trim();
            String hashedPassword = hashPassword(new String(passwordTxt.getPassword()));
            String emailVal = emailTxt.getText().trim();
            String fullN = fullnameTxt.getText().trim();
            String rol = (String) roleCombo.getSelectedItem();

            if (user.isEmpty() || hashedPassword.isEmpty() || fullN.isEmpty() || rol == null || rol.isEmpty()) {
                JOptionPane.showMessageDialog(this, " Please fill all the required fields");
                return;
            }

            if (!emailVal.isEmpty() && !emailVal.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
                JOptionPane.showMessageDialog(this, " Invalid email format");
                return;
            }
            
            String sql = "INSERT INTO users (username, password_hash, email, full_name, role) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, user);
                ps.setString(2, hashedPassword);
                ps.setString(3, emailVal);
                ps.setString(4, fullN);
                ps.setString(5, rol);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, " Registration successful! Please login.");
                    dispose();
                    new Elogin();
                } else {
                    JOptionPane.showMessageDialog(this, " Registration failed");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "  Database error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        new Registration();
    }
}
