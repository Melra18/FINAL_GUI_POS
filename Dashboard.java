/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pos.ui;

/**
 *
 * @author ramel
 */
import com.pos.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.CardLayout;
import javax.swing.table.DefaultTableModel;
import com.pos.config.ConnectDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.awt.Dimension;
import com.pos.model.Product;
import com.pos.model.Cart;
import com.pos.model.CartItem;
import com.pos.model.Discount;
import com.pos.model.NoDiscount;
import com.pos.model.SeniorPwdDiscount;


public class Dashboard extends javax.swing.JFrame {
    private CardLayout cardLayout;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private User currentUser;
    private SalesPanel salesPanel;
    private ExpensesPanel expensesPanel;
    private double currentTotalDue = 0;
    private final Cart cart = new Cart();
   
    public User getCurrentUser() {
    return currentUser;
}
    private void setupRolePermissions() {

    String role = currentUser.getRole();

    // ==========================================
    // CASHIER
    // ==========================================
    if (role.equalsIgnoreCase("Cashier")) {

    btnNavProducts.setVisible(true);
    btnNavEmployees.setVisible(true);
    btnNavExpenses.setVisible(true);
    btnNavSales.setVisible(true);
    btnNavSecurity.setVisible(true);

    // Keep buttons enabled so their colors stay normal
    btnNavProducts.setEnabled(true);
    btnNavEmployees.setEnabled(true);
    btnNavExpenses.setEnabled(true);
    btnNavSales.setEnabled(true);
    btnNavSecurity.setEnabled(true);

      
    }

    // ==========================================
    // SHIFT SUPERVISOR
    // ==========================================
    else if (role.equalsIgnoreCase("Shift Supervisor")) {

        // Full access to operational features.

        btnNavProducts.setVisible(true);
        btnNavExpenses.setVisible(true);
        btnNavSales.setVisible(true);
        btnNavSecurity.setVisible(true);

        // Employees stays hidden for Shift Supervisor
        btnNavEmployees.setVisible(false);
    }

    // ==========================================
    // STORE MANAGER
    // ==========================================
    else if (role.equalsIgnoreCase("Store Manager")) {

    // Store Manager has full access
    btnNavProducts.setEnabled(true);
    btnNavEmployees.setEnabled(true);
    btnNavExpenses.setEnabled(true);
    btnNavSales.setEnabled(true);
    btnNavSecurity.setEnabled(true);
    }
}
    
