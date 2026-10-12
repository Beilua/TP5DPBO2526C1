// imports for swing components
import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.table.DefaultTableModel;
// import for layouts colors fonts
import java.awt.*;
// imports for action/mouse events
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import for current year
import java.time.Year;
// imports for database
import java.sql.ResultSet;
import java.sql.SQLException;
// import for custom font file
import java.io.File;

public class PeopleMenu extends JFrame {

    // ui components
    private JPanel mainPanel;
    private JTable personTable;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JComboBox<String> genreComboBox;
    private JSlider popularitySlider;
    private JLabel popularityValueLabel;
    private JRadioButton soloRadioButton;
    private JRadioButton duoRadioButton;
    private JRadioButton bandRadioButton;
    private ButtonGroup tipeButtonGroup;
    private JButton addUpdateButton;
    private JButton deleteButton;
    private JButton cancelButton;

    // selected index and database
    private int selectedIndex = -1; // currently no row selected/add mode
    private Database database;

    // theme palette
    private static final Color CREAM = new Color(0xFCF0DA);
    private static final Color OLIVE = new Color(0xAEAC78);
    private static final Color MUSTARD = new Color(0xF2C46A);
    private static final Color DARK = new Color(0x4C4541);
    private static final Color SOFT_WHITE = new Color(0xFFFEF9);

    // custom fonts
    private static Font BASE_FONT;
    private static Font TITLE_FONT;
    private static Font BODY_FONT;
    private static Font SMALL_FONT;

    // function to load cute dino font
    private static void loadCustomFont() {
        try {
            File f1 = new File("PeopleData/fonts/CuteDino.ttf");
            File f2 = new File("fonts/CuteDino.ttf");
            File f3 = new File("/home/beilua/IdeaProjects/TP5DPBO2526C1/PeopleData/fonts/CuteDino.ttf");
            File fontFile = f1.exists() ? f1 : (f2.exists() ? f2 : f3);
            BASE_FONT = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(BASE_FONT);
        } catch (Exception e) {
            System.err.println("Custom font load failed, using SansSerif: " + e.getMessage());
            BASE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        }
        TITLE_FONT = BASE_FONT.deriveFont(Font.BOLD, 24f);
        BODY_FONT = BASE_FONT.deriveFont(Font.PLAIN, 15f);
        SMALL_FONT = BASE_FONT.deriveFont(Font.PLAIN, 13f);
    }

    // main method
    public static void main(String[] args) {
        // load cute dino font first
        loadCustomFont();
        // override default blue accents with brown palette
        UIManager.put("ComboBox.selectionBackground", DARK);
        UIManager.put("ComboBox.selectionForeground", CREAM);
        UIManager.put("ComboBox.background", SOFT_WHITE);
        UIManager.put("ComboBox.foreground", DARK);
        UIManager.put("Slider.foreground", DARK);
        UIManager.put("Slider.highlight", OLIVE);
        UIManager.put("Slider.shadow", OLIVE);
        UIManager.put("Slider.background", CREAM);
        UIManager.put("RadioButton.select", DARK);
        UIManager.put("RadioButton.background", CREAM);
        UIManager.put("RadioButton.foreground", DARK);
        UIManager.put("Table.selectionBackground", OLIVE);
        UIManager.put("Table.selectionForeground", CREAM);
        UIManager.put("TextField.selectionBackground", OLIVE);
        UIManager.put("TextField.selectionForeground", DARK);
        SwingUtilities.invokeLater(() -> {
            // create window object
            PeopleMenu menu = new PeopleMenu();

            // set content pane and background
            menu.setContentPane(menu.mainPanel);
            menu.getContentPane().setBackground(CREAM);

            // set window title size and position
            menu.setTitle("Musician Data");
            menu.setSize(800, 650);
            menu.setLocationRelativeTo(null);

            // exit program when window is closed
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // show window
            menu.setVisible(true);
        });
    }

