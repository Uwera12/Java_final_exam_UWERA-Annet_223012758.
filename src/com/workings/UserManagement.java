package com.workings;


import com.test.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;


    public class UserManagement extends JFrame implements ActionListener {
        private JTabbedPane TabbedPane = new JTabbedPane();

        private JPanel usersPanel = new JPanel();
        private JTable usersTable = new JTable();
        private JScrollPane usersScrollPane = new JScrollPane(usersTable);
        private JTextField searchField = new JTextField(20);
        private JButton searchBtn = new JButton(" Search User");
        private JButton refreshBtn = new JButton("Refresh");
        private JButton editBtn = new JButton("Edit");
        private JButton addUserBtn = new JButton("Add User");
        private JButton deleteBtn = new JButton(" Delete User");
        private JLabel totalUsers = new JLabel("Total Users: 0");
        private JButton returnBackBtn = new JButton(" BACK TO HOME");


        public UserManagement() {
            setTitle("GET-ONE | Admin Dashboard ");
            setSize(1100, 700);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            initUsersPanel();

            add(TabbedPane, BorderLayout.CENTER);

            refreshBtn.addActionListener(this);
            searchBtn.addActionListener(this);
            editBtn.addActionListener(this);
            deleteBtn.addActionListener(this);
            returnBackBtn.addActionListener(this);
            addUserBtn.addActionListener(this);


            loadUsers();
            setVisible(true);
        }

        private void initUsersPanel() {
            usersPanel.setLayout(new BorderLayout(10, 10));
            usersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel header = new JPanel(new BorderLayout());
            returnBackBtn.setBackground(new Color(220, 53, 69));
            returnBackBtn.setForeground(Color.WHITE);
            returnBackBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.add(returnBackBtn, BorderLayout.EAST);


            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            searchPanel.add(new JLabel("Search  User:"));
            searchPanel.add(searchField);
            searchPanel.add(searchBtn);
            searchPanel.add(refreshBtn);
            header.add(searchPanel, BorderLayout.SOUTH);

            usersPanel.add(header, BorderLayout.NORTH);

            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            actionsPanel.add(editBtn);
            actionsPanel.add(deleteBtn);
            actionsPanel.add(addUserBtn);
            actionsPanel.add(Box.createHorizontalStrut(20));
            actionsPanel.add(totalUsers);
            usersPanel.add(actionsPanel, BorderLayout.SOUTH);

            TabbedPane.addTab("Users", usersPanel);
        }


        private String hashPassword(String password) throws Exception {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }

        private void showAddUserDialog() {
            JTextField userNameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField fullNameField = new JTextField();
            JTextField passwordField = new JTextField();
            String[] roles = new String[]{"Admin", "User"};
            JComboBox<String> roleComboBox = new JComboBox<>(roles);

            Object[] fields = {
                    "username: ", userNameField,
                    "email: ", emailField,
                    "full_name: ", fullNameField,
                    "password_hash: ", passwordField,
                    "role: ", roleComboBox
            };
            int option = JOptionPane.showConfirmDialog(
                    this,
                    fields,
                    "Add new user",
                    JOptionPane.OK_CANCEL_OPTION
            );
            if (option == JOptionPane.OK_OPTION) {
                String user_name = userNameField.getText().trim();
                String email = emailField.getText().trim();
                String full_name = fullNameField.getText().trim();
                String password_hash =  passwordField.getText().trim();
                String role = roleComboBox.getSelectedItem().toString();

                if (user_name.isEmpty() || email.isEmpty() || full_name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all the fields");
                    return;
                }
                try(Connection conn = DatabaseConnection.getConnection()){
                    String hashedPassword = hashPassword(password_hash);
                    String query = "INSERT INTO users(username, password_hash, email, full_name, role, created_at) VALUES (?, ?, ?, ?, ?, Now())";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, user_name);
                    ps.setString(2, hashedPassword);
                    ps.setString(3, email);
                    ps.setString(4, full_name);
                    ps.setString(5, role);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "User has been added successfully");
                    }
                    else  {
                        JOptionPane.showMessageDialog(this, "Failed to add user");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


        private void deleteUser(int productId) {
            try(Connection conn = DatabaseConnection.getConnection()){
                String query = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, productId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete User");
            }
        }


        private void showEditUserDialog(int userId, String userName, String email, String fullName, String role) {
            JTextField userNameField = new JTextField(userName);
            JTextField emailField = new JTextField(email);
            JTextField fullNameField = new JTextField(fullName);
            String[] roles = new String[]{"Admin", "Customer", "Seller"};
            JComboBox<String> roleComboBox = new JComboBox<>(roles);
            roleComboBox.setSelectedItem(role);
            Object[] fields = {
                    "username: ", userNameField,
                    "email: ", emailField,
                    "full_name: ", fullNameField,
                    "role: ", roleComboBox
            };
            int option = JOptionPane.showConfirmDialog(this, fields,"Edit User",JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String newUsername = userNameField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newFullName = fullNameField.getText().trim();
                String newRole = roleComboBox.getSelectedItem().toString();

                if (newUsername.isEmpty() || newEmail.isEmpty() || newFullName.isEmpty()  || newRole.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all the fields");
                    return;
                }
                try(Connection conn = DatabaseConnection.getConnection()){
                    String query = "UPDATE users SET username = ?, email = ?, full_name = ?, role = ? WHERE user_id = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, newUsername);
                    ps.setString(2, newEmail);
                    ps.setString(3, newFullName);
                    ps.setString(4, newRole);
                    ps.setInt(5, userId);

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(this, "User has been updated successfully");
                    }
                    else  {
                        JOptionPane.showMessageDialog(this, "Failed to update User");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error");

                }

            }
        }
        private void editUser(int UserID,  String Username, String PasswordHash, String Email, String FullName, String Role) {
            try(Connection conn = DatabaseConnection.getConnection()){
                String query = "UPDATE users SET username = ?, password_hash = ?, email = ?, full_name = ?, role = ? WHERE user_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, Username);
                ps.setString(2, PasswordHash);
                ps.setString(3, Email);
                ps.setString(4, FullName);
                ps.setString(5, Role);
                ps.setInt(6, UserID);

                int row = ps.executeUpdate();
                if (row > 0) {
                    JOptionPane.showMessageDialog(this, " User Updated Successfully");
                    loadUsers();
                }else  {
                    JOptionPane.showMessageDialog(this, "No User was Updated");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to Update User");
            }
        }


        //loading products
        public void loadUsers() {
            // Table model & table
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"user_id", "username", "password_hash", "email", "full_name", "role", "created_at", "last_login"});
            usersTable.setModel(model);
            usersTable.setAutoCreateRowSorter(true);
            usersTable.setRowHeight(26);
            usersTable.setFillsViewportHeight(true);
            usersTable.setGridColor(Color.LIGHT_GRAY);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT user_id, username, password_hash, email, full_name, role,created_at, last_login  FROM users ORDER BY user_id ASC";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("email"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("last_login"),
                    });
                }
                // center align ProductID
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                usersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

                usersPanel.add(usersScrollPane, BorderLayout.CENTER);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, " Failed to load User: " + ex.getMessage());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();

            //adding product to the list
            if (src == addUserBtn) {
                showAddUserDialog();
            }
            // this if where i refreshed my products
            else if (src == refreshBtn) {
                loadUsers();

                //progress bar before refreshing confirmation message message
                JProgressBar progressBar = new JProgressBar(0,100);
                progressBar.setStringPainted(true);
                JOptionPane pane = new JOptionPane(progressBar,JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION,null,new Object[]{},null);
                JDialog dialog = pane.createDialog("Refreshing Processing....");
                new Thread(() -> {
                    for(int i = 0; i < 100; i++){
                        progressBar.setValue(i);
                        try{Thread.sleep(30);}
                        catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }
                    dialog.dispose();
                }).start();
                dialog.setVisible(true);
                JOptionPane.showMessageDialog(this, "Now your Users are refreshed well!");
            }
            // condition to search a product
            else if (src == searchBtn) {
                String search = searchField.getText().trim();
                if (search.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a user name!");
                    return;
                }
                searchField.setText(search);
            } else if (src == editBtn) {
                int row = usersTable.getSelectedRow();
                if (row ==-1) {
                    JOptionPane.showMessageDialog(this, "Please select a user to edit!");
                    return;
                }
                int userId = (int)usersTable.getValueAt(row, 0);
                String username = (String)usersTable.getValueAt(row, 1);
                String passwordHash = (String)usersTable.getValueAt(row, 2);
                String email = (String)usersTable.getValueAt(row, 3);
                String fullName = (String)usersTable.getValueAt(row, 4);
                String role = (String)usersTable.getValueAt(row, 5);
                showEditUserDialog(userId, username, email, fullName, role);
            }
            else if (src == deleteBtn) {
                int row = usersTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a User to be deleted!");
                    return;
                }
                int productID = (int) usersTable.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this User?");
                if (confirm == JOptionPane.YES_OPTION){
                    deleteUser(productID);
                    loadUsers();
                    JOptionPane.showMessageDialog(this, "User deleted successfully !");
                }}
            else if (src == returnBackBtn) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?");
                if (confirm == JOptionPane.YES_OPTION){
                    dispose();
                    new AdminDashboard("Admin").setVisible(true);

                }
            }

        }

        private void deleteBtn(int productID) {
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(UserManagement::new);
        }
    }

