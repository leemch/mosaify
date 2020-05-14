package javafx;
// RandomPictureAPI.java
//
// Grabs random images from web API

import java.net.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;


/*
 * Usage:
 * 
 * create new PictureAPI with url database and starting amount of images to grab
 * 
 * Getters -
 * call numOfImg to find current number of images in arraylist
 * call getImage(num) to get a single picture // num < numOfImg
 * call getAllImages() to get an arraylist of all images
 * 
 * Setters -
 * call updateAmount(num) to increase amount of images in arraylist // num > numOfImg
 * 
 */

public class PictureAPI {
	
	/*
	 * VARIABLES
	 */
	
	// list of images from API
	private static ArrayList<BufferedImage> listOfImages;
	// API link
	private static String url;
	
	
	
	/*
	 * CONSTRUCTORS
	 */
	
	// default constructor
	public PictureAPI() { }
	
	// constructor to set url
	public PictureAPI(String url) {
		PictureAPI.url = url;
	}

	// constructor with set url and starting cache of images
	public PictureAPI(String url, int amt) {
		PictureAPI.listOfImages = new ArrayList<>();
		PictureAPI.url = url;
		updateAmount(amt);
	}
	
	
	
	/*
	 * SETTERS
	 */
	
	// setter to increase amount of images in arraylist
	public void updateAmount(int amt) {
		if (listOfImages.size() < amt && amt > 0) {
			for (int i = 0 ; i < amt; i++) {
				accessAPI();
			}
			
			System.out.println("Array updated with " + amt + " more images, amount of images in array is " + numOfImg());
		} else {
			System.out.println("Array saturated with more pictures than specified amount");
		}
	}
	
	// private setter to retrieve image from api
	private void accessAPI() {
		try {
			URL imageURL = new URL(url);
			
			BufferedImage random = ImageIO.read(imageURL);
			
			addImage(random);
			
		} catch (MalformedURLException e) {
			System.out.println("Invalid url, try retrieving from a valid address");
		} catch (IOException e) {
			System.out.println("Cannot retrieve from API, try to validate API source");
		}
	}
	
	// private setter to add image to array
	private void addImage(BufferedImage img) { PictureAPI.listOfImages.add(img); }
	
	
	
	/*
	 * GETTERS
	 */
	
	// getter to retrieve image at array[i]
	public BufferedImage getImage(int i) { 
		if ( i < numOfImg() && i >= 0) {
			System.out.println("Retrived image "+ i + " from array");
			return PictureAPI.listOfImages.get(i); 
		}
		
		System.out.println("Attempted to access out of array bounds, try index less than " + numOfImg());
		return null;
	}
	
	// getter for all images in array
	public ArrayList<BufferedImage> getAllImages() { return PictureAPI.listOfImages; }
	
	// getter for number of images in array
	public int numOfImg() { return PictureAPI.listOfImages.size(); }
	
	
	
	/*
	 * DESTRUCTORS
	 */
	
	// drop all images from array
	public void clearAll() { 
		if (listOfImages.size() > 0) {
			listOfImages.clear(); 
			System.out.println("Array cleared");
		} else {
			System.out.println("Array is already empty");
		}
	}
	
	// remove single image from array
	public void clearOne(int i) {
		if (listOfImages.size() > 0) {
			if (i>= 0 && i < listOfImages.size()) {
				listOfImages.remove(i);
				System.out.println("Removed image " + i + " from array");
			} else {
				System.out.println("Attempted to remove out of array bounds, try index less than " + numOfImg());
			}
		} else {
			System.out.println("Array is already empty");
		}
	}
	
}
