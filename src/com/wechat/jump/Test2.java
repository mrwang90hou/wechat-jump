package com.wechat.jump;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test2 {
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\User\\Desktop\\temp\\origin.png"));
        /*int w = image.getWidth(null);
        int h = image.getHeight(null);
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                Color color = new Color(image.getRGB(i,j));
                //提取亮度值

            }
        }*/
        Color color1 = new Color(image.getRGB(59,1185));
        System.out.println(getLight(color1.getRed(),color1.getGreen(),color1.getBlue()));
    }

    private static float getLight(int r,int g,int b){
        int minval = Math.min(r,Math.max(g,b));
        int maxval = Math.max(r,Math.max(g,b));
        return (float) ((maxval + minval) / 510.0);
    }
}
