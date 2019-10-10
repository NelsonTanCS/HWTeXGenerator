import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GenView extends JPanel {
    private JTextField inputTextField;
    private JTextField saveToTextField;
    private JButton browseButton1;
    private JButton browseButton2;
    private JButton generateButton;
    private JPanel contentPane;
    private JComboBox typeComboBox;
    private JLabel inputLabel;
    private JLabel saveToLabel;
    private JLabel savingLabel;
    private JRadioButton classRadioButton;
    private JRadioButton manualRadioButton;
    private JTextField partsTextField;
    private JTextField customQuestion;
    private JLabel customLabel;
    private JTextField customPart;
    private JFileChooser fc;

    private File inputFile;
    private String fileName; // inputFile in string form without file extension
    private static final String TEMPLATE_EXT = "Template.tex"; /* extension that goes on the generated file */
    private static final String[] classes = {"446", "311/312", "421", "Custom"}; // TODO: custom create combobox with this as the parameter
    private static final Numbering[] numberings =
            {new Numbering("#.", "*."),
                    new Numbering("#.", "(*)"),
                    new Numbering("Problem #", "!@#*#@!")};

    public GenView() {
        $$$setupUI$$$();
        inputFile = null;
        fileName = null;

        // file chooser
        fc = new JFileChooser();
        fc.addChoosableFileFilter(new PDFFilter());

        /**
         *  Sets respective textField to reflect the chosen file
         *  Also sets the saveTo field if that was chosen before the input file
         */
        browseButton1.addActionListener(e -> {
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileSelectionMode(fc.FILES_ONLY);
            int returnVal = fc.showOpenDialog(GenView.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputTextField.setText(fc.getSelectedFile().toString());
                inputFile = fc.getSelectedFile();
                String temp = fc.getSelectedFile().getName();
                fileName = temp.substring(0, temp.indexOf("."));

                // if SaveTo field was chosen before Input field
                if (!saveToTextField.getText().isEmpty() && !saveToTextField.getText().contains(".")) {
                    saveToTextField.setText(saveToTextField.getText() + "\\" + fileName + TEMPLATE_EXT);
                } else { // SaveTo field is empty -> autofill
                    String inputString = inputFile.toString();
                    inputString = inputString.substring(0, inputString.indexOf("."));
                    saveToTextField.setText(inputString + TEMPLATE_EXT);
                }
            }
        });

        /**
         *  Lets user choose directory to save in and autofills the filename if input has already been chosen.
         *  If no filename is given, choosing the input will autofill the filename.
         */
        browseButton2.addActionListener(e -> {
            fc.setFileSelectionMode(fc.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(GenView.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (inputFile != null) { // Input before SaveTo
                    saveToTextField.setText(fc.getSelectedFile().toString() + "\\" + fileName + TEMPLATE_EXT);
                } else if (manualRadioButton.isSelected()) {
                    saveToTextField.setText(fc.getSelectedFile().toString() + "\\manual" + TEMPLATE_EXT);
                } else { // SaveTo before Input
                    saveToTextField.setText(fc.getSelectedFile().toString());
                }
            }
        });

        /**
         * Enables input from a homework pdf and disables manual input
         */
        classRadioButton.addItemListener(e -> {
            typeComboBox.setEnabled(true);
            partsTextField.setEnabled(false);
            inputTextField.setEnabled(true);
            browseButton1.setEnabled(true);
            customQuestion.setEnabled(true);
            customPart.setEnabled(true);
        });

        /**
         * Enables manual input for questions and parts and disables classes and input file
         */
        manualRadioButton.addItemListener(e -> {
            typeComboBox.setEnabled(false);
            partsTextField.setEnabled(true);
            inputTextField.setEnabled(false);
            browseButton1.setEnabled(false);
            customQuestion.setEnabled(false);
            customPart.setEnabled(false);
        });

        /**
         *  Given a valid input file and save location, generates the tex template.
         */
        generateButton.addActionListener(e -> {
            if (inputFile == null && !manualRadioButton.isSelected()) {
                JOptionPane.showMessageDialog(contentPane, "Choose an input file");
            } else if (saveToTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(contentPane, "Choose a save location");
            } else if (!saveToTextField.getText().endsWith(".tex")) {
                JOptionPane.showMessageDialog(contentPane, "Save location must have file extension \".tex\"");
            } else if (classRadioButton.isSelected()) {
                Numbering numb;
                if (classes[typeComboBox.getSelectedIndex()].equals("Custom")) {
                    String question = customQuestion.getText();
                    String part = customPart.getText();

                    // Well formed data check
                    if (!question.contains("#") && !question.contains("*")) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Custom question must contain a \"#\" or \"*\"");
                        throw new IllegalArgumentException("Custom question must contain a \"#\" or \"*\"");
                    }
                    if (part.isEmpty()) {
                        part = "!@#*#@!";
                    }

                    numb = new Numbering(question, part);
                } else {
                    numb = numberings[typeComboBox.getSelectedIndex()];
                }
                try {
                    GenModel.makeTemplate(inputFile, saveToTextField.getText(), numb);
                    JOptionPane.showMessageDialog(contentPane, "Done");
                    File file = new File(saveToTextField.getText());
                    Desktop.getDesktop().open(file);
                } catch (IOException o) {
                    o.printStackTrace();
                }
            } else if (manualRadioButton.isSelected()) {
                String numOfParts = partsTextField.getText(); // TODO: check for malformed entry
                ArrayList<Integer> questionArray = new ArrayList<Integer>();
                while (numOfParts.indexOf(",") != -1) {
                    questionArray.add(Integer.parseInt(numOfParts.substring(0, numOfParts.indexOf(","))));
                    numOfParts = numOfParts.substring(numOfParts.indexOf(",") + 1);
                }
                questionArray.add(Integer.parseInt(numOfParts));
                GenModel.printTemplate(questionArray, saveToTextField.getText());
                File file = new File(saveToTextField.getText());
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(contentPane, "Done");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TeX Generator");
        frame.setContentPane(new GenView().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * File filter for JFileChooser that only accepts PDFs
     */
    public class PDFFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if (extension != null) {
                return extension.equalsIgnoreCase("pdf");
            }
            return false;
        }

        /*
         * Get the extension of a file.
         */
        private String getExtension(File f) {
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            return ext;
        }

        public String getDescription() {
            return "PDF";
        }
    }

    private void createUIComponents() {

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 8, new Insets(0, 0, 0, 0), -1, -1));
        saveToTextField = new JTextField();
        saveToTextField.setText("");
        contentPane.add(saveToTextField, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        browseButton1 = new JButton();
        browseButton1.setText("Browse");
        contentPane.add(browseButton1, new com.intellij.uiDesigner.core.GridConstraints(4, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        browseButton2 = new JButton();
        browseButton2.setText("Browse");
        contentPane.add(browseButton2, new com.intellij.uiDesigner.core.GridConstraints(5, 6, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        typeComboBox = new JComboBox();
        typeComboBox.setEnabled(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("446");
        defaultComboBoxModel1.addElement("311/312");
        defaultComboBoxModel1.addElement("421");
        defaultComboBoxModel1.addElement("Custom");
        typeComboBox.setModel(defaultComboBoxModel1);
        contentPane.add(typeComboBox, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        inputLabel = new JLabel();
        inputLabel.setText("Input");
        contentPane.add(inputLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveToLabel = new JLabel();
        saveToLabel.setText("Save To");
        contentPane.add(saveToLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        savingLabel = new JLabel();
        savingLabel.setText("");
        contentPane.add(savingLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(10, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        contentPane.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(30, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        contentPane.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 7, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(30, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        contentPane.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        contentPane.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 10), null, 0, false));
        classRadioButton = new JRadioButton();
        classRadioButton.setSelected(true);
        classRadioButton.setText("Class");
        contentPane.add(classRadioButton, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        contentPane.add(separator1, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        manualRadioButton = new JRadioButton();
        manualRadioButton.setEnabled(true);
        manualRadioButton.setText("Manual");
        contentPane.add(manualRadioButton, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partsTextField = new JTextField();
        partsTextField.setEnabled(false);
        partsTextField.setText("");
        partsTextField.setToolTipText("Comma-separated list of number of parts per question (no spaces)");
        contentPane.add(partsTextField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(100, -1), 0, false));
        generateButton = new JButton();
        generateButton.setText("Generate");
        contentPane.add(generateButton, new com.intellij.uiDesigner.core.GridConstraints(6, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(87, 30), null, 0, false));
        customQuestion = new JTextField();
        contentPane.add(customQuestion, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(50, -1), 0, false));
        inputTextField = new JTextField();
        inputTextField.setColumns(0);
        inputTextField.setEditable(true);
        inputTextField.setText("");
        contentPane.add(inputTextField, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(300, -1), null, 0, false));
        customLabel = new JLabel();
        customLabel.setText("Custom:");
        contentPane.add(customLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        customPart = new JTextField();
        customPart.setHorizontalAlignment(2);
        contentPane.add(customPart, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(50, -1), new Dimension(50, -1), new Dimension(50, -1), 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(manualRadioButton);
        buttonGroup.add(classRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
