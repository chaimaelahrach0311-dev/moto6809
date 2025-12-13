
package ao.project;
import java.util.Arrays;
import java.util.List;

public class Instruction {


        public String opcode;
        public String operand;
        public String mode;
        public String size;
        public static String errMsg = "";

        public Instruction(String opcode, String operand) {
            this.opcode = opcode;
            this.operand = operand;
        }

        public static final List<String> VALID_OPCODES = Arrays.asList(
                "LDA", "LDB", "LDU", "LDS", "LDX", "LDY",
                "STA", "STB",
                "INCA", "INCB", "DECA", "DECB",
                "ADDA", "ADDB", "SUBA", "SUBB",
                "EXG", "TFR", "NOP", "SWI", "END", "CLRA",
                "LSLA", "LSLB", "LSLU", "LSLS", "LSLX", "LSLY",
                "LSRA", "LSRB", "LSRU", "LSRS", "LSRX", "LSRY",
                "ROLA", "ROLB", "ROLU", "RORO", "ROLX", "ROLY",
                "RORA", "RORB", "RORU", "RORS", "RORX", "RORY",
                "CMPA","CMPB", "CMPU","CMPX", "CMPX","CMPY",
                "COMA", "COMB", "NEGA", "NEGB"
        );

        public static final List<String> VALID_LABELS = Arrays.asList(
                "JMP", "RTS", "BRA",
                "BEQ", "BNE",
                "BMI", "BPL",
                "BCC", "BCS",
                "BVC", "BVS"
        );

        public static String detectMode(Instruction instr) {
            String o = instr.operand;
            if (o == null || o.isEmpty())
                return "inherant";
            if (o.startsWith("#"))
                return "immediat";
            if (o.startsWith("$")) {
                if(o.replace("$", "").length() == 2)
                    return "direct";
                else
                    return "etendu";
            }
            if (o.contains(","))
                return "indexe";
            return "branche";
        }

        public static boolean goodSyntaxe(Instruction instruction) {
            if(!instruction.operand.contains(",") && !VALID_LABELS.contains(instruction.opcode)){
                if (instruction == null) {
                    Instruction.errMsg = "Erreur : L'instruction est nulle";
                    return false;}

                if (!VALID_OPCODES.contains(instruction.opcode)) {
                    Instruction.errMsg = "Erreur : Opcode non supporté " + instruction.opcode;
                    return false;
                }

                if (!Instruction.operandValide(instruction)) {
                    Instruction.errMsg = "Erreur : Opérande invalide pour l'opcode " + instruction.opcode;
                    return false;
                }

                if (!Instruction.isHexadecimal(instruction.operand) && instruction.operand != null) {
                    Instruction.errMsg = "Erreur : L'opérande doit être en hexadécimal " + instruction.operand;
                    return false;
                }

                if (!Instruction.operandSizeValide(instruction) && instruction.operand != null) {
                    Instruction.errMsg = "Erreur : L'opérande ne respecte pas la taille requise pour " + instruction.opcode;
                    return false;
                }
            }

            return true;
        }

        public static boolean operandSizeValide(Instruction instr) {
            String detectedMode = Instruction.detectMode(instr);

            switch (detectedMode) {
                case "immediat":
                    int x = instr.operand.replace("#", "").replace("$", "").length();
                    return (x == 2 || x == 4);
                case "direct":
                    return instr.operand.replace("$", "").length() == 2;
                case "etendu":
                    return instr.operand.replace("$", "").length() == 4;
                case "indexe":
                    return instr.operand.contains(",");
                default:
                    return false;
            }
        }

        public static boolean isHexadecimal(String x) {
            if (x == null || x.isEmpty()) return false;
            if (x.startsWith("#")) x = x.substring(1);
            return x.matches("^\\$?[0-9A-Fa-f]+$");
        }

        public static boolean operandValide(Instruction instr) {
            String opcode = instr.opcode;
            String operand = instr.operand;
            switch (opcode) {
                case "LDA":
                case "LDB":
                case "LDU":
                case "LDS":
                case "LDX":
                case "LDY":
                case "STA":
                case "STB":
                case "ADDA":
                case "ADDB":
                case "SUBA":
                case "SUBB":
                case "CMPA":
                case "CMPB":
                    return operand != null && !operand.isEmpty();

                case "PSHS":
                case "PSHU":
                case "PULS":
                case "PULU":
                case "INCA":
                case "INCB":
                case "INC":
                case "DECA":
                case "DECB":
                case "DEC":
                    return operand == null || operand.isEmpty();

                case "EXG":
                    return operand != null && operand.matches("[ABXYUS],[ABXYUS]");

                case "CWAI":
                    return operand != null && !operand.isEmpty();

                default:
                    return true;
            }
        }

