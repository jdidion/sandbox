package net.didion.om;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Set;
import java.util.Iterator;
import java.util.Random;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DefaultGUIDDataProvider implements GUIDDataProvider {
	private Long _systemId;
	private Long _vmId;
	private Random _random;
  private String _digest;
	private boolean _secure;

	public DefaultGUIDDataProvider() {
		this("MD5");
	}

	public DefaultGUIDDataProvider(String digest) {
		this(digest, true);
	}

	public DefaultGUIDDataProvider(boolean secure) {
		this("MD5", secure);
	}

	public DefaultGUIDDataProvider(String digest, boolean secure) {
		_digest = digest;
		_secure = secure;
	}

	public Long getTime() {
		return new Long(System.currentTimeMillis());
	}

	public Long getSystemId() {
		if (_systemId == null) {
			byte[] bytes = null;
			try {
				bytes = InetAddress.getLocalHost().getAddress();
			} catch (UnknownHostException e) {
				// No system address found. Generate one.
				bytes = new byte[4];
				for (int i = 0; i < bytes.length; i++) {
					getRandom().nextBytes(bytes);
				}
			}
			long l = (1 << 47);
      for (int i = 0; i < bytes.length; i++) {
	      l |= bytes[i] << ((bytes.length - 1 - i) * 8);
      }
			_systemId = new Long(l);
		}
		return _systemId;
	}

	public Long getVmId() {
		if (_vmId == null) {
			_vmId = new Long(new Object().hashCode());
		}
		return _vmId;
	}

	public Long getRandomNumber() {
		return new Long(getRandom().nextLong());
	}

	private Random getRandom() {
		if (_random == null) {
			_random = (_secure) ? new SecureRandom() : new Random();
		}
		return _random;
	}

	public MessageDigest getDigest() {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(_digest);
		} catch (NoSuchAlgorithmException e) {
			Set algorithms = Security.getAlgorithms("MessageDigest");
			for (Iterator itr = algorithms.iterator(); itr.hasNext();) {
				String algorithm = (String) itr.next();
				try {
					digest = MessageDigest.getInstance(algorithm);
					break;
				} catch (NoSuchAlgorithmException e1) {
				}
			}
		}
		if (digest == null) {
			throw new RuntimeException("no available instance of MessageDigest");
		}
		return digest;
	}
}