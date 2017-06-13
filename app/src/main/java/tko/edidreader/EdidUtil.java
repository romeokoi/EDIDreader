package tko.edidreader;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
/*
ignore
 */
public class EdidUtil {

    private byte[] edid; //128 bytes

    //constructor
    public EdidUtil(byte[] edid){
        this.edid = edid;
    }


    public byte[] getEdidRaw(){
        return edid;
    }
    /*
        gets 3 letter Manufacturer Id, from 5-bit letters in byte 8,9
     */
    public String getManufacturerId(){

        return "failed to get manufacturer id";
    }

    /*
        gets Manufacturer name from table given the id
     */
    public String getManufacturer(){
        return "failed to get manufacturer";

    }
    /*
        gets Manufacture Product Code from byte 10,11
     */
    public String getProductCode() {
        ByteBuffer bt = ByteBuffer.wrap(Arrays.copyOfRange(edid, 10, 12));
        bt.order(ByteOrder.LITTLE_ENDIAN);

        return bt.getShort() +"";
    }

    /*
        gets Serial Number from byte 12-15, 32 bits
     */
    public String getSerialNumber() {

        return "failed to get Serial Number";

    }
    /*
        gets year of manufacture, byte 17
     */
    public String getYear() {
        int year = edid[17] + 1990;
        return year + "";
    }
    /*
        gets week of year of manufacture, byte 16
     */
    public String getWeek() {

        return edid[16] + "";
    }

}


