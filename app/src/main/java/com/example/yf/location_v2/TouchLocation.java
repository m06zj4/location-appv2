package com.example.yf.location_v2;

/**
 * Created by wang on 7/28/15.
 */
public class TouchLocation {
    private Dot[] dots;
    private float imageHeight, imageWidth;
    private float circleWidth, circleHeight;

    public TouchLocation(float imageHeight, float imageWidth) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public void setDotWithJson(String json) {
        if (json == null) {
            return;
        }

        float widthRatio = imageWidth / 1365;
        float heightRatio = imageHeight / 544;

        circleWidth = widthRatio * 60;
        circleHeight = heightRatio * 60;

        dots = new Dot[31];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new Dot();
        }

        dots[0].X = 580;
        dots[0].Y = 330;

        dots[1].X = 445;
        dots[1].Y = 160;

        dots[2].X = 345;
        dots[2].Y = 160;

        dots[3].X = 270;
        dots[3].Y = 160;

        dots[4].X = 170;
        dots[4].Y = 160;

        dots[5].X = 100;
        dots[5].Y = 60;

        dots[6].X = 100;
        dots[6].Y = 160;

        dots[7].X = 100;
        dots[7].Y = 250;

        dots[8].X = 215;
        dots[8].Y = 250;

        dots[9].X = 100;
        dots[9].Y = 320;

        dots[10].X = 100;
        dots[10].Y = 400;

        dots[11].X = 215;
        dots[11].Y = 400;

        dots[12].X = 310;
        dots[12].Y = 490;

        dots[13].X = 445;
        dots[13].Y = 490;

        dots[14].X = 580;
        dots[14].Y = 490;

        dots[15].X = 750;
        dots[15].Y = 490;

        dots[16].X = 750;
        dots[16].Y = 415;

        dots[17].X = 750;
        dots[17].Y = 315;

        dots[18].X = 750;
        dots[18].Y = 200;

        dots[19].X = 750;
        dots[19].Y = 130;

        dots[20].X = 750;
        dots[20].Y = 60;

        dots[21].X = 890;
        dots[21].Y = 200;

        dots[22].X = 1015;
        dots[22].Y = 200;

        dots[23].X = 1085;
        dots[23].Y = 405;

        dots[24].X = 1085;
        dots[24].Y = 490;

        dots[25].X = 995;
        dots[25].Y = 490;

        dots[26].X = 870;
        dots[26].Y = 490;

        dots[27].X = 1240;
        dots[27].Y = 330;

        dots[28].X = 1240;
        dots[28].Y = 255;

        dots[29].X = 1240;
        dots[29].Y = 150;

        dots[30].X = 1240;
        dots[30].Y = 60;

        for (int i = 0; i < dots.length; i++) {
            dots[i].X = dots[i].X * widthRatio;
            dots[i].Y = dots[i].Y * heightRatio;
        }
    }

    public Object getX(int nodeNumber) {
        if (dots == null) {
            return null;
        }

        if (nodeNumber < dots.length){
            return dots[nodeNumber].X;
        }
        return null;
    }

    public Object getY(int nodeNumber) {
        if (dots == null) {
            return null;
        }

        if (nodeNumber < dots.length){
            return dots[nodeNumber].Y;
        }
        return null;
    }

    public Object analyseTouchLocation(float X, float Y) {
        if (dots == null) {
            return null;
        }

        for (int i = 0; i < dots.length; i++) {
            if ((Math.abs(X - dots[i].X) < circleWidth) && (Math.abs(Y - dots[i].Y) < circleHeight))
                return i;
        }
        return null;
    }

    private class Dot {
        public float X = 0;
        public float Y = 0;
    }
}
