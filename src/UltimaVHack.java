import java.io.IOException;
import java.io.RandomAccessFile;


public class UltimaVHack {
                                      //HP, Max HP, Str, Dex, Int, XP, Gold,
    private static String[] playerStatsOffsets = {"12", "14", "0E", "0F", "10", "16", "204"};
    //Magic axe,
    private static String[] playerItemsOffsets = {"240"};
    private static String[][] npcStatsOffsets = {{"32", "34", "2E", "2F", "30", "36"}, // Shamino
                                     {"52", "54", "4E", "4F", "50", "56"} //Iolo
    };
    private static final int MAX_SHORT_VAL = 65536;// max unsigned int value for a short

    private static RandomAccessFile file;

    public static void main(String[] args) {
        try {
            file = new RandomAccessFile("c:/Users/Rith/Documents/dos/Ultima_5/Ultima_5/SAVED.GAM", "rw");
            long offset = Long.parseLong("240", 16);
            file.seek(Long.parseLong(playerStatsOffsets[0], 16));

            safeWrite( checkShort(65537), playerStatsOffsets[6]);

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
                System.out.println(offsetVal);
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