     public Dashboard(User user) {
     initComponents();
     this.currentUser = user;
     
    

     setupRolePermissions();
         setupCartTable();
      jScrollPane1.getVerticalScrollBar().setPreferredSize(
        new Dimension(0, 0)
      );
     tblCart.getColumnModel().getColumn(0).setPreferredWidth(150); // Product
     tblCart.getColumnModel().getColumn(1).setPreferredWidth(50);  // Qty
     tblCart.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
     tblCart.getColumnModel().getColumn(3).setPreferredWidth(90);  // Total

     tblCart.getColumnModel().getColumn(0).setMinWidth(120);
     tblCart.getColumnModel().getColumn(1).setMinWidth(40);
     tblCart.getColumnModel().getColumn(2).setMinWidth(70);
     tblCart.getColumnModel().getColumn(3).setMinWidth(70);
     jScrollPane1.setHorizontalScrollBarPolicy(
     javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
     );
     jScrollPane1.setVerticalScrollBarPolicy(
     javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
     );
     javax.swing.table.DefaultTableCellRenderer centerRenderer =
        new javax.swing.table.DefaultTableCellRenderer();

     centerRenderer.setHorizontalAlignment(
        javax.swing.SwingConstants.CENTER
);

     tblCart.getColumnModel().getColumn(1)
         .setCellRenderer(centerRenderer);

    tblCart.getColumnModel().getColumn(2)
         .setCellRenderer(centerRenderer);

     tblCart.getColumnModel().getColumn(3)
         .setCellRenderer(centerRenderer);

        
    jLabel1.setText("Staff: " + user.getFullName() + " (" + user.getRole() + ")");

      // =========================
    // CARD LAYOUT
    // =========================

    cardLayout = new CardLayout();
    pnlMainContent.setLayout(cardLayout);

    // =========================
    // SALES PANEL
    // =========================

    salesPanel = new SalesPanel(this);
    pnlMainContent.add(salesPanel, "sales");

    // =========================
    // INVENTORY PANEL
    // =========================

    InventoryPanel inventoryPanel = new InventoryPanel(this);
    pnlMainContent.add(inventoryPanel, "inventory");

    
    expensesPanel = new ExpensesPanel(this);
    pnlMainContent.add(expensesPanel, "expenses");
    
    
    
    
    // =========================
    // CART TABLE
    // =========================

    

    pnlCart.setVisible(true);

    // =========================
    // BUTTON EVENTS
    // =========================

    jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            updateChangeDue();
        }
    });

    // =========================
    // CLOCK
    // =========================

    lblTime.setText(
        LocalDateTime.now().format(
            DateTimeFormatter.ofPattern(
                "EEE, MMM dd, yyyy | hh:mm:ss a"
            )
        )
    );

    Timer liveClockTimer = new Timer(1000, e -> {
        lblTime.setText(
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                    "EEE, MMM dd, yyyy | hh:mm:ss a"
                )
            )
        );
    });

    liveClockTimer.setInitialDelay(0);
    liveClockTimer.start();
    javax.swing.KeyStroke undoKey = javax.swing.KeyStroke.getKeyStroke(
    java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK);
    
    
    RootPanel.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(undoKey, "undoCart");
    RootPanel.getActionMap().put("undoCart", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        undoLastCartAction();
    }
});
    setExtendedState(Dashboard.MAXIMIZED_BOTH);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
     private void setupCartTable() {

    tblCart.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

    tblCart.setRowHeight(30);

    tblCart.setFont(
        new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)
    );

    tblCart.getTableHeader().setFont(
        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12)
    );

    // Product
    tblCart.getColumnModel()
        .getColumn(0)
        .setPreferredWidth(115);

    // Qty
    tblCart.getColumnModel()
        .getColumn(1)
        .setPreferredWidth(40);

    // Price
    tblCart.getColumnModel()
        .getColumn(2)
        .setPreferredWidth(65);

    // Total
    tblCart.getColumnModel()
        .getColumn(3)
        .setPreferredWidth(70);

    tblCart.getColumnModel()
        .getColumn(0)
        .setMinWidth(100);

    tblCart.getColumnModel()
        .getColumn(1)
        .setMinWidth(35);

    tblCart.getColumnModel()
        .getColumn(2)
        .setMinWidth(60);

    tblCart.getColumnModel()
        .getColumn(3)
        .setMinWidth(65);
    tblCart.setRowSelectionAllowed(true);
