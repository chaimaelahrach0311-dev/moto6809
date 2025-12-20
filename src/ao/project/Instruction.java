package ao.project;

//IMPORTER TOUTES LES CLASSES DE PACKAGE (ARRAYLIST,LIST)
//Si opcode est non null, alors on applique .toUpperCase() pour mettre le //texte en majuscules.
//Si opcode est null, alors on met une chaîne vide

//Si operand est non null ==> on enlève les espaces autour avec .trim().
//Si operand est null ==>on met ""
//Detecte le mode d'adressage
//info:startsWith():méthode en Java,Elle permet de vérifier si une chaîne //de caractères commence par une autre chaîne donnée.
//Si l'operande commence par $, c’est une adresse memoire en hexadecimal.
//On enlève le $ avec substring(1)
//Si l’operande contient une virgule, c’est un mode indexé.
//Méthode pour détecter les erreurs de syntaxe simple
//info:equals():compare 2 chaines (true si identiques)
//info:contains():vérifie si une sous-chaine est présente
//info:isEmpty():vérifie si la chaine est vide

import java.util.*;

public class Instruction {
    public final String opcode;
    public final String operand;
    public String mode;
    String code = "";

    public Instruction(String opcode, String operand) {
        this.opcode = opcode != null ? opcode.toUpperCase() : "";
        this.operand = operand != null ? operand.trim() : "";
    }

    public String detecteMode() {
        if (operand.isEmpty()) {
            return "inherent";
        } else if (operand.startsWith("#")) {
            return "immediat";
        } else if (operand.startsWith("$")) {
            String addr = operand.substring(1);
            if (addr.length() <= 2) {
                return "direct";
            } else {
                return "etendu";
            }
        } else if (operand.contains(",")) {
            if (operand.contains("[")) {
                return "indexe-indirect";
            }
            return "indexe";
        } else if (operand.startsWith("[")) {
            return "etendu-indirect";
        } else if (opcode.equals("BEQ") || opcode.equals("BNE") || opcode.equals("BMI") || opcode.equals("JMP") || opcode.equals("JSR")) {
            return "relatif";
        }
        return "Inconnu";
    }

