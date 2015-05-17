package net.didion.om;

import junit.framework.TestCase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Date;

public class TestGUID extends TestCase {
	protected void tearDown() throws Exception {
		super.tearDown();
		GUID.setDataProvider(null);
	}

	public void testEquality() {
		GUID.setDataProvider(new MockGUIDDataProvider());
		GUID id1 = GUID.nextGUID();
		GUID id2 = GUID.nextGUID();
		assertEquals(id1, id2);
	}

	public void testTimeEnsuresUniqueness() {
		GUIDDataProvider provider = new MockGUIDDataProvider() {
			public Long getTime() {
				return new Long(System.currentTimeMillis());
			}
		};
		GUID.setDataProvider(provider);
		GUID id1 = GUID.nextGUID();
		try {
			// probably longer than we need to sleep, but
			// might as well account for bad platforms that
			// have bad System.currentTimeMillis()
			// implementations
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
		GUID id2 = GUID.nextGUID();
		assertNotEquals(id1, id2);
	}

	public void testRandomEnsuresUniqueness() {
		GUIDDataProvider provider = new MockGUIDDataProvider() {
			Random _random = new Random(new Date().getTime());
			public Long getRandomNumber() {
				return new Long(_random.nextLong());
			}
		};
		GUID.setDataProvider(provider);
		GUID id1 = GUID.nextGUID();
		GUID id2 = GUID.nextGUID();
		assertNotEquals(id1, id2);
	}

	public void testSystemIdEnsuresUniqueness() {
		GUIDDataProvider provider = new MockGUIDDataProvider() {
			Random _random = new Random(new Date().getTime());
			public Long getSystemId() {
				// a random number is a reasonable approximation
				// of the system id
				return new Long(_random.nextLong());
			}
		};
		GUID.setDataProvider(provider);
		GUID id1 = GUID.nextGUID();
		GUID id2 = GUID.nextGUID();
		assertNotEquals(id1, id2);
	}

	public void testVmIdEnsuresUniqueness() {
		GUIDDataProvider provider = new MockGUIDDataProvider() {
			Random _random = new Random(new Date().getTime());
			public Long getVmId() {
				// a random number is a reasonable approximation
				// of the vm id
				return new Long(_random.nextLong());
			}
		};
		GUID.setDataProvider(provider);
		GUID id1 = GUID.nextGUID();
		GUID id2 = GUID.nextGUID();
		assertNotEquals(id1, id2);
	}

  public void testGetValue() {
	  String value = GUID.nextGUID().getValue();
	  assertEquals('-', value.charAt(8));
	  assertEquals('-', value.charAt(13));
	  assertEquals('-', value.charAt(18));
	  assertEquals('-', value.charAt(23));
	  assertEquals(36, value.length());
  }

	private void assertNotEquals(Object obj1, Object obj2) {
		assertTrue((obj1 != obj2) && (obj1 == null) ? (obj2 != null) : (obj2 == null || !obj1.equals(obj2)));
	}

	private class MockGUIDDataProvider implements GUIDDataProvider {
		public Long getTime() {
			return new Long(0);
		}

		public Long getRandomNumber() {
			return new Long(0);
		}

		public Long getSystemId() {
			return new Long(0);
		}

		public Long getVmId() {
			return new Long(0);
		}

		public MessageDigest getDigest() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}
	}
}