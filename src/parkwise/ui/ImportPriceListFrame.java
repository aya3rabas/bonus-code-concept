package parkwise.ui;

import parkwise.controller.ParkingLotController;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImportPriceListFrame extends JFrame {

    private final ParkingLotController controller = new ParkingLotController();

    public ImportPriceListFrame() {
        setTitle("Import Price Lists");
        setSize(500, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Import Price Lists from JSON", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton chooseButton = new JButton("Choose JSON File");

        chooseButton.addActionListener(e -> chooseJsonFile());

        panel.add(title, BorderLayout.NORTH);
        panel.add(chooseButton, BorderLayout.CENTER);

        setContentPane(panel);
    }

    private void chooseJsonFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select JSON File");

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            boolean success = controller.importPriceListsFromJson(file.getAbsolutePath());

            if (success) {
                StyledMessageDialog.showMessage(
                        this,
                        "Price lists imported successfully from JSON.",
                        "Success"
                );
            } else {
                StyledMessageDialog.showMessage(
                        this,
                        "Failed to import price lists from JSON.",
                        "Error"
                );
            }
        }
    }
}