        public static String getAdress(int k) {
            return "F" + (600 + k);
        }

        public static String getCode(Instruction instr) {
            String opcode = instr.opcode;
            String mode = Instruction.detectMode(instr);
            String code = "";

            switch (mode) {
                case "inherant":
                    switch (opcode) {
                        case "INCA": code = "4C"; break;
                        case "INCB": code = "5C"; break;
                        case "INC": code = "7C"; break;
                        case "DECA": code = "4A"; break;
                        case "DECB": code = "5A"; break;
                        case "DEC": code = "7A"; break;
                        case "PSHS": code = "34"; break;
                        case "PSHU": code = "36"; break;
                        case "PULS": code = "35"; break;
                        case "PULU": code = "37"; break;
                        case "EXG": code = "1E"; break;
                        case "TFR": code = "1F"; break;
                        case "NOP": code = "12"; break;
                        case "SWI": code = "3F"; break;
                        case "END": code = "39"; break;
                        case "LSLA": code = "48"; break;
                        case "LSLB": code = "58"; break;
                        case "LSRA": code = "44"; break;
                        case "LSRB": code = "54"; break;
                        case "ROLA": code = "49"; break;
                        case "ROLB": code = "59"; break;
                        case "RORA": code = "46"; break;
                        case "RORB": code = "56"; break;
                        case "COMA": code = "43"; break;
                        case "COMB": code = "53"; break;
                        case "NEGA": code = "40"; break;
                        case "NEGB": code = "50"; break;
                        case "CLRA": code = "4F"; break;
                        case "CLRB": code = "5F"; break;
                        case "RTS": code = "39"; break;
                    }
                    break;

                case "immediat":
                    switch (opcode) {
                        case "LDA": code = "86"; break;
                        case "LDB": code = "C6"; break;
                        case "LDU": code = "CE"; break;
                        case "LDS": code = "8E"; break;
                        case "LDX": code = "8E"; break;
                        case "LDY": code = "CE"; break;
                        case "ADDA": code = "8B"; break;
                        case "ADDB": code = "CB"; break;
                        case "SUBA": code = "80"; break;
                        case "SUBB": code = "C0"; break;
                        case "CMPA": code = "81"; break;
                        case "CMPB": code = "C1"; break;
                    }
                    break;

                case "direct":
                    switch (opcode) {
                        case "LDA": code = "96"; break;
                        case "LDB": code = "D6"; break;
                        case "LDU": code = "DE"; break;
                        case "LDS": code = "9E"; break;
                        case "LDX": code = "9E"; break;
                        case "LDY": code = "DE"; break;
                        case "STA": code = "97"; break;
                        case "STB": code = "D7"; break;
                        case "CMPA": code = "91"; break;
                        case "CMPB": code = "D1"; break;
                    }
                    break;

                case "etendu":
                    switch (opcode) {
                        case "JMP": code = "7E"; break;
                        case "LDA": code = "B6"; break;
                        case "LDB": code = "F6"; break;
                        case "LDU": code = "FE"; break;
                        case "LDS": code = "BE"; break;
                        case "LDX": code = "BE"; break;
                        case "LDY": code = "FE"; break;
                        case "STA": code = "B7"; break;
                        case "STB": code = "F7"; break;
                    }
                    break;

                case "indexe":
                    switch (opcode) {
                        case "LDA": code = "A6"; break;
                        case "LDB": code = "E6"; break;
                        case "LDX": code = "AE"; break;
                        case "LDY": code = "10"; break;
                        case "STA": code = "A7"; break;
                        case "STB": code = "E7"; break;
                        case "CMPA": code = "A1"; break;
                        case "CMPB": code = "E1"; break;
                        case "EXG": code = "1E"; break;
                        case "TFR": code = "1F"; break;
                    }
                    break;

                case "branche":
                    switch (opcode) {
                        case "JMP": code = "7E"; break;
                        case "BRA": code = "20"; break;
                        case "BEQ": code = "27"; break;
                        case "BMI": code = "2B"; break;
                        case "BNE": code = "26"; break;
                        case "BPL": code = "2A"; break;
                        case "BCC": code = "24"; break;
                        case "BCS": code = "25"; break;
                        case "BVC": code = "28"; break;
                        case "BVS": code = "29"; break;
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Mode d'adressage inconnu : " + mode);
            }

            return code;
        }

        public static String getFilter(String operand) {
            return operand.replaceAll("[#$\\[&]", "");
        }

    }


