package ws.suid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Stores a 52-bit Scoped Unique ID in a 64-bit long.
 * 
 * <p>The bits are distributed over the 64-bit long as depicted below:</p>
 * 
 * <pre>
 *                     HIGH INT                                         LOW INT
 * __________________________________________________________________________________________
 * |                                           ||                                           |
 * | 0000 0000  0000 bbbb  bbbb bbbb  bbbb bbbb  bbbb bbbb  bbbb bbbb  bbbb bbbb  bbbb iiii |
 * |___________________________________________||___________________________________________|
 * 
 *   0 = 12 reserved bits
 *   b = 48 block bits
 *   i = 4 ID bits
 * </pre>
 * 
 * <p>The first 12 bits are reserved and always set to 0. The next 48 bits are used for
 * the block number. These are handed out by the server(s). The last 4 bits are used 
 * as ID bits which are to be filled in by the client.</p>
 * 
 * <p>To prevent a single point of failure, multiple separate hosts can be handing out block numbers
 * for the same scope(s), by limiting the numbers they will generate. For example, to split the ID
 * space for the block numbers into two shards, set both servers to add 2 for each new block number,
 * setting one server to start numbering at 0 and the other server to start at 1. This can be done
 * later easily and can also be extended to 4 or even more servers... As such, no bits are explicitly
 * reserved for sharding; instead this should be arranged at the application level.</p>
 * 
 * <p>To make {@code Suid}s both short and easily human-readable and write-able, Suids are represented 
 * as base-36 encoded strings by default. Using only lowercase makes suids easy for humans to read, 
 * write and pronounce. This class comes with a `toString` method and Json adapter to make sure it is 
 * always serialized to/from strings.</p>
 *  
 * @author Stijn de Witt [StijnDeWitt@hotmail.com]
 */
@JsonFormat(shape=JsonFormat.Shape.STRING)
@JsonDeserialize(using=Suid.Deserializer.class)
@JsonSerialize(using=Suid.Serializer.class)
public final class Suid extends Number implements CharSequence, Comparable<Suid> {
	private static final long serialVersionUID = 1L;
	
	/** Alphabet used when converting suid to string */
	public static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
	/** Mask that singles out the reserved bits */
	public static final long MASK_RESERVED =  0xfff0000000000000L;
	/** Mask that singles out the block bits */
	public static final long MASK_BLOCK =     0x000ffffffffffff0L;
	/** Mask that singles out the ID bits */
	public static final long MASK_ID =        0x000000000000000fL;
	/** Number of reserved bits */
	public static final byte COUNT_RESERVED = 12;
	/** Number of block bits */
	public static final byte COUNT_BLOCK =    48;
	/** Number of ID bits */
	public static final byte COUNT_ID =       4;
	/** Offset of reserved bits within suid (from LSB) */
	public static final byte OFFSET_RESERVED = COUNT_BLOCK + COUNT_ID;
	/** Offset of block bits within suid (from LSB) */
	public static final byte OFFSET_BLOCK =    COUNT_ID;
	/** Offset of ID bits within suid (from LSB) */
	public static final byte OFFSET_ID =       0;
	/** The number of blocks available. */
	public static final long BLOCK_SIZE =      1 << COUNT_BLOCK;
	/** The number of IDs available in each block. */
	public static final long IDSIZE =          1 << COUNT_ID;

	/** Converts Suid to/from it's database representation. */ 
	@Converter(autoApply=true)
	public static class SuidConverter implements AttributeConverter<Suid, Long> {
		@Override public Long convertToDatabaseColumn(Suid suid) {
			return suid == null ? null : suid.toLong();
		}
		@Override public Suid convertToEntityAttribute(Long suid) {
			return suid == null ? null : new Suid(suid);
		}
	}
	
	/** Serializes to JSON */
	@SuppressWarnings("serial")
	public static final class Serializer extends StdSerializer<Suid> {
		public Serializer(){super(Suid.class);}
		@Override public void serialize(Suid value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonGenerationException {
			generator.writeString(value.toString());
		}
	}
	/** Deserializes from JSON */
	@SuppressWarnings("serial")
	public static final class Deserializer extends StdDeserializer<Suid> {
		public Deserializer() {super(Suid.class);}
		@Override public Suid deserialize(JsonParser parser, DeserializationContext ctx) throws IOException, JsonProcessingException {
			return new Suid(parser.getValueAsString());
		}
		
	}

