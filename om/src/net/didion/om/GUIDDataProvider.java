package net.didion.om;

import java.security.MessageDigest;

public interface GUIDDataProvider {
	Long getTime();
	Long getRandomNumber();
	Long getSystemId();
	Long getVmId();
	MessageDigest getDigest();
}
