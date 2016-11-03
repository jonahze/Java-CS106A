/*
 * File: ImageShop.java
 * --------------------
 * This program implements a much simplified version of Photoshop that allows
 * the user to manipulate images. Some possible manipulations include
 * flipping the image, rotating it, equalizing it, grayscale, cropping it, mirroring it, 
 * equalizing it, etc. Most of the programs include implementation using the box. There is also a button
 * to turn boxes green if you want to use the green screen, and if you want to designate a 
 * very specific area green there is a button which changes your brush into a green paint brush
 */

import acm.graphics.*;
import acm.program.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ImageShop extends GraphicsProgram {

	public void init() {
		add(new FlipVerticalButton(), WEST);
		add(new FlipHorizontalButton(), WEST);
		add(new RotateLeftButton(), WEST);
		add(new RotateRightButton(),WEST);
		add(new MirrorRightButton(), WEST);
		add(new MirrorLeftButton(), WEST);
		add(new MirrorBottomButton(), WEST);
		add(new MirrorTopButton(), WEST);
		add(new CropButton(), WEST);
		add(new GrayscaleButton(), WEST);
		add(new GreenScreenButton(), WEST);
		add(new EqualizeButton(), WEST);
		add(new AveragingFilterButton(), WEST);
		add(new DeleteBoxButton(), WEST);
		add(new PaintBoxGreenButton(), WEST);
		add(new PaintGreenButton(), WEST);
		addActionListeners();
		addMouseListeners();
		ui = new ImageShopUI(this);
	}

	/**
	 * Gets the image from the ImageShopUI.
	 *
	 * @return The current image or null if there isn't one
	 */

	public GImage getImage() {
		return ui.getImage();
	}

	/**
	 * Sets the current image.
	 *
	 * @param image The new GImage
	 */

	public void setImage(GImage image) {
		ui.setImage(image);
	}

	/* Implement the ActionListener interface */

	public void actionPerformed(ActionEvent e) {
		ImageShopButton button = (ImageShopButton) e.getSource();
		button.execute(this);
	}

	/*Method: mousePressed*/
	/*
	 * this method is called whenever the mouse is pressed to create the 
	 * crop box. It first checks to see if the mouse is in the bounds
	 * of the picture, then it begins creating the crop box
	 */
	public void mousePressed(MouseEvent e){
		GImage image = getImage();
		if(image == null) return;
		if(!isPainting){
			int xStart = e.getX();
			int yStart = e.getY();
			if(!checkBounds(xStart, yStart) || !checkBounds(xStart, yStart)){
				if( xStart > (image.getWidth() + image.getX())) xStart = (int)(image.getWidth() + image.getX());
				if( xStart < image.getX()) xStart = (int)image.getX();
				if( yStart > (image.getHeight() + image.getY())) yStart = (int)(image.getHeight() + image.getY());
				if (yStart < image.getY()) yStart = (int)image.getY();
			}
			startX = xStart;
			startY = yStart;
			currentRect.setColor(Color.RED);
		} else {
			paintPixels(e, image);
		}
	}

	/*Method: mouseDragged*/
	/*
	 * is called whenever the mouse is drags, checks to make sure the
	 * mouse is within the boundaries of the image then resizes the crop
	 * box if it is. If it isn't it snaps the crop box to the edge of the image
	 */
	public void mouseDragged(MouseEvent e){
		GImage image = getImage();
		if(image == null) return;
		if(!isPainting){
			double x = e.getX();
			double y = e.getY();
			if(!checkBounds((int)x, (int)y) || !checkBounds((int)startX, (int)startY)){
				if( x > (image.getWidth() + image.getX())) x = (image.getWidth() + image.getX());
				if( x < image.getX()) x = image.getX();
				if( y > (image.getHeight() + image.getY())) y = (image.getHeight() + image.getY());
				if (y < image.getY()) y = image.getY();
			}
			double newX = Math.min(x, startX);
			double newY = Math.min(y, startY);
			double width = Math.abs(x - startX);
			double height = Math.abs(y - startY);
			currentRect.setBounds(newX, newY, width, height);
			add(currentRect);
		} else {
			paintPixels(e, image);
		}
	}

	/*Method: getCropBox*/
	/*
	 * returns the GRect crop box when called
	 */
	public GRect getCropBox() {
		return currentRect;
	}

	/*Method: removeCropBox*/
	/*
	 * removes the GRect crop box when called and then resets the crop box
	 * to start at the beginning of the image and have a width and height
	 * of zero
	 */
	public void removeCropBox() {
		GImage image = getImage();
		remove(currentRect);
		startX = image.getX();
		startY = image.getY();
		currentRect.setBounds(startX, startY, 0, 0);
	}

	/*Method: checkBounds*/
	/*
	 * checks to make sure the integers for the x and y value are inside
	 * the bounds of the GImage and if they are returns a true boolean
	 */
	private boolean checkBounds(int xStart, int yStart) {
		GImage image = getImage();
		if( xStart > image.getX() && xStart < (image.getWidth() + image.getX())){
			if( yStart > image.getY() && yStart < (image.getHeight() + image.getY())){
				return true;
			}
		}
		return false;
	}

	/*Method: computeLuminosity*/
	/*
	 * computes the Luminosity values for a pixel given the red, green,
	 * and blue values
	 */
	public int computeLuminosity(int r, int g, int b) {
		return GMath.round(0.299 * r + 0.587 * g + 0.114 * b);
	}

	/*Method: SwitchIsPainting*/
	/*
	 * switches the isPainting boolean
	 */
	public void switchIsPainting(){
		if(isPainting) isPainting = false;
		else isPainting = true;
	}

	private void paintPixels(MouseEvent e, GImage image){
		int[][] array = image.getPixelArray();
		int x = (int)e.getX() - (int)image.getX();
		int y = (int)e.getY() - (int)image.getY();
		int pixel = array[y][x];
		pixel = Color.GREEN.getRGB();
		array[y][x] = pixel;
		array[y-1][x] = pixel;
		array[y-2][x] = pixel;
		array[y-1][x-1] = pixel;
		array[y-1][x+1] = pixel;
		array[y+1][x] = pixel;
		array[y+2][x] = pixel;
		array[y+1][x-1] = pixel;
		array[y+1][x+1] = pixel;
		array[y][x+1] = pixel;
		array[y][x+2] = pixel;
		array[y][x-1] = pixel;
		array[y][x-2] = pixel;
		setImage(new GImage(array));
	}

	/* Constants */

	public static final int APPLICATION_WIDTH = 950;
	public static final int APPLICATION_HEIGHT = 600;

	/* Private instance variables */

	private ImageShopUI ui;
	private double startX = 0;
	private double startY = 0;
	private GRect currentRect = new GRect(startX, startY, 0, 0);
	private boolean isPainting = false;

}

