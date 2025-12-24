package com.workings;

import com.form.Elogin;
import com.test.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.awt.event.*;
import java.sql.*;

public class SellerDashboard extends JFrame implements ActionListener {

    private int userId;
    private JLabel welcomeLabel;
    private JTabbedPane tabbedPane = new JTabbedPane();

    private JPanel productPanel = new JPanel();
    private JPanel orderPanel = new JPanel();
    private JPanel shipmentPanel = new JPanel();
    private JPanel categoryPanel = new JPanel();
    private JPanel paymentPanel = new JPanel();
    private JPanel orderItemPanel= new JPanel();
    private JPanel orderPaymentPanel = new JPanel();

    private JTable productTable = new JTable();
    private JTable orderTable = new JTable();
    private JTable shipmentTable = new JTable();
    private JTable categoryTable = new JTable();
    private JTable paymentTable = new JTable();
    private JTable orderItemTable = new JTable();
    private JTable orderPaymentTable = new JTable();

    private JButton logoutButton = new JButton("Logout");

    private JButton addProductButton = new JButton("Add Product");
    private JButton updateProductButton = new JButton("Update Product");
    private JButton deleteProductButton = new JButton("Delete Product");

    private JButton approveOrderButton = new JButton("Approve Order");
    private JButton deleteOrderButton = new JButton("Delete Order");
    private JButton rejectOrderButton = new JButton("Reject Order");

    private JButton addShipmentButton = new JButton("Add Shipment");
    private JButton updateShipmentButton= new JButton("Update Shipment");
    private JButton deleteShipmentButton = new JButton("Delete Shipment");

    private JButton addCategoryButton = new JButton("Add Category");
    private JButton updateCategoryButton = new JButton("Update Category");
    private JButton deleteCategoryButton = new JButton("Delete Category");

    private JButton addPaymentButton = new JButton("Add Payment");
    private JButton updatePaymentButton = new JButton("Update Payment");
    private JButton deletePaymentButton = new JButton("Delete Payment");

