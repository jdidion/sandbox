/*
 * RandomGUID
 * @version 1.2.1 11/05/02
 * @author Marc A. Mnich
 *
 * From www.JavaExchange.com, Open Software licensing
 *
 * 11/05/02 -- Performance enhancement from Mike Dubman.
 *             Moved InetAddr.getLocal to static block.  Mike has measured
 *             a 10 fold improvement in run time.
 * 01/29/02 -- Bug fix: Improper seeding of nonsecure Random object
 *             caused duplicate GUIDs to be produced.  Random object
 *             is now only created once per JVM.
 * 01/19/02 -- Modified random seeding and added new constructor
 *             to allow secure random feature.
 * 01/14/02 -- Added random function seeding with JVM run time
 *
 */
package net.didion.om;

import java.security.MessageDigest;

public class GUID {
	private static GUIDDataProvider _config;

	public synchronized static GUID nextGUID() {
		StringBuffer raw = new StringBuffer().
		    append(getDataProvider().getSystemId()).append(":").
		    append(getDataProvider().getVmId()).append(":").
		    append(getDataProvider().getTime()).append(":").
		    append(getDataProvider().getRandomNumber());

		MessageDigest digest = getDataProvider().getDigest();
		digest.update(raw.toString().getBytes());
		byte[] array = digest.digest();
		StringBuffer value = new StringBuffer();
		for (int j = 0; j < array.length; ++j) {
			int b = array[j] & 0xFF;
			if (b < 0x10) value.append('0');
			value.append(Integer.toHexString(b));
		}

		return new GUID(value.toString());
	}

	public static void setDataProvider(GUIDDataProvider config) {
		_config = config;
	}

	public static GUIDDataProvider getDataProvider() {
		if (_config == null) {
			_config = new DefaultGUIDDataProvider();
		}
		return _config;
	}

  private String _value;
	private String _displayValue;

	private GUID(String value) {
		_value = value;
	}

	public String getValue() {
		maybeSetDisplayValue();
		return _displayValue;
	}

	public int hashCode() {
		return _value.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof GUID)) return false;

		// TODO: should probably log a warning/error here,
		// since GUID's are not supposed to have equivalents.
		return (_value.equals(((GUID) obj)._value));
	}

	public String toString() {
		return getValue();
	}

	private synchronized void maybeSetDisplayValue() {
		if (_displayValue == null) {
			// Convert to the standard format for GUID
			// Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
			String raw = _value.toUpperCase();
			StringBuffer sb = new StringBuffer();
			sb.append(raw.substring(0, 8));
			sb.append("-");
			sb.append(raw.substring(8, 12));
			sb.append("-");
			sb.append(raw.substring(12, 16));
			sb.append("-");
			sb.append(raw.substring(16, 20));
			sb.append("-");
			sb.append(raw.substring(20));
			_displayValue = sb.toString();
		}
	}
}