    public String getOpcodeHex() {
        String mode = detecteMode();

        switch (opcode) {
            //  CONTRÔLE
            case "NOP": code = "12"; break;
            case "RTS": code = "39"; break;
            case "RTI": code = "3B"; break;
            case "SWI": code = "3F"; break;
            case "SWI2": code = "10 3F"; break;
            case "SWI3": code = "11 3F"; break;

            // BRANCHES CONDITIONNELLES (3 instructions)
            case "BEQ": code = "27"; break;
            case "BNE": code = "26"; break;
            case "BMI": code = "2B"; break;

            //  REGISTRES (Inherent)
            case "INCA": code = "4C"; break;
            case "DECA": code = "4A"; break;
            case "CLRA": code = "4F"; break;
            case "CLRB": code = "5F"; break;
            case "COMA": code = "43"; break;
            case "COMB": code = "53"; break;

            // TRANSFERT/ÉCHANGE
            case "TFR": code = "1F"; break;
            case "EXG": code = "1E"; break;

            //  PILE
            case "PSHS": code = "34"; break;
            case "PULS": code = "35"; break;
            case "PSHU": code = "36"; break;
            case "PULU": code = "37"; break;

            //  LDA
            case "LDA":
                switch(mode) {
                    case "immediat": code = "86"; break;
                    case "direct": code = "96"; break;
                    case "etendu": code = "B6"; break;
                    case "etendu-indirect": code = "9F"; break;
                    case "indexe": code = "A6"; break;
                    case "indexe-indirect": code = "A6"; break;
                }
                break;

            //  LDB
            case "LDB":
                switch(mode) {
                    case "immediat": code = "C6"; break;
                    case "direct": code = "D6"; break;
                    case "etendu": code = "F6"; break;
                    case "etendu-indirect": code = "D6"; break;
                    case "indexe": code = "E6"; break;
                    case "indexe-indirect": code = "E6"; break;
                }
                break;

            //  LDX
            case "LDX":
                switch(mode) {
                    case "immediat": code = "8E"; break;
                    case "direct": code = "9E"; break;
                    case "etendu": code = "BE"; break;
                    case "etendu-indirect": code = "9E"; break;
                    case "indexe": code = "AE"; break;
                    case "indexe-indirect": code = "AE"; break;
                }
                break;

            //  STX
            case "STX":
                switch(mode) {
                    case "direct": code = "9F"; break;
                    case "etendu": code = "BF"; break;
                    case "etendu-indirect": code = "9F"; break;
                    case "indexe": code = "AF"; break;
                    case "indexe-indirect": code = "AF"; break;
                }
                break;

            //  STA
            case "STA":
                switch(mode) {
                    case "direct": code = "97"; break;
                    case "etendu": code = "B7"; break;
                    case "etendu-indirect": code = "97"; break;
                    case "indexe": code = "A7"; break;
                    case "indexe-indirect": code = "A7"; break;
                }
                break;

            //  STB
            case "STB":
                switch(mode) {
                    case "direct": code = "D7"; break;
                    case "etendu": code = "F7"; break;
                    case "indexe": code = "E7"; break;
                    case "indexe-indirect": code = "E7"; break;
                }
                break;

            //  ADDA
            case "ADDA":
                switch(mode) {
                    case "immediat": code = "8B"; break;
                    case "direct": code = "9B"; break;
                    case "etendu": code = "BB"; break;
                    case "etendu-indirect": code = "9B"; break;
                    case "indexe": code = "AB"; break;
                    case "indexe-indirect": code = "AB"; break;
                }
                break;

            //  ADDB
            case "ADDB":
                switch(mode) {
                    case "immediat": code = "CB"; break;
                    case "direct": code = "DB"; break;
                    case "etendu": code = "FB"; break;
                    case "etendu-indirect": code = "DB"; break;
                    case "indexe": code = "EB"; break;
                    case "indexe-indirect": code = "EB"; break;
                }
                break;

            //  SUBA
            case "SUBA":
                switch(mode) {
                    case "immediat": code = "80"; break;
                    case "direct": code = "90"; break;
                    case "etendu": code = "B0"; break;
                    case "etendu-indirect": code = "90"; break;
                    case "indexe": code = "A0"; break;
                    case "indexe-indirect": code = "A0"; break;
                }
                break;

            // SUBB
            case "SUBB":
                switch(mode) {
                    case "immediat": code = "C0"; break;
                    case "direct": code = "D0"; break;
                    case "etendu": code = "F0"; break;
                    case "etendu-indirect": code = "D0"; break;
                    case "indexe": code = "E0"; break;
                    case "indexe-indirect": code = "E0"; break;
                }
                break;

            //  MUL
            case "MUL": code = "3D"; break;

            //  CMPA
            case "CMPA":
                switch(mode) {
                    case "immediat": code = "81"; break;
                    case "direct": code = "91"; break;
                    case "etendu": code = "B1"; break;
                    case "etendu-indirect": code = "91"; break;
                    case "indexe": code = "A1"; break;
                    case "indexe-indirect": code = "A1"; break;
                }
                break;

            //  CMPB
            case "CMPB":
                switch(mode) {
                    case "immediat": code = "C1"; break;
                    case "direct": code = "D1"; break;
                    case "etendu": code = "F1"; break;
                    case "etendu-indirect": code = "D1"; break;
                    case "indexe": code = "E1"; break;
                    case "indexe-indirect": code = "E1"; break;
                }
                break;

            // JMP (Jump)
            case "JMP":
                switch(mode) {
                    case "direct": code = "0E"; break;
                    case "etendu": code = "7E"; break;
                    case "indexe": code = "6E"; break;
                    case "etendu-indirect": code = "9E"; break;
                    case "indexe-indirect": code = "6E"; break;
                }
                break;

            //  JSR
            case "JSR":
                switch(mode) {
                    case "direct": code = "9D"; break;
                    case "etendu": code = "BD"; break;
                    case "indexe": code = "AD"; break;
                }
                break;

            //  ANDA
            case "ANDA":
                switch(mode) {
                    case "immediat": code = "84"; break;
                    case "direct": code = "94"; break;
                    case "etendu": code = "B4"; break;
                    case "etendu-indirect": code = "94"; break;
                    case "indexe": code = "A4"; break;
                    case "indexe-indirect": code = "A4"; break;
                }
                break;

            //  ANDB
            case "ANDB":
                switch(mode) {
                    case "immediat": code = "C4"; break;
                    case "direct": code = "D4"; break;
                    case "etendu": code = "F4"; break;
                    case "etendu-indirect": code = "D4"; break;
                    case "indexe": code = "E4"; break;
                    case "indexe-indirect": code = "E4"; break;
                }
                break;

            // ORA
            case "ORA":
                switch(mode) {
                    case "immediat": code = "8A"; break;
                    case "direct": code = "9A"; break;
                    case "etendu": code = "BA"; break;
                    case "etendu-indirect": code = "9A"; break;
                    case "indexe": code = "AA"; break;
                    case "indexe-indirect": code = "AA"; break;
                }
                break;

            //  ORB
            case "ORB":
                switch(mode) {
                    case "immediat": code = "CA"; break;
                    case "direct": code = "DA"; break;
                    case "etendu": code = "FA"; break;
                    case "etendu-indirect": code = "DA"; break;
                    case "indexe": code = "EA"; break;
                    case "indexe-indirect": code = "EA"; break;
                }
                break;

            //  EORA
            case "EORA":
                switch(mode) {
                    case "immediat": code = "88"; break;
                    case "direct": code = "98"; break;
                    case "etendu": code = "B8"; break;
                    case "etendu-indirect": code = "98"; break;
                    case "indexe": code = "A8"; break;
                    case "indexe-indirect": code = "A8"; break;
                }
                break;

            //  EORB
            case "EORB":
                switch(mode) {
                    case "immediat": code = "C8"; break;
                    case "direct": code = "D8"; break;
                    case "etendu": code = "F8"; break;
                    case "etendu-indirect": code = "D8"; break;
                    case "indexe": code = "E8"; break;
                    case "indexe-indirect": code = "E8"; break;
                }
                break;
        }
        return code;
    }

