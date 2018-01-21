package com.wechat.jump;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 顶点搜索算法
 * 参考资料：https://zhuanlan.zhihu.com/p/32643870
 */
public class ImageSearcher {

    /**
     * 缓存所有像素点的颜色值
     * 需要注意的是，这里的int，不是直接能计算的颜色值，必须通过new Color(int)构造
     */
    private int[][] rgbs;
    /**
     * 原始图像
     */
    private BufferedImage image;
    /**
     * 宽度
     */
    private int width;
    /**
     * 相对高度，原始图像高度的1/3
     */
    private int height;



    /**
     * 起点“小人”的颜色，引入多个点，可以提高准确性
     */
    private static final int[][] STARTS ;

    static {
        //初始化起点小人的颜色
        URL resource = ImageSearcher.class.getClassLoader().getResource("start.jpg");
        if(resource == null){
            throw new RuntimeException("start data not exists.");
        }
        try {

            BufferedImage startImage = ImageIO.read(resource);
            int w = startImage.getWidth(null);
            int h = startImage.getHeight(null);
            STARTS = new int[w][h];
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++){
                    STARTS[i][j] = startImage.getRGB(i,j);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("start data not found.",e);
        }
    }


    public ImageSearcher(BufferedImage image) {
        //整个图像，其实只有中间部分，也就是1/3被处理就行了
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        this.image = image;
        this.width = w;
        this.height = h / 3;
        this.rgbs = new int[this.width][this.height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rgbs[i][j] = image.getRGB(i, height + j);
            }
        }
    }

    //判断两点的颜色rgb值是否接近
    public boolean isNear(int x1, int y1, int x2, int y2, int delta) {
        Color c1 = new Color(rgbs[x1][y1]);
        Color c2 = new Color(rgbs[x2][y2]);
        return isNear(c1,c2,delta);
    }

    public boolean isNear(int x1, int y1, int x2, int y2) {
        return this.isNear(x1, y1, x2, y2, 30);
    }

    //判断是否与起点小人的颜色接近
    public boolean isNearStarter(int x, int y,int delta) {
        Color c = new Color(rgbs[x][y]);
        for(int[] cl :STARTS){
            for(int color : cl){
                if(isNear(c,new Color(color),delta)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNearStarter(int x,int y){
        return isNearStarter(x,y,20);
    }

    public boolean isNear(Color c1,Color c2,int delta){
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        int value =  Math.abs(r) + Math.abs(g) + Math.abs(b);
        return value < delta;
    }

    //判断一个点是否与背景颜色接近，背景颜色是渐变的，需要多扫描几个点。
    private boolean isNearBg(int i, int j,int delta){
        for(int t=0;t<100;t++){
            if(isNear(i,j,0,t,delta)){
                return true;
            }
        }
        return false;
    }

    private boolean isNearBg(int i,int j){
        return isNearBg(i,j,30);
    }

    //是否是大色差点（与背景颜色差异较大，同时与小人差异较大）
    public boolean isFarBack(int i, int j,int delta) {
        return !isNearBg(i,j,delta) && !isNearStarter(i, j,delta);
    }

    public boolean isFarBack(int i, int j){
        return !isNearBg(i,j) && !isNearStarter(i, j);
    }

    //搜索起点坐标，也就是小人“脚”的坐标
    public Point searchStart() {
        //从左往右，从下往上搜索
        for (int j = height - 1; j > 0; j--) {
            for (int i = 0; i < width; i++) {
                if (isNearStarter(i, j)) { //如果接近，还要往右搜索，然后取中点坐标
                    int x = i;
                    do {
                        i++;
                    } while (i < width && isNearStarter(i, j));//只要那个点跟起点颜色接近，就继续往右搜索。
                    return new Point((x + i) / 2, j);
                }
            }
        }
        throw new RuntimeException("starter point not found");
    }

    //找顶点坐标 从上往下搜索第一个跟背景色差别较大的点即可。（忽略小人）
    public Point searchTop(Point startPoint) {
        //从上往下，从右往左搜索
        //忽略的区域：
        //哥的分辨率是1080*1920，横向是-74~74像素，纵向是-237~15,1920/ 3 = 640
        int x1 = (int) (startPoint.getX() - (float)width / 1080 * 74);
        int x2 = (int) (startPoint.getX() + (float)width / 1080 * 74);
        int y1 = (int) (startPoint.getY() - (float)height / 640 * 237);
        int y2 = (int) (startPoint.getY() + (float)height / 640 * 15);
        for (int j = 0; j < height; j++) {
            for (int i = width - 1; i > 0; i--) {
                if(!isNearBg(i,j,30) && (i > x2 || i < x1 || j > y2 || j < y1)){
                    int x = i;//修正
                    do{
                        x--;
                    }while (!isNearBg(x,j,30) && (x > x2 || x < x1 || j > y2 || j < y1));
                    return new Point(Math.round((x + i) / 2),j);
                }
            }
        }
        throw new RuntimeException("top point not found");
    }


    //搜索右方点坐标，topPoint为顶点坐标
    public Point searchRight(Point topPoint) {
        int x = topPoint.getX() + 1,y = topPoint.getY();//候选行的x、y坐标
        while (isFarBack(x , y)){
            x++;
        }
        int count = 0;
        int i = x , j = y + 1;
        while (count < 5){
            //向右搜索大色差点，横坐标等于x.纵坐标 = y + 1
            while (!isNearBg(i,j,20)){
                i++;
            }
            if(i > x){
                count = 0;
                x = i;
                y = j;
            }else{
                count++;
            }
            j++;
        }
        return new Point(x,y);
    }



    public int[][] getRgbs() {
        return rgbs;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }




    public Point toRealPoint(Point point) {
        return new Point(point.getX(), height + point.getY());
    }

    public Point toRealPoint(int x, int y) {
        return new Point(x, y + height);
    }


   /* public static void main(String[] args) throws Exception {
        long a = System.currentTimeMillis();
        String basePath = "C:\\Users\\User\\Desktop\\temp";
        BufferedImage image = ImageIO.read(new File(basePath,"118.png"));
        ImageSearcher searcher = new ImageSearcher(image);
        Point point = searcher.searchStart();
        System.out.println("start:"+searcher.toRealPoint(point));
        point = searcher.searchTop(point);
        System.out.println("top:"+searcher.toRealPoint(point));
        point = searcher.searchRight(point);
        System.out.println("right:" + searcher.toRealPoint(point));
    }*/
}
