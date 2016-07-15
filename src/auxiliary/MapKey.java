package auxiliary;

import java.math.BigInteger;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class is a model for the association array.
 */
public class MapKey {

	public final String k1; // Key of the first element
	public final String k2; // Key of the second element

	/**
	 * A constructor for String elements
	 * 
	 * @param k1
	 * @param k2
	 */
	public MapKey(String k1, String k2) {
		this.k1 = k1;
		this.k2 = k2;
	}

	/**
	 * A constructor for BigInteger elements
	 * 
	 * @param k1
	 * @param k2
	 */
	public MapKey(BigInteger k1, BigInteger k2) {
		this.k1 = k1.toString();
		this.k2 = k2.toString();
	}

	/**
	 * A constructor for Integer elements
	 * 
	 * @param k1
	 * @param k2
	 */
	public MapKey(Integer k1, Integer k2) {
		this.k1 = k1.toString();
		this.k2 = k2.toString();
	}

	@Override
	/**
	 * Compares a given object with the current object.
	 * 
	 * @return true if two objects have same keys and values, otherwise false.
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MapKey theKey = (MapKey) o;

		if (k1 != null ? !k1.equals(theKey.k1) : theKey.k1 != null)
			return false;
		if (k2 != null ? !k2.equals(theKey.k2) : theKey.k2 != null)
			return false;

		return true;
	}

	@Override
	/**
	 * returns a unique hash code of the element
	 */
	public int hashCode() {
		int result = k1 != null ? k1.hashCode() : 0;
		result = 31 * result + (k2 != null ? k2.hashCode() : 0);
		return result;
	}
}
