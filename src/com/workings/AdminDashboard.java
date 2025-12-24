package com.workings;

import com.form.Elogin;
import com.test.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class AdminDashboard extends JFrame implements ActionListener {

    private JLabel welcomeLabel;
    private JTabbedPane tabbedPane;
    private JTextField searchUserField;


    private JPanel usersPanel, sellersPanel, productsPanel, ordersPanel, paymentsPanel, shipmentsPanel, reportsPanel, settingsPanel;
    private JTable usersTable, sellersTable, productsTable, ordersTable, paymentsTable, shipmentsTable;

    private JButton logoutButton, searchUserButton, addUserBtn,updateUserBtn,deleteUserBtn,refreshUserBtn, addProductButton, deleteProductButton;

    private HashMap<Integer, String> sellerStatusMap = new HashMap<>();

    public AdminDashboard(String fullname) {
        setTitle("ECOMMERCE PLATFORM SYSTEM | Admin Dashboard");
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        header.setBackground(new Color(245, 245, 245));

        JLabel platformLabel = new JLabel(" GET-ONE | ADMIN ");
        platformLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        welcomeLabel = new JLabel("Welcome, " + fullname);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(25, 96, 225));

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(245, 245, 245));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(this);

        header.add(platformLabel);
        header.add(welcomeLabel);
        header.add(logoutButton);
        add(header, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));

        initUsersPanel();
        initSellersPanel();
        initProductsPanel();
        initOrdersPanel();
        initPaymentsPanel();
        initShipmentsPanel();
        initReportsPanel();
        initSettingsPanel();

        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Sellers", sellersPanel);
        tabbedPane.addTab("Products", productsPanel);
        tabbedPane.addTab("Orders", ordersPanel);
        tabbedPane.addTab("Payments", paymentsPanel);
        tabbedPane.addTab("Shipments", shipmentsPanel);
        tabbedPane.addTab("Reports", reportsPanel);
        tabbedPane.addTab("Settings", settingsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        loadUsersTable();
        loadSellersTable();
        loadProductsTable();
        loadOrdersTable();
        loadPaymentsTable();
        loadShipmentsTable();

    }
    private void initUsersPanel() {
        usersPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchUserField = new JTextField(20);
        searchUserButton = new JButton("Search");
        searchUserButton.addActionListener(this); 
        searchPanel.add(new JLabel("Search User: "));
        searchPanel.add(searchUserField);
        searchPanel.add(searchUserButton);

        usersTable = new JTable();
        usersPanel.add(searchPanel, BorderLayout.NORTH);
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        addUserBtn = new JButton("Add User");
        updateUserBtn = new JButton("Update User");
        deleteUserBtn = new JButton("Delete User");
        refreshUserBtn = new JButton("Refresh");

        addUserBtn.addActionListener(this);
        updateUserBtn.addActionListener(this);
        deleteUserBtn.addActionListener(this);
        refreshUserBtn.addActionListener(this);

        btnPanel.add(addUserBtn);
        btnPanel.add(updateUserBtn);
        btnPanel.add(deleteUserBtn);
        btnPanel.add(refreshUserBtn);

        usersPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadUsersTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"User ID", "Username", "Email", "Full Name", "Role", "Created At", "Last Login"});
        usersTable.setModel(model);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id, username, email, full_name, role, created_at, last_login FROM users ORDER BY user_id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("last_login")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Users: " + e.getMessage());
        }
    }
    private void addUser() {
        JTextField username = new JTextField();
        JTextField email = new JTextField();
        JTextField fullname = new JTextField();
        JComboBox<String> role = new JComboBox<>(new String[]{"admin", "seller", "customer"});
        JPasswordField password = new JPasswordField();

        Object[] fields = {
                "Username:", username,
                "Email:", email,
                "Full Name:", fullname,
                "Role:", role,
                "Password:", password
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO users (username,email,full_name,role,password_hash) VALUES (?,?,?,?,SHA2(?,256))"
                );
                pst.setString(1, username.getText());
                pst.setString(2, email.getText());
                pst.setString(3, fullname.getText());
                pst.setString(4, role.getSelectedItem().toString());
                pst.setString(5, new String(password.getPassword()));
                pst.executeUpdate();
                loadUsersTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void updateUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) return;

        int userId = (int) usersTable.getValueAt(row, 0);
        JTextField email = new JTextField(usersTable.getValueAt(row, 2).toString());
        JTextField fullname = new JTextField(usersTable.getValueAt(row, 3).toString());
        JComboBox<String> role = new JComboBox<>(new String[]{"admin", "seller", "customer"});
        role.setSelectedItem(usersTable.getValueAt(row, 4).toString());

        Object[] fields = {"Email:", email, "Full Name:", fullname, "Role:", role};

        if (JOptionPane.showConfirmDialog(this, fields, "Update User", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pst = conn.prepareStatement(
                        "UPDATE users SET email=?, full_name=?, role=? WHERE user_id=?"
                );
                pst.setString(1, email.getText());
                pst.setString(2, fullname.getText());
                pst.setString(3, role.getSelectedItem().toString());
                pst.setInt(4, userId);
                pst.executeUpdate();
                loadUsersTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) return;

        int userId = (int) usersTable.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this user?") == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE user_id=?");
                pst.setInt(1, userId);
                pst.executeUpdate();
                loadUsersTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void searchUsers(String keyword) {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"User ID", "Username", "Email", "Full Name", "Role", "Created At", "Last Login"});
        usersTable.setModel(model);

        if (keyword.isEmpty()) {
            loadUsersTable();
            return;
        }

        String sql = "SELECT user_id, username, email, full_name, role, created_at, last_login " +
                "FROM users " +
                "WHERE username LIKE ? OR full_name LIKE ? OR email LIKE ? " +
                "ORDER BY user_id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("last_login")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to search users: " + e.getMessage());
        }
    }

    private void initSellersPanel() {
        sellersPanel = new JPanel(new BorderLayout(10, 10));
        sellersTable = new JTable();
        sellersPanel.add(new JScrollPane(sellersTable), BorderLayout.CENTER);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"User ID", "Full Name", "Email", "Status", "Created At", "Last Login"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        sellersTable.setModel(model);

        JButton saveStatusesButton = new JButton("Save Statuses");
        saveStatusesButton.addActionListener(e -> saveSellerStatuses());

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveStatusesButton);
        sellersPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadSellersTable() {
        DefaultTableModel model = (DefaultTableModel) sellersTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id, full_name, email, created_at, last_login FROM users WHERE role='seller' ORDER BY user_id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String status = sellerStatusMap.getOrDefault(userId, "Pending");
                model.addRow(new Object[]{
                        userId,
                        rs.getString("full_name"),
                        rs.getString("email"),
                        status,
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("last_login")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load Sellers: " + e.getMessage());
        }
    }

    private void saveSellerStatuses() {
        DefaultTableModel model = (DefaultTableModel) sellersTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int userId = (int) model.getValueAt(i, 0);
            String status = (String) model.getValueAt(i, 3);
            sellerStatusMap.put(userId, status);
        }
        JOptionPane.showMessageDialog(this, "Seller statuses saved successfully!");
    }

    private void initProductsPanel() {
        productsPanel = new JPanel(new BorderLayout(10, 10));
        productsTable = new JTable();
        productsPanel.add(new JScrollPane(productsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        addProductButton = new JButton("Add Product");
        deleteProductButton = new JButton("Delete Product");
        addProductButton.addActionListener(this);
        deleteProductButton.addActionListener(this);

        btnPanel.add(addProductButton);
        btnPanel.add(deleteProductButton);
        productsPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadProductsTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Product ID", "Name", "Description", "Price", "Category ID", "Stock", "Seller ID", "Status", "Created At"});
        productsTable.setModel(model);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products ORDER BY product_id DESC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("category_id"),
                        rs.getInt("stock"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void initOrdersPanel() {
        ordersPanel = new JPanel(new BorderLayout(10, 10));
        ordersTable = new JTable();
        ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
    }

    private void loadOrdersTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{ "Order ID", "User ID", "Order Number", "Date", "Status", "Total Amount", "Payment Method"});
        ordersTable.setModel(model);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM orders ORDER BY order_id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getString("order_number"),
                        rs.getTimestamp("date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void initPaymentsPanel() {
        paymentsPanel = new JPanel(new BorderLayout(10, 10));
        paymentsTable = new JTable();
        paymentsPanel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
    }

    private void loadPaymentsTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "Payment ID",
                "Order ID",
                "Order Number",
                "User ID",
                "Amount",
                "Payment Method",
                "Status",
                "Date"
        });
        paymentsTable.setModel(model);

        String sql = """
    SELECT
        pay.payment_id,
        o.order_id,
        o.order_number,
        o.user_id,
        pay.amount,
        pay.type AS payment_method,
        pay.status,
        pay.date
    FROM payments pay
    JOIN order_payments op ON pay.payment_id = op.payment_id
    JOIN orders o ON op.order_id = o.order_id
    ORDER BY pay.payment_id DESC
""";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("order_id"),
                        rs.getString("order_number"),
                        rs.getInt("user_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getTimestamp("date")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }
    }

    private void initShipmentsPanel() {
        shipmentsPanel = new JPanel(new BorderLayout(10, 10));
        shipmentsTable = new JTable();
        shipmentsPanel.add(new JScrollPane(shipmentsTable), BorderLayout.CENTER);
    }

    private void loadShipmentsTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Shipment ID", "Order ID", "Status", "Created At"});
        shipmentsTable.setModel(model);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM shipments ORDER BY shipment_id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("shipment_id"),
                        rs.getInt("order_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading shipments: " + e.getMessage());
        }
    }

    private void initReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.add(new JLabel("Reports & Analytics (placeholder)", JLabel.CENTER), BorderLayout.CENTER);
    }

    private void initSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.add(new JLabel("Platform Settings (placeholder)", JLabel.CENTER), BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == logoutButton) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?");
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Elogin().setVisible(true);
            }
        } else if (src == searchUserButton) {
            searchUsers(searchUserField.getText().trim());
        }
        else if (src == addProductButton) {
            addProductDialog();
        } else if (src == deleteProductButton) {
            deleteProduct();
        }else if (src == addUserBtn){
            addUser();
        } else if (src == updateUserBtn) {
            updateUser();
        } else if (src == deleteUserBtn) {
            deleteUser();
        }else if (src == refreshUserBtn){
            loadUsersTable();
        }
    }

    private void addProductDialog() {
        JTextField name = new JTextField();
        JTextField desc = new JTextField();
        JTextField price = new JTextField();
        JTextField categoryId = new JTextField();
        JTextField stock = new JTextField();
        JTextField userId = new JTextField();

        Object[] fields = {
                "Name:", name,
                "Description:", desc,
                "Price:", price,
                "Category ID:", categoryId,
                "Stock: ", stock,
                "Seller ID:", userId
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO products (name, description, price, category_id, stock, user_id, status) VALUES (?, ?, ?, ?, ?, ?, 'active')";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, name.getText());
                pst.setString(2, desc.getText());
                pst.setBigDecimal(3, new java.math.BigDecimal(price.getText()));
                pst.setInt(4, Integer.parseInt(categoryId.getText()));
                pst.setInt(5, Integer.parseInt(stock.getText()));
                pst.setInt(6, Integer.parseInt(userId.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                loadProductsTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void deleteProduct() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) productsTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected product?");
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM products WHERE product_id=?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, productId);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                    loadProductsTable();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a product first!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String adminFullN = getFirstAdminFullName();
            if (adminFullN == null) adminFullN = "";
            AdminDashboard dashboard = new AdminDashboard(adminFullN);
            dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }

    public static String getFirstAdminFullName() {
        String fullName = "";
        String sql = "SELECT full_name FROM users WHERE role = 'admin' ORDER BY user_id ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                fullName = rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullName;
    }
}
