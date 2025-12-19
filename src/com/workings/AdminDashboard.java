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

    private JPanel usersPanel, sellersPanel, productsPanel, ordersPanel, paymentsPanel, shipmentsPanel, reportsPanel, settingsPanel;
    private JTable usersTable, sellersTable, productsTable, ordersTable, paymentsTable, shipmentsTable;

    private JButton logoutButton, addProductButton, deleteProductButton;

    // In-memory storage for seller statuses
    private HashMap<Integer, String> sellerStatusMap = new HashMap<>();

    public AdminDashboard(String fullname) {
        setTitle("ECOMMERCE PLATFORM SYSTEM | Admin Dashboard");
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        // Header
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

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Initialize Panels
        initUsersPanel();
        initSellersPanel();
        initProductsPanel();
        initOrdersPanel();
        initPaymentsPanel();
        initShipmentsPanel();
        initReportsPanel();
        initSettingsPanel();

        // Add Tabs
        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Sellers", sellersPanel);
        tabbedPane.addTab("Products", productsPanel);
        tabbedPane.addTab("Orders", ordersPanel);
        tabbedPane.addTab("Payments", paymentsPanel);
        tabbedPane.addTab("Shipments", shipmentsPanel);
        tabbedPane.addTab("Reports", reportsPanel);
        tabbedPane.addTab("Settings", settingsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Load Data
        loadUsersTable();
        loadSellersTable();
        loadProductsTable();
        loadOrdersTable();
        loadPaymentsTable();
        loadShipmentsTable();
    }

    // -------------------- Users Panel --------------------
    private void initUsersPanel() {
        usersPanel = new JPanel(new BorderLayout(10, 10));
        usersTable = new JTable();
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
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

    // -------------------- Sellers Panel --------------------
    private void initSellersPanel() {
        sellersPanel = new JPanel(new BorderLayout(10, 10));
        sellersTable = new JTable();
        sellersPanel.add(new JScrollPane(sellersTable), BorderLayout.CENTER);

        // Table model
        DefaultTableModel model = new DefaultTableModel(new Object[]{"User ID", "Full Name", "Email", "Status", "Created At", "Last Login"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Status editable
            }
        };
        sellersTable.setModel(model);

        // Save Statuses Button
        JButton saveStatusesButton = new JButton("Save Statuses");
        saveStatusesButton.addActionListener(e -> saveSellerStatuses());

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveStatusesButton);
        sellersPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadSellersTable() {
        DefaultTableModel model = (DefaultTableModel) sellersTable.getModel();
        model.setRowCount(0); // Clear existing data

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id, full_name, email, created_at, last_login FROM users WHERE role='seller' ORDER BY user_id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String status = sellerStatusMap.getOrDefault(userId, "Pending"); // load from memory
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

    // -------------------- Save Seller Statuses --------------------
    private void saveSellerStatuses() {
        DefaultTableModel model = (DefaultTableModel) sellersTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int userId = (int) model.getValueAt(i, 0);
            String status = (String) model.getValueAt(i, 3);
            sellerStatusMap.put(userId, status); // Save in memory
        }
        JOptionPane.showMessageDialog(this, "Seller statuses saved successfully!");
    }

    // -------------------- Products Panel --------------------
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

    // -------------------- Orders Panel --------------------
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

    // -------------------- Payments Panel --------------------
    private void initPaymentsPanel() {
        paymentsPanel = new JPanel(new BorderLayout(10, 10));
        paymentsTable = new JTable();
        paymentsPanel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
    }

    private void loadPaymentsTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"Payment ID", "Order ID", "Amount", "Status", "Created At"});
        paymentsTable.setModel(model);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM payments ORDER BY payment_id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }
    }

    // -------------------- Shipments Panel --------------------
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

    // -------------------- Reports Panel --------------------
    private void initReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.add(new JLabel("Reports & Analytics (placeholder)", JLabel.CENTER), BorderLayout.CENTER);
    }

    // -------------------- Settings Panel --------------------
    private void initSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.add(new JLabel("Platform Settings (placeholder)", JLabel.CENTER), BorderLayout.CENTER);
    }

    // -------------------- Action Handling --------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == logoutButton) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?");
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new Elogin().setVisible(true);
            }
        } else if (src == addProductButton) {
            addProductDialog();
        } else if (src == deleteProductButton) {
            deleteProduct();
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

    // -------------------- Main --------------------
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
