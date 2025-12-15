package ao.project;
public class Memoire {
    private ROM rom;
    private RAM ram;

    public Memoire(byte[] programme) {
        this.rom = new ROM(programme);
        this.ram = new RAM();
    }

    public byte read(short adresse) {
        int adr = adresse & 0xFFFF;
        if (adr <= 0x03FF) {
            return ram.read(adresse);
        } else if (adr >= 0xFC00) {
            return rom.read(adresse);
        } else {
            return (byte) 0x00;
        }
    }

    public void write(short adresse, byte valeur) {
        int adr = adresse & 0xFFFF;
        if (adr <= 0x03FF) {
            ram.write(adresse, valeur);
        }
    }

}
