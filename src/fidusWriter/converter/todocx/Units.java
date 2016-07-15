package fidusWriter.converter.todocx;

/**
 * @author Mahdi, Jaberzadeh Ansari
 * @role This class contains functions for converting pixels to twips in images.
 */
public class Units {
	/**
	 * Fixed converting ratio
	 */
	private static final double emuRatio = 9525;

	/**
	 * Converts pixel to emu
	 * 
	 * @param pixels
	 * @return
	 */
	public static int pixels2Emu(int pixels) {

		return (int) Math.round(pixels * emuRatio);
	}

	/**
	 * Converts emu to pixel
	 * 
	 * @param emu
	 * @return
	 */
	public static int emu2Pixels(int emu) {
		if (emu != 0) {
			return (int) Math.round(emu / emuRatio);
		} else {
			return 0;
		}
	}

	/**
	 * Converts pixel to cm
	 * 
	 * @param px
	 * @return
	 */
	public static double pixels2Cm(int px) {
		return (px * 0.026458);
	}

	/**
	 * Converts cm to pixel
	 * 
	 * @param cm
	 * @return
	 */
	public static double cm2Pixels(double cm) {
		return (cm * 37.795276);
	}

	/**
	 * Converts pixel to twip
	 * 
	 * @param px
	 * @return
	 */
	public static long pixels2Twip(int px) {
		// 567 twips = 1 centimeter.
		return (long) Math.round(567 * pixels2Cm(px));
	}

	/**
	 * Converts twip to pixel
	 * 
	 * @param tw
	 * @return
	 */
	public static int twip2Pixels(long tw) {
		// 567 twips = 1 centimeter.
		return (int) Math.round(cm2Pixels(tw / 567.0d));
	}
}
