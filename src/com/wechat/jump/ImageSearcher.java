package com.wechat.jump;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * 顶点搜索算法
 * 参考资料：https://zhuanlan.zhihu.com/p/32643870
 */
public class ImageSearcher {

    private int[][] rgbs;
    private BufferedImage image;
    private int width;
    private int height;
    private Point topPoint;

    //起点“小人”的颜色
    private static final Color START_COLOR = new Color(56, 59, 92);


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
                rgbs[i][j] = image.getRGB(i, h / 3 + j);
            }
        }
    }

    //判断两点的颜色rgb值是否接近
    public boolean isNear(int x1, int y1, int x2, int y2, int delta) {
        Color c1 = new Color(rgbs[x1][y1]);
        Color c2 = new Color(rgbs[x2][y2]);
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        int value =  Math.abs(r) + Math.abs(g) + Math.abs(b);
        return value < delta;
    }

    public boolean isNear(int x1, int y1, int x2, int y2) {
        return this.isNear(x1, y1, x2, y2, 30);
    }

    //判断是否与起点小人的颜色接近
    public boolean isNearStarter(int x, int y) {
        Color c = new Color(rgbs[x][y]);
        int r = c.getRed() - START_COLOR.getRed();
        int g = c.getGreen() - START_COLOR.getGreen();
        int b = c.getBlue() - START_COLOR.getBlue();
        return Math.abs(r) + Math.abs(g) + Math.abs(b) < 30;
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

    //找顶点坐标 从上往下搜索第一个跟背景色差别较大的点即可。
    public Point searchTop() {
        //从上往下，从右往左搜索，并且跳过跟小人颜色接近的点
        for (int j = 0; j < height; j++) {
            for (int i = width - 1; i > 0; i--) {
                if (isFarBack(i, j)) {    //跟背景色差别较大，同时跟小人颜色差别较大
                    System.out.println(i + "," + (j+height));
                    System.out.println(new Color(rgbs[i][j]));
                    System.out.println(isNear(i,j,0,0));
                    System.out.println(isNearStarter(i,j));
                    int x = i;
                    do {
                        i--;
                    } while (i > 0 && isFarBack(i, j));
                    topPoint = new Point((x + i) / 2, j);
                    return topPoint;
                }
            }
        }
        throw new RuntimeException("top point not found");
    }

    //搜索右方点坐标
    public Point searchRight() {
        return null;
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


    public boolean isFarBack(int i, int j) {
        return !isNear(i, j, 0, 0) && !isNearStarter(i, j);
    }

    public Point toRealPoint(Point point) {
        return new Point(point.getX(), height + point.getY());
    }

    public Point toRealPoint(int x, int y) {
        return new Point(x, y + height);
    }


    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("F:\\idea\\wechat-jump\\temp\\circle.jpg"));
        ImageSearcher searcher = new ImageSearcher(image);
        Point point = searcher.searchStart();
        System.out.println(searcher.toRealPoint(point));
        point = searcher.searchTop();
        System.out.println(searcher.toRealPoint(point));
//        System.out.println(searcher.isFarBack(324,560-searcher.height));
//        System.out.println(searcher.isNearStarter(324,560 - searcher.height));
//        System.out.println(searcher.isNear(324,560 - searcher.height,0,0));
//        System.out.println(new Color(searcher.rgbs[324][560-searcher.height]));
//        System.out.println(new Color(searcher.rgbs[0][0]));
    }
}
