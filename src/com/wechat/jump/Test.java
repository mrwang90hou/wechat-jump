package com.wechat.jump;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {


    private static final String ADB_PATH = "D:/Android/adb/adb.exe";
    private static final File TEMP = new File("C:\\Users\\User\\Desktop\\temp");


    public static void main(String[] args) throws Exception {
        //截图，并且拉取到本地，图片直接加入内存
        File temp = File.createTempFile("wechat_jump_", ".png", TEMP);
        File gray = File.createTempFile("wechat_jump_gray_", ".png", TEMP);
        execAdb("shell screencap /sdcard/screen.png");
        execAdb("pull /sdcard/screen.png " + temp);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try{
            fileInputStream = new FileInputStream(temp);
            BufferedImage image = ImageIO.read(fileInputStream);
            BufferedImage grayImage = getGrayImage(image);  //灰度化
            fileOutputStream = new FileOutputStream(gray);
            ImageIO.write(grayImage,"png",fileOutputStream);
            System.out.println("test success");
        }finally {
            if(fileInputStream != null){
                try{
                    fileInputStream.close();
                }catch (IOException ex){}
            }
            if(fileOutputStream != null){
                try{
                    fileOutputStream.close();
                }catch (IOException ex){}
            }
        }

    }

    public static void execAdb(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(ADB_PATH + " " + command);
        process.waitFor();
    }



    private static BufferedImage getGrayImage(Image img) {
        BufferedImage bfi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bfi.getGraphics().drawImage(img, 0, 0, null);
        for (int y = 0; y < bfi.getHeight(); y++) {
            for (int x = 0; x < bfi.getWidth(); x++) {
                Color pixel = new Color(bfi.getRGB(x, y));
                if(getLight(pixel.getRed(),pixel.getGreen(),pixel.getBlue()) < 0.6){
                    //r = g = b
                    int rgb = (int) (pixel.getRed() * 0.3 + pixel.getGreen() * 0.59 + pixel.getBlue() * 0.11);
                    bfi.setRGB(x, y,new Color(rgb,rgb,rgb).getRGB());
                }
            }
        }
        return bfi;
    }


    private static float getLight(int r,int g,int b){
        int minval = Math.min(r,Math.max(g,b));
        int maxval = Math.max(r,Math.max(g,b));
        return (float) ((maxval + minval) / 510.0);
    }

}
