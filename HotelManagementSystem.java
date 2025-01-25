import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class HotelManagementSystem extends JFrame {
private JTextField customerIdField, nameField, proofField, roomNoField,
priceField;
private JTextArea displayArea;
private JComboBox<String> bedTypeComboBox, roomTypeComboBox;
private JSpinner checkInSpinner, checkOutSpinner;
public HotelManagementSystem() {
initializeUI();
}
private void initializeUI() {
setTitle("Hotel Management System");
setSize(800, 600);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
customerIdField = new JTextField(10);
nameField = new JTextField(20);
proofField = new JTextField(20);
roomNoField = new JTextField(20);
priceField = new JTextField(20);
displayArea = new JTextArea(20, 40);
displayArea.setEditable(false);
bedTypeComboBox = new JComboBox<>(new String[]{"Single Bed", "Double Bed",
"Attached"});
roomTypeComboBox = new JComboBox<>(new String[]{"AC", "Non-AC"});
SpinnerDateModel checkInModel = new SpinnerDateModel();
SpinnerDateModel checkOutModel = new SpinnerDateModel();
checkInSpinner = new JSpinner(checkInModel);
checkOutSpinner = new JSpinner(checkOutModel);
JButton addButton = new JButton("Add Customer");

JButton searchButton = new JButton("Search Customer");
setLayout(new BorderLayout());
setLocationRelativeTo(null);
JPanel buttonPanel = new JPanel();
buttonPanel.add(addButton);
buttonPanel.add(searchButton);
JPanel inputPanel = new JPanel(new GridLayout(13, 2));
inputPanel.add(new JLabel("Customer ID:"));
inputPanel.add(customerIdField);
inputPanel.add(new JLabel("Name:"));
inputPanel.add(nameField);
inputPanel.add(new JLabel("Proof:"));
inputPanel.add(proofField);
inputPanel.add(new JLabel("Check In Date:"));
inputPanel.add(checkInSpinner);
inputPanel.add(new JLabel("Check Out Date:"));
inputPanel.add(checkOutSpinner);
inputPanel.add(new JLabel("Bed Type:"));
inputPanel.add(bedTypeComboBox);
inputPanel.add(new JLabel("Room Type:"));
inputPanel.add(roomTypeComboBox);
inputPanel.add(new JLabel("Price:"));
inputPanel.add(priceField);
inputPanel.add(new JLabel("Room No:"));
inputPanel.add(roomNoField);
JScrollPane scrollPane = new JScrollPane(displayArea);
add(buttonPanel, BorderLayout.NORTH);
add(inputPanel, BorderLayout.WEST);
add(scrollPane, BorderLayout.CENTER);
addButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
addCustomer();
}
});
searchButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
createSearchFrame();
}
});
}
private void addCustomer() {
try {
Connection connection =
DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root",
"ashu@123");
String query = "INSERT INTO customers (customer_id, name, proof, check_in, check_out, price, room_no, bed_type, room_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, customerIdField.getText());
preparedStatement.setString(2, nameField.getText());
preparedStatement.setString(3, proofField.getText());
preparedStatement.setString(4, formatDateTime((Date)
checkInSpinner.getValue()));
preparedStatement.setString(5, formatDateTime((Date)
checkOutSpinner.getValue()));
preparedStatement.setString(6, calculateTotalPrice());
preparedStatement.setString(7, roomNoField.getText());
preparedStatement.setString(8,
bedTypeComboBox.getSelectedItem().toString());
preparedStatement.setString(9,
roomTypeComboBox.getSelectedItem().toString());
preparedStatement.executeUpdate();
displayArea.setText("Customer added successfully!");
preparedStatement.close();
connection.close();
} catch (SQLException ex) {
ex.printStackTrace();
displayArea.setText("Error adding customer: " + ex.getMessage());
}
}
private String formatDateTime(Date date) {
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
return dateFormat.format(date);
}
@SuppressWarnings("unused")
private boolean isCustomerIdExists(String customerId, Connection connection) throws SQLException 
{
String query = "SELECT * FROM customers WHERE customer_id = ?";
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, customerId);
ResultSet resultSet = preparedStatement.executeQuery();
boolean exists = resultSet.next();
resultSet.close();
preparedStatement.close();
return exists;
}
private void createSearchFrame() {
JFrame searchFrame = new JFrame("Search Customer");
searchFrame.setSize(400, 300);
searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
JTextField searchCustomerIdField = new JTextField(10);
JTextField searchNameField = new JTextField(20);
JTextArea searchDisplayArea = new JTextArea(10, 30);
searchDisplayArea.setEditable(false);
JButton searchButton = new JButton("Search");

JPanel searchInputPanel = new JPanel(new GridLayout(3, 2));
searchInputPanel.add(new JLabel("Customer ID:"));
searchInputPanel.add(searchCustomerIdField);
searchInputPanel.add(new JLabel("Name:"));
searchInputPanel.add(searchNameField);
searchInputPanel.add(searchButton);
JScrollPane searchScrollPane = new JScrollPane(searchDisplayArea);
searchFrame.setLayout(new BorderLayout());
searchFrame.add(searchInputPanel, BorderLayout.NORTH);
searchFrame.add(searchScrollPane, BorderLayout.CENTER);
searchButton.addActionListener(new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
searchCustomerInFrame(searchCustomerIdField.getText(),
searchNameField.getText(), searchDisplayArea);
}
});
searchFrame.setVisible(true);
}
private void searchCustomerInFrame(String customerId, String name, JTextArea
displayArea) {
try {
Connection connection =
DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root",
"ashu@123");
String query = "SELECT * FROM customers WHERE customer_id = ? OR name = ?";
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, customerId);
preparedStatement.setString(2, name);
ResultSet resultSet = preparedStatement.executeQuery();
displayArea.setText("");
while (resultSet.next()) {
displayArea.append("Customer ID: " +
resultSet.getString("customer_id") + "\n");
displayArea.append("Name: " + resultSet.getString("name") + "\n");
displayArea.append("Proof: " + resultSet.getString("proof") + "\n");
displayArea.append("Check In: " +
formatDateTime(resultSet.getTimestamp("check_in")) + "\n");
displayArea.append("Check Out: " +
formatDateTime(resultSet.getTimestamp("check_out")) + "\n");
displayArea.append("Price: " + resultSet.getString("price") + "\n");
displayArea.append("Room No: " + resultSet.getString("room_no") +
"\n");
displayArea.append("Bed Type: " + resultSet.getString("bed_type") +
"\n");
displayArea.append("Room Type: " + resultSet.getString("room_type") +
"\n");
displayArea.append("--------------\n");
}

