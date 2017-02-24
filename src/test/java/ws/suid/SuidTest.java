package ws.suid;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

public class SuidTest {
	@Test
	@DisplayName("Create a Suid from a long")
	public void testCreateASuidFromALong() {
		Suid id = new Suid(1903154L);
		assertEquals(id, new Suid("14she"));
	}
	
	@Test
	@DisplayName("Create a Suid from a string")
	public void testCreateASuidFromAString() {
		Suid id = new Suid("14she");
		assertEquals(id, new Suid(1903154L));
	}
	
	@Test
	@DisplayName("Get a Suid's underlying value")
	public void testGetASuidsUnderlyingValue() {
		Suid id = new Suid("14she");
		assertEquals(id.longValue(), 1903154L);
	}
	
	@Test
	@DisplayName("Convert a Suid to a Long")
	public void testConvertASuidToALong() {
		Suid id = new Suid("14she");
		Long val = id.toLong();
		assertEquals(val.longValue(), 1903154L);
	}
	
	@Test
	@DisplayName("Convert a Suid to a String")
	public void testConvertASuidToAString() {
		Suid id = new Suid("14she");
		String val = id.toString();
		assertEquals(val, "14she");
	}
	
	@Test
	@DisplayName("Convert a List<Suid> to a List<Long>")
	public void testConvertAListSuidToAListLong() {
		List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
		assertArrayEquals(ids.toArray(), new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
		List<Long> vals = Suid.toLong(ids);
		assertArrayEquals(vals.toArray(), new Long[]{Long.valueOf(1903154), Long.valueOf(1903155), Long.valueOf(1903156)});
	}

	@Test
	@DisplayName("Convert a List<Suid> to a List<String>")
	public void testConvertAListSuidToAListString() {
		List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
		assertArrayEquals(ids.toArray(), new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
		List<String> vals = Suid.toString(ids);
		assertArrayEquals(vals.toArray(), new String[]{"14she", "14shf", "14shg"});
	}

	@Test
	@DisplayName("Convert a List<Long> to a List<Suid>")
	public void testConvertAListLongToAListSuid() {
		List<Long> vals = Arrays.asList(new Long[]{Long.valueOf(1903154), Long.valueOf(1903155), Long.valueOf(1903156)});
		assertArrayEquals(vals.toArray(), new Long[]{Long.valueOf(1903154), Long.valueOf(1903155), Long.valueOf(1903156)});
		List<Suid> ids = Suid.fromLong(vals);
		assertArrayEquals(ids.toArray(), new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
	}

	@Test
	@DisplayName("Convert a List<String> to a List<Suid>")
	public void testConvertAListStringToAListSuid() {
		List<String> vals = Arrays.asList(new String[]{"14she", "14shf", "14shg"});
		assertArrayEquals(vals.toArray(), new String[]{"14she", "14shf", "14shg"});
		List<Suid> ids = Suid.fromString(vals);
		assertArrayEquals(ids.toArray(), new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
	}
}
