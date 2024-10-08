import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VisualMergeSort extends JFrame {
    private static final int BLOCK_SIZE = 50;
    private static final int DELAY = 500; // milliseconds
    private int[] array;
    private JLabel[] labels;
    private JTextField inputField;
    private JButton startButton;
    private JButton infoButton;
    private JButton resetButton;
    private JButton back;
    private JPanel inputPanel;
    private JPanel sortingPanel;

    public VisualMergeSort() {
        setTitle("Visual Merge Sort");
        setSize(850, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setVisible(true);

        // Set up the input field and buttons
        inputField = new JTextField(20);
        startButton = new JButton("Merge Sort");
        infoButton = new JButton("Show Info");
        resetButton = new JButton("Reset");
        back = new JButton("Back");
        inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter numbers separated by commas:"));
        inputPanel.add(inputField);
        inputPanel.add(startButton);
        inputPanel.add(resetButton);
        inputPanel.add(infoButton);
        inputPanel.add(back);
        add(inputPanel, BorderLayout.NORTH);

        // Set up the sorting panel
        sortingPanel = new JPanel();
        sortingPanel.setLayout(null);
        add(sortingPanel, BorderLayout.CENTER);

        // Set up the start button action
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                String[] inputNumbers = input.split(",");
                array = new int[inputNumbers.length];
                labels = new JLabel[inputNumbers.length];

                int panelWidth = sortingPanel.getWidth();
                int totalBlocksWidth = inputNumbers.length * BLOCK_SIZE;
                int startX = (panelWidth - totalBlocksWidth) / 2;

                for (int i = 0; i < inputNumbers.length; i++) {
                    array[i] = Integer.parseInt(inputNumbers[i].trim());
                    labels[i] = new JLabel(String.valueOf(array[i]), SwingConstants.CENTER);
                    labels[i].setOpaque(true);
                    labels[i].setBackground(Color.CYAN);
                    labels[i].setBorder(new LineBorder(Color.BLACK)); // Add border to each block
                    labels[i].setBounds(startX + i * BLOCK_SIZE, 100, BLOCK_SIZE, BLOCK_SIZE);
                    sortingPanel.add(labels[i]);
                }
                sortingPanel.revalidate();
                sortingPanel.repaint();
                startSorting();
            }
        });

        // Set up the info button action
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfoDialog();
            }
        });

        // Set up the reset button action
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSorting();
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mainpage mp = new Mainpage();
                mp.setVisible(true);
                setVisible(false);
            }
        });
    }

    private void showInfoDialog() {
        String info = "<html><body>"
                + "<h2>Merge Sort Algorithm</h2>"
                + "<p>Merge Sort is a divide-and-conquer sorting algorithm that divides the array into halves, recursively </p>"
                + "<p>sorts each half, and then merges the sorted halves.</p>"
                + "<h3>Time Complexity:</h3>"
                + "<ul>"
                + "<li>Best Case: O(n log n)</li>"
                + "<li>Average Case: O(n log n)</li>"
                + "<li>Worst Case: O(n log n)</li>"
                + "</ul>"
                + "<h3>Space Complexity:</h3>"
                + "<ul>"
                + "<li>O(n) due to the additional space needed for merging.</li>"
                + "</ul>"
                + "</body></html>";

        JOptionPane.showMessageDialog(this, info, "Merge Sort Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void startSorting() {
        new Thread(() -> {
            try {
                mergeSort(array, 0, array.length - 1, 0); // Pass depth for visual
                highlightSorted();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void mergeSort(int[] arr, int left, int right, int depth) throws InterruptedException {
        if (left < right) {
            int mid = (left + right) / 2;

            // Display the array with blocks shifted above/below
            displayDivision(left, right, depth);

            // Recursively sort the two halves
            mergeSort(arr, left, mid, depth + 1);
            mergeSort(arr, mid + 1, right, depth + 1);

            // Merge the sorted halves
            merge(arr, left, mid, right);

            // Reset position after merge
            resetLabels();
        }
    }

    private void merge(int[] arr, int left, int mid, int right) throws InterruptedException {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            // Highlight merging elements
            labels[k].setBackground(Color.YELLOW); // Highlight current element
            labels[k + 1].setBackground(Color.YELLOW); // Highlight next element
            if (L[i] <= R[j]) {
                arr[k] = L[i++];
            } else {
                arr[k] = R[j++];
            }
            updateLabels();
            Thread.sleep(DELAY);
            labels[k].setBackground(Color.CYAN); // Reset color
            k++;
        }

        while (i < n1) {
            arr[k++] = L[i++];
            updateLabels();
            Thread.sleep(DELAY);
        }

        while (j < n2) {
            arr[k++] = R[j++];
            updateLabels();
            Thread.sleep(DELAY);
        }
    }

    private void updateLabels() {
        int panelWidth = sortingPanel.getWidth();
        int totalBlocksWidth = array.length * BLOCK_SIZE;
        int startX = (panelWidth - totalBlocksWidth) / 2;

        for (int i = 0; i < array.length; i++) {
            labels[i].setBounds(startX + i * BLOCK_SIZE, labels[i].getY(), BLOCK_SIZE, BLOCK_SIZE);
            labels[i].setText(String.valueOf(array[i])); // Update text
        }
        sortingPanel.revalidate();
        sortingPanel.repaint();
    }

    private void displayDivision(int left, int right, int depth) throws InterruptedException {
        int offset = depth * 60; // Move blocks further with increasing depth
        int panelWidth = sortingPanel.getWidth();
        int totalBlocksWidth = array.length * BLOCK_SIZE;
        int startX = (panelWidth - totalBlocksWidth) / 2;

        for (int i = 0; i < array.length; i++) {
            if (i >= left && i <= right) {
                labels[i].setBounds(startX + i * BLOCK_SIZE, 100 + offset, BLOCK_SIZE, BLOCK_SIZE);
            } else {
                labels[i].setBounds(startX + i * BLOCK_SIZE, 100, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
        sortingPanel.revalidate();
        sortingPanel.repaint();
        Thread.sleep(DELAY);
    }

    private void resetLabels() {
        int panelWidth = sortingPanel.getWidth();
        int totalBlocksWidth = array.length * BLOCK_SIZE;
        int startX = (panelWidth - totalBlocksWidth) / 2;

        for (int i = 0; i < array.length; i++) {
            labels[i].setBounds(startX + i * BLOCK_SIZE, 100, BLOCK_SIZE, BLOCK_SIZE);
        }
        sortingPanel.revalidate();
        sortingPanel.repaint();
    }

    private void highlightSorted() {
        for (JLabel label : labels) {
            label.setBackground(Color.GREEN);
        }
    }

    // Reset the sorting panel and input field
    private void resetSorting() {
        sortingPanel.removeAll();
        sortingPanel.revalidate();
        sortingPanel.repaint();
        inputField.setText("");
        array = null;
        labels = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VisualMergeSort frame = new VisualMergeSort();
            frame.setVisible(true);
        });
    }
}