	private long value;
	private transient String strVal; // cache, should not be serialized

	/**
	 * Creates a suid based on the given string {@code value}.
	 * 
	 * @param value The value of the new suid as a base-36 string, or {@code null}.
	 * 
	 * @throws NumberFormatException When the supplied value can not be parsed as base-36.
	 */
	public Suid(String value) throws NumberFormatException {
		this(value == null ? 0L : Long.parseLong(value, 36));
	}

	/**
	 * Creates a suid based on the given long {@code value}.
	 * 
	 * @param value The long value to set this Suid to.
	 */
	public Suid(long value) {
		this.value = value;
	}

	/**
	 * Creates a suid based on the given {@code block} and {@code id} constituent parts.
	 * 
	 * @param block The block bits for the suid, in a long.
	 * @param id The id bits for the suid, in a byte.
	 */
	public Suid(long block, byte id) {
		this.value = ~MASK_RESERVED & ((MASK_BLOCK & (block << OFFSET_BLOCK)) | (MASK_ID & (id << OFFSET_ID)));
	}

	/**
	 * Returns a base-36 string representation of this suid.
	 * 
	 * @return A String of at least 1 character and at most 11 characters.
	 */
	@Override public String toString() {
		if (strVal == null) {strVal = Long.toString(value, 36);}
		return strVal;
	}
	
	/**
	 * Use {@link #longValue()} instead.
	 */
	@Override public int intValue() {
		return (int) value;
	}

	/**
	 * Returns this suid's underlying value.
	 * 
	 * <p>Suid's use a {@code long} as underlying value. Avoid using {@code intValue} 
	 * and {@code floatValue} as these perform narrowing conversions.</p>
	 */
	@Override public long longValue() {
		return value;
	}

	/**
	 * Use {@link #longValue()} instead.
	 * 
	 * <p>If you must have a floating point number, use {@code doubleValue} which can 
	 * actually store all possible suids (they are limited to 52 bits for this purpose).</p>
	 */
	@Override public float floatValue() {
		return (float) value;
	}

	/**
	 * Returns the value of this Suid as a double.
	 * 
	 * <p>Although suids internally use {@code long}s to store the bits, since they
	 * are limited to 52 bits, they can actually be represented as {@code double} as
	 * well without loss of precision.</p>
	 */
	@Override public double doubleValue() {
		return (double) value;
	}

	/**
	 * Converts this Suid to a Java Long.
	 * 
	 * @return This suid's value converted to a Long, never {@code null}.
	 */
	public Long toLong() {
		return Long.valueOf(value);
	}
	
	/**
	 * Gets the block bits.
	 * 
	 * @return A long with the block bits.
	 */
	public long getBlock() {
		return (value & MASK_BLOCK) >> OFFSET_BLOCK;
	}

	/**
	 * Gets the ID bits.
	 * 
	 * @return An int with the ID bits (always in range {@code 0 .. 63}).
	 */
	public byte getId() {
		return (byte) ((value & MASK_ID) >> OFFSET_ID);
	}

	/**
	 * Returns the length of the string representation of this suid.
	 * 
	 * <p>Equivalent to {@code toString().length()}.</p>
	 */
	@Override public int length() {
		return toString().length();
	}