/**
 * This class represents the abstract superclass of all ImageShop buttons
 * as they appear on the left side of the window.  All subclasses must
 * invoke the constructor for this class by calling
 *
 *     super(name)
 *
 * where name is a string indicating the name of the button.  The abstract
 * class ImageShopButton specifies a method
 *
 *    public abstract void execute(ImageShop app)
 *
 * This definition forces each of the concrete classes to supply a
 * definition of execute that performs the appropriate operations.
 * The execute method takes the ImageShop application as an argument,
 * which makes it possible for the button to manipulate the image
 * and determine the crop box.
 */

abstract class ImageShopButton extends JButton {

	/**
	 * Constructs a new ImageShopButton.  Subclasses must invoke this
	 * constructor with the appropriate button name.
	 *
	 * @param name The name that appears on the button
	 */

	public ImageShopButton(String name) {
		super(name);
	}

	/**
	 * Executes the operation for the specific button.  This method must
	 * be defined individually for each subclass.
	 *
	 * @param app The ImageShop application
	 */

	public abstract void execute(ImageShop app);

}

/**
 * This class implements the "Flip Vertical" button, which flips the
 * image vertically.  All other buttons will have a similar structure.
 */

class FlipVerticalButton extends ImageShopButton {

	public FlipVerticalButton() {
		super("Flip Vertical");
	}

	/*
	 * Creates a new image which consists of the bits in the original image
	 * flipped vertically around the center line.  This code comes from
	 * page 434 of The Art and Science of Java.
	 */

