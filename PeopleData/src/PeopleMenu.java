// imports for swing components
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
// import for layouts colors fonts
import java.awt.*;
// imports for action/mouse events
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// import for dynamic array
import java.util.ArrayList;
// import for current year
import java.time.Year;
// imports for database
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // data list and selected index
    ArrayList<Person> listPerson;
    private int selectedIndex = -1; // currently no row selected/add mode
    private Database database;

    // main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // create window object
            PeopleMenu menu = new PeopleMenu();

            // set content pane and background
            menu.setContentPane(menu.mainPanel);
            menu.getContentPane().setBackground(Color.WHITE);

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

        // initialize person list
        listPerson = new ArrayList<>();
        database = new Database();

        // fill list with dummy data
        populateList();

        // fill table with list data
        personTable.setModel(setTable());

        // change title styling
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

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
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // create title label
        titleLabel = new JLabel("Musician Data", SwingConstants.CENTER);

        // create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        // create text fields
        idField = new JTextField(20);
        namaField = new JTextField(20);
        tahunLahirField = new JTextField(20);

        // create combo boxes
        kategoriComboBox = new JComboBox<>();
        genreComboBox = new JComboBox<>();

        // create slider and value label
        popularitySlider = new JSlider();
        popularityValueLabel = new JLabel("50");

        // create radio buttons
        soloRadioButton = new JRadioButton("Solo");
        duoRadioButton = new JRadioButton("Duo");
        bandRadioButton = new JRadioButton("Band");
        soloRadioButton.setBackground(Color.WHITE);
        duoRadioButton.setBackground(Color.WHITE);
        bandRadioButton.setBackground(Color.WHITE);

        // create tipe panel for radios
        JPanel tipePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tipePanel.setBackground(Color.WHITE);
        tipePanel.add(soloRadioButton);
        tipePanel.add(duoRadioButton);
        tipePanel.add(bandRadioButton);

        // create slider panel with value label
        JPanel sliderPanel = new JPanel(new BorderLayout(8, 0));
        sliderPanel.setBackground(Color.WHITE);
        sliderPanel.add(popularitySlider, BorderLayout.CENTER);
        sliderPanel.add(popularityValueLabel, BorderLayout.EAST);

        // define labels and fields arrays
        String[] labels = {"ID", "Name", "Birth Year", "Category (Role)", "Genre", "Popularity", "Type"};
        JComponent[] fields = {idField, namaField, tahunLahirField, kategoriComboBox, genreComboBox, sliderPanel, tipePanel};

        // add each label and field to form
        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0.25;
            formPanel.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            c.weightx = 0.75;
            formPanel.add(fields[i], c);
        }

        // create buttons
        addUpdateButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
        deleteButton = new JButton("Delete");

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addUpdateButton);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(deleteButton);

        // create top panel for form and buttons
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // create table with scroll pane
        personTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(personTable);

        // add all panels to main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // set table area height
        scrollPane.setPreferredSize(new Dimension(700, 250));
    }

    // function to fill list with dummy data
    private void populateList() {
        // add dummy musicians data
        listPerson.add(new Person("MUS001", "Han Jisung", 2000, "Vocalist", "K-Pop", 93, "Band"));
        listPerson.add(new Person("MUS002", "Gaon", 2002, "Guitarist", "Rock", 82, "Band"));
        listPerson.add(new Person("MUS003", "Sabrina Carpenter", 1999, "Vocalist", "Pop", 95, "Solo"));
        listPerson.add(new Person("MUS004", "Gerard Way", 1977, "Vocalist", "Rock", 90, "Band"));
        listPerson.add(new Person("MUS005", "Taka Moriuchi", 1988, "Vocalist", "Rock", 88, "Band"));
        listPerson.add(new Person("MUS006", "Lee Suhyun", 1999, "Vocalist", "K-Pop", 86, "Duo"));
        listPerson.add(new Person("MUS007", "Wendy", 1994, "Vocalist", "K-Pop", 89, "Band"));
        listPerson.add(new Person("MUS008", "IU", 1993, "Vocalist", "K-Pop", 97, "Solo"));
        listPerson.add(new Person("MUS009", "Gunil", 1998, "Drummer", "Rock", 78, "Band"));
        listPerson.add(new Person("MUS010", "Dongmyeong", 2000, "Keyboardist", "Rock", 75, "Band"));

        // reset selected index
        selectedIndex = -1;
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

        // validate duplicate id
        for (int i = 0; i < listPerson.size(); i++) {
            // compare id case insensitive
            if (listPerson.get(i).getId().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(null, "ID already exists! Use another ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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

        // add data to list
        listPerson.add(new Person(id, nama, tahunLahir, kategori, genre, popularitas, tipe));

        // tambahkan data ke database
        String sqlQuery = "INSERT INTO person VALUES('" + id + "', '" + nama + "', '" + tahunLahir + "', '" + kategori + "', '" + genre + "', '" + popularitas + "', '" + tipe + "')";
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
        if (selectedIndex < 0 || selectedIndex >= listPerson.size()) {
            JOptionPane.showMessageDialog(null, "Select the data you want to update.");
            return;
        }

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

        // validate duplicate id
        for (int i = 0; i < listPerson.size(); i++) {
            // skip current row on update
            if (i == selectedIndex) {
                continue;
            }
            // compare id case insensitive
            if (listPerson.get(i).getId().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(null, "ID already exists! Use another ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
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

        // update data in list
        // get old id for WHERE clause (table holds DB truth, fallback to list)
        String oldId = listPerson.get(selectedIndex).getId();
        try {
            Object idVal = personTable.getValueAt(selectedIndex, 1);
            if (idVal != null) {
                oldId = idVal.toString();
            }
        } catch (Exception ex) {
            // keep list-based oldId
        }

        Person p = listPerson.get(selectedIndex);
        p.setId(id);
        p.setNama(nama);
        p.setTahunLahir(tahunLahir);
        p.setKategori(kategori);
        p.setGenre(genre);
        p.setPopularitas(popularitas);
        p.setTipe(tipe);

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
        if (selectedIndex < 0 || selectedIndex >= listPerson.size()) {
            JOptionPane.showMessageDialog(null, "Select the data you want to delete.");
            return;
        }

        // remove data from list
        listPerson.remove(selectedIndex);

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
