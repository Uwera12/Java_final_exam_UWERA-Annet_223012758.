package com.workings;

import com.test.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

    public class CartPage extends JFrame {

        private JTable cartTable;
        private DefaultTableModel model;
        private int userId;

        public CartPage(int userId) {
            this.userId = userId;
            setTitle("My Cart");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            model = new DefaultTableModel(new String[]{"Product", "Price", "Quantity", "Total"}, 0);
            cartTable = new JTable(model);
            JScrollPane scroll = new JScrollPane(cartTable);

            JButton orderBtn = new JButton("Place Order");
            orderBtn.addActionListener(e -> placeOrder());

            add(scroll, BorderLayout.CENTER);
            add(orderBtn, BorderLayout.SOUTH);

            loadCart();
            setVisible(true);
        }

        private void loadCart() {
            model.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                SELECT p.name, p.price, c.quantity, (p.price*c.quantity) AS total
                FROM cart_items c JOIN products p ON c.product_id = p.product_id
                WHERE c.user_id=?
            """;
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDouble("total")
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void placeOrder() {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                PreparedStatement totalPs = conn.prepareStatement(
                        "SELECT SUM(p.price*c.quantity) AS total FROM cart_items c JOIN products p ON c.product_id=p.product_id WHERE c.user_id=?"
                );
                totalPs.setInt(1, userId);
                ResultSet rs = totalPs.executeQuery();
                rs.next();
                double totalAmount = rs.getDouble("total");

                // create order
                PreparedStatement orderPs = conn.prepareStatement(
                        "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                orderPs.setInt(1, userId);
                orderPs.setDouble(2, totalAmount);
                orderPs.executeUpdate();

                rs = orderPs.getGeneratedKeys();
                rs.next();
                int orderId = rs.getInt(1);

                // insert order items
                PreparedStatement movePs = conn.prepareStatement("""
                INSERT INTO order_items (order_id, product_id, quantity, price)
                SELECT ?, c.product_id, c.quantity, p.price
                FROM cart_items c JOIN products p ON c.product_id=p.product_id
                WHERE c.user_id=?
            """);
                movePs.setInt(1, orderId);
                movePs.setInt(2, userId);
                movePs.executeUpdate();

                // reduce stock
                PreparedStatement stockPs = conn.prepareStatement("""
                UPDATE products p
                JOIN cart_items c ON p.product_id=c.product_id
                SET p.stock = p.stock - c.quantity
                WHERE c.user_id=?
            """);
                stockPs.setInt(1, userId);
                stockPs.executeUpdate();

                // clear cart
                PreparedStatement clearPs = conn.prepareStatement("DELETE FROM cart_items WHERE user_id=?");
                clearPs.setInt(1, userId);
                clearPs.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                model.setRowCount(0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void main(String[] args) {
            new CartPage(1);
        }
    }


   /* private void deleteSelected(JTable table, String tableName, String idColumn) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM " + tableName + " WHERE " + idColumn + "=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");
            if (table == productTable) loadProducts();
            else if (table == orderTable) loadOrders();
            else loadShipments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
        }
    }

    private void exportData() {
        JTable selectedTable = switch (tabbedPane.getSelectedIndex()) {
            case 0 -> productTable;
            case 1 -> orderTable;
            case 2 -> shipmentTable;
            default -> null;
        };
        if (selectedTable == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("export.xls"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.PrintWriter out = new java.io.PrintWriter(chooser.getSelectedFile())) {
            DefaultTableModel model = (DefaultTableModel) selectedTable.getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                out.print(model.getColumnName(i) + "\t");
            }
            out.println();
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    out.print(model.getValueAt(r, c) + "\t");
                }
                out.println();
            }
            JOptionPane.showMessageDialog(this, "Data exported successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == logoutButton) {
            dispose();
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            new Elogin().setVisible(true);
        }
        else if (src == exportButton) {
            exportData();
        }
        else if (src == addProductButton) {
            addProduct();

        } else if (src == deleteProductButton) {
            deleteSelected(productTable, "products", "product_id");
        }
        else if (src == approveOrderButton) {
            approveOrder();
        }
        else if (src == deleteOrderButton) {
            deleteSelected(orderTable, "orders", "order_id");

        } else if (src == rejectOrdersButton) {
            int row = orderTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an order first to reject!");
            } else {
                Object idObj = orderTable.getValueAt(row, 0);
                int orderId;
                try {
                    orderId = parseId(idObj);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Cannot determine Order ID from selected row.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject order " + orderId + "?", "Confirm Reject", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    rejectOrder(orderId);
                }
            }

        } else if (src == addShipmentButton) {
            addShipment();
        }
        else if (src == deleteShipmentButton) {
            deleteSelected(shipmentTable, "shipments", "shipment_id");
        }
    }

    private int parseId(Object idObj) throws NumberFormatException {
        if (idObj == null) throw new NumberFormatException("ID is null");
        if (idObj instanceof Integer) {
            return (Integer) idObj;
        } else if (idObj instanceof Long) {
            return ((Long) idObj).intValue();
        } else if (idObj instanceof Number) { // covers BigDecimal, Double, etc.
            return ((Number) idObj).intValue();
        } else {

            String s = idObj.toString().trim();
            return Integer.parseInt(s);
        }
    }


    public static void main(String[] args) {
        new SellerDashboard("Seller");
    }
}*/