	/**
	 * Returns the char present at the given {@code index} in the base-36 string representation of this suid.
	 * 
	 * <p>Equivalent to {@code toString().charAt(index)}.</p>
	 */
	@Override public char charAt(int index) {
		return toString().charAt(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public CharSequence subSequence(int start, int end) {
		return toString().subSequence(start, end);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public int hashCode() {
		return Long.hashCode(value);
	}

	/**
	 * Compares this suid to {@code that} for equality.
	 * 
	 * @param that The suid to compare to, may be {@code null}, in which case {@code false} will be returned.
	 * @return {@code true} if this suid is equals to {@code that}, {@code false} otherwise.
	 */
	@Override public boolean equals(Object that) {
		return that != null && that.getClass().equals(Suid.class) && ((Suid) that).value == value;
	}

	/**
	 * Compares this suid to {@code that} for order.
	 * 
	 * @param that The suid to compare to, never {@code null}.
	 * @return {@code -1} if this suid is less than, {@code 0} if it is equals to, and {@code 1} if it is greater than {@code that}.
	 * @throws NullPointerException when {@code that} is {@code null}.
	 */
	@Override public int compareTo(Suid that) throws NullPointerException {
		return Long.compare(value, that.value);
	}

	/**
	 * Indicates whether the given {@code value} looks like a valid suid string.
	 * 
	 * <p>If this method returns {@code true}, this only indicates that it's *probably*
	 * valid. There are no guarantees.</p>
	 * 
	 * @param value The value, may be {@code null}, in which case this method returns {@code false}.
	 * @return {@code true} if it looks valid, {@code false} otherwise.
	 */
	public static boolean looksValid(String value) {
		if (value == null) {return false;}
		int len = value.length();
		if ((len == 0) || (len > 11)) {return false;}
		if ((len == 11) && (ALPHABET.indexOf(value.charAt(0)) > 2)) {return false;}
		for (int i=0; i<len; i++) {
			if (ALPHABET.indexOf(value.charAt(i)) == -1) {return false;}
		}
		return true;
	}

	/**
	 * Converts the given list of {@code ids} to a list of longs.
	 * 
	 * @param ids The ids to convert, may be empty but not {@code null}.
	 * @return The list of longs, may be empty but never {@code null}.
	 */
	public static List<Long> toLong(List<Suid> ids) {
		List<Long> results = new ArrayList<Long>();
		for (Suid id : ids) {results.add(id.toLong());}
		return results;
	}
	
	/**
	 * Converts the given list of {@code ids} to a list of strings.
	 * 
	 * @param ids The ids to convert, may be empty but not {@code null}.
	 * @return The list of strings, may be empty but never {@code null}.
	 */
	public static List<String> toString(List<Suid> ids) {
		List<String> results = new ArrayList<String>();
		for (Suid id : ids) {results.add(id.toString());}
		return results;
	}
	
	/**
	 * Converts the given list of {@code ids} to a list of Suids.
	 * 
	 * @param ids The ids to convert, may be empty but not {@code null}.
	 * @return The list of Suids, may be empty but never {@code null}.
	 */
	public static List<Suid> fromLong(List<Long> ids) {
		List<Suid> results = new ArrayList<Suid>();
		for (Long id : ids) {results.add(new Suid(id.longValue()));}
		return results;
	}
	
	/**
	 * Converts the given list of {@code ids} to a list of Suids.
	 * 
	 * @param ids The ids to convert, may be empty but not {@code null}.
	 * @return The list of Suids, may be empty but never {@code null}.
	 */
	public static List<Suid> fromString(List<String> ids) {
		List<Suid> results = new ArrayList<Suid>();
		for (String id : ids) {results.add(new Suid(id));}
		return results;
	}
	
	public static void main(String... args) {
//		List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
//		System.out.println(ids);  // [14she, 14shf, 14shg]
//		List<Long> vals = Suid.toLong(ids);
//		System.out.println(vals); // [1903154, 1903155, 1903156]
		
		//
		
//		List<Suid> ids = Arrays.asList(new Suid[]{new Suid(1903154), new Suid(1903155), new Suid(1903156)});
//		System.out.println(ids);  // [14she, 14shf, 14shg]
//		List<String> vals = Suid.toString(ids);
//		System.out.println(vals); // [14she, 14shf, 14shg]

		//
		
//		List<Long> vals = Arrays.asList(new Long[]{Long.valueOf(1903154), Long.valueOf(1903155), Long.valueOf(1903156)});
//		System.out.println(vals); // [1903154, 1903155, 1903156]
//		List<Suid> ids = Suid.fromLong(vals);
//		System.out.println(ids);  // [14she, 14shf, 14shg]
		
		//
		
		List<String> vals = Arrays.asList(new String[]{"14she", "14shf", "14shg"});
		System.out.println(vals); // [14she, 14shf, 14shg]
		List<Suid> ids = Suid.fromString(vals);
		System.out.println(ids);  // [14she, 14shf, 14shg]
	}
}
