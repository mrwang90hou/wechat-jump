package com.wechat.jump;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test3 {
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\User\\Desktop\\temp\\origin.png"));
        BufferedImage bfi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        bfi.getGraphics().drawImage(image, 0, 0, null);
        for (int y = 0; y < bfi.getHeight(); y++) {
            for (int x = 0; x < bfi.getWidth(); x++) {
                Color pixel = new Color(bfi.getRGB(x, y));
                if(getLight(pixel.getRed(),pixel.getGreen(),pixel.getBlue()) < 0.6){
                    //r = g = b
                    int rgb = (int) (pixel.getRed() * 0.3 + pixel.getGreen() * 0.59 + pixel.getBlue() * 0.11);
                    bfi.setRGB(x, y,new Color(rgb,rgb,rgb).getRGB());
                }else{
                    bfi.setRGB(x,y,new Color(255,255,255).getRGB());
                }
            }
        }
        ImageIO.write(bfi,"png",new File("C:\\Users\\User\\Desktop\\temp\\temp.png"));
    }

    private static float getLight(int r,int g,int b){
        int minval = Math.min(r,Math.max(g,b));
        int maxval = Math.max(r,Math.max(g,b));
        return (float) ((maxval + minval) / 510.0);
    }
}
