package fidusWriter.converter.todocx;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import fidusWriter.model.images.Image;
import fidusWriter.model.images.Images;

public class Figure {
	// filled from properties
	private String dataEquation = null;					// from FIGURE.data_equation
	private Long dataImage = null;					// from FIGURE.data_image
	private FigureCategory dataFigureCategory = null;	// from FIGURE.data_figure_category
	private String dataCaption = null;					// from FIGURE.data_caption
	//
	private String path = null; 						// from FIGURE>DIV>IMG.src
	//
	public String getDataEquation() {
		return dataEquation;
	}
	public void setDataEquation(String dataEquation) {
		this.dataEquation = dataEquation;
	}
	public Long getDataImage() {
		return dataImage;
	}
	public void setDataImage(Long dataImage) {
		this.dataImage = dataImage;
	}
	public FigureCategory getDataFigureCategory() {
		return dataFigureCategory;
	}
	public void setDataFigureCategory(FigureCategory dataFigureCategory) {
		this.dataFigureCategory = dataFigureCategory;
	}
	public String getDataCaption() {
		return dataCaption;
	}
	public void setDataCaption(String dataCaption) {
		this.dataCaption = dataCaption;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public org.docx4j.wml.P addIt2Docx(WordprocessingMLPackage wordMLPackage, 
		Images images, String mediaDirectoryPrefixPath) throws Exception{
		Image image = images.getImage(this.getDataImage());
		String filenameHint = image.getPk().toString();
		String altText = (image!=null) ? image.getTitle() : null;
		Integer widthPx = (image!=null) ? image.getWidth() : null;
		Long widthTw = null;
		if(widthPx != null){
			if(widthPx.intValue()>500)
				widthTw = new Long(7501); // 7501 == Units.pixels2Twip(500);
			else
				widthTw = new Long(Units.pixels2Twip(widthPx.intValue()));
		}
		return ImageUtil.addImageToDocx(wordMLPackage,
					mediaDirectoryPrefixPath+this.getPath(), widthTw, 
					filenameHint, altText);
	}
}
