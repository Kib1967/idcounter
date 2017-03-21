import java.time.Instant;

public class TimeTest {
	
	private static final String PADDING = "0000000000000000000000000000000000000000000000000000000000000000";
	
	public static void main(String[] args) throws Exception {
		
		Instant now = Instant.now();
		System.out.println("Epoch second as binary: " + pad(Long.toBinaryString(now.getEpochSecond()), 64));
		System.out.println("Nanos as binary: " + pad(Integer.toBinaryString(now.getNano()), 32));

		long lastId = 0L;
		
		while(true) {
			now = Instant.now();
			long id = getId(now);
			long part1 = (id & 0xfffffffff8000000L) >> 27;
			long part2 = (id & 0x0000000007fff800L) >> 11;
			long part3 =  id & 0x00000000000007ffL;
			
			System.out.print(pad(Long.toBinaryString(part1), 37));
			System.out.print(" ");
			System.out.print(pad(Long.toBinaryString(part2), 16));
			System.out.print(" ");
			System.out.print(pad(Long.toBinaryString(part3), 11));
			System.out.println();
			
			if(id < lastId) {
				throw new Exception("IDs not increasing!");
			}
			
			lastId = id;
			
			Thread.sleep(0, 10);
		}
	}
	
	private static String pad(String unpadded, int desiredLength) {
		return PADDING.substring(0, desiredLength - unpadded.length()) + unpadded;
	}
	
	private static long getId(Instant instant) {

		// First: number of seconds since the epoch
		long epochSecond = instant.getEpochSecond();
		
		// Second: number of nanoseconds, trimmed to preserve just the most significant 16 bits
		int nano = instant.getNano();
		int nanoMasked = (nano & 0xffff0000) >>> 16;
		
		// Third: a random number, to 11 bits, doesn't matter which 11
		long random = Double.doubleToLongBits(Math.random()) & 0x7ff;
		
		// Finally: all parts combined
		long id = (epochSecond << 27) | (nanoMasked << 11) | random;
		
		return id;
	}
}
