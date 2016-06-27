package mathEquations;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

public class EquationConvertor {
	private String fontsPath = "./templates/fonts"; 
	private String glyphsPath = "./templates/glyphs";
	private String mathmlDtd = "./templates/mathml_libs/mathml3.dtd";
	private String mml2ommlXsltPath = "./templates/mathml_libs/MML2OMML.XSL";
	private String omml2mmlXsltPath = "./templates/mathml_libs/OMML2MML.XSL";
	private String mmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	//
	public EquationConvertor() {
		this(null, null, null, null, null, EquationConvertor.netIsAvailable());
	}
	public EquationConvertor(boolean onlineHeader) {
		this(null, null, null, null, null, onlineHeader);
	}
	public EquationConvertor(String fontsPath, String glyphsPath, String mathmlDtd, 
					 String mml2ommlXsltPath, String omml2mmlXsltPath,
					 boolean onlineHeader) {
		super();
		if(fontsPath!=null)
			this.fontsPath = fontsPath;
		if(glyphsPath!=null)
			this.glyphsPath = glyphsPath;
		if(mathmlDtd != null)
			this.mathmlDtd = mathmlDtd;
		if(mml2ommlXsltPath!=null)
			this.mml2ommlXsltPath = mml2ommlXsltPath;
		if(omml2mmlXsltPath!=null)
			this.setOmml2mmlXsltPath(omml2mmlXsltPath);
		if(onlineHeader)
			this.setMmlHeader(this.getMmlHeader() + "\n<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 3.0//EN\" \"http://www.w3.org/Math/DTD/mathml3/mathml3.dtd\">\n");
		else
			this.setMmlHeader(this.getMmlHeader() + "\n<!DOCTYPE math SYSTEM \""+this.mathmlDtd+"\">\n");
		fmath.ApplicationConfiguration.setFolderUrlForFonts(this.fontsPath);
		fmath.ApplicationConfiguration.setFolderUrlForGlyphs(this.glyphsPath);
	}
	public String getFontsPath() {
		return fontsPath;
	}
	public void setFontsPath(String fontsPath) {
		this.fontsPath = fontsPath;
	}
	public String getGlyphsPath() {
		return glyphsPath;
	}
	public void setGlyphsPath(String glyphsPath) {
		this.glyphsPath = glyphsPath;
	}
	public String getMathmlDtd() {
		return mathmlDtd;
	}
	public void setMathmlDtd(String mathmlDtd) {
		this.mathmlDtd = mathmlDtd;
	}
	public String getMmlHeader() {
		return mmlHeader;
	}
	public void setMmlHeader(String mmlHeader) {
		this.mmlHeader = mmlHeader;
	}
	public String getOmml2mmlXsltPath() {
		return omml2mmlXsltPath;
	}
	public void setOmml2mmlXsltPath(String omml2mmlXsltPath) {
		this.omml2mmlXsltPath = omml2mmlXsltPath;
	}
	public String getMml2ommlXsltPath() {
		return mml2ommlXsltPath;
	}
	public void setMml2ommlXsltPath(String mml2ommlXsltPath) {
		this.mml2ommlXsltPath = mml2ommlXsltPath;
	}
	private static boolean netIsAvailable() {                                                                                                                                                                                                 
	    try {                                                                                                                                                                                                                                 
	        final URL url = new URL("http://www.w3.org/Math/DTD/mathml3/mathml3.dtd");                                                                                                                                                                                 
	        final URLConnection conn = url.openConnection();                                                                                                                                                                                  
	        conn.connect();                                                                                                                                                                                                                   
	        return true;                                                                                                                                                                                                                      
	    } catch (MalformedURLException e) {                                                                                                                                                                                                   
	        throw new RuntimeException(e);                                                                                                                                                                                                    
	    } catch (IOException e) {                                                                                                                                                                                                             
	        return false;                                                                                                                                                                                                                     
	    }                                                                                                                                                                                                                                     
	}
	public String mml2latex(String mmlString){
		String latex = fmath.conversion.ConvertFromMathMLToLatex.convertToLatex(mmlString);
		return latex;
	}
	public String omml2mml(String ommlXmlContent) throws TransformerException, UnsupportedEncodingException {
		InputStreamReader xmlFile = new InputStreamReader(IOUtils.toInputStream(ommlXmlContent));
		File xsltFile = new File(this.omml2mmlXsltPath);

        // JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(xmlFile);
        Source xsltSource = new StreamSource(xsltFile);

        // the factory pattern supports different XSLT processors
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        trans.transform(xmlSource, new StreamResult(byteArrayOutputStream));
        String mml = byteArrayOutputStream.toString(java.nio.charset.StandardCharsets.UTF_16.displayName());
        mml = new String(mml.getBytes("UTF-8"));
		mml = mml.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");
        return mml;
	}
	public String latex2mml(String latexString){
		String mathml = fmath.conversion.ConvertFromLatexToMathML.convertToMathML(latexString);
		return mathml;
	}
	public String mml2omml(String mmlXmlContent, boolean inline) throws TransformerException, UnsupportedEncodingException {
		InputStreamReader xmlFile = new InputStreamReader(IOUtils.toInputStream(mmlXmlContent));
		File xsltFile = new File(this.mml2ommlXsltPath);

        // JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(xmlFile);
        Source xsltSource = new StreamSource(xsltFile);

        // the factory pattern supports different XSLT processors
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        trans.transform(xmlSource, new StreamResult(byteArrayOutputStream));
        String omml = byteArrayOutputStream.toString(java.nio.charset.StandardCharsets.UTF_8.displayName());
        if(inline){
        	omml = omml.replace("<m:oMath", "<m:oMath xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" ");
        	omml = omml.replace("<m:r>", "<m:r><w:rPr><w:rFonts w:ascii=\"Cambria Math\" w:hAnsi=\"Cambria Math\"/></w:rPr>");
        }
        return omml;
	}
	public String latex2omml(String latexString, boolean inline) throws TransformerException, UnsupportedEncodingException{
		String mmlXmlContent = this.getMmlHeader()+this.latex2mml(latexString);
		return this.mml2omml(mmlXmlContent, inline);
	}
	public String omml2latex(String ommlString) throws TransformerException, UnsupportedEncodingException{
		String mmlXmlContent = this.omml2mml(ommlString);
		return this.mml2latex(mmlXmlContent);
	}
}