    public boolean estSyntaxeValide() {
        String mode = detecteMode();

        if (mode.equals("Inconnu")) return false;
        if (opcode.isEmpty()) return false;

        switch (mode) {
            case "immediat":
                if (!operand.startsWith("#")) return false;
                String cleanOpcd = operand.substring(1).replace("$", "");
                if (cleanOpcd.isEmpty()) return false;
                if (cleanOpcd.length() != 2 && cleanOpcd.length() != 4) return false;
                if (!cleanOpcd.matches("[0-9A-Fa-f]+")) return false;
                break;

            case "direct":
                if (!operand.startsWith("$")) return false;
                cleanOpcd = operand.substring(1);
                if (cleanOpcd.length() > 2) return false;
                if (!cleanOpcd.matches("[0-9A-Fa-f]+")) return false;
                break;

            case "etendu":
                if (!operand.startsWith("$")) return false;
                cleanOpcd = operand.substring(1);
                if (cleanOpcd.length() != 2 && cleanOpcd.length() != 4) return false;
                if (!cleanOpcd.matches("[0-9A-Fa-f]+")) return false;
                break;

            case "indexe":
            case "indexe-indirect":
                if (!operand.contains(",")) return false;
                break;

            case "relatif":
                break;

            case "inherent":
                if (!operand.isEmpty()) return false;
                break;

            default:
                return false;
        }

        return !getOpcodeHex().isEmpty();
    }

    public String getMessageErreur() {
        if (!estSyntaxeValide()) {
            return "Erreur : mauvaise syntaxe → '" + opcode + " " + operand + "'";
        }
        return "OK";
    }

    public byte[] assemble() {
        if (!estSyntaxeValide()) {
            return new byte[0];
        }

        String opHex = getOpcodeHex().trim();
        List<Byte> octs = new ArrayList<>();

        // Gestion SWI2 / SWI3
        if (opHex.contains(" ")) {
            for (String part : opHex.split(" ")) {
                octs.add((byte) Integer.parseInt(part, 16));
            }
        } else {
            octs.add((byte) Integer.parseInt(opHex, 16));
        }

        String cleanOpcd = operand.replaceAll("[#$,\\[\\]]", "").toUpperCase();
        String mode = detecteMode();

        switch (mode) {
            case "immediat":
                // Gérer #44 et #$44
                String immVal = operand.substring(1).replace("$", "");
                if (immVal.length() == 2) {
                    octs.add((byte) Integer.parseInt(immVal, 16));
                } else if (immVal.length() == 4) {
                    // Pour LDX #$1234 (16 bits) - BIG ENDIAN
                    octs.add((byte) Integer.parseInt(immVal.substring(0, 2), 16)); // high
                    octs.add((byte) Integer.parseInt(immVal.substring(2, 4), 16)); // low
                }
                break;

            case "direct":
                if (cleanOpcd.length() >= 1) {
                    if (cleanOpcd.length() == 1) {
                        octs.add((byte) Integer.parseInt("0" + cleanOpcd, 16));
                    } else {
                        octs.add((byte) Integer.parseInt(cleanOpcd.substring(0, 2), 16));
                    }
                }
                break;

            case "etendu":
                if (cleanOpcd.length() == 2) {
                    // Adresse courte $05 / devient $0005
                    octs.add((byte) 0x00); // high = 00
                    octs.add((byte) Integer.parseInt(cleanOpcd, 16)); // low
                } else if (cleanOpcd.length() == 4) {
                    // occct haut
                    octs.add((byte) Integer.parseInt(cleanOpcd.substring(0, 2), 16)); // high
                    octs.add((byte) Integer.parseInt(cleanOpcd.substring(2, 4), 16)); // low
                }
                break;

            case "indexe":
                if (operand.equals(",X")) {
                    octs.add((byte) 0x84);
                } else if (operand.contains(",")) {
                    String offsetStr = operand.split(",")[0].replace("$", "");
                    if (!offsetStr.isEmpty()) {
                        try {
                            octs.add((byte) Integer.parseInt(offsetStr, 16));
                        } catch (Exception e) {  }
                    }
                }
                break;

            case "relatif":

                octs.add((byte) 0x00);
                break;
        }

        byte[] result = new byte[octs.size()];
        for (int i = 0; i < octs.size(); i++) {
            result[i] = octs.get(i);
        }
        return result;
    }
}