import java.io.IOException;
import java.io.RandomAccessFile;


public class UltimaVHack {
    //HP, Max HP, Str, Dex, Int, EXP, Gold,
    private static String[] playerStatsOffsets = {"12", "14", "0E", "0F", "10", "16", "204"};
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
    private static final int MAX_SHORT_VAL = 65536;// max unsigned int value for a short

    private static RandomAccessFile file;

    public static void main(String[] args) {
        try {
            file = new RandomAccessFile("c:/Users/Rith/Documents/dos/Ultima_5/Ultima_5/SAVED.GAM", "rw");
            long offset = Long.parseLong("240", 16);
            file.seek(Long.parseLong(playerStatsOffsets[0], 16));

            safeWrite( checkShort(599), npcStatsOffsets[0][5]);
            safeWrite( checkShort(100), npcStatsOffsets[2][5]);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("");
                file.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }


    private static int checkShort(int val) {
        return val % MAX_SHORT_VAL;
    }

    private static void safeWrite(int value, String offset) {
        String hexStr = Integer.toHexString(value);
        long offsetVal = Long.parseLong(offset, 16);
        try {
            file.seek(offsetVal);
            if(hexStr.length() > 2) {
                //get little endian integer value
                value = Integer.parseInt(removeTrailingZeroes(Integer.toHexString(Integer.reverseBytes(value))), 16);
                file.writeShort(value);
            } else {
                file.writeByte(value);
                file.seek(offsetVal+1);
                file.writeByte(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String removeTrailingZeroes(String s) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() > 0 && sb.charAt(sb.length()-1) == '0') {
            sb.setLength(sb.length()-1);
        }
        return sb.toString();
    }

}
