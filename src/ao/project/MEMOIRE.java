package ao.project;
public class MEMOIRE {
    private ROM rom;
    private RAM ram;

    public MEMOIRE(byte[] programme) {
        this.rom = new ROM(programme);
        this.ram = new RAM();
    }

    public byte read(short adresse) {
        int adr = adresse & 0xFFFF;
        if (adr <= 0x03FF) {        // ✅ RAM : 0000–03FF
            return ram.read(adresse);
        } else if (adr >= 0xFC00) { // ✅ ROM : FC00–FFFF
            return rom.read(adresse);
        } else {
            return (byte) 0x00;     // zone vide : 0400–FBFF
        }
    }

    public void write(short adresse, byte valeur) {
        int adr = adresse & 0xFFFF;
        if (adr <= 0x03FF) {        // ✅ écriture uniquement en RAM
            ram.write(adresse, valeur);
        }
    }

}