tblCart.setColumnSelectionAllowed(false);
tblCart.setCellSelectionEnabled(false);
tblCart.setSelectionMode(
    javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
);
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RootPanel = new javax.swing.JPanel();
        pnlTopBar = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        pnlSidebar = new javax.swing.JPanel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 19), new java.awt.Dimension(0, 19), new java.awt.Dimension(0, 19));
        btnNavPos = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavProducts = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavEmployees = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavExpenses = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavSales = new javax.swing.JButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        jButton7 = new javax.swing.JButton();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        btnNavSecurity = new javax.swing.JButton();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        jButton9 = new javax.swing.JButton();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10));
        jButton10 = new javax.swing.JButton();
        pnlCart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCart = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ButtonDisc = new javax.swing.JButton();
        ButtonClear = new javax.swing.JButton();
        ButtonComp = new javax.swing.JButton();
        lblSubtotal = new javax.swing.JLabel();
        lblDiscount = new javax.swing.JLabel();
        lblVAT = new javax.swing.JLabel();
        lblTotalDue = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        ButtonRemove = new javax.swing.JButton();
        pnlMainContent = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        RootPanel.setBackground(new java.awt.Color(27, 40, 56));
        RootPanel.setLayout(new java.awt.BorderLayout());

        pnlTopBar.setBackground(new java.awt.Color(27, 31, 92));
        pnlTopBar.setPreferredSize(new java.awt.Dimension(0, 60));

        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("       POS AND INVENTORY SYSTEM");

        lblTime.setForeground(new java.awt.Color(255, 255, 255));
        lblTime.setText("THURSDAY, SEP 16, 2026 2:13:46 AM");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(56, 219, 248));
        jLabel1.setText("Staff: Operator Name (Role)");

        jButton11.setBackground(new java.awt.Color(255, 0, 0));
        jButton11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("Sign Out");
        jButton11.addActionListener(this::jButton11ActionPerformed);

        javax.swing.GroupLayout pnlTopBarLayout = new javax.swing.GroupLayout(pnlTopBar);
        pnlTopBar.setLayout(pnlTopBarLayout);
        pnlTopBarLayout.setHorizontalGroup(
            pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopBarLayout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 760, Short.MAX_VALUE)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jButton11)
                .addGap(27, 27, 27))
        );
        pnlTopBarLayout.setVerticalGroup(
            pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTopBarLayout.createSequentialGroup()
                .addGroup(pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTopBarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlTopBarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(pnlTopBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11)
                            .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addContainerGap())
        );

        RootPanel.add(pnlTopBar, java.awt.BorderLayout.NORTH);

        pnlSidebar.setBackground(new java.awt.Color(18, 18, 58));
        pnlSidebar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pnlSidebar.setMinimumSize(new java.awt.Dimension(109, 23));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(200, 0));

        btnNavPos.setBackground(new java.awt.Color(51, 65, 85));
        btnNavPos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavPos.setForeground(new java.awt.Color(255, 255, 255));
        btnNavPos.setText("Sales Register");
        btnNavPos.setDoubleBuffered(true);
        btnNavPos.setMaximumSize(new java.awt.Dimension(200, 45));
        btnNavPos.setPreferredSize(new java.awt.Dimension(200, 45));
        btnNavPos.addActionListener(this::btnNavPosActionPerformed);

        btnNavProducts.setBackground(new java.awt.Color(51, 65, 85));
        btnNavProducts.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnNavProducts.setText("Inventory");
        btnNavProducts.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavProducts.setPreferredSize(new java.awt.Dimension(200, 0));
        btnNavProducts.addActionListener(this::btnNavProductsActionPerformed);

        btnNavEmployees.setBackground(new java.awt.Color(51, 65, 85));
        btnNavEmployees.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavEmployees.setForeground(new java.awt.Color(255, 255, 255));
        btnNavEmployees.setText("Employees");
        btnNavEmployees.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavEmployees.setPreferredSize(new java.awt.Dimension(200, 0));
        btnNavEmployees.addActionListener(this::btnNavEmployeesActionPerformed);

        btnNavExpenses.setBackground(new java.awt.Color(51, 65, 85));
        btnNavExpenses.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavExpenses.setForeground(new java.awt.Color(255, 255, 255));
        btnNavExpenses.setText("Expenses");
        btnNavExpenses.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavExpenses.addActionListener(this::btnNavExpensesActionPerformed);

        btnNavSales.setBackground(new java.awt.Color(51, 65, 85));
        btnNavSales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavSales.setForeground(new java.awt.Color(255, 255, 255));
        btnNavSales.setText("Sales Summary");
        btnNavSales.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavSales.addActionListener(this::btnNavSalesActionPerformed);

        jButton7.setBackground(new java.awt.Color(51, 65, 85));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Z-Read Report");
        jButton7.setMaximumSize(new java.awt.Dimension(200, 50));
        jButton7.addActionListener(this::jButton7ActionPerformed);

        btnNavSecurity.setBackground(new java.awt.Color(51, 65, 85));
        btnNavSecurity.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNavSecurity.setForeground(new java.awt.Color(255, 255, 255));
        btnNavSecurity.setText("Security Logs");
        btnNavSecurity.setMaximumSize(new java.awt.Dimension(200, 50));
        btnNavSecurity.addActionListener(this::btnNavSecurityActionPerformed);

        jButton9.setBackground(new java.awt.Color(51, 65, 85));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("My Account");
        jButton9.setMaximumSize(new java.awt.Dimension(200, 50));
        jButton9.addActionListener(this::jButton9ActionPerformed);

        jButton10.setBackground(new java.awt.Color(255, 0, 0));
        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Sign Out");
        jButton10.setMaximumSize(new java.awt.Dimension(200, 50));
        jButton10.addActionListener(this::jButton10ActionPerformed);

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlSidebarLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNavSales, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .addComponent(btnNavExpenses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNavEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavPos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnNavSecurity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addComponent(filler7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavPos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavExpenses, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavSales, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnNavSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(filler10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        RootPanel.add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlCart.setBackground(new java.awt.Color(247, 247, 247));
        pnlCart.setName(""); // NOI18N
        pnlCart.setPreferredSize(new java.awt.Dimension(350, 0));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setMinimumSize(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 430));

        tblCart.setBackground(new java.awt.Color(255, 255, 255));
        tblCart.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblCart.setForeground(new java.awt.Color(0, 0, 0));
        tblCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Qty", "Price", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCart.setGridColor(new java.awt.Color(225, 225, 225));
        tblCart.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        tblCart.setMinimumSize(new java.awt.Dimension(60, 600));
        tblCart.setName(""); // NOI18N
        tblCart.setPreferredSize(new java.awt.Dimension(300, 500));
        tblCart.setRowHeight(28);
        tblCart.setRowMargin(5);
        tblCart.setSelectionBackground(new java.awt.Color(255, 255, 255));
        tblCart.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblCart.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblCart);
        if (tblCart.getColumnModel().getColumnCount() > 0) {
            tblCart.getColumnModel().getColumn(0).setResizable(false);
            tblCart.getColumnModel().getColumn(1).setResizable(false);
            tblCart.getColumnModel().getColumn(2).setResizable(false);
            tblCart.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel2.setBackground(new java.awt.Color(245, 245, 245));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(30, 30, 30));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("CUSTOMER CART");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Subtotal:");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("PWD/Senior Disc%");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("VAT (12%)");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("TOTAL DUE:");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Cash Received (₱):");

        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Change Due:");

        ButtonDisc.setBackground(new java.awt.Color(255, 255, 255));
        ButtonDisc.setForeground(new java.awt.Color(0, 0, 0));
        ButtonDisc.setText("20% Disc");
        ButtonDisc.addActionListener(this::ButtonDiscActionPerformed);

        ButtonClear.setBackground(new java.awt.Color(255, 255, 255));
        ButtonClear.setForeground(new java.awt.Color(0, 0, 0));
        ButtonClear.setText("Clear");
        ButtonClear.addActionListener(this::ButtonClearActionPerformed);

        ButtonComp.setBackground(new java.awt.Color(0, 153, 0));
        ButtonComp.setForeground(new java.awt.Color(255, 255, 255));
        ButtonComp.setText("Complete Sale");
        ButtonComp.addActionListener(this::ButtonCompActionPerformed);

        lblSubtotal.setForeground(new java.awt.Color(0, 0, 0));
        lblSubtotal.setText("₱0.00");

        lblDiscount.setForeground(new java.awt.Color(0, 0, 0));
        lblDiscount.setText("-₱0.00");

        lblVAT.setForeground(new java.awt.Color(0, 0, 0));
        lblVAT.setText("₱0.00");

        lblTotalDue.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalDue.setForeground(new java.awt.Color(0, 51, 255));
        lblTotalDue.setText("₱0.00");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel13.setForeground(new java.awt.Color(204, 102, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("₱0.00");
        jLabel13.setPreferredSize(new java.awt.Dimension(140, 25));

        ButtonRemove.setBackground(new java.awt.Color(255, 255, 255));
        ButtonRemove.setForeground(new java.awt.Color(0, 0, 0));
        ButtonRemove.setText("Remove");
        ButtonRemove.addActionListener(this::ButtonRemoveActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 62, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblVAT)
                                    .addComponent(lblDiscount)
                                    .addComponent(lblSubtotal)
                                    .addComponent(lblTotalDue))))
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(ButtonDisc)
                                .addGap(38, 38, 38)
                                .addComponent(ButtonRemove)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ButtonClear))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(27, 27, 27))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ButtonComp)
                .addGap(120, 120, 120))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblDiscount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblVAT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblTotalDue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonDisc)
                    .addComponent(ButtonClear)
                    .addComponent(ButtonRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ButtonComp))
        );

        javax.swing.GroupLayout pnlCartLayout = new javax.swing.GroupLayout(pnlCart);
        pnlCart.setLayout(pnlCartLayout);
        pnlCartLayout.setHorizontalGroup(
            pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCartLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        pnlCartLayout.setVerticalGroup(
            pnlCartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCartLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        RootPanel.add(pnlCart, java.awt.BorderLayout.LINE_END);

        pnlMainContent.setBackground(new java.awt.Color(247, 247, 247));

        javax.swing.GroupLayout pnlMainContentLayout = new javax.swing.GroupLayout(pnlMainContent);
        pnlMainContent.setLayout(pnlMainContentLayout);
        pnlMainContentLayout.setHorizontalGroup(
            pnlMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        pnlMainContentLayout.setVerticalGroup(
            pnlMainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 784, Short.MAX_VALUE)
        );

        RootPanel.add(pnlMainContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(RootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNavPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavPosActionPerformed
      cardLayout.show(pnlMainContent, "sales");

    // Show customer cart on Sales Register
    pnlCart.setVisible(true);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    RootPanel.repaint();

    }//GEN-LAST:event_btnNavPosActionPerformed

    private void btnNavProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavProductsActionPerformed
       if (currentUser.getRole().equalsIgnoreCase("Cashier")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Inventory.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    cardLayout.show(pnlMainContent, "inventory");
    pnlCart.setVisible(false);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();
    RootPanel.revalidate();
    RootPanel.repaint();
    }//GEN-LAST:event_btnNavProductsActionPerformed

    private void btnNavSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavSalesActionPerformed
 if (currentUser.getRole().equalsIgnoreCase("Cashier")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Sales Summary.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }


    }//GEN-LAST:event_btnNavSalesActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void btnNavSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavSecurityActionPerformed
 if (currentUser.getRole().equalsIgnoreCase("Cashier")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Security Logs.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
 
 
 }      
    }//GEN-LAST:event_btnNavSecurityActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
MyAccount account = new MyAccount(this, true, currentUser);
account.setVisible(true);       
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
      int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to sign out and lock terminal?", 
            "Sign Out Confirmation", 
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        // Isara ang Dashboard at bumalik sa Login Screen
        new Login().setVisible(true);
        this.dispose();
    }
  // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to sign out and lock terminal?", 
            "Sign Out Confirmation", 
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        // Isara ang Dashboard at bumalik sa Login Screen
        new Login().setVisible(true);
        this.dispose();
    }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnNavEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavEmployeesActionPerformed
       if (!currentUser.getRole().equalsIgnoreCase("Store Manager")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Employee Management.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    Employees employeesDialog = new Employees(this, true);
    employeesDialog.setLocationRelativeTo(this);
    employeesDialog.setVisible(true);
    }//GEN-LAST:event_btnNavEmployeesActionPerformed

    private void ButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonClearActionPerformed
 cart.clear();
        jTextField1.setText("");
        refreshCartTable();        
    }//GEN-LAST:event_ButtonClearActionPerformed

    private void ButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonRemoveActionPerformed
     int[] selectedRows = tblCart.getSelectedRows();

    // No item selected
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(
            this,
            "Please select at least one item to remove.",
            "Remove Item",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // Correct number of selected items
    int count = selectedRows.length;

    String message;

    if (count == 1) {
        message = "Remove 1 item from the cart?";
    } else {
        message = "Remove " + count + " items from the cart?";
    }

    int confirm = JOptionPane.showConfirmDialog(
        this,
        message,
        "Remove Item",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Remove from highest row to lowest row
    // so the indexes don't shift
    for (int i = selectedRows.length - 1; i >= 0; i--) {
        cart.remove(selectedRows[i]);
    }

    // Refresh the table and totals
    refreshCartTable();

    // Clear selection
    tblCart.clearSelection();




    }//GEN-LAST:event_ButtonRemoveActionPerformed

    private void ButtonDiscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonDiscActionPerformed
    boolean wasActive = cart.getDiscount().getRate() > 0;
        Discount next = wasActive ? new NoDiscount() : new SeniorPwdDiscount();
        cart.setDiscount(next);
        recalculateTotals();
        if (!wasActive) {
            JOptionPane.showMessageDialog(this, next.getName() + " discount applied.");
        }      
    }//GEN-LAST:event_ButtonDiscActionPerformed

    private void ButtonCompActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCompActionPerformed
