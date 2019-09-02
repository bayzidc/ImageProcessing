import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.lang.Math;

public class Demo extends Component implements ActionListener {
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original", 
            "Negative",
            "Re-Scaling",
            "Shifting",
            "Re-Scaling and Shifting",
            "Addition",
            "Subtraction",
            "Multiplication",
            "Division",
            "BitwiseNOT",
            "BitwiseAND",
            "BitwiseOR",
            "BitwiseXOR",
            "ROI",
            "NegativeLinear",
            "Logarithmic",
            "PowerLaw",
            "LookUpTable",
            "BitPlaneSlicing",
            "Histogram",
            "Convolution",
            "SaltPepper",
            "MinFilter",
            "MaxFilter",
            "MidpointFilter",
            "MedianFilter",
            "MeanAndStandardDeviation",
            "SimpleThresholding",
            "AutomatedThresholding",

        };
    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi,bi2, biFiltered;   // the input image saved as bi;//
    int w, h;

    ArrayList<BufferedImage> imgList = new ArrayList<BufferedImage>();

    String filePath = "Lena.bmp";

    public Demo() {
        try {
            bi = ImageIO.read(new File(filePath));
            bi2 = ImageIO.read(new File("PeppersRGB.bmp"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);

            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }catch(Exception e2)
        {
            try
            {

                bi = ReadRaw(filePath);

                w = bi.getWidth( null );
                h = bi.getHeight( null );

                BufferedImage bi2 = new BufferedImage( w , h , BufferedImage.TYPE_INT_RGB );
                Graphics big = bi2.getGraphics();
                big.drawImage( bi , 0 , 0 , null );
                biFiltered = bi = bi2;

            }

            catch( Exception e )
            {

                e.printStackTrace();

            }
        }
    }                         

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();      

        g.drawImage(biFiltered, 0, 0, null);

    }

    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  Your turn now:  Add more function below
    //************************************

    public BufferedImage ReadRaw(String filePath)
    {
        try
        {
            FileInputStream file = new FileInputStream(filePath);

            int i = 0;
            int read;

            byte[] tempBuffer = new byte[512];

            String[] img = new String[512];

            while((read=file.read(tempBuffer))!=-1)
            {

                img[i++] = Arrays.toString(tempBuffer);

            }
            return convertToBimage((parseImageData(img)));
        }

        catch(Exception e)
        {
            return null;
        } 

    }

    public static int[][][] parseImageData(String[] stringImage)
    {
        int x = 0;
        int y = 0;

        int[][][] ImageArray = new int[512][512][4];

        int[] pixel = null;

        for(int i=0; i<stringImage.length; i++)
        {
            if(parseString(stringImage[i]) != null)
            {
                pixel = parseString(stringImage[i]);
            }

            y=0;

            for(int j=0; j<pixel.length; j++)
            {
                ImageArray [y][x][0] = 255;    

                ImageArray[y][x][1] = pixel[j];  //r
                ImageArray[y][x][2] = pixel[j];  //g
                ImageArray[y][x][3] = pixel[j];  //b
                y++;
            }

            x++;
        }
        return ImageArray;

    }

    private static int[] parseString(String line)
    {

        int[] formattedLine = new int[512];

        if(line.length()==0 || line.charAt(0) != '[' || line.charAt(line.length()-1) != ']')
        {
            return null;
        }

        String numbers = line.substring(1,line.length()-1).trim();

        String[] num = numbers.split(", ");

        for(int i=0; i<num.length; i++)
        {
            formattedLine[i] = Math.abs(Integer.parseInt(num[i]));
        }
        
        return formattedLine;
    }

    //************************************
    //  Example:  Image Re-scaling
    //************************************
    public BufferedImage ImageReScaling(BufferedImage timg, float scale){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image re scaling Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = (int)(scale * ImageArray[x][y][1]);  //r
                int g = (int)(scale * ImageArray[x][y][2]);  //g
                int b = (int)(scale * ImageArray[x][y][3]);  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  Example:  Image Shifting
    //************************************
    public BufferedImage ImageShifting(BufferedImage timg, int shiftR, int shiftG, int shiftB){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image shifting Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = shiftR + ImageArray[x][y][1];  //r
                int g = shiftG + ImageArray[x][y][2];  //g
                int b = shiftB + ImageArray[x][y][3];  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  Example:  Image Re Scaling and Shifting
    //************************************
    public BufferedImage ImageReScalingAndShifting(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image shifting Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int min = -50;
                int max = 50;
                int range = max-min+1;
                int value = (int)(Math.random()*range)+min;

                int r = value + ImageArray[x][y][1];  //r
                int g = value + ImageArray[x][y][2];  //g
                int b = value + ImageArray[x][y][3];  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************

    //************************************
    public BufferedImage ImageAddition(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] + ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] + ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] + ImageArray2[x][y][3];  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //
    //************************************
    public BufferedImage ImageSubtraction(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] - ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] - ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] - ImageArray2[x][y][3];  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageMultiplication(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  
       
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] * ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] * ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] * ImageArray2[x][y][3];  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageDivision(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r=0;
                int g=0;
                int b=0;
                if(ImageArray2[x][y][1]!=0 && ImageArray2[x][y][2]!=0 && ImageArray2[x][y][3]!=0)
                {

                    r = ImageArray[x][y][1] / ImageArray2[x][y][1];  //r
                    g = ImageArray[x][y][2] / ImageArray2[x][y][2];  //g
                    b = ImageArray[x][y][3] / ImageArray2[x][y][3];  //b
                    
                }
                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageBitwiseNOT(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ~ImageArray[x][y][1] & 0xff;  //r
                int g = ~ImageArray[x][y][2] & 0xff;  //g
                int b = ~ImageArray[x][y][3] & 0xff;  //b

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageBitwiseAND(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] & ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] & ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] & ImageArray2[x][y][3];  //b

                ImageArray[x][y][1] = r & 0xff; 
                ImageArray[x][y][2] = g & 0xff;
                ImageArray[x][y][3] = b & 0xff;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageBitwiseOR(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  
        // 
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] | ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] | ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] | ImageArray2[x][y][3];  //b

                ImageArray[x][y][1] = r & 0xff; 
                ImageArray[x][y][2] = g & 0xff;
                ImageArray[x][y][3] = b & 0xff;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  
    //************************************
    public BufferedImage ImageBitwiseXOR(BufferedImage timg, BufferedImage timg2){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(timg2);  

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] ^ ImageArray2[x][y][1];  //r
                int g = ImageArray[x][y][2] ^ ImageArray2[x][y][2];  //g
                int b = ImageArray[x][y][3] ^ ImageArray2[x][y][3];  //b

                ImageArray[x][y][1] = r & 0xff; 
                ImageArray[x][y][2] = g & 0xff;
                ImageArray[x][y][3] = b & 0xff;

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public int[][][] Mask(BufferedImage timg){

        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (x > 100 && x < 300 && y > 100 && y < 300)
                {

                    ImageArray[x][y][1] = 255; 
                    ImageArray[x][y][2] = 255;
                    ImageArray[x][y][3] = 255;
                }

            }
        }

        return ImageArray;  
    }

    public BufferedImage ROI(BufferedImage timg){

        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = null;          //  Convert the image to array

        int[][][] ImageArray2 = Mask(timg);

        int[][][] ImageArray3 = new int[width][height][4];

        double val = Math.random();

        if(val < 0.5)
        {
            ImageArray = convertToArray(LogFunc(timg));
        }
        else
        {
            ImageArray = convertToArray(SimpleThresholding(timg));
        }

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                ImageArray3[x][y][0] = ImageArray[x][y][0];
                ImageArray3[x][y][1] = ImageArray[x][y][1] & ImageArray2[x][y][1]; 
                ImageArray3[x][y][2] = ImageArray[x][y][2] & ImageArray2[x][y][2];
                ImageArray3[x][y][3] = ImageArray[x][y][3] & ImageArray2[x][y][3];

            }
        }

        return convertToBimage(ImageArray3);  // Convert the array to BufferedImage
    }
    //************************************
    //  
    //************************************
    public BufferedImage ImageNegativeLinear(BufferedImage timg, int s){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = 255 - ImageArray[x][y][1];  //r
                int g = 255 - ImageArray[x][y][2];  //g
                int b = 255 - ImageArray[x][y][3];  //b

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage LogFunc(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int c = (int)(255/Math.log(256));

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = (int)(c * Math.log(1 + ImageArray[x][y][1]));  //r
                int g = (int)(c * Math.log(1 + ImageArray[x][y][2]));  //g
                int b = (int)(c * Math.log(1 + ImageArray[x][y][3]));  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage PowerLaw(BufferedImage timg, double p){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int c = (int)(255/Math.pow(255,1-p));

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = (int)(c * Math.pow(ImageArray[x][y][1], p));  //r
                int g = (int)(c * Math.pow(ImageArray[x][y][2], p));  //g
                int b = (int)(c * Math.pow(ImageArray[x][y][3], p));  //b

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage LookUpTable(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[] LUT = new int[256];

        for(int i=0; i<LUT.length; i++)
        {
            LUT[i] = (int)(Math.random()*256);
        }

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                r = LUT[r];  //r
                g = LUT[g];  //g
                b = LUT[b];  //b

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage BitPlane(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int k = 7;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                r = (r>>k)&1; //r
                g = (g>>k)&1; //g
                b = (b>>k)&1; //b

                if(r==1)
                {
                    r=255;
                }

                if(g==1)
                {
                    g=255;
                }

                if(b==1)
                {
                    b=255;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage Histogram(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        ArrayList<Integer> rPixel = new ArrayList<Integer>();
        ArrayList<Integer> rOccurance = new ArrayList<Integer>();

        emptyPixel(rPixel,width/2);
        emptyOccur(rOccurance,width/2);

        ArrayList<Integer> gPixel = new ArrayList<Integer>();
        ArrayList<Integer> gOccurance = new ArrayList<Integer>();

        emptyPixel(gPixel,width/2);
        emptyOccur(gOccurance,width/2);

        ArrayList<Integer> bPixel = new ArrayList<Integer>();
        ArrayList<Integer> bOccurance = new ArrayList<Integer>();

        emptyPixel(bPixel,width/2);
        emptyOccur(bOccurance,width/2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){ 

                int r = ImageArray[x][y][1]; //r
                int g = ImageArray[x][y][2]; //g
                int b = ImageArray[x][y][3]; //b

                addPixel(rPixel,rOccurance,r);
                addPixel(gPixel,gOccurance,g);
                addPixel(bPixel,bOccurance,b);

            }
        }

        System.out.println("Red");
        Equalise(rPixel,rOccurance,width,height);
        System.out.println("\nGreen");
        Equalise(gPixel,gOccurance,width,height);
        System.out.println("\nBlue");
        Equalise(bPixel,bOccurance,width,height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                ImageArray[x][y][1] = (int) rPixel.get(ImageArray[x][y][1]);
                ImageArray[x][y][2] = (int) gPixel.get(ImageArray[x][y][2]);
                ImageArray[x][y][3] = (int) bPixel.get(ImageArray[x][y][3]);
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public void addPixel(ArrayList<Integer> pixel, ArrayList<Integer> occurance, int value)
    {

        int i = pixel.indexOf(value);
        int j = occurance.get(i);
        j++;
        occurance.set(i,j);         

    }

    public void Equalise(ArrayList<Integer> pixel, ArrayList<Integer> occurance, int width, int height)
    {

        String s = "";
        int total = width*height;
        String p = "";
        ArrayList<Double> hist = new ArrayList<Double>();

        for(int i=0; i<occurance.size(); i++)
        {
            s += i+"="+occurance.get(i)+", ";
            hist.add((double)occurance.get(i)/total);
        }

        System.out.print(s+"\n");

        for(int i=0; i<hist.size(); i++)
        {
            System.out.print(hist.get(i)+", ");
        }

        double current = 0;
        for(int i=0; i<hist.size(); i++)
        {
            current += occurance.get(i);      
            pixel.set(i,(int)Math.round(current*255/total)); 
        }       
    }

    public void emptyPixel(ArrayList<Integer> a, int n)
    {
        for(int i=0; i<n; i++)
        {
            a.add(i);
        }
    }

    public void emptyOccur(ArrayList<Integer> a,int n)
    {
        for(int i=0; i<n; i++)
        {
            a.add(0);
        }
    }

    public BufferedImage Convolution(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[][][] ImageArray2 = new int[width][height][4];

        int input = Integer.parseInt(JOptionPane.showInputDialog("0 None \n1 Average "+
                    "\n2 Weighted Average \n3 4-neighbour Laplacian \n4 8-neighbour Laplacian"+
                    "\n5 4-neighbour Laplacian Enhancement \n6 8-neighbour Laplacian Enhancement"+
                    "\n7 Roberts 1 \n8 Roberts 2 \n9 Sobel X \n10 Sobel Y"));
                    
        double[][] Mask = new double[3][3];
        if(input==0)
        {
            return convertToBimage(ImageArray);
        }
        else if(input==1)
        {
            for(int i=0;i<=2;i++)
            {
                for(int j=0;j<=2;j++)
                {
                    Mask[i][j] = (double)1/9;
                }
            }
        }
        else if(input==2)
        {
            for(int i=0;i<=2;i++)
            {
                for(int j=0;j<=2;j++)
                { 
                    if(i==0 && j==0 || i==0 && j==2 || i==2 && j==0 || i==2 && j==2)
                    {
                        Mask[i][j] = (double)1/16;
                    }
                    else if(i==1 && j==0 || i==1 && j==2 || i==0 && j==1 || i==2 && j==1)
                    {
                        Mask[i][j] = (double)2/16;
                    }
                    else if(i==1 && j==1)
                    {
                        Mask[i][j] = (double)4/16;
                    }
                }
            }

        }
        else if(input==3)
        {
            double[][] temp = {{0,-1,0},{-1,4,-1},{0,-1,0}};
            Mask = temp;
        }
        else if(input==4)
        {
            double[][] temp = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
            Mask = temp;
        }
        else if(input==5)
        {
            double[][] temp = {{0,-1,0},{-1,5,-1},{0,-1,0}};
            Mask = temp;
        }
        else if(input==6)
        {
            double[][] temp = {{-1,-1,-1},{-1,9,-1},{-1,-1,-1}};
            Mask = temp;
        }
        else if(input==7)
        {
            double[][] temp = {{0,0,0},{0,0,-1},{0,1,0}};
            Mask = temp;
        }
        else if(input==8)
        {
            double[][] temp = {{0,0,0},{0,-1,0},{0,0,1}};
            Mask = temp;
        }
        else if(input==9)
        {
            double[][] temp = {{-1,0,1},{-2,0,2},{-1,0,1}};
            Mask = temp;
        }
        else if(input==10)
        {
            double[][] temp = {{-1,-2,-1},{0,0,0},{1,2,1}};
            Mask = temp;
        }

        for(int y=1; y<height-1; y++){
            for(int x =1; x<width-1; x++){
                int r = 0;
                int g = 0;
                int b = 0;

                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        r += (int)(Mask[1-s][1-t]*ImageArray[x+s][y+t][1]); //r
                        g += (int)(Mask[1-s][1-t]*ImageArray[x+s][y+t][2]); //g
                        b += (int)(Mask[1-s][1-t]*ImageArray[x+s][y+t][3]); //b
                    }
                }

                if(r<0)
                {
                    r=0;
                }
                else if(r>255)
                {
                    r=255;
                }

                if(g<0)
                {
                    g=0;
                }
                else if(g>255)
                {
                    g=255;
                }

                if(b<0)
                {
                    b=0;
                }
                else if(b>255)
                {
                    b=255;
                }

                ImageArray2[x][y][0] = ImageArray[x][y][0];
                if(input>6)
                {
                    ImageArray2[x][y][1] = Math.abs(r); 
                    ImageArray2[x][y][2] = Math.abs(g);
                    ImageArray2[x][y][3] = Math.abs(b);
                }
                else
                {
                    ImageArray2[x][y][1] = r; 
                    ImageArray2[x][y][2] = g;
                    ImageArray2[x][y][3] = b;
                }

            }
        }

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

    public BufferedImage SaltPepper(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                double value = Math.random();

                if(value < 0.05)
                {
                    ImageArray[x][y][1] = 0;  //r
                    ImageArray[x][y][2] = 0;  //g
                    ImageArray[x][y][3] = 0;  //b
                }
                else if(value > 0.95)
                {
                    ImageArray[x][y][1] = 255;  //r
                    ImageArray[x][y][2] = 255;  //g
                    ImageArray[x][y][3] = 255;  //b
                }
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage MinFilter(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[][][] ImageArray2 = new int[width][height][4];

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];

        int k=0;

        for(int y=1; y<height-1; y++){

            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; //r
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    }

                }

                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);

                ImageArray2[x][y][0] = ImageArray[x][y][0];
                ImageArray2[x][y][1] = rWindow[0]; //r
                ImageArray2[x][y][2] = gWindow[0]; //g
                ImageArray2[x][y][3] = bWindow[0]; //b

            }
        }
        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

    public BufferedImage MaxFilter(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[][][] ImageArray2 = new int[width][height][4];

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];

        int k=0;

        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; //r
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    }
                }

                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);

                ImageArray2[x][y][0] = ImageArray[x][y][0];
                ImageArray2[x][y][1] = rWindow[8]; //r
                ImageArray2[x][y][2] = gWindow[8]; //g
                ImageArray2[x][y][3] = bWindow[8]; //b
            }

        }

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

    public BufferedImage MidpointFilter(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[][][] ImageArray2 = new int[width][height][4];

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];

        int k=0;

        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; //r
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    }
                }

                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);

                ImageArray2[x][y][0] = ImageArray[x][y][0];
                ImageArray2[x][y][1] = (int)(rWindow[0]+rWindow[8])/2; //r
                ImageArray2[x][y][2] = (int)(gWindow[0]+gWindow[8])/2;; //g
                ImageArray2[x][y][3] = (int)(bWindow[0]+bWindow[8])/2;; //b
            }

        }

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

    public BufferedImage MedianFilter(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int[][][] ImageArray2 = new int[width][height][4];

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];

        int k=0;

        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                k = 0;
                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; //r
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    }
                }

                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);

                ImageArray2[x][y][0] = ImageArray[x][y][0];
                ImageArray2[x][y][1] = rWindow[4]; //r
                ImageArray2[x][y][2] = gWindow[4]; //g
                ImageArray2[x][y][3] = bWindow[4]; //b
            }

        }

        return convertToBimage(ImageArray2);  // Convert the array to BufferedImage
    }

    public BufferedImage MeanAndStandardDeviation(BufferedImage timg)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        ArrayList<Integer> rPixel = new ArrayList<Integer>();
        ArrayList<Integer> rOccurance = new ArrayList<Integer>();

        emptyPixel(rPixel,width/2);
        emptyOccur(rOccurance,width/2);

        ArrayList<Integer> gPixel = new ArrayList<Integer>();
        ArrayList<Integer> gOccurance = new ArrayList<Integer>();

        emptyPixel(gPixel,width/2);
        emptyOccur(gOccurance,width/2);

        ArrayList<Integer> bPixel = new ArrayList<Integer>();
        ArrayList<Integer> bOccurance = new ArrayList<Integer>();

        emptyPixel(bPixel,width/2);
        emptyOccur(bOccurance,width/2);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){ 

                int r = ImageArray[x][y][1]; //r
                int g = ImageArray[x][y][2]; //g
                int b = ImageArray[x][y][3]; //b

                addPixel(rPixel,rOccurance,r);
                addPixel(gPixel,gOccurance,g);
                addPixel(bPixel,bOccurance,b);

                ImageArray[x][y][1] = r;  //r
                ImageArray[x][y][2] = g;  //g
                ImageArray[x][y][3] = b;  //b
            }
        }

        System.out.println("Red");
        PrintMeanAndSd(rPixel,rOccurance,width,height);
        System.out.println("Green");
        PrintMeanAndSd(gPixel,gOccurance,width,height);
        System.out.println("Blue");
        PrintMeanAndSd(bPixel,bOccurance,width,height);       

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public void PrintMeanAndSd(ArrayList<Integer> pixel, ArrayList<Integer> occurance, int width, int height)
    {
        double mean=0;
        double var=0;

        ArrayList<Double> hist = new ArrayList<Double>();

        int total = width*height;

        for(int i=0; i<pixel.size(); i++)
        {
            hist.add((double)occurance.get(i)/total);

        }

        for(int i=0; i<pixel.size(); i++)
        {
            mean += (hist.get(i)*i);
        }

        for(int i=0; i<pixel.size(); i++)
        {
            var += Math.pow(i-mean,2)*hist.get(i);
        }

        System.out.println("mean "+mean);

        System.out.println("sd "+Math.sqrt(var));

    }

    public BufferedImage SimpleThresholding(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        int input = 70;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                if(r >= input)
                {
                    r=255;
                }
                else if(r < input)
                {
                    r=0;
                }

                if(g >= input)
                {
                    g=255;
                }
                else if(g < input)
                {
                    g=0;
                }

                if(b >= input)
                {
                    b=255;
                }
                else if(b < input)
                {
                    b=0;
                }

                ImageArray[x][y][1] = r; 
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage AutomatedThresholding(BufferedImage timg){

        int avgRedBG = 0;
        int avgGreenBG = 0;
        int avgBlueBG = 0;
        int avgRedObj = 0;
        int avgGreenObj = 0;
        int avgBlueObj = 0;

        int rThresh, gThresh, bThresh, 
        rObjCount, gObjCount, bObjCount, rBGCount, gBGCount, bBGCount, 
        tempR, tempG, tempB;

        int[][][] ImageArray = convertToArray(timg);
        int height = timg.getHeight();
        int width = timg.getWidth();
        int r, g, b;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                r = ImageArray[x][y][1];
                g = ImageArray[x][y][2];
                b = ImageArray[x][y][3];

                if ((y == 0 && x == 0) || (y == width - 1 && x == 0) || (y == 0 && x == height - 1) || (y == width - 1 && x == height - 1)) 
                {                 
                    avgRedBG += r;
                    avgGreenBG += g;
                    avgBlueBG += b;        
                } 
                else 
                {
                    avgRedObj += r;
                    avgGreenObj += g;
                    avgBlueObj += b;
                }
            }
        }

        avgRedBG = avgRedBG / 4;
        avgGreenBG = avgGreenBG / 4;
        avgBlueBG = avgBlueBG / 4;
        avgRedObj = avgRedObj / ((width*height) - 4);
        avgGreenObj = avgGreenObj / ((width*height)-4);
        avgBlueObj = avgBlueObj / ((width*height)-4);
        rThresh = (avgRedBG + avgRedObj) / 2;
        gThresh = (avgGreenBG + avgGreenObj) / 2;
        bThresh = (avgBlueBG + avgBlueObj) / 2;

        while (true) {

            tempR = rThresh;
            rThresh = getThresh(ImageArray,width,height,tempR,"red");

            if (Math.abs(rThresh - tempR) < 1) 
            {
                break;
            }
        }

        while (true) {

            tempG = gThresh;
            gThresh = getThresh(ImageArray,width,height,tempR,"green");          

            if (Math.abs(gThresh - tempG) < 1) 
            {
                break;
            }
        }

        while (true) {

            tempB = bThresh;
            bThresh = getThresh(ImageArray,width,height,tempR,"blue");

            if (Math.abs(bThresh - tempB) < 1) 
            {
                break;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                r = ImageArray[x][y][1];
                g = ImageArray[x][y][2];
                b = ImageArray[x][y][3];

                if (r >= rThresh) 
                {
                    r = 255;
                } 
                else if (r < rThresh) 
                {
                    r = 0;
                }
                if (g >= gThresh) 
                {
                    g = 255;
                } 
                else if (g < gThresh) 
                {
                    g = 0;
                }
                if (b >= bThresh) 
                {
                    b = 255;
                } 
                else if (b < bThresh) 
                {
                    b = 0;
                }

                ImageArray[x][y][1] = r;
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }    
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public int getThresh(int[][][] ImageArray, int width, int height, int temp, String colour)
    {
        int avgObj = 0;
        int avgBG = 0;
        int objCount = 0;
        int BGCount = 0;
        int i=0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if(colour.equals("red"))
                {
                    i = ImageArray[x][y][1];
                }
                else if(colour.equals("green"))
                {
                    i = ImageArray[x][y][2];
                }
                else if(colour.equals("blue"))
                {
                    i = ImageArray[x][y][3];
                }
                
                if (i >= temp) 
                {
                    avgObj += i;
                    objCount++;
                } else if (i < temp) 
                {
                    avgBG += i;
                    BGCount++;
                }
            }
        }

        if (avgBG > 0) 
        {
            avgBG = avgBG / BGCount;
        }
        
        if (avgObj > 0) 
        {
            avgObj = avgObj / objCount;
        }

        return (avgBG + avgObj)/2;
    }

    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
            imgList.add(biFiltered);
            return; 
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
            imgList.add(biFiltered);
            return;
            case 2: biFiltered = ImageReScaling(bi,2); /*  */
            imgList.add(biFiltered);
            return;
            case 3: biFiltered = ImageShifting(bi,150,150,0); /*  */
            imgList.add(biFiltered);
            return;
            case 4: biFiltered = ImageReScalingAndShifting(bi); /*  */
            imgList.add(biFiltered);
            return;
            case 5: biFiltered = ImageAddition(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 6: biFiltered = ImageSubtraction(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 7: biFiltered = ImageMultiplication(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 8: biFiltered = ImageDivision(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 9: biFiltered = ImageBitwiseNOT(bi); /*  */
            imgList.add(biFiltered);
            return;
            case 10: biFiltered = ImageBitwiseAND(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 11: biFiltered = ImageBitwiseOR(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 12: biFiltered = ImageBitwiseXOR(bi,bi2); /*  */
            imgList.add(biFiltered);
            return;
            case 13: biFiltered = ROI(bi); /*  */
            imgList.add(biFiltered);
            return;
            case 14: biFiltered = ImageNegativeLinear(bi,20); /*  */
            imgList.add(biFiltered);
            return;
            case 15: biFiltered = LogFunc(bi); /*  */
            imgList.add(biFiltered);
            return;
            case 16: biFiltered = PowerLaw(bi,0.53); /*  */
            imgList.add(biFiltered);
            return;
            case 17: biFiltered = LookUpTable(bi); /*  */
            imgList.add(biFiltered);
            return;
            case 18: biFiltered = BitPlane(bi);
            imgList.add(biFiltered);
            return;
            case 19: biFiltered = Histogram(bi);
            imgList.add(biFiltered);
            return;
            case 20: biFiltered = Convolution(bi);
            imgList.add(biFiltered);
            return;
            case 21: biFiltered = SaltPepper(bi);
            imgList.add(biFiltered);
            return;
            case 22: biFiltered = MinFilter(bi);
            imgList.add(biFiltered);
            return;
            case 23: biFiltered = MaxFilter(bi);
            imgList.add(biFiltered);
            return;
            case 24: biFiltered = MidpointFilter(bi);
            imgList.add(biFiltered);
            return;
            case 25: biFiltered = MedianFilter(bi);
            imgList.add(biFiltered);
            return;
            case 26: biFiltered = MeanAndStandardDeviation(bi);
            imgList.add(biFiltered);
            return;
            case 27: biFiltered = SimpleThresholding(bi);
            imgList.add(biFiltered);
            return;
            case 28: biFiltered = AutomatedThresholding(bi);
            imgList.add(biFiltered);
            return;

        }
    }

    public void Undo()
    {

        if(imgList.size()-1>0)
        {
            imgList.remove(imgList.size()-1);
            biFiltered = imgList.get(imgList.size()-1);
        }
        else
        {
            biFiltered = bi;
        }
        repaint();

    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }

    };

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
        Demo de = new Demo();
        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);

        JButton b = new JButton("Undo");
        b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {

                    de.Undo();

                }
            });     

        panel.add(b);

        f.add("North", panel);
        f.pack();
        f.setVisible(true);

    }
}
