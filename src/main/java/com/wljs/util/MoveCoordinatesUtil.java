package com.wljs.util;

import com.wljs.pojo.MoveCoordinates;
import org.openqa.selenium.Dimension;

public class MoveCoordinatesUtil {

    /**
     * 计算滑动坐标点
     *
     * @param dimension
     * @param type      1向左滑动 2向上滑动
     * @return
     */
    public MoveCoordinates getMoveCoordinates(Dimension dimension, int type) {

        int width = dimension.width;
        int height = dimension.height;

        MoveCoordinates coordinates = new MoveCoordinates();

        if (1 == type) {//向左滑动
            coordinates.setOrginWith((new Double(width * 0.9)).intValue());
            coordinates.setOrginHeight(height / 2);
            coordinates.setMoveWidth((new Double(width * 0.15)).intValue());
            coordinates.setMoveHeight(height / 2);
        }
        if (2 == type) {//向上滑动
            coordinates.setOrginWith(width / 2);
            coordinates.setOrginHeight((new Double(height * 0.9)).intValue());
            coordinates.setMoveWidth(width / 2);
            coordinates.setMoveHeight(new Double(height * 0.1).intValue());
        }


        return coordinates;

    }
}
