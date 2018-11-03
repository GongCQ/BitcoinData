package BitcoinDataTest;
import BitcoinData.*;

import java.util.HashMap;

public class Main {
    private static String addressStr0 = "1CK6KHY6MHgYvmRQ4PAafKYDrg1ejbH1cE";
    private static byte[] address0 = Utils.AddressStringToBytes(addressStr0);
    private static String addressStr1 = "3NXFP59rfuADPKznKE9CiFafS7BhtXXuaN";
    private static byte[] address1 = Utils.AddressStringToBytes(addressStr1);
    private static String addressStr2 = "32wU7L4CX84rRngHSzVcxEv7GBpjM5hjCZ";
    private static byte[] address2 = Utils.AddressStringToBytes(addressStr2);
    private static String addressStr3 = "1DEdPCBBxPBt15sLKih3puKguMw2sTsFi6";
    private static byte[] address3 = Utils.AddressStringToBytes(addressStr3);
    private static String addressStr4 = "1Dht5rRQxpdYtZYGPQB2qrkxAxHGsKUKod";
    private static byte[] address4 = Utils.AddressStringToBytes(addressStr4);
    private static String transHashStr0 = "4b49ba6f02e53fbc27653ce2cbb625aa3864e5cfc85fadec8502f1beaa1ac867";
    private static byte[] transHash0 = Utils.TransHashStringToBytes(transHashStr0);
    private static String transHashStr1 = "982ea8a52438c0985f937eeff376a2790eec660bcc7d60956f4b9107d3352e3d";
    private static byte[] transHash1 = Utils.TransHashStringToBytes(transHashStr1);
    private static String transHashStr2 = "b768be5501d8fa6fe1f611e0ca24034cddf849a3c0b96d45052fb8d660279094";
    private static byte[] transHash2 = Utils.TransHashStringToBytes(transHashStr2);
    private static String transHashStr3 = "eb60651b158e3480189ad5ae0bc0ba9081f30e6e5c1e5d5f261bf858c4fa9013";
    private static byte[] transHash3 = Utils.TransHashStringToBytes(transHashStr3);
    private static String transHashStr4 = "ac1d538baf31fee1fd8937a91936b2b81b5084c646df96cd732bb0818b693359";
    private static byte[] transHash4 = Utils.TransHashStringToBytes(transHashStr4);

    public static void main(String[] args) throws Exception {
        ConstructorText();
        System.out.println("==== end of main test.");
    }
    public static void ConstructorText(){
        // HashIndexRecord
        HashIndexRecord hir = new HashIndexRecord(address0, new HashConflictLocation((short)22, 62839, true),
                new SectionLocation((short)56, 985), new SectionLocation((short)27, 9483), new HashConflictLocation((short)1,42, false));
        HashIndexRecord hir_ = new HashIndexRecord(hir.Bytes());

        // SectionRecord
        SectionRecord sr = new SectionRecord(transHash0, 11, 3434, (byte)-128, (byte)-128, (byte)127,
                0.1, 11.11, 234, 43434, 23423.33, 456.3);
        SectionRecord sr_ = new SectionRecord(sr.Bytes());
        int shift = 10;
        byte[] tempByteArr = new byte[shift + sr.Bytes().length];
        System.arraycopy(sr.Bytes(), 0, tempByteArr, shift, sr.Bytes().length);
        SectionRecord sr__ = new SectionRecord(tempByteArr, shift);
        SectionRecord sr1 = new SectionRecord(transHash1, 144, 897, (byte)-128, (byte)-128, (byte)127,
                0.66634, 44.444, 841, 94873, 7103, 734.3);


        // Section
        SectionRecord[] records = new SectionRecord[1];
        records[0] = sr;
        Section s = new Section(address1, new SectionLocation((short)4, 100), new SectionLocation((short)67, 922),
                (short)1, new SectionLocation((short)38, 3848405), (short)0, records);
        Section s_ = new Section(s.Bytes());
        s.AppendRecord(sr1);
        Section s__ = new Section(s.Bytes());

        System.out.println("end of constructor test.");
    }
}