    // constructor
    public PeopleMenu() {
        // build manual ui for plain javac run
        buildManualUI();

        // initialize database
        database = new Database();

        // fill table with database data
        personTable.setModel(setTable());

        // change title styling
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(DARK);

        // fill kategori combo box
        String[] kategoriData = {"Select Role", "Vocalist", "Guitarist", "Bassist", "Drummer", "Keyboardist", "DJ/Producer"};
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        // fill genre combo box
        String[] genreData = {"Select Genre", "Pop", "Rock", "Jazz", "Classical", "Hip-Hop", "Dangdut", "K-Pop", "EDM"};
        genreComboBox.setModel(new DefaultComboBoxModel<>(genreData));

        // setup popularity slider
        popularitySlider.setMinimum(0);
        popularitySlider.setMaximum(100);
        popularitySlider.setValue(50);
        popularitySlider.setMajorTickSpacing(25);
        popularitySlider.setMinorTickSpacing(5);
        popularitySlider.setPaintTicks(true);
        popularitySlider.setPaintLabels(true);
        popularityValueLabel.setText("50");

        // update label when slider moves
        popularitySlider.addChangeListener(e -> popularityValueLabel.setText(String.valueOf(popularitySlider.getValue())));

        // setup tipe radio group
        tipeButtonGroup = new ButtonGroup();
        tipeButtonGroup.add(soloRadioButton);
        tipeButtonGroup.add(duoRadioButton);
        tipeButtonGroup.add(bandRadioButton);

        // handle add update button click
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // add new data if nothing selected
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    // update selected data
                    updateData();
                }
            }
        });

        // handle delete button click
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ask confirmation if data selected
                if (selectedIndex != -1) {
                    // show confirmation dialog
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this data?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    // delete data if user confirms
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                } else {
                    // warn if nothing selected
                    JOptionPane.showMessageDialog(null, "Select the data you want to delete.");
                }
            }
        });

        // handle cancel button click
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reset form inputs
                clearForm();
            }
        });

        // handle table row click
        personTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // get selected row index
                selectedIndex = personTable.getSelectedRow();
                if (selectedIndex == -1) {
                    return;
                }

                // fill form with selected row data
                idField.setText(personTable.getValueAt(selectedIndex, 1).toString());
                namaField.setText(personTable.getValueAt(selectedIndex, 2).toString());
                tahunLahirField.setText(personTable.getValueAt(selectedIndex, 3).toString());
                kategoriComboBox.setSelectedItem(personTable.getValueAt(selectedIndex, 4).toString());
                genreComboBox.setSelectedItem(personTable.getValueAt(selectedIndex, 5).toString());

                // fill slider with selected row data
                try {
                    int pop = Integer.parseInt(personTable.getValueAt(selectedIndex, 6).toString());
                    popularitySlider.setValue(pop);
                } catch (NumberFormatException ex) {
                    popularitySlider.setValue(50);
                }

                // fill radio with selected row data
                setSelectedTipe(personTable.getValueAt(selectedIndex, 7).toString());

                // change button to update mode
                addUpdateButton.setText("Update");

                // show delete button
                deleteButton.setVisible(true);
            }
        });

        // reset form to initial state
        clearForm();
    }

    // function to build ui manually
    private void buildManualUI() {
        // create main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CREAM);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // create title label
        titleLabel = new JLabel("Musician Data", SwingConstants.CENTER);
        titleLabel.setForeground(DARK);
        titleLabel.setFont(TITLE_FONT);

        // create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CREAM);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // create text fields
        idField = new JTextField(20);
        namaField = new JTextField(20);
        tahunLahirField = new JTextField(20);
        styleTextField(idField);
        styleTextField(namaField);
        styleTextField(tahunLahirField);

        // create combo boxes
        kategoriComboBox = new JComboBox<>();
        genreComboBox = new JComboBox<>();
        styleComboBox(kategoriComboBox);
        styleComboBox(genreComboBox);

        // create slider and value label
        popularitySlider = new JSlider();
        popularityValueLabel = new JLabel("50");

        // create radio buttons
        soloRadioButton = new JRadioButton("Solo");
        duoRadioButton = new JRadioButton("Duo");
        bandRadioButton = new JRadioButton("Band");
        styleRadioButton(soloRadioButton);
        styleRadioButton(duoRadioButton);
        styleRadioButton(bandRadioButton);

        // create tipe panel for radios
        JPanel tipePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tipePanel.setBackground(CREAM);
        tipePanel.add(soloRadioButton);
        tipePanel.add(duoRadioButton);
        tipePanel.add(bandRadioButton);

        // create slider panel with value label
        JPanel sliderPanel = new JPanel(new BorderLayout(8, 0));
        sliderPanel.setBackground(CREAM);
        sliderPanel.add(popularitySlider, BorderLayout.CENTER);
        sliderPanel.add(popularityValueLabel, BorderLayout.EAST);
        styleSlider(popularitySlider);
        popularityValueLabel.setForeground(DARK);
        popularityValueLabel.setFont(BODY_FONT);

        // define labels and fields arrays
        String[] labels = {"ID", "Name", "Birth Year", "Category (Role)", "Genre", "Popularity", "Type"};
        JComponent[] fields = {idField, namaField, tahunLahirField, kategoriComboBox, genreComboBox, sliderPanel, tipePanel};

        // add each label and field to form
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(DARK);
            lbl.setFont(BODY_FONT);
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0.25;
            formPanel.add(lbl, c);
            c.gridx = 1;
            c.weightx = 0.75;
            formPanel.add(fields[i], c);
        }

        // create buttons
        addUpdateButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
        deleteButton = new JButton("Delete");
        addUpdateButton.setBackground(MUSTARD);
        addUpdateButton.setForeground(DARK);
        addUpdateButton.setFocusPainted(false);
        addUpdateButton.setFont(BODY_FONT);
        cancelButton.setBackground(OLIVE);
        cancelButton.setForeground(DARK);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(BODY_FONT);
        deleteButton.setBackground(DARK);
        deleteButton.setForeground(CREAM);
        deleteButton.setFocusPainted(false);
        deleteButton.setFont(BODY_FONT);

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(CREAM);
        buttonPanel.add(addUpdateButton);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(deleteButton);

        // create top panel for form and buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CREAM);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // create table with scroll pane
        personTable = new JTable();
        personTable.setBackground(SOFT_WHITE);
        personTable.setForeground(DARK);
        personTable.setSelectionBackground(OLIVE);
        personTable.setSelectionForeground(CREAM);
        personTable.setGridColor(OLIVE);
        personTable.setFont(SMALL_FONT);
        personTable.setRowHeight(22);
        personTable.getTableHeader().setBackground(DARK);
        personTable.getTableHeader().setForeground(CREAM);
        personTable.getTableHeader().setFont(BODY_FONT);
        JScrollPane scrollPane = new JScrollPane(personTable);
        scrollPane.getViewport().setBackground(SOFT_WHITE);
        scrollPane.setBackground(SOFT_WHITE);

        // add all panels to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // set table area height
        scrollPane.setPreferredSize(new Dimension(700, 250));
    }

    // function to style text fields with soft white
    private void styleTextField(JTextField field) {
        field.setBackground(SOFT_WHITE);
        field.setForeground(DARK);
        field.setCaretColor(DARK);
        field.setFont(BODY_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE, 1),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    // function to style combo boxes with brown accents
    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(SOFT_WHITE);
        combo.setForeground(DARK);
        combo.setFont(BODY_FONT);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(BODY_FONT);
                if (isSelected) {
                    setBackground(DARK);
                    setForeground(CREAM);
                } else {
                    setBackground(SOFT_WHITE);
                    setForeground(DARK);
                }
                return this;
            }
        });
    }

    // function to style slider with brown track and thumb
    private void styleSlider(JSlider slider) {
        slider.setBackground(CREAM);
        slider.setForeground(DARK);
        slider.setFont(SMALL_FONT);
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cy = trackRect.y + trackRect.height / 2 - 2;
                g2.setColor(SOFT_WHITE);
                g2.fillRect(trackRect.x, cy, trackRect.width, 4);
                g2.setColor(OLIVE);
                g2.fillRect(trackRect.x, cy, thumbRect.x + thumbRect.width / 2 - trackRect.x, 4);
                g2.setColor(DARK);
                g2.drawRect(trackRect.x, cy, trackRect.width - 1, 3);
                g2.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(MUSTARD);
                g2.fillRoundRect(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, 6, 6);
                g2.setColor(DARK);
                g2.drawRoundRect(thumbRect.x, thumbRect.y, thumbRect.width - 1, thumbRect.height - 1, 6, 6);
                g2.dispose();
            }
        });
    }

    // function to style radio buttons with brown dot
    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(CREAM);
        rb.setForeground(DARK);
        rb.setFont(BODY_FONT);
        int size = 16;
        Icon unselected = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SOFT_WHITE);
                g2.fillOval(x, y, size, size);
                g2.setColor(OLIVE);
                g2.drawOval(x, y, size, size);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
        Icon selected = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SOFT_WHITE);
                g2.fillOval(x, y, size, size);
                g2.setColor(DARK);
                g2.drawOval(x, y, size, size);
                g2.setColor(DARK);
                g2.fillOval(x + 4, y + 4, size - 8, size - 8);
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
        rb.setIcon(unselected);
        rb.setSelectedIcon(selected);
    }

    // function to create table model
    public final DefaultTableModel setTable() {
        // define table columns
        Object[] cols = {"No", "ID", "Name", "Birth Year", "Category", "Genre", "Popularity", "Type"};

        // create table model with uneditable cells
        DefaultTableModel tmp = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");

            // fill table with query result
            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[8];
                row[0] = i + 1;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("tahun_lahir");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("genre");
                row[6] = resultSet.getString("popularitas");
                row[7] = resultSet.getString("tipe");
                tmp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tmp;
    }

    // function to get tipe from radio buttons
    private String getSelectedTipe() {
        // check each radio selection
        if (soloRadioButton.isSelected()) return "Solo";
        if (duoRadioButton.isSelected()) return "Duo";
        if (bandRadioButton.isSelected()) return "Band";
        return null;
    }

    // function to set radio buttons from tipe string
    private void setSelectedTipe(String tipe) {
        // clear selection if tipe is null
        if (tipe == null) {
            tipeButtonGroup.clearSelection();
            return;
        }

        // select radio based on tipe value
        switch (tipe) {
            case "Solo": soloRadioButton.setSelected(true); break;
            case "Duo": duoRadioButton.setSelected(true); break;
            case "Band": bandRadioButton.setSelected(true); break;
            default: tipeButtonGroup.clearSelection(); break;
        }
    }

    // function to insert new data
    public void insertData() {
        // get values from form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunStr = tahunLahirField.getText().trim();
        Object katObj = kategoriComboBox.getSelectedItem();
        Object genreObj = genreComboBox.getSelectedItem();
        String kategori = katObj == null ? "" : katObj.toString();
        String genre = genreObj == null ? "" : genreObj.toString();
        int popularitas = popularitySlider.getValue();
        String tipe = getSelectedTipe();

        // validate empty fields
        if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID, Name, and Birth Year cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate id format
        if (!id.matches("^MUS\\d{3}$")) {
            JOptionPane.showMessageDialog(null, "ID must be in MUSXXX format! Example: MUS001.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate duplicate id via database
        try {
            ResultSet rs = database.selectQuery("SELECT id FROM person WHERE id='" + id + "'");
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "ID already exists! Use another ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // validate nama length
        if (nama.length() < 2 || nama.length() > 50) {
            JOptionPane.showMessageDialog(null, "Name must be 2-50 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate nama format
        if (!nama.matches("^[A-Za-z .'-]+$")) {
            JOptionPane.showMessageDialog(null, "Name can only contain letters, spaces, ., ' and -!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate kategori selection
        if (katObj == null || kategoriComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a Category (Role)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate genre selection
        if (genreObj == null || genreComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a Genre!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate tipe selection
        if (tipe == null) {
            JOptionPane.showMessageDialog(null, "Please select a Type (Solo/Duo/Band)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate popularitas range
        if (popularitas < 0 || popularitas > 100) {
            JOptionPane.showMessageDialog(null, "Popularity must be between 0-100!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate tahun lahir number and range
        int tahunLahir;
        int currentYear = Year.now().getValue();
        try {
            // reject non digit input
            if (!tahunStr.matches("^[0-9]+$")) {
                throw new NumberFormatException();
            }
            tahunLahir = Integer.parseInt(tahunStr);
            if (tahunLahir < 1900 || tahunLahir > currentYear) {
                JOptionPane.showMessageDialog(null, "Birth Year must be between 1900-" + currentYear + "!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Birth Year must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // tambahkan data ke database
        String sqlQuery = "INSERT INTO person VALUES('" + id + "', '" + nama.replace("'", "''") + "', '" + tahunLahir + "', '" + kategori + "', '" + genre + "', '" + popularitas + "', '" + tipe + "')";
        database.insertUpdateDeleteQuery(sqlQuery);

        // refresh table
        personTable.setModel(setTable());

        // reset form inputs
        clearForm();

        // show success message
        System.out.println("Insert successful");
        JOptionPane.showMessageDialog(null, "Data added successfully");
    }

    // function to update selected data
    public void updateData() {
        // check if data is selected
        if (selectedIndex < 0 || selectedIndex >= personTable.getRowCount()) {
            JOptionPane.showMessageDialog(null, "Select the data you want to update.");
            return;
        }

        // get old id for WHERE clause (table holds DB truth)
        String oldId = personTable.getValueAt(selectedIndex, 1).toString();

        // get values from form
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunStr = tahunLahirField.getText().trim();
        Object katObj = kategoriComboBox.getSelectedItem();
        Object genreObj = genreComboBox.getSelectedItem();
        String kategori = katObj == null ? "" : katObj.toString();
        String genre = genreObj == null ? "" : genreObj.toString();
        int popularitas = popularitySlider.getValue();
        String tipe = getSelectedTipe();

        // validate empty fields
        if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID, Name, and Birth Year cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate id format
        if (!id.matches("^MUS\\d{3}$")) {
            JOptionPane.showMessageDialog(null, "ID must be in MUSXXX format! Example: MUS001.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate duplicate id via database (skip if unchanged)
        if (!id.equalsIgnoreCase(oldId)) {
            try {
                ResultSet rs = database.selectQuery("SELECT id FROM person WHERE id='" + id + "'");
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "ID already exists! Use another ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // validate nama length
        if (nama.length() < 3 || nama.length() > 50) {
            JOptionPane.showMessageDialog(null, "Name must be 3-50 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate nama format
        if (!nama.matches("^[A-Za-z .'-]+$")) {
            JOptionPane.showMessageDialog(null, "Name can only contain letters, spaces, ., ' and -!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate kategori selection
        if (katObj == null || kategoriComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a Category (Role)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate genre selection
        if (genreObj == null || genreComboBox.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(null, "Please select a Genre!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate tipe selection
        if (tipe == null) {
            JOptionPane.showMessageDialog(null, "Please select a Type (Solo/Duo/Band)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate popularitas range
        if (popularitas < 0 || popularitas > 100) {
            JOptionPane.showMessageDialog(null, "Popularity must be between 0-100!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validate tahun lahir number and range
        int tahunLahir;
        int currentYear = Year.now().getValue();
        try {
            // reject non digit input
            if (!tahunStr.matches("^[0-9]+$")) {
                throw new NumberFormatException();
            }
            tahunLahir = Integer.parseInt(tahunStr);
            if (tahunLahir < 1900 || tahunLahir > currentYear) {
                JOptionPane.showMessageDialog(null, "Birth Year must be between 1900-" + currentYear + "!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Birth Year must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // update data in database
        String sqlQuery = "UPDATE person SET id='" + id + "', nama='" + nama.replace("'", "''") + "', tahun_lahir='" + tahunLahir + "', kategori='" + kategori + "', genre='" + genre + "', popularitas='" + popularitas + "', tipe='" + tipe + "' WHERE id='" + oldId + "'";
        database.insertUpdateDeleteQuery(sqlQuery);

        // refresh table
        personTable.setModel(setTable());

        // reset form inputs
        clearForm();

        // show success message
        System.out.println("Update successful");
        JOptionPane.showMessageDialog(null, "Data updated successfully");
    }

    // function to delete selected data
    public void deleteData() {
        // check if data is selected
        if (selectedIndex < 0 || selectedIndex >= personTable.getRowCount()) {
            JOptionPane.showMessageDialog(null, "Select the data you want to delete.");
            return;
        }

        // get id for WHERE clause (table holds DB truth)
        String id = personTable.getValueAt(selectedIndex, 1).toString();

        // delete data from database
        String sqlQuery = "DELETE FROM person WHERE id='" + id + "'";
        database.insertUpdateDeleteQuery(sqlQuery);

        // refresh table
        personTable.setModel(setTable());

        // reset form inputs
        clearForm();

        // show success message
        System.out.println("Delete successful");
        JOptionPane.showMessageDialog(null, "Data deleted successfully");
    }

    // function to reset form inputs
    public void clearForm() {
        // clear text fields
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");

        // reset combo boxes to default
        kategoriComboBox.setSelectedIndex(0);
        genreComboBox.setSelectedIndex(0);

        // reset slider to default
        popularitySlider.setValue(50);
        popularityValueLabel.setText("50");

        // clear radio selection
        tipeButtonGroup.clearSelection();

        // change button back to add mode
        addUpdateButton.setText("Add");

        // hide delete button
        deleteButton.setVisible(false);

        // reset selected index
        selectedIndex = -1;
    }
}
