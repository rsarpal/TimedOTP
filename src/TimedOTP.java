/*
	Package: TimedOTP
	Author : Rishu Sarpal
	Date : 01/04/2015
	Description: Open Connections to MQ. Put and Get meessages from MQ in Text and Byte formats

    Required Jars: commons.codec-1.9.jar package for TOTP
            import org.apache.commons.codec.binary.*;
            import org.apache.commons.codec.DecoderException;

	Compile :
	javac -d . jmsMessaging.java -cp "com.ibm.mq.allclient-9.0.4.0.jar;javax.jms-api-2.0.1.jar"
	jar -cvf messaging.jar com/rsarpal/xmloperations/*.class com/rsarpal/JmsMQOperations/*.class

	Constructor:
        JmsMQOperations(String host, int port,String channel, String manager, String user, String password, String queue)

    Methods:







    Usage:

            //Get time from NTP server
            NTPTime timenow = new NTPTime();
            //lr.output_message("DEBUG NTPTime = " + timenow.timeInMillis);


            //Call OTP class constructor
            TOTP t = new TOTP(lr.eval_string("{otpSecretKey}"), timenow.timeInMillis);
            lr.vuser_status_message("DEBUG otpSecretKey = " + lr.eval_string("{otpSecretKey}") + "DEBUG OTP = " + t.otp);


 */

package com.rsarpal.TimedOTP;

//Addtional for OTP
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import org.apache.commons.codec.binary.*;
import org.apache.commons.codec.DecoderException;
//import java.util.Date;


//import lrapi.*;

public class TimedOTP {


    public int otp;


    public TimedOTP(String s, long timeInMillis, int otpValidity){

        int PASS_CODE_LENGTH = 6;

        //remove blank spaces from Key if any
        s= s.replaceAll("\\s","");

        //lr.output_message("DEBUG_KEY = " + s);

        byte [] key = s.getBytes();


        //long timeInMillis = new Date().getTime();

        //	lr.output_message("timeInMillis = " + timeInMillis);

        long timeSlice = timeInMillis / otpValidity;

        try{
            otp = generateTOTP( key , timeSlice, PASS_CODE_LENGTH,"HmacSHA1");
        }catch (DecoderException de){}
        //lr.output_message("OTP = " + otp);

    }


    /**
     * This method uses the JCE to provide the crypto algorithm. HMAC computes a
     * Hashed Message Authentication Code with the crypto hash algorithm as a
     * parameter.
     *
     * @param crypto
     *            : the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     * @param keyBytes
     *            : the bytes to use for the HMAC key
     * @param text
     *            : the message or text to be authenticated
     */

    public byte[] hmacSha(String crypto, byte[] keyBytes, byte[] text) throws DecoderException {
        try {

            Base32 binaryDecoder = new Base32();
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            //SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            SecretKeySpec macKey = new SecretKeySpec((byte[]) binaryDecoder.decode(keyBytes), "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }



    /**
     * This method generates a TOTP value for the given set of parameters.
     *
     * @param key
     *            : the shared secret
     * @param time
     *            : a value that reflects a time
     * @param digits
     *            : number of digits to return
     * @param crypto
     *            : the crypto function to use
     *
     * @return digits
     */

    public int generateTOTP(byte[] key, long time, int digits, String crypto) throws DecoderException{

        int[] DIGITS_POWER
                // 0 1 2 3 4 5 6 7 8
                = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

        byte[] msg = ByteBuffer.allocate(8).putLong(time).array();
        byte[] hash = hmacSha(crypto, key, msg);


        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;


        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);


        int otp = binary % DIGITS_POWER[digits];


        return otp;
    }


}
