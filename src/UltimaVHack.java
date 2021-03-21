import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/*
    Rithrangsey Suon
    CECS 378
    3/21/2021
 */

public class UltimaVHack {
    //HP, Max HP, Str, Dex, Int, EXP, Gold, Food
    private static String[] playerStatsOffsets = {"12", "14", "0E", "0F", "10", "16", "204", "202"};
    //Keys, Skull keys, Gems, Black badge, Magic carpets, Magic axes
    private static String[] playerItemsOffsets = {"206", "20B", "207", "218", "20A", "240"};
    private static String[][] npcStatsOffsets = {
            {"32", "34", "2E", "2F", "30", "36"},       //0: Shamino
            {"52", "54", "4E", "4F", "50", "56"},       //1: Iolo
            {"72", "74", "6E", "6F", "70", "76"},       //2: Mariah
            {"92", "94", "8E", "8F", "90", "96"},       //3: Geoffrey
            {"B2", "B4", "AE", "AF", "B0", "B6"},       //4: Jaana
            {"D2", "D4", "CE", "CF", "D0", "D6"},       //5: Julia
            {"F2", "F4", "EE", "EF", "F0", "F6"},       //6: Dupre
            {"112", "114", "10E", "10F", "110", "116"}, //7: Katrina
            {"132", "134", "12E", "12F", "130", "136"}, //8: Sentri
            {"152", "154", "14E", "14F", "150", "156"}, //9: Gwenno
            {"172", "174", "16E", "16F", "170", "176"}, //10: Johne
            {"192", "194", "18E", "18F", "190", "196"}, //11: Gorn
            {"1B2", "1B4", "1AE", "1AF", "1B0", "1B6"}, //12: Maxwell
            {"1D2", "1D4", "1CE", "1CF", "1D0", "1D6"}, //13: Toshi
            {"1F2", "1F4", "1EE", "1EF", "1F0", "1F6"}, //14: Saduj
    };
    private static final int MAX_SHORT_VAL = 65536;// range of unsigned int value for a short
    private static final String SAVE_FILEPATH = "c:/Users/Rith/Documents/dos/Ultima_5/Ultima_5/SAVED.GAM"; //Change file path to locate Ultima 5 SAVED.GAM file
    private static Scanner input = new Scanner(System.in);
    private static RandomAccessFile file;

    public static void main(String[] args) {
        try {
            file = new RandomAccessFile(SAVE_FILEPATH, "rw"); //
            boolean inUse = true;
            while(inUse) {
                System.out.println("Select a character to edit:\n1) Player\n2) Shamino\n3) Iolo\n4) Mariah\n5) Geoffrey" +
                        "\n6) Jaana\n7) Julia\n8) Dupre\n9) Katrina\n10) Sentri\n11) Johne\n12) Gorn\n13) Maxwell\n14) Toshi\n15) Saduj\n-1) Save changes and quit.");
                int choice = validateInt(input);
                if(choice == -1) {
                    inUse = false;
                } else if (choice == 1) {
                    playerMenu();
                } else if (choice >= 2 && choice <= 15) {
                    editStatsMenu(choice-2); //pass index of character to stats menu
                } else {
                    System.out.println("Invalid Choice.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Saving changes and quitting...");
                file.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    private static void playerMenu() {
        while(true) {
            System.out.println("Select an option:\n1) Edit player items\n2) Edit player stats");
            int choice = validateInt(input);
            if(choice == 1) {
                editItemsMenu();
                break;
            } else if (choice == 2) {
                editStatsMenu(-1); //-1 indicates editing stats for the player
                break;
            } else {
                System.out.println("Invalid Choice.");
            }
        }
    }

    private static void editItemsMenu() {
        int choice = 0;
        while(true) {
            System.out.println("Pick an item to edit:\n0) Keys\n1) Skull keys\n2) Gems\n3) Black badge\n4) Magic carpets\n5) Magic axes");
            choice = validateInt(input);
            if(choice >= 0 && choice <= 5) {
                break;
            } else {
                System.out.println("Invalid Choice.");
            }
        }

        System.out.println("Enter a value for the item: ");
        int val = validateInt(input);

        safeWriteByte(val, playerItemsOffsets[choice]); //item values only write to 1 byte
    }

    private static void editStatsMenu(int charIdx) {
        String offset;
        int choice = 0;

        while(true) {
            System.out.println("Pick a stat to edit:\n0) HP\n1) Max HP\n2) Strength\n3) Dexterity\n4) Intellect\n5) Experience\n6) Gold\n7) Food");
             choice = validateInt(input);
             if(choice >= 0 && choice <= 7) {
                 break;
             } else {
                 System.out.println("Invalid Choice.");
             }
        }

        System.out.println("Enter new stat value: ");
        int statVal = validateInt(input);

        if(charIdx < 0) {
            offset = playerStatsOffsets[choice];
        } else {
            offset = npcStatsOffsets[charIdx][choice];
        }

        if(choice == 0 || choice == 1 || choice == 5 || choice == 6 || choice == 7) { //allow certain stats to write to 2 bytes
            safeWriteShort(statVal, offset);
        } else {
            safeWriteByte(statVal, offset); //stats that can only write to 1 byte
        }
    }

    //bounds integer value to 0 - 65535 (unsigned integer range of two bytes)
    private static int checkShort(int val) {
        return val % MAX_SHORT_VAL;
    }

    private static void safeWriteShort(int value, String offset) {
        value = checkShort(value); //verify value is within range of 2 bytes representation
        String hexStr = Integer.toHexString(value);
        long offsetVal = Long.parseLong(offset, 16); //get offset value from hex -> long
        try {
            file.seek(offsetVal);
            if(hexStr.length() > 2) {
                //get little endian integer value by reversing the bytes and getting the string hex
                value = Integer.parseInt(removeTrailingZeroes(Integer.toHexString(Integer.reverseBytes(value))), 16);
                file.writeShort(value);
            } else {
                file.writeByte(value);
                file.seek(offsetVal+1); //overwrites most significant byte to zero
                file.writeByte(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void safeWriteByte(int value, String offset) {
        long offsetVal = Long.parseLong(offset, 16);
        try {
            file.seek(offsetVal);
            file.writeByte(value); //writes value to 1 Byte at offset (automatically handles overflow)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //helper function to validate that user gives an integer
    private static int validateInt(Scanner userInput) {
        while(!userInput.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            userInput.next();
        }
        return userInput.nextInt();
    }

    //helper function to remove zeros from the end of the string hex
    private static String removeTrailingZeroes(String s) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() > 0 && sb.charAt(sb.length()-1) == '0') {
            sb.setLength(sb.length()-1);
        }
        return sb.toString();
    }

}
