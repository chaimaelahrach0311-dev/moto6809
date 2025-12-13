package ao.project;

public class TestCPU {
    public static void main(String[] args) {

        // Programme simple : LDA #$0A ; RTS
        byte[] programme = new byte[] {
                (byte) 0x86, (byte) 0x0A, // LDA #$0A
                (byte) 0x12                // RTS
        };

        // Création de la mémoire et CPU
        MEMOIRE mem = new MEMOIRE(programme);
        Register reg = new Register();
        CPU cpu = new CPU(mem, reg);

        // Reset CPU → PC à 0xFC00
        cpu.reset();
        reg.setPC(0xFC00);

        System.out.println("État initial du CPU :");
        reg.printState();

        // Exécuter instruction par instruction
        while (!cpu.isHalted()) {
            cpu.step();
            reg.printState(); // Affiche les registres après chaque step
        }

        System.out.println("État final du CPU :");
        reg.printState();
    }
}
