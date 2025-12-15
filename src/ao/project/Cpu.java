package ao.project;

public class Cpu {

    private Register reg;
    private Memoire mem;
    private boolean halted = false;

    public Cpu(Memoire mem, Register reg) {
        this.mem = mem;
        this.reg = reg;
    }

    // -------------------------
    // Reset CPU
    // -------------------------
    public void reset() {
        halted = false;
        reg.reset(); // Reset tous les registres et flags
    }

    public boolean isHalted() { return halted; }

    private int fetch8() {
        int pc = reg.getPC();
        int value = mem.read((short) pc) & 0xFF;
        reg.setPC(pc + 1);
        return value;
    }

    private int fetch16() {
        int hi = fetch8();
        int lo = fetch8();
        return (hi << 8) | lo;
    }

    public void step() {
        if(halted) return;

        int opcode = fetch8();

        switch(opcode) {
            case 0x12: break; // NOP

            // LDA
            case 0x86: reg.setA(fetch8()); reg.updateNZFlags8(reg.getA()); break;
            case 0x96: reg.setA(mem.read((short) fetch8())); reg.updateNZFlags8(reg.getA()); break;
            case 0xB6: reg.setA(mem.read((short) fetch16())); reg.updateNZFlags8(reg.getA()); break;

            // LDB
            case 0xC6: reg.setB(fetch8()); reg.updateNZFlags8(reg.getB()); break;
            case 0xD6: reg.setB(mem.read((short) fetch8())); reg.updateNZFlags8(reg.getB()); break;
            case 0xF6: reg.setB(mem.read((short) fetch16())); reg.updateNZFlags8(reg.getB()); break;
            // LDX
            case 0x8E: { int value = fetch16(); reg.setX(value);reg.updateNZFlags16(value); break;}
            case 0xBE: { int addr = fetch16(); int value = mem.read((short) addr); reg.setX(value); reg.updateNZFlags16(value); break; }
            // LDY
            case 0x10: {int op2 = fetch8(); switch (op2) {
                case 0x8E: {  int value = fetch16();reg.setY(value); reg.setY(value); reg.updateNZFlags16(value);break;}
                case 0xBE: {int addr = fetch16(); int value = mem.read((short) addr);reg.setY(value); reg.updateNZFlags16(value); break;}
                default:
                    System.err.println("Unknown 0x10 opcode " + String.format("%02X", op2));
                    halted = true; }
                break;}
            // STA
            case 0x97: mem.write((short) fetch8(), (byte) reg.getA()); break;
            case 0xB7: mem.write((short) fetch16(), (byte) reg.getA()); break;
            // STB
            case 0xD7: { int addr = (reg.getDP() << 8) | fetch8(); mem.write((short) addr, (byte) reg.getB()); reg.updateNZFlags8(reg.getB()); break;}
            case 0xF7: { int addr = fetch16(); mem.write((short) addr, (byte) reg.getB()); reg.updateNZFlags8(reg.getB());break;}

            // ADD A
            case 0x8B: addA(fetch8()); break;
            case 0x9B: addA(mem.read((short) fetch8())); break;
            case 0xBB: addA(mem.read((short) fetch16())); break;

            // ADD B
            case 0xCB: addB(fetch8()); break;
            case 0xDB: addB(mem.read((short) fetch8())); break;
            case 0xFB: addB(mem.read((short) fetch16())); break;
            // ADDX
            case 0x8F: { int value = fetch16();int x = reg.getX(); int res = (x + value) & 0xFFFF; reg.setX(res); reg.updateNZFlags16(res); reg.setFlagC(x + value > 0xFFFF);  break;}
            case 0xBF: {int addr = fetch16(); int value = mem.read((short) addr); int x = reg.getX(); int res = (x + value) & 0xFFFF; reg.setX(res);  reg.updateNZFlags16(res);  reg.setFlagC(x + value > 0xFFFF);break;}

            // SUB A
            case 0x80: subA(fetch8()); break;
            case 0x90: subA(mem.read((short) fetch8())); break;
            case 0xB0: subA(mem.read((short) fetch16())); break;

            // SUB B
            case 0xC0: subB(fetch8()); break;
            case 0xD0: subB(mem.read((short) fetch8())); break;
            case 0xF0: subB(mem.read((short) fetch16())); break;

            // MUL
            case 0x3D:
                int res = reg.getA() * reg.getB();
                reg.setD(res);
                reg.updateNZFlags16(res);
                break;

            // CMP A
            case 0x81: cmpA(fetch8()); break;
            case 0x91: cmpA(mem.read((short) fetch8())); break;
            case 0xB1: cmpA(mem.read((short) fetch16())); break;

            // CMP B
            case 0xC1: cmpB(fetch8()); break;
            case 0xD1: cmpB(mem.read((short) fetch8())); break;
            case 0xF1: cmpB(mem.read((short) fetch16())); break;

            // JMP
            case 0x7E: reg.setPC(fetch16()); break;

            // BRA
            case 0x20:
                int offset = fetch8();
                if((offset & 0x80)!=0) offset |= 0xFFFFFF00; // signed
                reg.setPC(reg.getPC() + offset);
                break;

            // RTS
            case 0x39:
                reg.setPC(mem.read((short) reg.getSP()));
                reg.setSP(reg.getSP() + 2);
                break;

            // HALT (SWI)
            case 0x3F: halted = true; break;

            default:
                System.err.println("Unknown opcode "+String.format("%02X",opcode)+" at PC="+String.format("%04X",reg.getPC()));
                halted = true;
        }
    }

    public void run() {
        while(!halted) step();
    }

    private void addA(int val) {
        int a = reg.getA();
        int res = (a+val)&0xFF;
        reg.setA(res);
        reg.updateNZFlags8(res);
        reg.updateHFlag(a,val);
        reg.setFlagC(a+val>0xFF);
    }

    private void addB(int val) {
        int b = reg.getB();
        int res = (b+val)&0xFF;
        reg.setB(res);
        reg.updateNZFlags8(res);
        reg.updateHFlag(b,val);
        reg.setFlagC(b+val>0xFF);
    }

    private void subA(int val) {
        int a = reg.getA();
        int res = (a-val)&0xFF;
        reg.setA(res);
        reg.updateNZFlags8(res);
        reg.setFlagC(a>=val);
    }

    private void subB(int val) {
        int b = reg.getB();
        int res = (b-val)&0xFF;
        reg.setB(res);
        reg.updateNZFlags8(res);
        reg.setFlagC(b>=val);
    }

    private void cmpA(int val) {
        int res = (reg.getA()-val)&0xFF;
        reg.updateNZFlags8(res);
        reg.setFlagC(reg.getA()>=val);
    }

    private void cmpB(int val) {
        int res = (reg.getB()-val)&0xFF;
        reg.updateNZFlags8(res);
        reg.setFlagC(reg.getB()>=val);
    }
    public Memoire getMem() {
        return mem;
    }
    public Register getRegister() {
        return reg;
    }

}
