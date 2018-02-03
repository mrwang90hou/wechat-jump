package com.wechat.jump;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class JumperMain {

    private static final String ADB_PATH = "D:/Android/adb/adb.exe";


    private static final String workPath = "C:\\Users\\User\\Desktop\\temp";
    /**
     * 弹跳系数
     */
    private static final double JUMP_RATIO = 1.42f;

    private static Random RANDOM = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {


        double jumpRatio = 0;

        for(int i = 0;i<5000;i++){
            //adb截图
            Process process = Runtime.getRuntime().exec(ADB_PATH + " shell /system/bin/screencap -p /sdcard/screenshot.png");
            process.waitFor();
            //保存到本地
            File file = new File(workPath,i + ".png");
            process = Runtime.getRuntime().exec(ADB_PATH + " pull /sdcard/screenshot.png " + file.getAbsolutePath());
            process.waitFor();
            System.out.println("screenshot, file: " + file);
            long a = System.currentTimeMillis();
            //搜索
            ImageSearcher searcher = new ImageSearcher(ImageIO.read(file));
            Point startPoint = searcher.searchStart();
            Point topPoint = searcher.searchTop(startPoint);
            Point rightPoint = searcher.searchRight(topPoint);
            startPoint = searcher.toRealPoint(startPoint);
            topPoint = searcher.toRealPoint(topPoint);
            rightPoint = searcher.toRealPoint(rightPoint);
            searcher.drawHistory(startPoint,topPoint,rightPoint);
            ImageIO.write(searcher.getImage(),"png",file);
            System.out.println(String.format("search result:start[%s],top[%s],right[%s].",startPoint,topPoint,rightPoint));
            if (jumpRatio == 0) {
                jumpRatio = JUMP_RATIO * 1080 / searcher.getWidth();
            }
            Point centerPoint = new Point(topPoint.getX(),rightPoint.getY());
            int distance = (int) Math.round(Math.sqrt((centerPoint.getX() - startPoint.getX()) * (centerPoint.getX() - startPoint.getX())
                                + (centerPoint.getY() - startPoint.getY()) * (centerPoint.getY() - startPoint.getY()))  * jumpRatio);
            int pressX = 400 + RANDOM.nextInt(100);
            int pressY = 500 + RANDOM.nextInt(100);
            long b = System.currentTimeMillis();
            System.out.println("search cost time :" + (b-a) + "ms.distance:" + distance);
            String adbCommand = ADB_PATH + String.format(" shell input swipe %d %d %d %d %d", pressX, pressY, pressX, pressY, distance);
            Runtime.getRuntime().exec(adbCommand);
            process.waitFor();
            System.out.println("wait few seconds.");
            int r = 3000 + RANDOM.nextInt(1000);
            Thread.sleep(r);
        }
    }
}
