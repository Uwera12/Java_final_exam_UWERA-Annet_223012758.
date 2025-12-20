package com.form;

import com.test.DisplayNames;
import com.test.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class Elogin extends JFrame implements ActionListener {

    JLabel header = new JLabel(" ECOMMERCE PLATFORM ");
    JLabel username = new JLabel("username:");
    JTextField usernameTxt = new JTextField();

    JLabel password = new JLabel("password_hash");
    JPasswordField passwordTxt = new JPasswordField();

    JButton login = new JButton("Login");
    JButton register = new JButton("Register");
    JCheckBox check = new JCheckBox("Show me password");
    private char defaultEchoChar;
    JLabel footer = new JLabel("---E-Commerce Platform System--");


    public Elogin() {
        setTitle("GET-ONE!!");
        setBounds(100, 100, 450, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        setResizable(false);

        header.setBounds(0, 10, 450, 50);
        header.setFont(new Font("Arial", Font.BOLD, 30));
        header.setForeground(new Color(255, 255, 255));
        header.setBackground(new Color(0, 75, 125));
        header.setOpaque(true);
        header.setHorizontalAlignment(JLabel.CENTER);

        username.setBounds(50, 70, 100, 30);
        usernameTxt.setBounds(170, 70, 150, 30);

        password.setBounds(50, 120, 100, 30);
        passwordTxt.setBounds(170, 120, 150, 30);

        login.setBounds(50, 190, 100, 30);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        register.setBounds(170, 190, 100, 30);
        register.setCursor(new Cursor(Cursor.HAND_CURSOR));
        check.setBounds(170, 160, 200, 30);
        check.setBackground(getBackground());
        defaultEchoChar = passwordTxt.getEchoChar();

        footer.setBounds(100, 230, 450, 50);
        footer.setForeground(new Color(10, 117, 2));

        add(header); add(username); add(usernameTxt); add(password); add(passwordTxt);
        add(login); add(register); add(footer); add(check);

        login.addActionListener(this);
        register.addActionListener(this);
        check.addActionListener(this);
        this.add(login); this.add(username); this.add(password); this.add(register);

    }

    private String hashPassword(String password) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == check) {
            if (check.isSelected()) {
                passwordTxt.setEchoChar((char) 0);
                check.setText("Hide me password");
            }
            else {
                passwordTxt.setEchoChar(defaultEchoChar);
                check.setText("Show me password");
                check.setForeground(Color.BLACK);
            }
            return;
        }

        if (e.getSource() == register) {
            dispose();
            new Registration();

        }

        else if (e.getSource() == login) {
            String user = usernameTxt.getText();
            String password = new String(passwordTxt.getPassword());

            try (Connection conn = DatabaseConnection.getConnection()){
                String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
                var ps = conn.prepareStatement(sql);

                ps.setString(1, user);
                ps.setString(2, password);
                String hashedPassword = hashPassword(password);
                ps.setString(2, hashedPassword);

                var rs = ps.executeQuery();
                if (rs.next()){
                    JOptionPane.showMessageDialog(null, " Login successful!\nWelcome ! "+ rs.getString("full_name"));

                    com.test.DisplayNames.CurrentUserFullName = rs.getString("full_name");
                    DisplayNames.CurrentUserId = rs.getInt("user_id");


                    String updateSql =   "UPDATE users SET last_login = NOW() WHERE user_id = ?";
                    var updatePs = conn.prepareStatement(updateSql);
                    updatePs.setInt(1, rs.getInt("user_id"));
                    updatePs.executeUpdate();

                    dispose();


                    if ("Admin".equalsIgnoreCase(rs.getString("role"))) {
                        new com.workings.AdminDashboard(rs.getString("full_name")).setVisible(true);

                    } else if ("Seller".equalsIgnoreCase(rs.getString("role"))) {
                        int userId = rs.getInt("user_id");
                        new com.workings.SellerDashboard("", userId);

                    } else if ("Customer".equalsIgnoreCase(rs.getString("role"))) {
                        new com.workings.CustomerDashboard();

                    }

                }
                else{
                    JOptionPane.showMessageDialog(null, "Invalid username or password!");
                }
            }catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, " Database error: " + ex.getMessage());
            }
        }
        setLocationRelativeTo(null);

    }public static void main(String[] arg){
        new Elogin();
    }
}