	public void execute(ImageShop app) {
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int p1 = yStart; p1 < (yStart +(croppedArrayHeight / 2)); p1++) {
				int p2 = croppedArrayHeight + yStart - (p1 - yStart) -1;
				for (int i = xStart; i < xStart + croppedArrayWidth; i++){
					int temp = croppedArray[p1][i];
					croppedArray[p1][i] = croppedArray[p2][i];
					croppedArray[p2][i] = temp;
				}
			}
		} else{
			for (int p1 = 0; p1 < height / 2; p1++) {
				int p2 = height - p1 - 1;
				int[] temp = croppedArray[p1];
				croppedArray[p1] = croppedArray[p2];
				croppedArray[p2] = temp;
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

}

/*Class: FlipHorizontalButton*/
/*
 * implements a button which flips the image horizontally, it also can use
 * the crop box and only flip that which is inside it
 */
class FlipHorizontalButton extends ImageShopButton {

	public FlipHorizontalButton(){
		super("Flip Horizontal");
	}

	/*
	 * Creates a new image which consists of the bits from the original 
	 * image flipped horizontally around the center line
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int row = yStart; row < croppedArrayHeight + yStart; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = xStart; i < (xStart +(croppedArrayWidth / 2)); i++) {
					swapElements(rowArray, i, xStart, croppedArrayWidth);
				}
			}
		} else {
			for (int row = 0; row < height; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = 0; i < (array[0].length) / 2; i++) {
					swapElements(rowArray, i, 0, array[0].length);
				}
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

	/*Method: swapElements*/
	/*
	 * swaps the elements in a given array front to back
	 */
	private void swapElements(int [] array, int i, int xStart, int croppedArrayWidth){
		int p1 = array[i];
		int p2 = array[croppedArrayWidth + xStart - (i - xStart) -1];
		array[i] = p2;
		array[croppedArrayWidth + xStart - (i - xStart) -1] = p1;
	}
}

/*Class: RotateLeftButton*/
/*
 * implements a button which rotates the image 90 degrees counter-clockwise
 */
class RotateLeftButton extends ImageShopButton {

	public RotateLeftButton(){
		super("Rotate Left");
	}

	/*
	 *creates a new image which consists of bits from the old one. The 
	 *new image has the inverse amount of rows and columns of pixels, 
	 *and has had its row pixels become column pixels and its column 
	 *pixels become row pixels. finally you flip it vertically and it
	 *appears to have rotated left
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int width = array[0].length;
		int[][] newArray = new int[width][height];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				newArray[width - j -1][i] = array[i][j];
			}
		}
		app.setImage(new GImage(newArray));
	}

}

/*Class: RotateRightButton*/
/*
 * implements a button which rotates the image 90 degrees clockwise
 */
class RotateRightButton extends ImageShopButton {

	public RotateRightButton(){
		super("Rotate Right");
	}

	/*
	 *creates a new image which consists of bits from the old one. The 
	 *new image has the inverse amount of rows and columns of pixels, 
	 *and has had its row pixels become column pixels and its column 
	 *pixels become row pixels. finally you flip it horizontally and it
	 *appears to have rotated right
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int width = array[0].length;
		int[][] newArray = new int[width][height];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				newArray[j][height - i -1] = array[i][j];
			}
		}
		app.setImage(new GImage(newArray));
	}
}

/*Class: GrayscaleButton*/
/*
 * implements a button which turns the image into a black and white 
 * version of itself or a grayscale version, it is also compatible with the box
 */
class GrayscaleButton extends ImageShopButton {

	public GrayscaleButton(){
		super("Grayscale");
	}

	/*
	 *creates a new image by taking the luminosity value of each pixel 
	 *replacing that pixel in the array with just the luminosity value
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if(croppedArrayHeight == 0 ||croppedArrayWidth == 0){
			yStart = 0;
			xStart = 0;
			croppedArrayHeight = array.length;
			croppedArrayWidth = array[0].length;
		}
		for (int i = yStart; i < croppedArrayHeight + yStart; i++){
			for (int j = xStart; j < croppedArrayWidth + xStart; j++){
				int pixel = array[i][j];
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
				int xx = app.computeLuminosity(red, green, blue);
				pixel = (0xFF << 24) | (xx << 16) | (xx << 8) | xx;
				croppedArray[i][j] = pixel;
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

}

/*class: GreenScreenButton*/
/*
 * implements a button which turns all the very green pixels transparent
 */
class GreenScreenButton extends ImageShopButton {

	public GreenScreenButton(){
		super("Green Screen");
	}

	/*
	 *creates a new image by iterating through the image array and turning
	 *all the green pixels transparent
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int width = array[0].length;
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){
				int pixel = array[i][j];
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
				if(pixelIsGreen(red, green, blue)){
					pixel = pixel >>> 8;
				}
				array[i][j] = pixel;
			}
		}
		app.setImage(new GImage(array));
	}

	/*Method: pixelIsGreen*/
	/*
	 * returns a true boolean if the current pixel is green enough to be
	 * considered a green screen and therefore should be turn transparent.
	 * returns false if it isn't green enough
	 */
	private boolean pixelIsGreen(int r, int g, int b) {
		if(g > 2 * r && g > 2 * b){
			return true;
		}
		return false;
	}
}

/*Class: EqualizeButton*/
/*
 * implements a button which increases the contrast of the image to 
 * optimum amounts
 */
class EqualizeButton extends ImageShopButton {

	public EqualizeButton(){
		super("Equalize");
	}

	/*
	 * creates an Image by taking a cumulative histogram of the luminosity
	 * values of the old one and then using that to reassign the luminosity
	 * value of the pixel do the optimum value based on its percent of the
	 * total luminosity
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int width = array[0].length;
		int [] histogram = new int[256];
		computeHistogram(histogram, height, width, array, app);
		int [] cumHistogram = new int[256];
		computeCumHistogram(histogram, cumHistogram);
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++){
				int pixel = array[i][j];
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
				int xx = app.computeLuminosity(red, green, blue);
				double value = cumHistogram[xx];
				double percent = value / (height * width);
				int newLumin = GMath.round(255 * percent);
				pixel = (0xFF << 24) | (newLumin << 16) | (newLumin << 8) | newLumin;
				array[i][j] = pixel;
			}
		}
		app.setImage(new GImage(array));
	}

	/*Method: computeHistogram*/
	/*
	 * creates a histogram of all the luminosity values of the pixels in 
	 * an image array
	 */
	private void computeHistogram(int[] histogram, int height, int width, int[][] array, ImageShop app) {
		for(int i = 0; i < height; i ++){
			for(int j = 0; j < width; j++){
				int pixel = array[i][j];
				int red = (pixel >> 16) & 0xFF;
				int green = (pixel >> 8) & 0xFF;
				int blue = pixel & 0xFF;
				int xx = app.computeLuminosity(red, green, blue);
				histogram[xx] ++;
			}
		}
	}

	/*Method computeCumHistogram*/
	/*
	 * creates a cumulative histogram by taking a previous one and adding
	 * the previous values for each spot in the histogram
	 */
	private void computeCumHistogram(int[] histogram, int[] cumHistogram){
		for( int i = 0; i < 256; i++){
			int previousValue = 0;
			if (i > 0) {
				previousValue = cumHistogram[i - 1];
			}
			cumHistogram[i] = histogram[i] + previousValue;
		}
	}
}

/*Class CropButton*/
/*
 * implements a crop button which makes an image containing only the pixels
 * inside a given crop box
 */
class CropButton extends ImageShopButton{

	public CropButton(){
		super("Crop"); 
	}

	/*
	 * creates a new image by getting the boundaries of the crop box and 
	 * making a new array with those boundaries and filling in those values
	 * from the old array
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		if(croppedArrayHeight == 0 ||croppedArrayWidth == 0) return;
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = new int[croppedArrayHeight][croppedArrayWidth];
		for(int i = yStart; i < croppedArrayHeight + yStart; i++){
			for(int j = xStart; j < croppedArrayWidth + xStart; j++){
				croppedArray[i - yStart][j - xStart] = array[i][j];
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

}

/*Method: DeleteBoxButton*/
/*
 * implements a button which can be used to delete the current box
 */
class DeleteBoxButton extends ImageShopButton{

	public DeleteBoxButton(){
		super("Delete Box"); 
	}

	public void execute(ImageShop app){
		app.removeCropBox();
	}
}

/*Class AveragingFilterButton*/
/*
 * implements a button that filters the image so that it replaces each pixel with the average
 * luminosity of that pixel and its four neighbors
 */
class AveragingFilterButton extends ImageShopButton{

	public AveragingFilterButton(){
		super("Averaging Filter");
	}

	/*
	 * creates an image that has replaced each pixel with the average lumonisty of it and its
	 * four neighbors
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		if(image == null) return;		
		int[][] array = image.getPixelArray();
		int height = array.length;
		int width = array[0].length;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				int xx = averageNeighborLuminosity(array, i, j, app);
				array[i][j] = (0xFF << 24) | (xx << 16) | (xx << 8) | xx;
			}
		}
		app.setImage(new GImage(array));
	}

	/*Method: averageNeighborLuminosity*/
	/*
	 * computes the average lumonisity of a pixel and its four neighbors and returns the value
	 */
	private int averageNeighborLuminosity(int[][] array, int i, int j, ImageShop app){
		int sum = getLuminosity(array, i, j, app);
		int count = 1;
		if (i > 0) {
			sum += getLuminosity(array, i - 1, j, app);
			count ++;
		}
		if (i < array.length - 1) {
			sum += getLuminosity(array, i + 1, j, app);
			count ++;
		}
		if (j > 0) {
			sum += getLuminosity(array, i, j - 1, app);
			count ++;
		}
		if (j < array[0].length - 1) {
			sum += getLuminosity(array, i, j + 1, app);
			count ++;
		}
		return GMath.round((double) sum / count);
	}

	/*Method: getLuminosity*/
	/*
	 * returns the value of the luminosity of a single pixel given its array and location
	 */
	private int getLuminosity(int[][] array, int i, int j, ImageShop app) {
		int pixel = array[i][j];
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		return app.computeLuminosity(red, green, blue);
	}
}

/*Class: MirrorRightButton*/
/*
 * implements a button that takes the right half of the image and replaces the left half 
 * with it, creating a mirroring effect. Is compatible with the crop box
 */
class MirrorRightButton extends ImageShopButton {

	public MirrorRightButton(){
		super("Mirror Right Half");
	}
	/*
	 * creates an image that mirrors the right side of the original image by replacing all 
	 * the pixels on the left half with the corresponding ones on the right
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int row = yStart; row < croppedArrayHeight + yStart; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = xStart; i < (xStart +(croppedArrayWidth / 2)); i++) {
					mirrorRight(rowArray, i, xStart, croppedArrayWidth);
				}
			}
		} else {
			for (int row = 0; row < height; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = 0; i < (array[0].length) / 2; i++) {
					mirrorRight(rowArray, i, 0, array[0].length);
				}
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

	/*Method: mirrorRight*/
	/*
	 * sets the elements in the left side of the array to mirror the right
	 */
	private void mirrorRight(int [] array, int i, int xStart, int croppedArrayWidth){
		int p2 = array[croppedArrayWidth + xStart - (i - xStart) -1];
		array[i] = p2;
	}
}

/*Class: MirrorLeftButton*/
/*
 * implements a button that takes the left half of the image and replaces the right half 
 * with it, creating a mirroring effect. Is compatible with the crop box
 */
class MirrorLeftButton extends ImageShopButton {

	public MirrorLeftButton(){
		super("Mirror Left Half");
	}

	/*
	 * creates an image that mirrors the left side of the original image by replacing all 
	 * the pixels on the right half with the corresponding ones on the left
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int row = yStart; row < croppedArrayHeight + yStart; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = xStart; i < (xStart +(croppedArrayWidth / 2)); i++) {
					mirrorLeft(rowArray, i, xStart, croppedArrayWidth);
				}
			}
		} else {
			for (int row = 0; row < height; row++) {
				int[] rowArray = croppedArray[row];
				for(int i = 0; i < (array[0].length) / 2; i++) {
					mirrorLeft(rowArray, i, 0, array[0].length);
				}
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}

	/*Method: mirrorRight*/
	/*
	 * sets the elements in the right side of the array to mirror the left
	 */
	private void mirrorLeft(int [] array, int i, int xStart, int croppedArrayWidth){
		int p1 = array[i];
		array[croppedArrayWidth + xStart - (i - xStart) -1] = p1;
	}
}

/*Class: MirrorBottomButton*/
/*
 * implements a button that takes the bottom half of the image and replaces the top half 
 * with it, creating a mirroring effect. Is compatible with the crop box
 */
class MirrorBottomButton extends ImageShopButton{

	public MirrorBottomButton(){
		super("Mirror Bottom Half");
	}

	/*
	 * creates an image that mirrors the bottom side of the original image by replacing all 
	 * the pixels on the top half with the corresponding ones on the bottom
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int p1 = yStart; p1 < (yStart +(croppedArrayHeight / 2)); p1++) {
				int p2 = croppedArrayHeight + yStart - (p1 - yStart) -1;
				for (int i = xStart; i < xStart + croppedArrayWidth; i++){
					croppedArray[p1][i] = croppedArray[p2][i];
				}
			}
		} else{
			for (int p1 = 0; p1 < height / 2; p1++) {
				int p2 = height - p1 - 1;
				croppedArray[p1] = croppedArray[p2];
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}
}

/*Class: MirrorTopButton*/
/*
 * implements a button that takes the top half of the image and replaces the bottom half 
 * with it, creating a mirroring effect. Is compatible with the crop box
 */
class MirrorTopButton extends ImageShopButton{

	public MirrorTopButton(){
		super("Mirror Top Half");
	}

	/*
	 * creates an image that mirrors the top half of the original image by replacing all 
	 * the pixels on the bottom half with the corresponding ones on the top
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int height = array.length;
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if (!(croppedArrayHeight == 0 ||croppedArrayWidth == 0)){
			for (int p1 = yStart; p1 < (yStart +(croppedArrayHeight / 2)); p1++) {
				int p2 = croppedArrayHeight + yStart - (p1 - yStart) -1;
				for (int i = xStart; i < xStart + croppedArrayWidth; i++){
					croppedArray[p2][i] = croppedArray[p1][i];
				}
			}
		} else{
			for (int p1 = 0; p1 < height / 2; p1++) {
				int p2 = height - p1 - 1;
				croppedArray[p2] = croppedArray[p1];
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
	}
}

/*Class: PaintGreenButton*/
/*
 * implements a button which toggles if the user is painting green with their mouse or making
 * boxes
 */
class PaintGreenButton extends ImageShopButton{

	public PaintGreenButton(){
		super("Paint Green Toggle on/off");
	}

	public void execute(ImageShop app){
		app.removeCropBox();
		app.switchIsPainting();
	}
}
/*Method: PaintBoxGreen*/
/*
 * implements a button that given a box, it turns all pixels inside of it green so that 
 * they can be made transparent by the green screen button
 */
class PaintBoxGreenButton extends ImageShopButton{
	
	public PaintBoxGreenButton(){
		super("Paint Box Green");
	}
	
	/*
	 * takes the given box and sets the color of each pixel inside it to green
	 */
	public void execute(ImageShop app){
		GImage image = app.getImage();
		GRect cropBox = app.getCropBox();
		if(image == null) return;
		int[][] array = image.getPixelArray();
		int croppedArrayHeight = (int)(cropBox.getHeight());
		int croppedArrayWidth = (int)(cropBox.getWidth());
		int xStart = (int)cropBox.getX() - (int)image.getX();
		int yStart = (int)cropBox.getY() - (int)image.getY();
		int[][] croppedArray = array;
		if(croppedArrayHeight == 0 ||croppedArrayWidth == 0) return;
		for (int i = yStart; i < croppedArrayHeight + yStart; i++){
			for (int j = xStart; j < croppedArrayWidth + xStart; j++){
				int pixel = array[i][j];
				pixel = Color.GREEN.getRGB();
				croppedArray[i][j] = pixel;
			}
		}
		app.setImage(new GImage(croppedArray));
		app.removeCropBox();
		
	}
}
