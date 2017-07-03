package com.huyn.demogroup.bahavior.widget;

/**
 * Created by huyaonan on 2017/6/19.
 */

public class MathUtils {

    static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

}
