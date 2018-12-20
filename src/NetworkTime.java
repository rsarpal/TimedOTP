/*
 

apache.commons.net package for NTPTime
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

http://commons.apache.org/proper/commons-net/download_net.cgi
http://commons.apache.org/proper/commons-net/
http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ntp/NTPUDPClient.html
http://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ntp/NtpV3Packet.html


 */


//import lrapi.*;

package com.rsarpal.NetworkTime;
import org.apache.commons.net.ntp.NTPUDPClient;
//import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
//import java.util.Date;

public class NetworkTime {
	
	private long timeInMillis;
	private String TIME_SERVER;
	
	public NetworkTime( String timeServer ) throws Exception{
		//TIME_SERVER = "ns16.europe.nokia.com";   //nokia time server
		TIME_SERVER = timeServer;

	}

	public long getTimeInMillis(){


		NTPUDPClient timeClient = new NTPUDPClient();
		try {
			InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);
			timeInMillis = timeInfo.getMessage().getTransmitTimeStamp().getTime();
			//lr.output_message("time =" + returnTime);
		}catch (UnknownHostException uhe){
			uhe.printStackTrace();
		}catch (IOException ie){
			ie.printStackTrace();
		}


		return timeInMillis;

	}
	
	
}

		