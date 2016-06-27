package fidusWriter.converter.todocx;

public class Units {
	private static final double emuRatio = 9525;
	public static int pixels2Emu(int pixels)
    {
		
        return (int)Math.round(pixels*emuRatio);
    }

    public static int emu2Pixels(int emu)
    {
        if (emu != 0){
            return (int) Math.round(emu/emuRatio);
        } else {
            return 0;
        }
    }
    
    public static double pixels2Cm(int px){
    	return (px*0.026458);
    }
    
    public static double cm2Pixels(double cm){
    	return (cm*37.795276);
    }
    
    public static long pixels2Twip(int px){
    	// 567 twips = 1 centimeter.
    	return (long)Math.round(567*pixels2Cm(px));
    }
    
    public static int twip2Pixels(long tw){
    	// 567 twips = 1 centimeter.
    	return (int)Math.round(cm2Pixels(tw/567.0d));
    }
}
