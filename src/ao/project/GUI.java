package ao.project;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GUI {

    // ===== VARIABLES GLOBALES =====
    private static Cpu cpu;

    private static JButton stepBtn, runBtn, resetBtn, editBtn;
    private static JButton applyBtn, closeBtn;
    private static JButton irqBtn, firqBtn, nmiBtn, execBtn, stopBtn;

    private static JTextField aField, bField, xField, yField, pcField, spField, ccField;
    private static JTextField sField, uField;
    private static JTextField dpField, dpBinaryField, dpBitsField;
    private static JTextArea codeArea;

    private static JFrame programFrame, registersFrame, ramWindow, romWindow;

    public static void main(String[] args) {

        // ===== INITIALISATION CPU ET MEMOIRE =====
        byte[] programmeVide = new byte[0];
        cpu = new Cpu(new Memoire(programmeVide), new Register());
        cpu.reset();

        // ===== FENÊTRE PRINCIPALE =====
        JFrame menuFrame = new JFrame("MOTO6809 - Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(1200, 120);
        menuFrame.setLocation(0, 0);
        menuFrame.getContentPane().setBackground(Color.PINK);

        // ===== MENU =====
        JMenuBar menuBar = new JMenuBar();
        JMenu windowsMenu = new JMenu("Fenêtres");

        JMenuItem programItem = new JMenuItem("Programme");
        programItem.addActionListener(e -> showProgramWindow());
        windowsMenu.add(programItem);

        JMenuItem ramItem = new JMenuItem("RAM");
        ramItem.addActionListener(e -> showRamWindow());
        windowsMenu.add(ramItem);

        JMenuItem romItem = new JMenuItem("ROM");
        romItem.addActionListener(e -> showRomWindow());
        windowsMenu.add(romItem);

        menuBar.add(windowsMenu);
        menuFrame.setJMenuBar(menuBar);

        // ===== BARRE D'OUTILS (améliorée) =====
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // bouton éditeur
        editBtn = new JButton("📝");
        editBtn.setToolTipText("Ouvrir l'éditeur");
        toolBar.add(editBtn);

        // operational buttons similar to the screenshot
        resetBtn = new JButton("RESET");
        resetBtn.setToolTipText("Reset CPU");
        toolBar.add(resetBtn);

        irqBtn = new JButton("IRQ");
        irqBtn.setToolTipText("Déclencher IRQ (stub)");
        toolBar.add(irqBtn);

        firqBtn = new JButton("FIRQ");
        firqBtn.setToolTipText("Déclencher FIRQ (stub)");
        toolBar.add(firqBtn);

        nmiBtn = new JButton("NMI");
        nmiBtn.setToolTipText("Déclencher NMI (stub)");
        toolBar.add(nmiBtn);

        execBtn = new JButton("▶ Exécuter");
        execBtn.setToolTipText("Exécuter le programme");
        toolBar.add(execBtn);

        stopBtn = new JButton("STOP");
        stopBtn.setToolTipText("Arrêter l'exécution (stub)");
        toolBar.add(stopBtn);

        // step and run (garde tes icônes)
        stepBtn = new JButton("👣");
        stepBtn.setToolTipText("Step");
        toolBar.add(stepBtn);

        runBtn = new JButton("▶ RUN");
        runBtn.setToolTipText("Run (assemble + run)");
        toolBar.add(runBtn);

        // place toolbar
        menuFrame.add(toolBar, BorderLayout.NORTH);
        menuFrame.setVisible(true);

        // ===== FENÊTRE REGISTRES (layout personnalisé) =====
        registersFrame = new JFrame("Registres");
        registersFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        registersFrame.setSize(380, 560);
        registersFrame.setLocation(0, 130);
        registersFrame.getContentPane().setBackground(Color.PINK);

        registersFrame.add(createRegistersPanel());
        registersFrame.setVisible(true);

        // ===== FENÊTRE ÉDITEUR =====
        programFrame = new JFrame("Éditeur");
        programFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        programFrame.setSize(400, 400);
        programFrame.setLocation(360, 130);
        programFrame.getContentPane().setBackground(Color.PINK);

        codeArea = new JTextArea();
        codeArea.setFont(new Font("Arial", Font.PLAIN, 14));
        codeArea.setLineWrap(true);
        codeArea.setWrapStyleWord(true);
        codeArea.setBackground(Color.PINK);
        codeArea.setForeground(Color.BLACK);
        programFrame.add(new JScrollPane(codeArea), BorderLayout.CENTER);

        JToolBar editorToolBar = new JToolBar();
        editorToolBar.setFloatable(false);
        applyBtn = new JButton("🔄");
        applyBtn.setToolTipText("Appliquer les changements");
        editorToolBar.add(applyBtn);

        closeBtn = new JButton("❌");
        closeBtn.addActionListener(e -> programFrame.setVisible(false));
        editorToolBar.add(closeBtn);

        programFrame.add(editorToolBar, BorderLayout.NORTH);
        programFrame.setVisible(false);

        // ===== LISTENERS =====
        editBtn.addActionListener(e -> programFrame.setVisible(true));

        stepBtn.addActionListener(e -> {
            cpu.step();
            updateRegisterFields();
            updateRamWindow();
            updateRomWindow();
        });

        runBtn.addActionListener(e -> {
            try {
                byte[] programme = assembleProgram(codeArea.getText());
                cpu = new Cpu(new Memoire(programme), new Register());
                cpu.reset();
                cpu.run();
                updateRegisterFields();
                updateRamWindow();
                updateRomWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage());
            }
        });

        resetBtn.addActionListener(e -> {
            cpu.reset();
            updateRegisterFields();
            updateRamWindow();
            updateRomWindow();
        });

        // boutons additionnels (stubs)
        irqBtn.addActionListener(e -> {
            // ici tu peux appeler une méthode pour simuler IRQ
            JOptionPane.showMessageDialog(null, "IRQ déclenché (stub)");
        });

        firqBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "FIRQ déclenché (stub)");
        });

        nmiBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "NMI déclenché (stub)");
        });

        execBtn.addActionListener(e -> {
            // même comportement que run mais sans réinitialiser si tu veux
            try {
                byte[] programme = assembleProgram(codeArea.getText());
                cpu = new Cpu(new Memoire(programme), new Register());
                cpu.reset();
                cpu.run();
                updateRegisterFields();
                updateRamWindow();
                updateRomWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage());
            }
        });

        stopBtn.addActionListener(e -> {
            // si ta CPU supporte un stop, appelle-le ici ; sinon stub
            JOptionPane.showMessageDialog(null, "STOP (stub)");
        });

        applyBtn.addActionListener(e -> {
            byte[] programme = assembleProgram(codeArea.getText());
            cpu = new Cpu(new Memoire(programme), new Register());
            cpu.reset();
            updateRegisterFields();
            updateRamWindow();
            updateRomWindow();
        });

        // Initial update
        updateRegisterFields();
    }

    // crée un panneau imitant l'interface des registres de la photo
    private static JPanel createRegistersPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.PINK);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.NONE;

        // Ligne 0 : PC (grand)
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        JLabel pcLabel = new JLabel("PC");
        pcLabel.setFont(new Font("Arial", Font.BOLD, 20));
        main.add(pcLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        pcField = new JTextField("FC00", 8);
        pcField.setEditable(false);
        pcField.setFont(new Font("Arial", Font.BOLD, 24));
        pcField.setForeground(Color.BLUE);
        pcField.setHorizontalAlignment(JTextField.CENTER);
        main.add(pcField, c);

        // Ligne S U
        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;
        JLabel sLabel = new JLabel("S");
        sLabel.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(sLabel, c);

        c.gridx = 1;
        sField = new JTextField("0000", 4);
        sField.setEditable(false);
        sField.setFont(new Font("Arial", Font.BOLD, 16));
        sField.setForeground(Color.BLUE);
        sField.setHorizontalAlignment(JTextField.CENTER);
        main.add(sField, c);

        c.gridx = 2;
        JLabel uLabel = new JLabel("U");
        uLabel.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(uLabel, c);

        c.gridx = 3;
        uField = new JTextField("0000", 4);
        uField.setEditable(false);
        uField.setFont(new Font("Arial", Font.BOLD, 16));
        uField.setForeground(Color.BLUE);
        uField.setHorizontalAlignment(JTextField.CENTER);
        main.add(uField, c);

        // A B and UAL block (A and B on left, UAL box at center)
        // A
        c.gridx = 0;
        c.gridy = 3;
        JLabel aLabel = new JLabel("A");
        aLabel.setFont(new Font("Arial", Font.BOLD, 18));
        main.add(aLabel, c);

        c.gridx = 1;
        aField = new JTextField("00", 3);
        aField.setEditable(false);
        aField.setFont(new Font("Arial", Font.BOLD, 18));
        aField.setForeground(Color.BLUE);
        aField.setHorizontalAlignment(JTextField.CENTER);
        main.add(aField, c);

        // UAL panel in middle
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 2;
        JPanel ualPanel = new JPanel(new BorderLayout());
        ualPanel.setBackground(new Color(200, 200, 200));
        ualPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        JLabel ualLabel = new JLabel("UAL", SwingConstants.CENTER);
        ualLabel.setFont(new Font("Arial", Font.BOLD, 22));
        ualPanel.add(ualLabel, BorderLayout.CENTER);
        ualPanel.setPreferredSize(new Dimension(160, 90));
        main.add(ualPanel, c);

        // B
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 4;
        JLabel bLabel = new JLabel("B");
        bLabel.setFont(new Font("Arial", Font.BOLD, 18));
        main.add(bLabel, c);

        c.gridx = 1;
        bField = new JTextField("00", 3);
        bField.setEditable(false);
        bField.setFont(new Font("Arial", Font.BOLD, 18));
        bField.setForeground(Color.BLUE);
        bField.setHorizontalAlignment(JTextField.CENTER);
        main.add(bField, c);

        // DP row (hex + binary)
        c.gridx = 0;
        c.gridy = 5;
        JLabel dpLabel = new JLabel("DP");
        dpLabel.setFont(new Font("Arial", Font.BOLD, 18));
        main.add(dpLabel, c);

        c.gridx = 1;
        dpField = new JTextField("00", 3);
        dpField.setEditable(false);
        dpField.setFont(new Font("Monospaced", Font.BOLD, 16));
        dpField.setForeground(Color.BLUE);
        dpField.setHorizontalAlignment(JTextField.CENTER);
        main.add(dpField, c);

        c.gridx = 2;
        dpBinaryField = new JTextField("00000100", 10);
        dpBinaryField.setEditable(false);
        dpBinaryField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        dpBinaryField.setHorizontalAlignment(JTextField.CENTER);
        main.add(dpBinaryField, c);

        // Big bits line under DP
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        dpBitsField = new JTextField("0 0 0 0 0 0 0 0");
        dpBitsField.setEditable(false);
        dpBitsField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        dpBitsField.setHorizontalAlignment(JTextField.CENTER);
        main.add(dpBitsField, c);

        // X and Y bottom
        c.gridwidth = 1;
        c.gridy = 7;
        c.gridx = 0;
        JLabel xLabel = new JLabel("X");
        xLabel.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(xLabel, c);

        c.gridx = 1;
        xField = new JTextField("0000", 4);
        xField.setEditable(false);
        xField.setFont(new Font("Arial", Font.BOLD, 16));
        xField.setForeground(Color.BLUE);
        xField.setHorizontalAlignment(JTextField.CENTER);
        main.add(xField, c);

        c.gridx = 2;
        JLabel yLabel = new JLabel("Y");
        yLabel.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(yLabel, c);

        c.gridx = 3;
        yField = new JTextField("0000", 4);
        yField.setEditable(false);
        yField.setFont(new Font("Arial", Font.BOLD, 16));
        yField.setForeground(Color.BLUE);
        yField.setHorizontalAlignment(JTextField.CENTER);
        main.add(yField, c);

        // SP / CC small fields (right side)
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        JLabel spLabel = new JLabel("SP");
        spLabel.setFont(new Font("Arial", Font.BOLD, 14));
        main.add(spLabel, c);

        c.gridx = 1;
        spField = new JTextField("0000", 4);
        spField.setEditable(false);
        spField.setFont(new Font("Arial", Font.BOLD, 14));
        spField.setForeground(Color.BLUE);
        main.add(spField, c);

        c.gridx = 2;
        JLabel ccLabel = new JLabel("CC");
        ccLabel.setFont(new Font("Arial", Font.BOLD, 14));
        main.add(ccLabel, c);

        c.gridx = 3;
        ccField = new JTextField("00", 3);
        ccField.setEditable(false);
        ccField.setFont(new Font("Arial", Font.BOLD, 14));
        ccField.setForeground(Color.BLUE);
        main.add(ccField, c);

        return main;
    }

    private static void showProgramWindow() {
        programFrame.setVisible(true);
        programFrame.toFront();
    }

    private static void showRamWindow() {
        if (ramWindow == null) {
            ramWindow = new JFrame("RAM");
            ramWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            ramWindow.setSize(220, 300);
            ramWindow.setLocation(720, 130);
        }
        updateRamWindow();
        ramWindow.setVisible(true);
        ramWindow.toFront();
    }

    private static void showRomWindow() {
        if (romWindow == null) {
            romWindow = new JFrame("ROM");
            romWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            romWindow.setSize(220, 300);
            romWindow.setLocation(960, 130);
        }
        updateRomWindow();
        romWindow.setVisible(true);
        romWindow.toFront();
    }

    private static void updateRamWindow() {
        if (ramWindow == null) return;
        ramWindow.getContentPane().removeAll();
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        StringBuilder sb = new StringBuilder();
        Memoire mem = cpu.getMem();
        for (int addr = 0x0000; addr <= 0x03FF; addr++) {
            byte b = mem.read((short) addr);
            sb.append(String.format("%04X: %02X\n", addr, b & 0xFF));
        }
        area.setText(sb.toString());
        ramWindow.add(new JScrollPane(area));
        ramWindow.revalidate();
        ramWindow.repaint();
    }

    private static void updateRomWindow() {
        if (romWindow == null) return;
        romWindow.getContentPane().removeAll();
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        Memoire mem = cpu.getMem();
        StringBuilder sb = new StringBuilder();
        for (int addr = 0xFC00; addr <= 0xFFFF; addr++) {
            byte b = mem.read((short) addr);
            sb.append(String.format("%04X: %02X\n", addr, b & 0xFF));
        }
        area.setText(sb.toString());
        romWindow.add(new JScrollPane(area));
        romWindow.revalidate();
        romWindow.repaint();
    }

    public static byte[] assembleProgram(String code) {
        String[] lignes = code.split("\n");
        List<Byte> programList = new ArrayList<>();
        for (String ligne : lignes) {
            ligne = ligne.trim();
            if (ligne.isEmpty()) continue;  // ignore les lignes vides
            String[] parts = ligne.split("\\s+");
            String opcode = parts[0].toUpperCase();
            String operand = (parts.length > 1) ? parts[1] : null;

            Instruction instr = new Instruction(opcode, operand);
            String codeHex = Instruction.getCode(instr);
            if (codeHex == null || codeHex.isEmpty()) continue; // ignore si aucun code

            int byte1 = Integer.parseInt(codeHex, 16);
            programList.add((byte) byte1);

            if (operand != null && !operand.isEmpty()) {
                String cleanOperand = Instruction.getFilter(operand);
                if (cleanOperand.length() == 2) {
                    programList.add((byte) Integer.parseInt(cleanOperand, 16));
                } else if (cleanOperand.length() == 4) {
                    programList.add((byte) Integer.parseInt(cleanOperand.substring(0, 2), 16));
                    programList.add((byte) Integer.parseInt(cleanOperand.substring(2), 16));
                }
            }
        }

        byte[] programme = new byte[programList.size()];
        for (int i = 0; i < programList.size(); i++) {
            programme[i] = programList.get(i);
        }
        return programme;
    }

    public static void updateRegisterFields() {
        if (cpu == null) return;
        Register reg = cpu.getRegister();
        aField.setText(String.format("%02X", reg.getA()));
        bField.setText(String.format("%02X", reg.getB()));
        xField.setText(String.format("%04X", reg.getX()));
        yField.setText(String.format("%04X", reg.getY()));
        pcField.setText(String.format("%04X", reg.getPC()));
        spField.setText(String.format("%04X", reg.getSP()));
        ccField.setText(String.format("%02X", reg.getCC()));

        // champs supérieurs / DP (si disponibles)
        sField.setText(String.format("%04X", reg.getS()));
        uField.setText(String.format("%04X", reg.getU()));
        dpField.setText(String.format("%02X", reg.getDP()));
        // exemple de conversion binaire sur 8 bits
        dpBinaryField.setText(String.format("%8s", Integer.toBinaryString(reg.getDP() & 0xFF)).replace(' ', '0'));
        dpBitsField.setText(dpBinaryField.getText().replace("", " ").trim().replaceAll("", " ").trim());
    }
}
