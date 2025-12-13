
    package ao.project;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

    public class GUI {

        // ===== VARIABLES GLOBALES =====
        private static CPU cpu;

        private static JButton stepBtn, runBtn, resetBtn, editBtn;
        private static JButton applyBtn, closeBtn;
        private static JTextField aField, bField, xField, yField, pcField, spField, ccField;
        private static JTextArea codeArea;

        private static JFrame programFrame, registersFrame, ramWindow, romWindow;

        public static void main(String[] args) {

            // ===== INITIALISATION CPU ET MEMOIRE =====
            byte[] programmeVide = new byte[0];
            cpu = new CPU(new MEMOIRE(programmeVide), new Register());
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

            // ===== BARRE D'OUTILS =====
            JToolBar toolBar = new JToolBar();
            editBtn = new JButton("📝");
            editBtn.setToolTipText("Ouvrir l'éditeur");
            stepBtn = new JButton("👣");
            runBtn = new JButton("▶ RUN");
            resetBtn = new JButton("RESET");

            toolBar.add(editBtn);
            toolBar.add(stepBtn);
            toolBar.add(runBtn);
            toolBar.add(resetBtn);

            menuFrame.add(toolBar, BorderLayout.NORTH);
            menuFrame.setVisible(true);

            // ===== FENÊTRE REGISTRES =====
            registersFrame = new JFrame("Registres");
            registersFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            registersFrame.setSize(350, 500);
            registersFrame.setLocation(0, 130);
            registersFrame.getContentPane().setBackground(Color.PINK);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.PINK);

            aField = createRegisterField(mainPanel, "A");
            bField = createRegisterField(mainPanel, "B");
            xField = createRegisterField(mainPanel, "X");
            yField = createRegisterField(mainPanel, "Y");
            pcField = createRegisterField(mainPanel, "PC");
            spField = createRegisterField(mainPanel, "SP");
            ccField = createRegisterField(mainPanel, "CC");

            registersFrame.add(mainPanel);
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
                    cpu = new CPU(new MEMOIRE(programme), new Register());
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

            applyBtn.addActionListener(e -> {
                byte[] programme = assembleProgram(codeArea.getText());
                cpu = new CPU(new MEMOIRE(programme), new Register());
                cpu.reset();
                updateRegisterFields();
                updateRamWindow();
                updateRomWindow();
            });

            // Initial update
            updateRegisterFields();
        }

        // ===== MÉTHODES UTILITAIRES =====
        private static JTextField createRegisterField(JPanel panel, String label) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            p.setBackground(Color.PINK);
            JLabel l = new JLabel(label);
            l.setFont(new Font("Arial", Font.BOLD, 16));
            p.add(l);
            JTextField tf = new JTextField("0000", 4);
            tf.setEditable(false);
            tf.setFont(new Font("Arial", Font.BOLD, 16));
            tf.setForeground(Color.BLUE);
            tf.setHorizontalAlignment(JTextField.CENTER);
            p.add(tf);
            panel.add(p);
            return tf;
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
                romWindow.setLocation(720, 150);
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
            MEMOIRE mem = cpu.getMem();
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
            MEMOIRE mem = cpu.getMem();
            for (int addr = 0xFC00; addr <= 0xFFFF; addr++) {
                byte b = mem.read((short) addr);
                area.append(String.format("%04X: %02X\n", addr, b & 0xFF));
            }
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
        }
    }