resultSet.close();
preparedStatement.close();
connection.close();
} catch (SQLException ex) {
ex.printStackTrace();
displayArea.setText("Error searching customer: " + ex.getMessage());
}
}
private String formatDateTime(Timestamp timestamp) {
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
return dateFormat.format(timestamp);
}
private String calculateTotalPrice() {
try {
Connection connection =
DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_db", "root",
"ashu@123");
String roomType = roomTypeComboBox.getSelectedItem().toString();
String query = "SELECT price FROM rooms WHERE room_type = ?";
PreparedStatement preparedStatement = connection.prepareStatement(query);
preparedStatement.setString(1, roomType);
ResultSet resultSet = preparedStatement.executeQuery();
if (resultSet.next()) {
int pricePerDay = resultSet.getInt("price");
Date checkInDate = (Date) checkInSpinner.getValue();
Date checkOutDate = (Date) checkOutSpinner.getValue();
if (checkOutDate.before(checkInDate)) {
return "Error: Check-out date must be after check-in date";
}
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
checkInDate = dateFormat.parse(dateFormat.format(checkInDate));
checkOutDate = dateFormat.parse(dateFormat.format(checkOutDate));
long duration = (checkOutDate.getTime() - checkInDate.getTime()) / (24
* 60 * 60 * 1000);
int totalPrice = pricePerDay * (int) duration;
return String.valueOf(totalPrice);
} else {
return "Error: Room type not found";
}
} catch (Exception ex) {
ex.printStackTrace();

return "Error calculating total price: " + ex.getMessage();
}
}
public static void main(String[] args) {
SwingUtilities.invokeLater(new Runnable() {
@Override
public void run() {
new HotelManagementSystem().setVisible(true);
}
});
}
}