if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

       double cash;

try {
    cash = Double.parseDouble(jTextField1.getText().trim());

    if (cash > 999999.99) {
        JOptionPane.showMessageDialog(
            this,
            "Cash amount cannot exceed ₱999,999.99.",
            "Invalid Cash Amount",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    if (cash < 0) {
        JOptionPane.showMessageDialog(
            this,
            "Cash amount cannot be negative.",
            "Invalid Cash Amount",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(
        this,
        "Please enter a valid cash amount.",
        "Invalid Cash Amount",
        JOptionPane.WARNING_MESSAGE
    );
    return;
}

        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? "
                   + "WHERE product_id = ? AND stock_quantity >= ?";

        try (Connection conn = ConnectDb.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (CartItem item : cart.getItems()) {
                    int piecesToDeduct = item.getTotalPiecesToDeduct();
                    ps.setInt(1, piecesToDeduct);
                    ps.setInt(2, item.getProduct().getProductId());
                    ps.setInt(3, piecesToDeduct);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this,
                            "Not enough stock for " + item.getProduct().getProductName()
                            + ". Sale cancelled.");
                        salesPanel.refreshProducts();
                        return;
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }

            JOptionPane.showMessageDialog(this, "Sale completed! Change: " + jLabel13.getText());

            cart.clear();
            jTextField1.setText("");
            refreshCartTable();
            salesPanel.refreshProducts();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error completing sale: " + e.getMessage());
        }
    }//GEN-LAST:event_ButtonCompActionPerformed

    private void btnNavExpensesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNavExpensesActionPerformed
      if (currentUser.getRole().equalsIgnoreCase("Cashier")) {
        JOptionPane.showMessageDialog(
            this,
            "You do not have permission to access Expenses.",
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    cardLayout.show(pnlMainContent, "expenses");

    pnlCart.setVisible(false);

    pnlMainContent.revalidate();
    pnlMainContent.repaint();

    RootPanel.revalidate();
    }//GEN-LAST:event_btnNavExpensesActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         javax.swing.JOptionPane.showMessageDialog(null,
        "This form cannot be run directly.\nPlease run Login.java to start the application.",
        "Cannot Run Dashboard",
        javax.swing.JOptionPane.WARNING_MESSAGE);
    
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonClear;
    private javax.swing.JButton ButtonComp;
    private javax.swing.JButton ButtonDisc;
    private javax.swing.JButton ButtonRemove;
    private javax.swing.JPanel RootPanel;
    private javax.swing.JButton btnNavEmployees;
    private javax.swing.JButton btnNavExpenses;
    private javax.swing.JButton btnNavPos;
    private javax.swing.JButton btnNavProducts;
    private javax.swing.JButton btnNavSales;
    private javax.swing.JButton btnNavSecurity;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalDue;
    private javax.swing.JLabel lblVAT;
    private javax.swing.JPanel pnlCart;
    private javax.swing.JPanel pnlMainContent;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTopBar;
    private javax.swing.JTable tblCart;
    // End of variables declaration//GEN-END:variables
 
    public void addProductToCart(Product product) {
        addProductToCart(product, "Piece", 1);
    }

    public void addProductToCart(Product product, String unitType, int quantity) {
        if (!product.isInStock()) {
            JOptionPane.showMessageDialog(this, "Out of stock: " + product.getProductName());
            return;
        }
        if ("Bulk".equalsIgnoreCase(unitType) && !product.hasBulkPricing()) {
            JOptionPane.showMessageDialog(this,
                "Bulk pricing is not configured for " + product.getProductName() + ".",
                "Bulk Not Available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!cart.add(product, unitType, quantity)) {
            JOptionPane.showMessageDialog(this,
                "Not enough stock for " + product.getProductName() + ".",
                "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refreshCartTable();
    }

    private void undoLastCartAction() {
        if (!cart.undoLast()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
            return;
        }
        refreshCartTable();
    }

    private void refreshCartTable() {
        DefaultTableModel model = (DefaultTableModel) tblCart.getModel();
        model.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            String displayName = item.getProduct().getProductName();
            if ("Bulk".equalsIgnoreCase(item.getUnitType())) {
                displayName += " (Bulk)";
            }
            model.addRow(new Object[]{
                displayName,
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
            });
        }
        recalculateTotals();
    }

    private void recalculateTotals() {
        double subtotal = cart.getSubtotal();
        double discount = cart.getDiscountAmount();
        double vat = cart.getVat();
        currentTotalDue = cart.getTotal();

        lblSubtotal.setText(String.format("₱%.2f", subtotal));
        lblDiscount.setText(String.format("-₱%.2f", discount));
        lblVAT.setText(String.format("₱%.2f", vat));
        lblTotalDue.setText(String.format("₱%.2f", currentTotalDue));

        updateChangeDue();
    }

  private void updateChangeDue() {
    DecimalFormat df = new DecimalFormat("#,##0.00");

    try {
        String input = jTextField1.getText().trim();

        if (input.isEmpty()) {
            jLabel13.setText("₱0.00");
            return;
        }

        double cash = Double.parseDouble(input);
        double change = cash - currentTotalDue;

        if (change >= 0) {
            jLabel13.setText("₱" + df.format(change));
        } else {
            jLabel13.setText("-₱" + df.format(Math.abs(change)));
        }


    } catch (NumberFormatException e) {
        jLabel13.setText("₱0.00");
    }
}
    public void refreshSalesPanel() {
        salesPanel.refreshProducts();
        pnlMainContent.revalidate();
        pnlMainContent.repaint();
    }

   

   

    
        
}