    public SellerDashboard(String fullname, int userId) {
        this.userId = userId;

        setTitle("Seller Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        JLabel title = new JLabel("GET-ONE PLATFORM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        welcomeLabel = new JLabel("Welcome, " + fullname);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.BLUE);

        header.add(title);
        header.add(Box.createHorizontalStrut(200));
        header.add(welcomeLabel);
        header.add(Box.createHorizontalStrut(200));
        header.add(logoutButton);
        add(header, BorderLayout.NORTH);


        setupProductPanel();
        setupOrderPanel();
        setupShipmentPanel();
        setupCategoryPanel();
        setupPaymentPanel();
        setupOrderItemPanel();
        setupOrderPaymentPanel();

        tabbedPane.addTab("Products", productPanel);
        tabbedPane.addTab("Orders", orderPanel);
        tabbedPane.addTab("Shipments", shipmentPanel);
        tabbedPane.addTab("Categories", categoryPanel);
        tabbedPane.addTab("Payments", paymentPanel);
        tabbedPane.addTab("Order Items", orderItemPanel);
        tabbedPane.addTab("Order Payments", orderPaymentPanel);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));

        add(tabbedPane, BorderLayout.CENTER);


        logoutButton.addActionListener(this);
        addProductButton.addActionListener(this);
        updateProductButton.addActionListener(this);
        deleteProductButton.addActionListener(this);

        approveOrderButton.addActionListener(this);
        deleteOrderButton.addActionListener(this);
        rejectOrderButton.addActionListener(this);

        addShipmentButton.addActionListener(this);
        updateShipmentButton.addActionListener(this);
        deleteShipmentButton.addActionListener(this);

        addCategoryButton.addActionListener(this);
        updateCategoryButton.addActionListener(this);
        deleteCategoryButton.addActionListener(this);

        addPaymentButton.addActionListener(this);
        updatePaymentButton.addActionListener(this);
        deletePaymentButton.addActionListener(this);

        loadProducts();
        loadOrders();
        loadShipments();
        loadCategories();
        loadPayments();
        loadOrderItems();
        loadOrderPayments();

        setVisible(true);
    }

    private void setupProductPanel() {
        productPanel.setLayout(new BorderLayout());
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel btn = new JPanel();
        btn.add(addProductButton);
        btn.add(updateProductButton);
        btn.add(deleteProductButton);

        productPanel.add(btn, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new Object[]{
                "ID","Name","Description","Price","Category","Stock","User ID ","Status","Created_at"
        });
        productTable.setModel(m);

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM products")) {

            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("product_id"),
                        r.getString("name"),
                        r.getString("description"),
                        r.getBigDecimal("price"),
                        r.getInt("category_id"),
                        r.getInt("stock"),
                        r.getInt("user_id"),
                        r.getString("status"),
                        r.getTimestamp("created_at")
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void setupOrderPanel() {
        orderPanel.setLayout(new BorderLayout());
        orderPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel btn = new JPanel();
        btn.add(approveOrderButton);
        btn.add(rejectOrderButton);
        btn.add(deleteOrderButton);

        orderPanel.add(btn, BorderLayout.SOUTH);
    }
    private void loadOrders() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new Object[]{
                "Order ID","Customer ID","Order Number","Date","Status","Total Amount","Payment Method"
        });
        orderTable.setModel(m);

        String sql = """
            SELECT DISTINCT o.order_id AS order_id,
                   o.user_id AS customer_id,
                   o.order_number,
                   o.date,
                   o.status,
                   o.total_amount,
                   o.payment_method
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN products p ON oi.product_id = p.product_id
            WHERE p.user_id = ?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet r = ps.executeQuery();

            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("order_id"),
                        r.getInt("customer_id"),
                        r.getString("order_number"),
                        r.getTimestamp("date"),
                        r.getString("status"),
                        r.getBigDecimal("total_amount"),
                        r.getString("payment_method")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void setupShipmentPanel() {
        shipmentPanel.setLayout(new BorderLayout());
        shipmentPanel.add(new JScrollPane(shipmentTable), BorderLayout.CENTER);

        JPanel btn = new JPanel();
        btn.add(addShipmentButton);
        btn.add(deleteShipmentButton);

        shipmentPanel.add(btn, BorderLayout.SOUTH);
    }

    private void loadShipments() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new Object[]{
                "Shipment ID","Order ID","Tracking Number","Status","Created At"
        });
        shipmentTable.setModel(m);

        String sql = """
        SELECT DISTINCT s.shipment_id, s.order_id, s.tracking_number, s.status, s.created_at
        FROM shipments s
        JOIN orders o ON s.order_id = o.order_id
        JOIN order_items oi ON o.order_id = oi.order_id
        JOIN products p ON oi.product_id = p.product_id
        WHERE p.user_id = ?
    """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet r = ps.executeQuery();

            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("shipment_id"),
                        r.getInt("order_id"),
                        r.getString("tracking_number"),
                        r.getString("status"),
                        r.getTimestamp("created_at")
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void setupCategoryPanel() {
        categoryPanel.setLayout(new BorderLayout());
        categoryPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel btn = new JPanel();
        btn.add(addCategoryButton);
        btn.add(updateCategoryButton);
        btn.add(deleteCategoryButton);

        categoryPanel.add(btn, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new Object[]{
                "ID","Name","Description","Status","Created"
        });
        categoryTable.setModel(m);

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT * FROM categories")) {

            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("category_id"),
                        r.getString("name"),
                        r.getString("description"),
                        r.getString("status"),
                        r.getTimestamp("created_at")
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }
    private void setupPaymentPanel() {
        paymentPanel.setLayout(new BorderLayout());
        paymentPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        JPanel btn = new JPanel();
        btn.add(addPaymentButton);
        btn.add(updatePaymentButton);
        btn.add(deletePaymentButton);

        paymentPanel.add(btn, BorderLayout.SOUTH);
    }
    private void loadPayments() {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Payment ID","Order ID","Customer","Amount","Payment Method","Status","Date"},0);
        paymentTable.setModel(m);

        String sql = """
            SELECT
                pay.payment_id AS pid,
                op.order_id AS oid,
                o.user_id AS customer,
                pay.amount,
                pay.type AS method,
                pay.status,
                pay.date
            FROM payments pay
            JOIN order_payments op ON pay.payment_id=op.payment_id
            JOIN orders o ON op.order_id=o.order_id
            JOIN order_items oi ON o.order_id=oi.order_id
            WHERE oi.user_id=?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("pid"),
                        r.getInt("oid"),
                        r.getInt("customer"),
                        r.getBigDecimal("amount"),
                        r.getString("method"),
                        r.getString("status"),
                        r.getTimestamp("date")
                });
            }
        } catch (Exception e) { showError(e); }
    }

    private void setupOrderItemPanel(){
        orderItemPanel.setLayout(new BorderLayout());
        orderItemPanel.add(new JScrollPane(orderItemTable));
    }
    private void loadOrderItems() {
        DefaultTableModel m = new DefaultTableModel();
        m.setColumnIdentifiers(new Object[]{
                "Order Item ID", "Order ID", "Product ID",
                "Quantity", "Price", "Seller ID"
        });
        orderItemTable.setModel(m);

        String sql = """
        SELECT oi.*
        FROM order_items oi
        JOIN products p ON oi.product_id = p.product_id
        WHERE p.user_id = ?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet r = ps.executeQuery();

            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("order_item_id"),
                        r.getInt("order_id"),
                        r.getInt("product_id"),
                        r.getInt("quantity"),
                        r.getBigDecimal("price"),
                        r.getInt("user_id")
                });
            }
        } catch (Exception e) {
            showError(e);
        }
    }

    private void setupOrderPaymentPanel(){
        orderPaymentPanel.setLayout(new BorderLayout());
        orderPaymentPanel.add(new JScrollPane(orderPaymentTable));
    }
    private void loadOrderPayments() {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"ID","Order ID","Payment ID"},0);
        orderPaymentTable.setModel(m);

        String sql = """
            SELECT op.id,op.order_id,op.payment_id
            FROM order_payments op
            JOIN order_items oi ON op.order_id=oi.order_id
            WHERE oi.user_id=?
        """;

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                m.addRow(new Object[]{
                        r.getInt("id"),
                        r.getInt("order_id"),
                        r.getInt("payment_id")
                });
            }
        } catch (Exception e) { showError(e); }
    }

    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();

        if (s == logoutButton) {
            dispose();
            new Elogin().setVisible(true);
        } else if (s == addProductButton) addProduct();
        else if (s == updateProductButton) updateProduct();
        else if (s == deleteProductButton) deleteProduct();
        else if (s == approveOrderButton) approveOrder();
        else if (s == rejectOrderButton) rejectOrder();
        else if (s == deleteOrderButton) deleteOrder();
        else if (s == addCategoryButton) addCategory();
        else if (s == updateCategoryButton) updateCategory();
        else if (s == deleteCategoryButton) deleteCategory();
        else if (s == addPaymentButton) addPayment();
        else if (s == updatePaymentButton) updatePayment();
        else if (s == deletePaymentButton) deletePayment();
        else if (s == addShipmentButton) addShipment();
        else if (s == updateShipmentButton) updateShipment();
        else if (s == deleteShipmentButton) deleteShipment();
    }

    private void addProduct() {

        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField description= new JTextField();
        JTextField stock = new JTextField();
        JTextField categoryId = new JTextField();

        Object[] fields = {
                "Product Name:", name,
                "Description:",description,
                "Price:", price,
                "Stock", stock,
                "Category ID:", categoryId
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Add Product",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "INSERT INTO products(name, description, price, stock, category_id, user_id) "
                        + "VALUES (?, ?, ?, ?,?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2,description.getText());
                ps.setDouble(3, Double.parseDouble(price.getText()));
                ps.setInt(4, Integer.parseInt(stock.getText()));
                ps.setInt(5, Integer.parseInt(categoryId.getText()));
                ps.setInt(6, userId);


                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product added successfully");
                loadProducts();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void updateProduct() {

        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first");
            return;
        }

        int productId = Integer.parseInt(
                productTable.getValueAt(row, 0).toString());

        JTextField name = new JTextField(
                productTable.getValueAt(row, 1).toString());
        JTextField description = new JTextField(
                productTable.getValueAt(row, 2).toString());
        JTextField price = new JTextField(
                productTable.getValueAt(row, 3).toString());
        JTextField stock = new JTextField(
                productTable.getValueAt(row, 4).toString());
        JTextField categoryId = new JTextField(
                productTable.getValueAt(row, 5).toString());

        Object[] fields = {
                "Product Name:", name,
                "Description:", description,
                "Price:", price,
                "Stock:", stock,
                "Category ID:", categoryId
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Update Product",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "UPDATE products SET name=?, price=?, stock=?, category_id=? "
                        + "WHERE product_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setDouble(2, Double.parseDouble(price.getText()));
                ps.setInt(3, Integer.parseInt(stock.getText()));
                ps.setInt(4, Integer.parseInt(categoryId.getText()));
                ps.setInt(5, productId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product updated");
                loadProducts();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void deleteProduct() {

        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product first");
            return;
        }

        int productId = Integer.parseInt(
                productTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM products WHERE product_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, productId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product deleted");
                loadProducts();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void approveOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = (int) orderTable.getValueAt(row, 0);
        int customerId = (int) orderTable.getValueAt(row, 1);
        BigDecimal total = (BigDecimal) orderTable.getValueAt(row, 5);
        String method = orderTable.getValueAt(row, 6).toString();

        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            PreparedStatement checkOrder =
                    c.prepareStatement("SELECT order_number FROM orders WHERE order_id=?");
            checkOrder.setInt(1, orderId);
            ResultSet rsOrder = checkOrder.executeQuery();

            String orderNumber = null;
            if (rsOrder.next()) {
                orderNumber = rsOrder.getString("order_number");
            }
            if (orderNumber == null || orderNumber.trim().isEmpty()) {

                orderNumber = "ORD-" + System.currentTimeMillis();

                PreparedStatement updateOrderNo =
                        c.prepareStatement(
                                "UPDATE orders SET order_number=? WHERE order_id=?"
                        );
                updateOrderNo.setString(1, orderNumber);
                updateOrderNo.setInt(2, orderId);
                updateOrderNo.executeUpdate();
            }

            c.prepareStatement(
                    "UPDATE orders SET status='paid' WHERE order_id="+orderId).executeUpdate();

            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO payments(amount,type,status,user_id,date) VALUES (?,?, 'completed', ?, NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, total);
            ps.setString(2, method);
            ps.setInt(3, customerId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int paymentId = rs.getInt(1);

            c.prepareStatement(
                            "INSERT INTO order_payments(order_id,payment_id) VALUES ("+orderId+","+paymentId+")")
                    .executeUpdate();

            c.prepareStatement(
                    "INSERT INTO shipments(order_id,tracking_number,status,created_at) VALUES ("+
                            orderId+",'', 'processing', NOW())");
                    ps.setInt(1, orderId);
                    ps.setString(2, "PENDING");
                    ps.executeUpdate();

            c.commit();
            loadOrders(); loadPayments(); loadOrderPayments(); loadShipments();

        } catch (Exception e) { showError(e); }
    }

    private void deleteOrder() {

        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first");
            return;
        }

        int orderId = Integer.parseInt(
                orderTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this order?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM orders WHERE order_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, orderId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Order deleted");
                loadOrders();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void rejectOrder() {

        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first");
            return;
        }

        int orderId = Integer.parseInt(
                orderTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reject this order?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "UPDATE orders SET status='rejected' WHERE order_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Order rejected");
            loadOrders();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addCategory() {

        JTextField name = new JTextField();
        JTextField description = new JTextField();

        Object[] fields = {
                "Category Name:", name,
                "Description:", description
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Add Category",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "INSERT INTO categories(name, description, status) VALUES (?, ?, 'active')";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2, description.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Category added successfully");
                loadCategories(); // refresh table

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void updateCategory() {

        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a category first");
            return;
        }

        int categoryId = Integer.parseInt(
                categoryTable.getValueAt(row, 0).toString());

        JTextField name = new JTextField(
                categoryTable.getValueAt(row, 1).toString());
        JTextField description = new JTextField(
                categoryTable.getValueAt(row, 2).toString());

        Object[] fields = {
                "Category Name:", name,
                "Description:", description
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Update Category",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "UPDATE categories SET name=?, description=? WHERE category_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2, description.getText());
                ps.setInt(3, categoryId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Category updated");
                loadCategories();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void deleteCategory() {

        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a category first");
            return;
        }

        int categoryId = Integer.parseInt(
                categoryTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM categories WHERE category_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, categoryId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Category deleted");
                loadCategories();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void addPayment() {

        JTextField orderId = new JTextField();
        JTextField amount = new JTextField();
        JComboBox<String> method = new JComboBox<>(
                new String[]{"Cash", "Mobile Money", "Card", "Bank Transfer"}
        );

        Object[] fields = {
                "Order ID:", orderId,
                "Amount:", amount,
                "Payment Method:", method
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Add Payment",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "INSERT INTO payments(order_id, amount, payment_method, payment_status) "
                        + "VALUES (?, ?, ?, 'PAID')";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setInt(1, Integer.parseInt(orderId.getText()));
                ps.setDouble(2, Double.parseDouble(amount.getText()));
                ps.setString(3, method.getSelectedItem().toString());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Payment recorded successfully");
                loadPayments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void updatePayment() {

        int row = paymentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a payment first");
            return;
        }

        int paymentId = Integer.parseInt(
                paymentTable.getValueAt(row, 0).toString());

        JComboBox<String> method = new JComboBox<>(
                new String[]{"Cash", "Mobile Money", "Card", "Bank Transfer"}
        );
        method.setSelectedItem(
                paymentTable.getValueAt(row, 3).toString());

        JComboBox<String> status = new JComboBox<>(
                new String[]{"PAID", "PENDING", "FAILED"}
        );
        status.setSelectedItem(
                paymentTable.getValueAt(row, 4).toString());

        Object[] fields = {
                "Payment Method:", method,
                "Payment Status:", status
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Update Payment",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "UPDATE payments SET payment_method=?, payment_status=? WHERE payment_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, method.getSelectedItem().toString());
                ps.setString(2, status.getSelectedItem().toString());
                ps.setInt(3, paymentId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Payment updated");
                loadPayments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void deletePayment() {

        int row = paymentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a payment first");
            return;
        }

        int paymentId = Integer.parseInt(
                paymentTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this payment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM payments WHERE payment_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, paymentId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Payment deleted");
                loadPayments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void addShipment() {

        JTextField orderId = new JTextField();
        JTextField tracking = new JTextField();

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"processing", "shipped", "delivered"}
        );

        Object[] fields = {
                "Order ID:", orderId,
                "Tracking Number:", tracking,
                "Status:", statusBox
        };

        int choice = JOptionPane.showConfirmDialog(
                this, fields, "Add Shipment",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                // Validate seller ownership
                String checkSql =
                        "SELECT 1 FROM order_items oi " +
                                "JOIN products p ON oi.product_id=p.product_id " +
                                "WHERE oi.order_id=? AND p.user_id=?";

                PreparedStatement check = conn.prepareStatement(checkSql);
                check.setInt(1, Integer.parseInt(orderId.getText()));
                check.setInt(2, userId);

                ResultSet rs = check.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,
                            "You can add shipments only for your orders");
                    return;
                }

                String sql =
                        "INSERT INTO shipments(order_id, tracking_number, status) " +
                                "VALUES (?, ?, ?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "tracking_number = VALUES(tracking_number), " +
                                "status = VALUES(status)";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setInt(1, Integer.parseInt(orderId.getText()));
                ps.setString(2, tracking.getText());
                ps.setString(3, statusBox.getSelectedItem().toString());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shipment added successfully");
                loadShipments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void updateShipment() {

        int row = shipmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a shipment first");
            return;
        }

        int shipmentId = Integer.parseInt(
                shipmentTable.getValueAt(row, 0).toString());

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{"processing", "shipped", "delivered"}
        );

        statusBox.setSelectedItem(
                shipmentTable.getValueAt(row, 3).toString());

        int choice = JOptionPane.showConfirmDialog(
                this, statusBox,
                "Update Shipment Status",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "UPDATE shipments SET status=? WHERE shipment_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, statusBox.getSelectedItem().toString());
                ps.setInt(2, shipmentId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shipment updated");
                loadShipments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }
    private void deleteShipment() {

        int row = shipmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a shipment first");
            return;
        }

        int shipmentId = Integer.parseInt(
                shipmentTable.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this shipment?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            try (Connection conn = DatabaseConnection.getConnection()) {

                String sql = "DELETE FROM shipments WHERE shipment_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, shipmentId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Shipment deleted");
                loadShipments();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new SellerDashboard("", 1);
    }
}
