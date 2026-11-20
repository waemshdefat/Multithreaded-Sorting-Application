package edu.aabu.os.sorting;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Random;

/**
 * Modern Swing desktop UI for the multithreaded sorting application.
 *
 * <p>All mutations of Swing components happen on the Event Dispatch Thread.
 * The {@link ProgressListener} passed to the controller explicitly wraps its
 * callbacks in {@code SwingUtilities.invokeLater()}.</p>
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color BG = new Color(9, 15, 28);
    private static final Color PANEL = new Color(16, 25, 42);
    private static final Color PANEL_2 = new Color(23, 35, 56);
    private static final Color BORDER = new Color(48, 68, 99);
    private static final Color TEXT = new Color(226, 232, 240);
    private static final Color MUTED = new Color(148, 163, 184);
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color PURPLE = new Color(167, 139, 250);
    private static final Color SUCCESS = new Color(52, 211, 153);
    private static final Color DANGER = new Color(248, 113, 113);

    private static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BOLD = new Font("SansSerif", Font.BOLD, 13);
    private static final Font MONO = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font TITLE = new Font("SansSerif", Font.BOLD, 26);
    private static final Font SECTION = new Font("SansSerif", Font.BOLD, 12);

    private static final String[] TEST_CASES = {
            "Small array", "Large array (40)", "Already sorted", "Reverse sorted",
            "Duplicates", "Negative numbers", "Odd-sized array"
    };

    private JTextArea inputArea;
    private JTextField randomCountField;
    private JComboBox<String> testCaseBox;
    private JButton generateButton;
    private JButton startButton;
    private JButton resetButton;

    private JLabel statusLabel;
    private JLabel threadCountLabel;
    private JLabel timeLabel;
    private JLabel validationLabel;
    private JLabel comparisonLabel;

    private JTextArea originalArea;
    private JTextArea firstHalfArea;
    private JTextArea secondHalfArea;
    private JTextArea sortedFirstArea;
    private JTextArea sortedSecondArea;
    private JTextArea finalArea;

    private ProcessPanel processPanel;
    private SortingController controller;
    private String lastGeneratedText = null;

    public MainFrame() {
        super("Multithreaded Sorting Application - Al al-Bayt University");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(BG);
        setLayout(new BorderLayout(14, 14));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadExample("Small array");
        setStatus("Ready. Enter integers or choose a test case, then press Start Sorting.", false);

        setSize(1280, 880);
        setMinimumSize(new Dimension(1040, 720));
        setLocationRelativeTo(null);
    }

    /* ------------------------------------------------------------------ */
    /* Construction                                                        */
    /* ------------------------------------------------------------------ */

    private JComponent createHeader() {
        GradientPanel header = new GradientPanel();
        header.setLayout(new BorderLayout(8, 8));
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Multithreaded Sorting Application");
        title.setFont(TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Operating Systems (901332)  |  Six actual Java threads  |  Parallel Merge Sort");
        subtitle.setFont(BODY);
        subtitle.setForeground(new Color(186, 215, 245));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        text.add(title);
        text.add(subtitle);

        JPanel chips = new JPanel();
        chips.setOpaque(false);
        chips.add(chip("Al al-Bayt University", ACCENT));
        chips.add(chip("First Semester 2026", PURPLE));
        chips.add(chip("Dr. Ashraf Al-Aoun", SUCCESS));

        header.add(text, BorderLayout.WEST);
        header.add(chips, BorderLayout.EAST);
        return header;
    }

    private JLabel chip(String text, Color color) {
        JLabel label = new JLabel("  " + text + "  ");
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(color);
        label.setBorder(BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(),
                color.getBlue(), 110), 1, true));
        return label;
    }

    private JComponent createContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 12);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.32;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        content.add(createControlPanel(), constraints);

        constraints.gridx = 1;
        constraints.insets = new Insets(0, 12, 0, 0);
        constraints.weightx = 0.68;
        content.add(createResultsPanel(), constraints);
        return content;
    }

    private JPanel createControlPanel() {
        JPanel panel = card();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 0, 5, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(sectionLabel("INPUT AND CONTROL"), c);

        c.gridy++;
        panel.add(createLabel("Integers (separated by commas)"), c);

        inputArea = new JTextArea(5, 18);
        inputArea.setFont(MONO);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(PANEL_2);
        inputArea.setForeground(TEXT);
        inputArea.setCaretColor(ACCENT);
        inputArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.16;
        panel.add(scroll(inputArea), c);

        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        panel.add(createLabel("Test case preset"), c);

        testCaseBox = new JComboBox<>(TEST_CASES);
        testCaseBox.setFont(BODY);
        testCaseBox.setBackground(PANEL_2);
        testCaseBox.setForeground(TEXT);
        testCaseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focused) {
                super.getListCellRendererComponent(list, value, index, selected, focused);
                setBackground(selected ? new Color(38, 70, 105) : PANEL_2);
                setForeground(TEXT);
                return this;
            }
        });
        testCaseBox.addActionListener(event -> loadExample((String) testCaseBox.getSelectedItem()));
        c.gridy++;
        panel.add(testCaseBox, c);

        c.gridy++;
        panel.add(createLabel("Number of random elements"), c);

        JPanel randomRow = new JPanel(new GridBagLayout());
        randomRow.setOpaque(false);
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0;
        rc.gridy = 0;
        rc.weightx = 1;
        rc.fill = GridBagConstraints.HORIZONTAL;
        randomCountField = new JTextField("20");
        styleField(randomCountField);
        randomRow.add(randomCountField, rc);

        generateButton = makeButton("Generate Random", ACCENT);
        generateButton.addActionListener(event -> generateRandomNumbers());
        rc.gridx = 1;
        rc.weightx = 0;
        rc.insets = new Insets(0, 8, 0, 0);
        randomRow.add(generateButton, rc);
        c.gridy++;
        panel.add(randomRow, c);

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);
        GridBagConstraints ac = new GridBagConstraints();
        ac.gridx = 0;
        ac.gridy = 0;
        ac.weightx = 1;
        ac.fill = GridBagConstraints.HORIZONTAL;
        startButton = makeButton("START SORTING", SUCCESS);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.addActionListener(event -> startSorting());
        actions.add(startButton, ac);

        resetButton = makeButton("Reset", new Color(100, 116, 139));
        resetButton.addActionListener(event -> resetAll());
        ac.gridx = 1;
        ac.insets = new Insets(0, 8, 0, 0);
        actions.add(resetButton, ac);
        c.gridy++;
        c.insets = new Insets(12, 0, 6, 0);
        panel.add(actions, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 5, 0);
        panel.add(sectionLabel("EXECUTION STATISTICS"), c);

        JPanel stats = new JPanel(new GridBagLayout());
        stats.setOpaque(false);
        GridBagConstraints sc = new GridBagConstraints();
        sc.gridx = 0;
        sc.gridy = 0;
        sc.weightx = 1;
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.insets = new Insets(4, 0, 4, 0);

        threadCountLabel = new JLabel("Threads used: 6 workers + 1 coordinator = 7");
        timeLabel = new JLabel("Pipeline execution time: -- ms");
        validationLabel = new JLabel("Validation: not run yet");
        comparisonLabel = new JLabel("Sequential vs parallel comparison: --");
        comparisonLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (JLabel label : new JLabel[]{threadCountLabel, timeLabel, validationLabel, comparisonLabel}) {
            label.setFont(label == comparisonLabel ? label.getFont() : BOLD);
            label.setForeground(label == validationLabel ? MUTED : TEXT);
            stats.add(label, sc);
            sc.gridy++;
        }
        c.gridy++;
        panel.add(stats, c);

        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer, c);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = card();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 0, 6, 0);
        panel.add(sectionLabel("SIX-THREAD PIPELINE VISUALIZATION"), c);

        processPanel = new ProcessPanel();
        processPanel.setPreferredSize(new Dimension(700, 180));
        c.gridy++;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.28;
        panel.add(processPanel, c);

        c.gridy++;
        c.insets = new Insets(12, 0, 6, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        panel.add(sectionLabel("ARRAY STAGES"), c);

        JPanel stages = new JPanel(new GridBagLayout());
        stages.setOpaque(false);
        GridBagConstraints s = new GridBagConstraints();
        s.insets = new Insets(4, 4, 4, 4);
        s.fill = GridBagConstraints.BOTH;
        s.weightx = 0.5;
        s.weighty = 0.5;

        originalArea = resultArea();
        firstHalfArea = resultArea();
        secondHalfArea = resultArea();
        sortedFirstArea = resultArea();
        sortedSecondArea = resultArea();
        finalArea = resultArea();

        addStage(stages, s, 0, 0, "Original Array", originalArea, ACCENT);
        addStage(stages, s, 1, 0, "First Half (T2)", firstHalfArea, ACCENT);
        addStage(stages, s, 0, 1, "Second Half (T2)", secondHalfArea, PURPLE);
        addStage(stages, s, 1, 1, "Sorted First Half (T3)", sortedFirstArea, ACCENT);
        addStage(stages, s, 0, 2, "Sorted Second Half (T4)", sortedSecondArea, PURPLE);
        addStage(stages, s, 1, 2, "Final Sorted Array (T5)", finalArea, SUCCESS);

        c.gridy++;
        c.weighty = 0.72;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(stages, c);
        return panel;
    }

    private void addStage(JPanel parent, GridBagConstraints constraints, int x, int y,
                          String title, JTextArea area, Color color) {
        JPanel cell = new JPanel(new BorderLayout(4, 4));
        cell.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(SECTION);
        label.setForeground(color);
        cell.add(label, BorderLayout.NORTH);
        cell.add(scroll(area), BorderLayout.CENTER);
        constraints.gridx = x;
        constraints.gridy = y;
        parent.add(cell, constraints);
    }

    private JComponent createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(BOLD);
        statusLabel.setForeground(ACCENT);
        footer.add(statusLabel, BorderLayout.WEST);
        JLabel hint = new JLabel("T3 and T4 start together - T5 waits for both with join()");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(MUTED);
        footer.add(hint, BorderLayout.EAST);
        return footer;
    }

    /* ------------------------------------------------------------------ */
    /* Actions                                                             */
    /* ------------------------------------------------------------------ */

    private void startSorting() {
        if (controller != null && controller.isAlive()) {
            setStatus("A sorting pipeline is already running.", true);
            return;
        }

        String input = inputArea.getText().trim();
        int count;
        try {
            count = Integer.parseInt(randomCountField.getText().trim());
        } catch (NumberFormatException exception) {
            setStatus("Random element count must be a valid integer.", true);
            return;
        }

        // Random mode is explicit: only pressing "Generate Random" activates it.
        // An empty input field must therefore produce the T1 validation error.
        boolean useRandom = lastGeneratedText != null && input.equals(lastGeneratedText);

        setControlsEnabled(false);
        setStatus("Starting coordinator thread...", false);
        processPanel.setPhase(0);
        processPanel.setContext(null);

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(String message, int phase) {
                SwingUtilities.invokeLater(() -> {
                    setStatus(message, false);
                    processPanel.setPhase(phase);
                    processPanel.repaint();
                });
            }

            @Override
            public void onComplete(SortingContext context) {
                SwingUtilities.invokeLater(() -> {
                    displayResults(context);
                    setControlsEnabled(true);
                });
            }

            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    setStatus(message, true);
                    setControlsEnabled(true);
                });
            }
        };

        controller = new SortingController(input, count, useRandom, listener);
        controller.start();
    }

    private void generateRandomNumbers() {
        int count;
        try {
            count = Integer.parseInt(randomCountField.getText().trim());
        } catch (NumberFormatException exception) {
            setStatus("Random element count must be a valid integer.", true);
            return;
        }
        if (count <= 0 || count > 100_000) {
            setStatus("Random element count must be between 1 and 100000.", true);
            return;
        }
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(random.nextInt(20_001) - 10_000);
        }
        lastGeneratedText = builder.toString();
        inputArea.setText(lastGeneratedText);
        setStatus("Generated " + count + " random integers in the input field.", false);
    }

    private void resetAll() {
        lastGeneratedText = null;
        inputArea.setText("");
        randomCountField.setText("20");
        for (JTextArea area : new JTextArea[]{originalArea, firstHalfArea, secondHalfArea,
                sortedFirstArea, sortedSecondArea, finalArea}) {
            area.setText("");
        }
        threadCountLabel.setText("Threads used: 6 workers + 1 coordinator = 7");
        timeLabel.setText("Pipeline execution time: -- ms");
        validationLabel.setText("Validation: not run yet");
        validationLabel.setForeground(MUTED);
        comparisonLabel.setText("Sequential vs parallel comparison: --");
        processPanel.setContext(null);
        processPanel.setPhase(0);
        processPanel.repaint();
        setControlsEnabled(true);
        setStatus("Reset complete. Enter values or load a test case.", false);
    }

    private void loadExample(String name) {
        if (inputArea == null || name == null) {
            return;
        }
        lastGeneratedText = null;
        inputArea.setText(exampleFor(name));
        if (!"Ready".equals(statusLabel.getText().substring(2))) {
            setStatus("Loaded test case: " + name + ".", false);
        }
    }

    private String exampleFor(String name) {
        switch (name) {
            case "Large array (40)": {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 40; i++) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append((i * 37) % 101 - 50);
                }
                return builder.toString();
            }
            case "Already sorted":
                return "-20, -9, -3, 0, 4, 11, 18, 25, 30, 42";
            case "Reverse sorted":
                return "42, 30, 25, 18, 11, 4, 0, -3, -9, -20";
            case "Duplicates":
                return "7, 3, 7, 3, 7, 1, 3, 7, 1, 3";
            case "Negative numbers":
                return "-15, -2, -99, 0, -7, -2, -45, 8, -1, -99";
            case "Odd-sized array":
                return "61, 4, -8, 22, 15, 9, 0, 33, 17, -5, 71";
            case "Small array":
            default:
                return "38, 12, 91, -4, 27, 63, 5, 50, 27, 0";
        }
    }

    private void displayResults(SortingContext context) {
        originalArea.setText(formatArray(context.getOriginalArray()));
        firstHalfArea.setText(formatArray(context.getFirstHalf()));
        secondHalfArea.setText(formatArray(context.getSecondHalf()));
        sortedFirstArea.setText(formatArray(context.getSortedFirstHalf()));
        sortedSecondArea.setText(formatArray(context.getSortedSecondHalf()));
        finalArea.setText(formatArray(context.getFinalArray()));

        double parallelMs = context.getTotalNanos() / 1_000_000.0;
        double sequentialMs = context.getSequentialNanos() / 1_000_000.0;
        double ratio = parallelMs > 0 ? sequentialMs / parallelMs : 0;

        threadCountLabel.setText("Threads used: 6 workers + 1 coordinator = 7 actual Threads");
        timeLabel.setText(String.format("Pipeline execution time: %.3f ms", parallelMs));
        validationLabel.setText("Validation: " + context.getValidationMessage());
        validationLabel.setForeground(context.isValid() ? SUCCESS : DANGER);
        comparisonLabel.setText(String.format(
                "Sequential: %.3f ms | Parallel: %.3f ms | seq/par: %.2fx",
                sequentialMs, parallelMs, ratio));
        comparisonLabel.setToolTipText(
                "A ratio near 1 is expected for small inputs because thread creation costs time.");

        processPanel.setContext(context);
        processPanel.setPhase(6);
        processPanel.repaint();
        setStatus("COMPLETED - validation passed in " + String.format("%.3f", parallelMs) + " ms.", false);
    }

    private void setControlsEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
        generateButton.setEnabled(enabled);
        testCaseBox.setEnabled(enabled);
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(">  " + message);
        statusLabel.setForeground(error ? DANGER : ACCENT);
    }

    /* ------------------------------------------------------------------ */
    /* Small UI helpers                                                    */
    /* ------------------------------------------------------------------ */

    private JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY);
        label.setForeground(MUTED);
        return label;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SECTION);
        label.setForeground(ACCENT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(BODY);
        field.setBackground(PANEL_2);
        field.setForeground(TEXT);
        field.setCaretColor(ACCENT);
        field.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
    }

    private JButton makeButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setBackground(color);
        button.setForeground(new Color(8, 15, 28));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private JTextArea resultArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(MONO);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(PANEL_2);
        area.setForeground(TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return area;
    }

    private JScrollPane scroll(JTextArea area) {
        JScrollPane pane = new JScrollPane(area);
        pane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        pane.getViewport().setBackground(PANEL_2);
        return pane;
    }

    private String formatArray(int[] values) {
        if (values.length == 0) {
            return "[]";
        }
        int limit = 80;
        if (values.length <= limit) {
            return Arrays.toString(values);
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(values[i]);
        }
        builder.append(", ..., ").append(values[values.length - 1]).append("]  (showing 80 of ")
                .append(values.length).append(" values)");
        return builder.toString();
    }

    /* ------------------------------------------------------------------ */
    /* Custom painted components                                           */
    /* ------------------------------------------------------------------ */

    private static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setPaint(new GradientPaint(0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(24, 62, 96)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.dispose();
        }
    }

    /** Draws the six stages and a scaled bar chart of original vs final arrays. */
    private class ProcessPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private int phase;
        private SortingContext context;

        ProcessPanel() {
            setOpaque(false);
        }

        void setPhase(int phase) {
            this.phase = phase;
        }

        void setContext(SortingContext context) {
            this.context = context;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(PANEL_2);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g.setColor(BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            String[] labels = {"1 Init", "2 Divide", "3 Sort A", "4 Sort B", "5 Merge", "6 Validate"};
            int outer = 18;
            int gap = 8;
            int boxWidth = (getWidth() - outer * 2 - gap * 5) / 6;
            int boxHeight = 46;

            for (int i = 0; i < labels.length; i++) {
                int x = outer + i * (boxWidth + gap);
                boolean active = phase == i + 1;
                boolean done = phase > i + 1;
                Color fill = active ? ACCENT : done ? new Color(16, 105, 90) : new Color(32, 45, 68);
                g.setColor(fill);
                g.fillRoundRect(x, 16, boxWidth, boxHeight, 10, 10);
                g.setColor(active ? BG : TEXT);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(labels[i], x + (boxWidth - fm.stringWidth(labels[i])) / 2,
                        16 + boxHeight / 2 + fm.getAscent() / 2 - 2);

                if (i < labels.length - 1) {
                    g.setColor(done ? SUCCESS : MUTED);
                    g.setFont(BODY);
                    g.drawString("->", x + boxWidth + 1, 44);
                }
            }

            g.setColor(BORDER);
            g.drawLine(outer, 74, getWidth() - outer, 74);

            if (context == null) {
                g.setColor(MUTED);
                g.setFont(BODY);
                g.drawString("Run the sorting pipeline to see original and final arrays here.",
                        outer, 110);
            } else {
                int[] original = context.getOriginalArray();
                int[] sorted = context.getFinalArray();
                drawBarRow(g, "Original", original, 78, ACCENT, -1);
                drawBarRow(g, "Final sorted", sorted, 124, SUCCESS, context.getFirstHalf().length);
            }
            g.dispose();
        }

        private void drawBarRow(Graphics2D g, String title, int[] values, int y,
                                Color color, int firstHalfLength) {
            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            g.drawString(title, 18, y + 12);

            int startX = 118;
            int available = Math.max(10, getWidth() - startX - 18);
            if (values.length == 0) {
                return;
            }

            int min = values[0];
            int max = values[0];
            for (int value : values) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            int range = Math.max(1, max - min);

            int samples = Math.min(values.length, 120);
            double step = samples <= 1 ? 0 : (double) (values.length - 1) / (samples - 1);
            double width = Math.max(1.0, Math.min(14.0, (double) available / samples));

            for (int s = 0; s < samples; s++) {
                int index = samples == 1 ? 0 : (int) Math.round(s * step);
                int value = values[index];
                int height = 5 + (int) Math.round(20.0 * (value - min) / range);
                int x = startX + (int) (s * width);
                Color barColor = color;
                if (firstHalfLength >= 0) {
                    barColor = index < firstHalfLength ? ACCENT : PURPLE;
                }
                g.setColor(barColor);
                g.fillRoundRect(x, y + 28 - height, Math.max(1, (int) width - 1), height, 3, 3);
            }
        }
    }
}
