package com.balu.convert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.print.Doc;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfRectangle;
import org.apache.commons.io.FileUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class TiffToPDF {
    public static String logpath = null;

    private static Float lowerX ;
    private static Float lowerY ;
    private static Float upperX ;
    private static Float upperY ;

    public TiffToPDF() {

        try{
            JFileChooser directory = new JFileChooser();
            directory.setFileSelectionMode(1);

            int option = directory.showOpenDialog(null);

            if (option == 0) {

                try {

                    String sourceDir = directory.getSelectedFile().toString();

                    //Get the source directory from JFileChooser
                    File fileSrc = new File(sourceDir);

                    //Extensions to filter tif file
                    String[] tif_exts = new String[] { "TIF" };

                    //Used to list all directory with contain tif image
                    List<File> files = (List<File>) FileUtils.listFiles(fileSrc, tif_exts, true);

                    System.out.println(" File count  : " +files.size());
                    int count = 0;
                    convertToPDF(files,sourceDir + "destination.pdf",lowerX,lowerY, upperX, upperY);
                    JOptionPane.showMessageDialog(null, "Completed");
                } catch (ArrayIndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(null, " Please try again");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User Cancelled the Operation");
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);    //System.in is a standard input stream
        System.out.print("Enter lowerX value : ");
        lowerX = sc.nextFloat();
        System.out.print("Enter lowerY value : ");
        lowerY = sc.nextFloat();
        System.out.print("Enter upperX value : ");
        upperX = sc.nextFloat();
        System.out.print("Enter upperY value : ");
        upperY = sc.nextFloat();
        new TiffToPDF();

    }

    public boolean convertToPDF(List<File> files,String targetfile, float lowerX, float lowerY, float upperX, float upperY){
        //Rectangle X, and Y values are below to draw image on the PDF.
        lowerX = (lowerX < 0)?0:lowerX;
        lowerY = (lowerY < 0)?0:lowerY;
        upperX = (upperX < 2000)?2000:upperX;
        upperY = (upperY < 3000)?3000:upperY;
        try {
            Rectangle rect = new Rectangle(lowerX, lowerY, upperX, upperY);
            Document document = new Document(rect);
            FileOutputStream fos = new FileOutputStream(targetfile);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.open();
            document.open();

            for (File srcFile : files) {
                //Check whether File converted to PDF
                File pdfFiles = new File(targetfile);
                Iterator readers=javax.imageio.ImageIO.getImageReadersBySuffix("tiff");

                if (readers.hasNext()) {
                    ImageInputStream iis=javax.imageio.ImageIO.createImageInputStream(srcFile);
                    TIFFDecodeParam param=null;
                    ImageDecoder dec=ImageCodec.createImageDecoder("tiff",srcFile,param);
                    int pageCount=dec.getNumPages();
                    ImageReader imageReader=(ImageReader)(readers.next());

                    if (imageReader != null) {

                        imageReader.setInput(iis,true);

                        for (int i=0; i < pageCount; i++) {
                            //Read the source file (tiff)
                            BufferedImage srcImg = imageReader.read(i);

                            BufferedImage bufferedImage = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(),
                                    BufferedImage.TYPE_INT_RGB);


                            //Set the RGB values for converted image (jpg)
                            for(int y = 0; y < srcImg.getHeight(); y++) {
                                for(int x = 0; x < srcImg.getWidth(); x++) {
                                    bufferedImage.setRGB(x, y, srcImg.getRGB(x, y));
                                }
                            }
                      /*
                        String s = "C:/Users/shrisowdhaman/Desktop/TIFF/A-04-000001/sample"+i+".jpg";
                        ImageIO.write(img2, "jpg", new File(s));
                        */
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "jpg", baos);
                            baos.flush();

                            // Convert byteArrayoutputSteam to ByteArray
                            byte[] imageInByte = baos.toByteArray();


                            document.add(Image.getInstance(imageInByte));

                            baos.close();

                        }//End of for loop
                    }else{
                        System.out.println("image is null for file :" + targetfile);
                        return false;
                    }
                }

            }
            //Close all open methods
            document.close();
            writer.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return true;
    }
}
