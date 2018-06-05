package ty.cloud.netty.mq.server.utils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BitConverter {

	public static boolean IsLittleEndian = false;

	public static byte[] GetBytes(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) (num >>> 24);// 
		result[1] = (byte) (num >>> 16);// 
		result[2] = (byte) (num >>> 8); //
		result[3] = (byte) (num); // 
		return result;
	}

	public static int ToUInt16(byte[] bytes, int start) {
		int value = 0;
		int m = start+2;
		for (int i = start; i < m; i++) {
			int shift = (m - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;// ���λ��
		}
		return value;
	}

	public static int ToUInt32(byte[] bytes, int start) {
		int value = 0;
		int m = start+4;
		for (int i = start; i < m; i++) {
			int shift = (m - 1 - i) * 8;
			value += (bytes[i] & 0x000000FF) << shift;//
		}
		return value;
	}

	public static int ToUInt32(byte b) {
		int value = 0;
		int m = 4;
		// �ɸ�λ����λ
		int shift = (m - 1 - 3) * 8;
		value += (b & 0x000000FF) << shift;// 
		return value;
	}

	public static byte[] getBytes(String str) {
		try {
			return str.getBytes("gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param bytes
	 * @param start
	 * @return
	 */
	public static Date getDate(byte[] bytes, int start) {
		StringBuilder sb = new StringBuilder();
		sb.append("20").append(String.format("%02X", bytes[start + 0]))
				.append("-").append(String.format("%02X", bytes[start + 1]))
				.append("-").append(String.format("%02X", bytes[start + 2]))
				.append(" ").append(String.format("%02X", bytes[start + 3]))
				.append(":").append(String.format("%02X", bytes[start + 4]))
				.append(":").append(String.format("%02X", bytes[start + 5]));
		String strDate = sb.toString();
		Date d = DateUtil.stringToDatetime(strDate, "yyyy-MM-dd HH:mm:ss");
		return d;
	}

	public static String getString(byte[] data) {
		return getString(data, 0, data.length);
	}

	public static String getString(byte[] data, int start, int len) {
		try {
			return new String(data, start, len, "gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getBytes(Date date) {
		int start = 0;
		byte[] bytes = new byte[6];
		Calendar cal = Calendar.getInstance();// ʹ
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);// 
		int month = cal.get(Calendar.MONTH) + 1;// 
		int day = cal.get(Calendar.DAY_OF_MONTH);// 
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 
		int minute = cal.get(Calendar.MINUTE);// 
		int second = cal.get(Calendar.SECOND);// 
		bytes[start++] = Byte.parseByte("" + (year - 2000), 16);
		bytes[start++] = Byte.parseByte((month + ""), 16);
		bytes[start++] = Byte.parseByte((day + ""), 16);
		bytes[start++] = Byte.parseByte((hour + ""), 16);
		bytes[start++] = Byte.parseByte((minute + ""), 16);
		bytes[start++] = Byte.parseByte((second + ""), 16);
		return bytes;
	}

	public static String format(Date date) {

		SimpleDateFormat time = new SimpleDateFormat("yy-MM-dd HH:mm:s");
		return time.format(date);
	}

	public int getUnsignedByte(byte data) { // 
		return data & 0x0FF;
	}

	public int getUnsignedByte(short data) { // 
												// WORD)
		return data & 0x0FFFF;
	}

	public long getUnsignedIntt(int data) { // 
											// 
		return data & 0x0FFFFFFFFl;
	}

	public static void main(String[] args) {
		byte b = (byte) 130;
		int x = ToUInt32(b);
		System.out.println(x);

		System.out.println((byte) 254);
	}

}
