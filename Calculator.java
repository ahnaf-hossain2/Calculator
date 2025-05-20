import javax.swing.*;
import javax.swing.border.LineBorder; // To modify the border of the buttons.
// For graphixs library and event handling
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays; // For listing the buttons.
// For file operations:
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime; // to get the current date and time
import java.time.format.DateTimeFormatter; // to format the date and time

public class Calculator
{
    private int calcWidth = 360;
    private int calcHeight = 540;
    private Color softWhite     = new Color(250, 250, 252);
    private Color richNavy      = new Color(30, 45, 80);
    private Color steelGray     = new Color(100, 110, 130);
    private Color accentCyan = new Color(0, 180, 216);
    private Color customBlack    = new Color(0, 0, 0, 0);


    private final String HISTORY_FILE = "calculator_history.txt"; // File to save the history

    private String[] buttonValues = {
        "AC", "+/-", "%", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "CLH", "="
    };
    private String[] rightSymbols = {"÷", "×", "-", "+", "="};
    private String[] topSymbols = { "AC", "+/-", "%" };

    // A+B, A-B, A/B
    private String A = "0"; //when I press any value it will be stored here
    private String operator = null; // then the operator will be stored here after pressing
    private String B = null; // then the second value will be stored here

    JFrame frame = new JFrame("Calculator");
    JLabel displayLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();


    public Calculator() {
        frame.setSize(calcWidth, calcHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayLabel.setBackground(richNavy);
        displayLabel.setForeground(softWhite); // this will be the text color.
        displayLabel.setFont(new Font("Arial", Font.PLAIN, 80)); // this will be plain font. Not bold or italic.
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0"); // default text will be 0
        displayLabel.setOpaque(true); // Makes the label's background fully painted with the specified background color (customBlack).

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(displayLabel);
        frame.add(displayPanel, BorderLayout.NORTH);

        buttonsPanel.setLayout(new GridLayout(5, 4));
        buttonsPanel.setBackground(softWhite);
        frame.add(buttonsPanel);

        for (int i = 0; i < buttonValues.length; i++) {
            JButton button = new JButton();
            String buttonValue = buttonValues[i]; // store text that will diplay on the buttons
            button.setFont(new Font("Arial", Font.PLAIN, 30));
            button.setText(buttonValue);
            button.setFocusable(false);
            button.setBorder(new LineBorder(customBlack)); // border of the buttons will be custom black

            // Style buttons by following below conditions:
            if (Arrays.asList(topSymbols).contains(buttonValue)) {
                button.setBackground(softWhite);
                button.setForeground(richNavy);
            } else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                button.setBackground(accentCyan);
                button.setForeground(softWhite);
            } else {
                button.setBackground(steelGray);
                button.setForeground(softWhite);
            }
            buttonsPanel.add(button);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource(); // this will get the button after clicking
                    String buttonValue = button.getText();

                    if (buttonValue.equals("CLH")) { // Direct check for CLH
                        clearHistory();
                        JOptionPane.showMessageDialog(frame,
                                "Calculation history cleared.",
                                "History Cleared",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (Arrays.asList(rightSymbols).contains(buttonValue)) {
                        if (buttonValue == "=") {
                            if (A != null) {
                                B = displayLabel.getText();
                                double numA = Double.parseDouble(A);
                                double numB = Double.parseDouble(B);
                                String result = "";

                                if (operator == "+") {
                                    result = removeZeroDecimal(numA + numB);
                                    displayLabel.setText(result);
                                    // now for logging the history
                                    logCalculation(A + " + " + B + " = " + result);
                                } else if (operator == "-") {
                                    result = removeZeroDecimal(numA - numB);
                                    displayLabel.setText(result);
                                    logCalculation(A + " - " + B + " = " + result);
                                } else if (operator == "×") {
                                    result = removeZeroDecimal(numA * numB);
                                    displayLabel.setText(result);
                                    logCalculation(A + " × " + B + " = " + result);
                                } else if (operator == "÷") {
                                    result = removeZeroDecimal(numA / numB);
                                    displayLabel.setText(result);
                                    logCalculation(A + " ÷ " + B + " = " + result);
                                }
                                clearAll();
                            }
                        } else if ("+-×÷".contains(buttonValue)) {
                            if (operator == null) {
                                A = displayLabel.getText();
                                displayLabel.setText("0");
                                B = "0";
                            }
                            operator = buttonValue;
                        }
                    } else if (Arrays.asList(topSymbols).contains(buttonValue)) {
                        if (buttonValue == "AC") {
                            clearAll();
                            displayLabel.setText("0");
                        } else if (buttonValue == "+/-") {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay *= -1;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        } else if (buttonValue == "%") {
                            double numDisplay = Double.parseDouble(displayLabel.getText());
                            numDisplay /= 100;
                            displayLabel.setText(removeZeroDecimal(numDisplay));
                        }
                    } else { // digits or decimals (.)
                        if (buttonValue == ".") {
                            if (!displayLabel.getText().contains(buttonValue))
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                        } else if ("0123456789".contains(buttonValue)) {
                            if (displayLabel.getText() == "0") {
                                displayLabel.setText(buttonValue);
                            } else {
                                displayLabel.setText(displayLabel.getText() + buttonValue);
                            }
                        }
                    }
                }
            });
        }
        frame.setVisible(true);
    }

    private void clearHistory() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HISTORY_FILE, false))) {
            // Opening with false parameter truncates the file
            writer.println("# Calculator History - Cleared on " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (IOException e) {
            System.err.println("Error clearing history file: " + e.getMessage());
            JOptionPane.showMessageDialog(frame,
                "Could not clear calculation history: " + e.getMessage(),
                "History Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to save the calculation history to a file
    // This method will be called after each calculation
    // It will append the calculation to the history file

    private void logCalculation(String calculation) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            // Get current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            // Write to file with timestamp
            writer.println("[" + timestamp + "] " + calculation);
        } catch (IOException e) {
            System.err.println("Error writing to history file: " + e.getMessage());
            // Show error message to user
            JOptionPane.showMessageDialog(frame,
                "Could not save calculation history: " + e.getMessage(),
                "History Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    void clearAll() {
        // set all to default values
        A = "0";
        operator = null;
        B = null;
    }

    String removeZeroDecimal(double numDisplay) {
        if (numDisplay % 1 == 0) {
            return Integer.toString((int) numDisplay);
        }
        return Double.toString(numDisplay);
    }
}


/*
 * First set the private variables that need. Then add more while need.
 * Then create the frame. And make it visible
 * set the size of the frame
 * set the fram in center and turn of resizing.
 * add default close operation
 * set border layout
 *
 * For the display panel:
 * create new a lable and a panel to add the label on it.
 * now need two panel. One is for diplaying the number and another for the buttons.
 * now edit the labels, add color, text, buttons etc. Then add the label in panel and the panel in frame.
 *
 * For the buttons panel:
 * now set a grid layout for the buttons.
 * now add the buttons using a loop.
 *
 *  now make the buttons responsive by using action listener
 *  here the actionPerformed will be a mouse click
 *  e refers to the action we need to get the button that was click on
 *  after that add the setVisible to true -> frame.setVisible(true);
 *
 */
