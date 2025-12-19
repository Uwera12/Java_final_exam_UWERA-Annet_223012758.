package com.workings;

import com.Image.backgroundwork;
import com.form.Elogin;
import com.test.DatabaseConnection;
import com.test.DisplayNames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;


public class CustomerDashboard extends JFrame implements ActionListener {

    private final JLabel homelabel = new JLabel("Home");
    private final JLabel profilelabel = new JLabel("Profile");
    private final JLabel orderlabel = new JLabel("All Orders");
    private final JLabel logoutlabel = new JLabel("Logout");

    private final JPanel contentPanel = new JPanel(new CardLayout());
    private JLabel welcomeLabel;

    private JComboBox<String> categoryComboBox;
    private JTextField searchField;
    private JTable productTable, cartTable;
    private DefaultTableModel productModel, cartModel;
    private JButton addToCartBtn, viewCartBtn, removeItemBtn, placeOrderBtn;

    private int userId = DisplayNames.CurrentUserId; // assume user id from login

    public CustomerDashboard() {
        setTitle("Customer Dashboard | GET-ONE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(Color.WHITE);

        JLabel logo = new JLabel(" GET-ONE PLATFORM ");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(new Color(0, 102, 204));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeLabel = new JLabel("Welcome, " + DisplayNames.CurrentUserFullName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(0, 102, 204));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 0));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logo);
        logoPanel.add(welcomeLabel);

        // --- Search Bar ---
        searchField = new JTextField("Search for anything...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(300, 35));

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setPreferredSize(new Dimension(150, 35));
        loadCategories();

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchButton.setBackground(new Color(0, 113, 255));
        searchButton.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(searchField);
        searchPanel.add(categoryComboBox);
        searchPanel.add(searchButton);

        // --- Navigation Labels ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setBackground(Color.WHITE);
        JLabel[] navLabels = {homelabel, profilelabel, orderlabel, logoutlabel};
        for (JLabel lbl : navLabels) {
            lbl.setForeground(Color.BLACK);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navPanel.add(lbl);

            lbl.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    CardLayout cl = (CardLayout) contentPanel.getLayout();
                    if (lbl == homelabel) cl.show(contentPanel, "Home");
                    else if (lbl == profilelabel) cl.show(contentPanel, "Profile");
                    else if (lbl == orderlabel) cl.show(contentPanel, "Orders");
                    else if (lbl == logoutlabel) {
                        int confirm = JOptionPane.showConfirmDialog(CustomerDashboard.this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            dispose();
                            new Elogin().setVisible(true);
                        }
                    }
                }

                public void mouseEntered(MouseEvent e) { lbl.setForeground(new Color(0, 150, 255)); }
                public void mouseExited(MouseEvent e) { lbl.setForeground(Color.BLACK); }
            });
        }

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);

        JPanel upperRow = new JPanel(new BorderLayout());
        upperRow.setBackground(Color.WHITE);
        upperRow.add(logoPanel, BorderLayout.WEST);
        upperRow.add(searchPanel, BorderLayout.CENTER);

        JPanel navRow = new JPanel(new BorderLayout());
        navRow.setBackground(Color.WHITE);
        navRow.add(navPanel, BorderLayout.EAST);

        topContainer.add(upperRow, BorderLayout.NORTH);
        topContainer.add(navRow, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // ===== CONTENT PANELS =====
        contentPanel.add(CreateHomePanel(), "Home");
        contentPanel.add(CreateProfilePanel(), "Profile");
        contentPanel.add(CreateOrderPanel(), "Orders");
        contentPanel.add(CreateProductsPanel(), "Products");
        contentPanel.add(CreateCartPanel(), "Cart");

        add(contentPanel, BorderLayout.CENTER);

        // Search button
        searchButton.addActionListener(e -> triggerSearch());
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            loadProducts(selectedCategory, null);
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Products");
        });
        searchField.addActionListener(e -> triggerSearch());

        setVisible(true);
    }

    // ===== PANELS =====
    private JPanel CreateHomePanel() {
        JPanel p = new backgroundwork("/com/image/MINE2.jpg");
        JLabel lbl = new JLabel("Welcome to GET-ONE Platform!", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 40));
        lbl.setForeground(Color.WHITE);
        p.add(lbl, BorderLayout.SOUTH);
        return p;
    }

    private JPanel CreateProfilePanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Email
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Phone
        JLabel phoneLabel = new JLabel("Telephone Number:");
        JTextField phoneField = new JTextField();
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Save button
        JButton saveButton = new JButton("Save Profile");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(20));

        panel.add(saveButton);

        // Button action (temporary – no DB yet)
        saveButton.addActionListener(e -> {
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "All fields are required!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(panel,
                    "Profile saved successfully!\n\n" +
                            "Name: " + fullName +
                            "\nEmail: " + email +
                            "\nPhone: " + phone,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel CreateOrderPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("My Order History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(JLabel.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JTable orderTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "Order ID", "Order Number", "Date",
                "Status", "Total Amount", "Payment Method"
        });
        orderTable.setModel(model);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setBackground(new Color(25, 96, 225));
        orderTable.getTableHeader().setForeground(Color.WHITE);

        // Load customer orders
        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql =
                    "SELECT order_id, order_number, date, status, total_amount, payment_method " +
                            "FROM orders " +
                            "WHERE user_id = ? " +
                            "ORDER BY date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId); // logged-in customer
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("order_number"),
                        rs.getTimestamp("date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method")
                });
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{
                        "-", "No orders yet", "-", "-", "-", "-"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel,
                    "Failed to load orders: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return panel;
    }


    private JPanel CreateProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Product ID", "Product Name", "Category", "Price", "Stock"};
        productModel = new DefaultTableModel(cols, 0);
        productTable = new JTable(productModel);
        productTable.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(productTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        addToCartBtn = new JButton("Add to Cart");
        viewCartBtn = new JButton("View Cart");
        btnPanel.add(addToCartBtn);
        btnPanel.add(viewCartBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        addToCartBtn.addActionListener(e -> addToCart());
        viewCartBtn.addActionListener(e -> {
            loadCartItems();
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Cart");
        });

        loadProducts(null, null);
        return panel;
    }

    private JPanel CreateCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        cartModel = new DefaultTableModel(new String[]{"Product ID", "Product Name", "Quantity", "Price"}, 0);
        cartTable = new JTable(cartModel);

        JScrollPane scroll = new JScrollPane(cartTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel();
        removeItemBtn = new JButton("Remove Item");
        placeOrderBtn = new JButton("Place Order");
        JButton backBtn = new JButton("Back to Products");

        btns.add(removeItemBtn);
        btns.add(placeOrderBtn);
        btns.add(backBtn);
        panel.add(btns, BorderLayout.SOUTH);

        removeItemBtn.addActionListener(e -> removeFromCart());
        placeOrderBtn.addActionListener(e -> placeOrder());
        backBtn.addActionListener(e -> ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Products"));

        return panel;
    }

    private void loadCategories() {
        categoryComboBox.addItem("All Categories");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM categories ORDER BY name")) {
            while (rs.next()) categoryComboBox.addItem(rs.getString("name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void loadProducts(String category, String keyword) {
        productModel.setRowCount(0);
        String sql = """
                SELECT p.product_id, p.name, c.name AS category, p.price, p.stock
                FROM products p JOIN categories c ON p.category_id=c.category_id WHERE 1=1
                """;

        if (category != null && !"All Categories".equals(category)) sql += " AND c.name=?";
        if (keyword != null && !keyword.isEmpty()) sql += " AND p.name LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            if (category != null && !"All Categories".equals(category)) ps.setString(i++, category);
            if (keyword != null && !keyword.isEmpty()) ps.setString(i, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void loadCartItems() {
        cartModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT c.product_id, p.name, c.quantity, p.price
                     FROM cart_items c JOIN products p ON c.product_id=p.product_id
                     WHERE c.user_id=?""")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cartModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }
        int pid = (int) productModel.getValueAt(row, 0);
        String name = (String) productModel.getValueAt(row, 1);
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity:", "1");
        if (qtyStr == null) return;
        int qty = Integer.parseInt(qtyStr);

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT * FROM cart_items WHERE user_id=? AND product_id=?");
            check.setInt(1, userId);
            check.setInt(2, pid);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement("UPDATE cart_items SET quantity = ? WHERE user_id=? AND product_id=?");
                update.setInt(1, qty);
                update.setInt(2, userId);
                update.setInt(3, pid);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement("INSERT INTO cart_items(user_id, product_id, quantity) VALUES(?,?,?)");
                insert.setInt(1, userId);
                insert.setInt(2, pid);
                insert.setInt(3, qty);
                insert.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, name + " added to cart.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select item to remove.");
            return;
        }
        int pid = (int) cartModel.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_items WHERE user_id=? AND product_id=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, pid);
            ps.executeUpdate();
            loadCartItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void placeOrder() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            if (userId <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid user session. Please log in again.");
                return;
            }

            conn.setAutoCommit(false);


            PreparedStatement totalPs = conn.prepareStatement(
                    "SELECT SUM(p.price * c.quantity) AS total " +
                            "FROM cart_items c " +
                            "JOIN products p ON c.product_id = p.product_id " +
                            "WHERE c.user_id = ?"
            );
            totalPs.setInt(1, userId);
            ResultSet rs = totalPs.executeQuery();

            if (!rs.next() || rs.getDouble("total") <= 0) {
                JOptionPane.showMessageDialog(this, "Your cart is empty.");
                return;
            }

            double totalAmount = rs.getDouble("total");

            // SELECT PAYMENT METHOD
            String[] methods = {"Cash on Delivery", "Credit Card", "Mobile Money"};
            String paymentMethod = (String) JOptionPane.showInputDialog(
                    this, "Select Payment Method:", "Checkout",
                    JOptionPane.PLAIN_MESSAGE, null, methods, methods[0]
            );

            if (paymentMethod == null) return;

            // INSERT ORDER
            PreparedStatement orderPs = conn.prepareStatement(
                    "INSERT INTO orders (user_id, total_amount, payment_method, date, status) " +
                            "VALUES (?, ?, ?, NOW(), 'pending')",
                    Statement.RETURN_GENERATED_KEYS
            );
            orderPs.setInt(1, userId);
            orderPs.setDouble(2, totalAmount);
            orderPs.setString(3, paymentMethod);
            orderPs.executeUpdate();

            rs = orderPs.getGeneratedKeys();
            rs.next();
            int orderId = rs.getInt(1);

            // INSERT ORDER ITEMS
            PreparedStatement movePs = conn.prepareStatement(
                    "INSERT INTO order_items (order_id, product_id, quantity, price) " +
                            "SELECT ?, c.product_id, c.quantity, p.price " +
                            "FROM cart_items c " +
                            "JOIN products p ON c.product_id = p.product_id " +
                            "WHERE c.user_id = ?"
            );
            movePs.setInt(1, orderId);
            movePs.setInt(2, userId);
            movePs.executeUpdate();

            // UPDATE STOCK
            PreparedStatement stockPs = conn.prepareStatement(
                    "UPDATE products p " +
                            "JOIN cart_items c ON p.product_id = c.product_id " +
                            "SET p.stock = p.stock - c.quantity " +
                            "WHERE c.user_id = ?"
            );
            stockPs.setInt(1, userId);
            stockPs.executeUpdate();

            // CLEAR CART
            PreparedStatement clearPs = conn.prepareStatement("DELETE FROM cart_items WHERE user_id = ?");
            clearPs.setInt(1, userId);
            clearPs.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(this,
                    "Order placed successfully!\nPayment Method: " + paymentMethod);

            cartModel.setRowCount(0);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order: " + e.getMessage());
        }
    }


    private void triggerSearch() {
        String cat = (String) categoryComboBox.getSelectedItem();
        String kw = searchField.getText().trim();
        loadProducts(cat, kw);
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Products");
    }

    public void actionPerformed(ActionEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerDashboard::new);
    }
}
