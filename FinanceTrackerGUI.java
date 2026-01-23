/* run the below 2 commands in terminal for frontend
javac -encoding UTF-8 FinanceTrackerGUI.java
java FinanceTrackerGUI
*/

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class FinanceTrackerGUI extends JFrame {
    private Audit audit;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private JLabel totalLabel, countLabel, avgLabel;
    private File currentFile;
    
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color ACCENT = new Color(16, 185, 129);
    private static final Color BG = new Color(249, 250, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(31, 41, 55);
    
    public FinanceTrackerGUI() {
        audit = new Audit();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Personal Finance Tracker");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG);
        
        mainPanel.add(createHomeScreen(), "home");
        mainPanel.add(createFileScreen(), "file");
        mainPanel.add(createViewScreen(), "view");
        mainPanel.add(createAddScreen(), "add");
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHomeScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        
        JPanel header = new JPanel();
        header.setBackground(PRIMARY);
        header.setBorder(new EmptyBorder(40, 40, 40, 40));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Finance Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Take control of your expenses");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(224, 231, 255));
        
        header.add(title);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitle);
        p.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(60, 60, 60, 60));
        
        JPanel buttons = new JPanel(new GridLayout(1, 2, 30, 0));
        buttons.setOpaque(false);
        buttons.setMaximumSize(new Dimension(900, 220));
        
        buttons.add(createActionCard("[OPEN]", "Open File", "Load existing records", 
            e -> cardLayout.show(mainPanel, "file")));
        buttons.add(createActionCard("[NEW]", "New File", "Start fresh", 
            e -> cardLayout.show(mainPanel, "file")));
        
        center.add(buttons);
        p.add(center);
        
        return p;
    }
    
    private JPanel createFileScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        
        JPanel header = createHeader("File Manager", "Open or create a file");
        p.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        
        JPanel card = createCard();
        card.setBackground(PRIMARY);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(40, 40, 40, 40)));
        
        JLabel lbl = new JLabel("Enter Details");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(Color.WHITE);
        card.add(lbl);
        card.add(Box.createVerticalStrut(30));
        
        JTextField monthField = createTextField("");
        JTextField yearField = createTextField("");
        
        card.add(createFieldWhiteLabel("Month (MM):", monthField));
        card.add(Box.createVerticalStrut(20));
        card.add(createFieldWhiteLabel("Year (YYYY):", yearField));
        card.add(Box.createVerticalStrut(40));
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(PRIMARY);
        
        JButton back = createButton("Back", false);
        back.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        
        JButton open = createButton("Open Existing File", true);
        open.setBackground(PRIMARY);
        open.setForeground(Color.BLACK);
        open.addActionListener(e -> loadFile(monthField.getText(), yearField.getText(), true));
        
        JButton create = createButton("Create New File", true);
        create.setBackground(ACCENT);
        create.setForeground(Color.BLACK);
        create.addActionListener(e -> loadFile(monthField.getText(), yearField.getText(), false));
        
        btns.add(back);
        btns.add(open);
        btns.add(create);
        card.add(btns);
        
        center.add(card);
        p.add(center);
        
        return p;
    }
    
    private JPanel createViewScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        
        JPanel header = createHeader("Transactions", "Manage your expenses");
        p.add(header, BorderLayout.NORTH);
        
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setBackground(BG);
        stats.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        totalLabel = new JLabel("$0.00");
        countLabel = new JLabel("0");
        avgLabel = new JLabel("$0.00");
        
        stats.add(createStat("[$]", "Total", totalLabel, ACCENT));
        stats.add(createStat("[#]", "Count", countLabel, PRIMARY));
        stats.add(createStat("[^]", "Average", avgLabel, new Color(99, 102, 241)));
        
        p.add(stats, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BG);
        tablePanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        String[] cols = {"#", "Category", "Name", "Cost", "Payment", "Details"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setRowHeight(45);
        transactionTable.setShowGrid(false);
        transactionTable.setSelectionBackground(new Color(224, 231, 255));
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(new Color(243, 244, 246));
        
        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        scroll.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scroll);
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setOpaque(false);
        
        JButton add = createButton("+ Add Transaction", true);
        add.setForeground(Color.BLACK);
        add.addActionListener(e -> cardLayout.show(mainPanel, "add"));
        
        JButton save = createButton("Save File", true);
        save.setForeground(Color.BLACK);
        save.addActionListener(e -> saveFile());
        
        JButton home = createButton("Home", false);
        home.addActionListener(e -> cardLayout.show(mainPanel, "home"));
        
        btns.add(add);
        btns.add(save);
        btns.add(home);
        tablePanel.add(btns, BorderLayout.SOUTH);
        
        p.add(tablePanel);
        return p;
    }
    
    private JPanel createAddScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        
        JPanel header = createHeader("Add Transaction", "Record new expense");
        p.add(header, BorderLayout.NORTH);
        
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);
        
        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(card.getBorder(), new EmptyBorder(30, 40, 30, 40)));
        
        JLabel titleLbl = new JLabel("Transaction Details");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(TEXT);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(20));
        
        JTextField nameF = createTextField("Transaction name");
        JTextField costF = createTextField("0.00");
        JTextField payF = createTextField("Cash/Credit/Debit");
        
        String[] cats = {"Food", "Transportation", "Entertainment", "Home"};
        JComboBox<String> catBox = new JComboBox<>(cats);
        catBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel details = new JPanel(new CardLayout());
        details.setOpaque(false);
        CardLayout detailLayout = (CardLayout) details.getLayout();
        
        JTextField tipsF = createTextField("0.00");
        JTextField storeF = createTextField("Store name");
        JPanel foodP = new JPanel();
        foodP.setLayout(new BoxLayout(foodP, BoxLayout.Y_AXIS));
        foodP.setOpaque(false);
        foodP.add(createField("Tip Amount ($):", tipsF));
        foodP.add(Box.createVerticalStrut(15));
        foodP.add(createField("Store/Restaurant Name:", storeF));
        
        JTextField transF = createTextField("Car/Bus/Train/Uber");
        JPanel transP = new JPanel();
        transP.setLayout(new BoxLayout(transP, BoxLayout.Y_AXIS));
        transP.setOpaque(false);
        transP.add(createField("Transportation Type:", transF));
        
        JCheckBox subBox = new JCheckBox("Is this a subscription?");
        subBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subBox.setOpaque(false);
        JPanel entP = new JPanel();
        entP.setLayout(new BoxLayout(entP, BoxLayout.Y_AXIS));
        entP.setOpaque(false);
        entP.add(subBox);
        
        JTextField homeF = createTextField("Utility/Rent/Mortgage/etc");
        JPanel homeP = new JPanel();
        homeP.setLayout(new BoxLayout(homeP, BoxLayout.Y_AXIS));
        homeP.setOpaque(false);
        homeP.add(createField("Home Expense Type:", homeF));
        
        details.add(foodP, "Food");
        details.add(transP, "Transportation");
        details.add(entP, "Entertainment");
        details.add(homeP, "Home");
        
        catBox.addActionListener(e -> detailLayout.show(details, (String)catBox.getSelectedItem()));
        
        card.add(createField("Transaction Name:", nameF));
        card.add(Box.createVerticalStrut(15));
        card.add(createField("Cost ($):", costF));
        card.add(Box.createVerticalStrut(15));
        card.add(createField("Payment Method:", payF));
        card.add(Box.createVerticalStrut(15));
        card.add(createField("Category:", catBox));
        card.add(Box.createVerticalStrut(15));
        
        JLabel detailsLbl = new JLabel("Additional Details:");
        detailsLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailsLbl.setForeground(TEXT);
        detailsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(detailsLbl);
        card.add(Box.createVerticalStrut(10));
        
        card.add(details);
        card.add(Box.createVerticalStrut(30));
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);
        
        JButton cancel = createButton("Cancel", false);
        cancel.addActionListener(e -> {
            nameF.setText("");
            costF.setText("");
            cardLayout.show(mainPanel, "view");
        });
        
        JButton save = createButton("Save", true);
        save.setForeground(Color.BLACK);
        save.addActionListener(e -> {
            try {
                String name = nameF.getText().trim();
                double cost = Double.parseDouble(costF.getText().trim());
                String pay = payF.getText().trim();
                String cat = (String)catBox.getSelectedItem();
                
                Transaction t = null;
                if (cat.equals("Food")) {
                    t = new Food(name, cost, pay, Double.parseDouble(tipsF.getText()), storeF.getText());
                } else if (cat.equals("Transportation")) {
                    t = new Transportation(name, cost, pay, transF.getText());
                } else if (cat.equals("Entertainment")) {
                    t = new Entertainment(name, cost, pay, subBox.isSelected());
                } else if (cat.equals("Home")) {
                    t = new Home(name, cost, pay, homeF.getText());
                }
                
                if (t != null) {
                    audit.addTransaction(t);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Transaction added!", "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    nameF.setText("");
                    costF.setText("");
                    cardLayout.show(mainPanel, "view");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input!", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btns.add(cancel);
        btns.add(save);
        card.add(btns);
        
        center.add(card);
        p.add(center);
        return p;
    }
    
    private JPanel createHeader(String title, String sub) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PRIMARY);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 32));
        t.setForeground(Color.WHITE);
        
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        s.setForeground(new Color(224, 231, 255));
        
        p.add(t);
        p.add(Box.createVerticalStrut(5));
        p.add(s);
        return p;
    }
    
    private JPanel createCard() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return p;
    }
    
    private JButton createActionCard(String icon, String title, String sub, ActionListener al) {
        JButton b = new JButton();
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setBackground(CARD);
        b.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
            new EmptyBorder(40, 30, 40, 30)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        
        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        ic.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(TEXT);
        t.setAlignmentX(CENTER_ALIGNMENT);
        
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        s.setForeground(new Color(107, 114, 128));
        s.setAlignmentX(CENTER_ALIGNMENT);
        
        b.add(ic);
        b.add(Box.createVerticalStrut(15));
        b.add(t);
        b.add(Box.createVerticalStrut(5));
        b.add(s);
        b.addActionListener(al);
        
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    new EmptyBorder(40, 30, 40, 30)
                ));
            }
            public void mouseExited(MouseEvent e) {
                b.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                    new EmptyBorder(40, 30, 40, 30)
                ));
            }
        });
        
        return b;
    }
    
    private JPanel createStat(String icon, String label, JLabel val, Color c) {
        JPanel p = createCard();
        p.setLayout(new BorderLayout());
        p.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(c, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel ic = new JLabel(icon);
        ic.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(107, 114, 128));
        
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(c);
        
        txt.add(lbl);
        txt.add(Box.createVerticalStrut(5));
        txt.add(val);
        
        p.add(ic, BorderLayout.WEST);
        p.add(Box.createHorizontalStrut(15), BorderLayout.CENTER);
        p.add(txt, BorderLayout.EAST);
        
        return p;
    }
    
    private JTextField createTextField(String ph) {
        JTextField f = new JTextField(20);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return f;
    }
    
    private JPanel createField(String lbl, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT);
        
        p.add(l, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
    
    private JPanel createFieldWhiteLabel(String lbl, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel l = new JLabel(lbl);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(Color.WHITE);
        
        p.add(l, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
    
    private JButton createButton(String txt, boolean primary) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (primary) {
            b.setForeground(Color.WHITE);
            b.setBackground(PRIMARY);
            b.setBorder(new EmptyBorder(12, 24, 12, 24));
        } else {
            b.setForeground(TEXT);
            b.setBackground(Color.WHITE);
            b.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(12, 24, 12, 24)
            ));
        }
        
        return b;
    }
    
    private void loadFile(String month, String year, boolean open) {
        if (month.isEmpty() || year.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter month and year!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = month + "_" + year + ".txt";
        currentFile = new File(name);
        
        try {
            if (open) {
                Scanner s = new Scanner(currentFile);
                audit.clearTransactions();
                audit.populateTransactions(s);
                s.close();
            } else {
                currentFile.createNewFile();
                audit.clearTransactions();
            }
            updateTable();
            cardLayout.show(mainPanel, "view");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            JOptionPane.showMessageDialog(this, "No file loaded!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            PrintWriter w = new PrintWriter(currentFile);
            ArrayList<Transaction> trans = audit.getTransactions();
            for (Transaction t : trans) {
                w.write(t.toFileString());
            }
            w.close();
            JOptionPane.showMessageDialog(this, "File saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Save error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        ArrayList<Transaction> trans = audit.getTransactions();
        
        for (int i = 0; i < trans.size(); i++) {
            Transaction t = trans.get(i);
            String cat = t.getClass().getSimpleName();
            String details = "";
            
            if (t instanceof Food) {
                Food f = (Food)t;
                details = "Tips: $" + f.getTips() + ", " + f.getStoreName();
            } else if (t instanceof Transportation) {
                details = ((Transportation)t).getTypeOfTransport();
            } else if (t instanceof Entertainment) {
                details = ((Entertainment)t).getSubscription() ? "Subscription" : "One-time";
            } else if (t instanceof Home) {
                details = ((Home)t).getCategory();
            }
            
            tableModel.addRow(new Object[]{
                i+1, cat, t.getName(), "$" + String.format("%.2f", t.getCost()),
                t.getTypeOfPayment(), details
            });
        }
        
        double total = audit.totalCost(trans);
        totalLabel.setText("$" + String.format("%.2f", total));
        countLabel.setText(String.valueOf(trans.size()));
        avgLabel.setText(trans.size() > 0 ? "$" + String.format("%.2f", total/trans.size()) : "$0.00");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FinanceTrackerGUI());
    }
}