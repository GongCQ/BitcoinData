package BitcoinData;

import wf.bitcoin.javabitcoindrpcclient.*;
import org.bitcoinj.core.Base58;
import java.math.BigInteger;
import java.util.HashMap;

import BitcoinData.Utils;


public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(34/71);
        System.out.println(70/71);
        System.out.println(72/71);

        HashMap<byte[], String> hm = new HashMap<byte[], String>();
        byte[] kkkkkk = {3,1,0};
        byte[] k0 = {0, 1, 2};
        byte[] k1 = {4,6,1};
        byte[] k2 = {9,3,0,4,2};
        String v0 = "tertwer";
        String v1 = "245096lkr";
        String v2 = "ijcer";
        String vv = "xxxxx";
        hm.put(k0, v0);
        hm.put(k1, v1);
        hm.put(k2, v2);
        System.out.println(null == hm.get(k0));
        System.out.println(hm.get(kkkkkk));
        System.out.println(hm.get(k1));
        System.out.println(hm.get(k2));
        hm.put(k0, vv);
        System.out.println(hm.get(k0));
        System.out.println(hm.get(k1));
        System.out.println(hm.get(k2));
        System.out.println("==== end of main.");
//        Test();
    }

    public static String binary(byte[] bytes, int radix)    {

        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }

    public static void Test(){
        String aa = "3C3zK8T64KPqqj7NishUFH9Vk3169e6JYL";
        byte[] ab = Utils.AddressStringToBytes(aa);
        String aac = Utils.AddressBytesToString(ab);



        long ll = 2938273745L;
        byte[] lb = Utils.LongToBytes(ll);
        long llc = Utils.BytesToLong(lb);

        int ii = 29347598;
        byte[] ib = Utils.IntToBytes(ii);
        int iic = Utils.BytesToInt(ib);


        double dd = 29353.345734345;
        byte[] db = Utils.DoubleToBytes(dd);
        double ddc = Utils.BytesToDouble(db);

//        String aa1 = "bc1qzjeg3h996kw24zrg69nge97fw8jc4v7v7yznftzk06j3429t52vse9tkp9";
//        byte[] ab1 = Utils.AddressStringToBytes(aa1);
//        String aac1 = Utils.AddressBytesToString(ab1);


        String tt = "c9674a675b99ac4e5500c5725b9e9496a24b358ca0e6194a194e112429114fd1";
        byte[] tb = Utils.TransHashStringToBytes(tt);
        String ttc = Utils.TransHashBytesToString(tb);

        int ddd = 0;
    }
}
