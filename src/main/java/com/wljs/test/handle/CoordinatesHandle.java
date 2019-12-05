package com.wljs.test.handle;

import com.wljs.pojo.Coordinates;
import com.wljs.pojo.MoveCoordinates;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Dimension;


public class CoordinatesHandle {


    /**
     * 计算滑动坐标点
     *
     * @param dimension
     * @param type      1向左滑动 2 向上滑动
     * @return
     */
    public MoveCoordinates getMoveCoordinates(Dimension dimension, int type) {

        int width = dimension.width;
        int height = dimension.height;

        MoveCoordinates moveCoordinates = new MoveCoordinates();

        if (1 == type) {
            moveCoordinates.setOrginWith((new Double(width * 0.9)).intValue());
            moveCoordinates.setOrginHeight(height / 2);
            moveCoordinates.setMoveWidth((new Double(width * 0.15)).intValue());
            moveCoordinates.setMoveHeight(height / 2);
        } else if (2 == type) {
            moveCoordinates.setOrginWith(width / 2);
            moveCoordinates.setOrginHeight((new Double(height * 0.9)).intValue());
            moveCoordinates.setMoveWidth(width / 2);
            moveCoordinates.setMoveHeight(new Double(height * 0.1).intValue());
        }

        return moveCoordinates;

    }

    /**
     * 获取元素坐标
     *
     * @param xmlStr
     * @param text
     * @param type
     * @return
     */
    public Coordinates getXy(String xmlStr, String text, int type) {
        String keyword = null;
        if (0 == type) {
            keyword = "class=\"android.widget.Button\" text=\"" + text + "\"";
        } else if (1 == type) {
            keyword = "class=\"android.widget.TextView\" text=\"" + text + "\"";
        }
        xmlStr = xmlStr.split(keyword)[1];
        xmlStr = xmlStr.split("bounds=\"")[1];
        xmlStr = xmlStr.substring(0, xmlStr.lastIndexOf("]"));
        xmlStr = xmlStr.replace("][", ",").replace("[", "");

        String[] bounsArray = xmlStr.split(",");

        Coordinates coordinates = new Coordinates();
        coordinates.setMinX(Integer.valueOf(bounsArray[0]));
        coordinates.setMinY(Integer.valueOf(bounsArray[1]));
        coordinates.setMaxX(Integer.valueOf(bounsArray[2]));
        coordinates.setMaxY(Integer.valueOf(bounsArray[3]));

        coordinates.setTotalX(coordinates.getMinX() + coordinates.getMaxX());
        coordinates.setTotalY(coordinates.getMinY() + coordinates.getMaxY());
        return coordinates;
    }
}
