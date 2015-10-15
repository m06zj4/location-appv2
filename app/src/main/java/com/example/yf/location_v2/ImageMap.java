package com.example.yf.location_v2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by yf on 2015/9/23.
 */
public class ImageMap {
    public final int User = 1;
    public final int Lock = 2;

    private TouchLocation touchLocation;

    private Bitmap pMap, pUser, pLock, pDirection, drawBitmap;
    private int mUser, mLock;
    private ImageView imageView;

    public ImageMap(ImageView imageView, Bitmap user, Bitmap lock, Bitmap direction) {
        this.imageView = imageView;
        this.pUser = user;
        this.pLock = lock;
        this.pDirection = direction;
        mUser = 0;
        mLock = 0;
    }

    public void setMapImage(Bitmap map) {     //get map
        int imageHeight, imageWidth;

        this.pMap = map;
        imageHeight = pMap.getHeight();
        imageWidth = pMap.getWidth();

        imageView.setMaxHeight(imageHeight);
        imageView.setMaxWidth(imageWidth);
        imageView.setImageBitmap(pMap);
        Log.w("mydebug_image", String.valueOf(imageHeight));

        mUser = 0;
        mLock = 0;

        touchLocation = new TouchLocation(imageHeight, imageWidth);
    }

    public void reloadImage() {

        Log.w("ImageMap111", "hello");

        // 判斷是否為主執行緒進入，否的話就跳出
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("ImageMap", "Reload Image Must Running Main Thread!");
            return;
        }

        if (drawBitmap != null) {
            imageView.setImageBitmap(drawBitmap);
            Log.w("ImageMap222", "drawBitmap != null");
            drawBitmap = null;
        } else {
            Log.w("ImageMap333", "drawBitmap == null");
        }
    }

    public boolean drawUserLocation(int node) {
        mUser = node;

        Log.w("mydebug_user_node", String.valueOf(node));

        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);
        TouchLocation.Dot dot = touchLocation.getDot(node);

        int x = 580;
        int y = 330;

//        if (dot != null) {
//        float X = dot.X - (pUser.getWidth() / 2);
//        float Y = dot.Y - (pUser.getHeight() / 2);

        float X = 3 * x - (pUser.getWidth() / 2);
        float Y = 3 * y - (pUser.getHeight() / 2);


        canvas.drawBitmap(pUser, X, Y, null);
//            Log.w("mydebug_12555", "good");
//        } else {
//            Log.w("mydebug_12555", "fail");
//        }

//        if (mLock != null) {
//            dot = touchLocation.getDot(mLock);
//
//            if (dot != null) {
//                float X = dot.X - (pLock.getWidth() / 2);
//                float Y = dot.Y - (pLock.getHeight() / 2);
//
//                canvas.drawBitmap(pLock, X, Y, null);
//                Log.w("mydebug_12666", "good");
//            } else {
//                Log.w("mydebug_12666", "fail");
//            }
//        }

        if (bitmap != null) {
            drawBitmap = bitmap;
            return true;
        } else {
            return false;
        }
    }

//    public boolean drawLockLocation(int node) {
//        mLock = node;
//
//        Log.w("mydebug_lock_node", String.valueOf(node));
//
//        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
//        Canvas canvas = new Canvas(bitmap);
//
//        TouchLocation.Dot dot = touchLocation.getDot(node);
//
//        if (dot != null) {
//            float X = dot.X - (pLock.getWidth() / 2);
//            float Y = dot.Y - (pLock.getHeight() / 2);
//
//            canvas.drawBitmap(pLock, X, Y, null);
//
//        } else {
//
//        }
//
//        if (mUser != null) {
//            dot = touchLocation.getDot(mUser);
//
//            if (dot != null) {
//                float X = dot.X - (pUser.getWidth() / 2);
//                float Y = dot.Y - (pUser.getHeight() / 2);
//
//                canvas.drawBitmap(pUser, X, Y, null);
//
//            } else {
//
//            }
//        }
//
//        if (bitmap != null) {
//            drawBitmap = bitmap;
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean drawPath(int[] path) {
        Log.w("5487598", "123456");
        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        for (int i = path.length - 1; i >= 0; i--) {
            TouchLocation.Dot dot = touchLocation.getDot(path[i]);

            if (dot == null) {
                break;
            }

            if (i == 0) {
                float X = dot.X - (pLock.getWidth() / 2);
                float Y = dot.Y - (pLock.getHeight() / 2);

                canvas.drawBitmap(pLock, X, Y, null);
            } else if (i >= (path.length - 1)) {
                float X = dot.X - (pUser.getWidth() / 2);
                float Y = dot.Y - (pUser.getHeight() / 2);

                canvas.drawBitmap(pUser, X, Y, null);
            } else {

                TouchLocation.Dot nDot = touchLocation.getDot(path[i - 1]);
                float mX = dot.X - nDot.X;
                float mY = dot.Y - nDot.Y;
                float mAngle = 0;

                if (Math.abs(mX) < 30) {
                    if (mY > 0)
                        mAngle = 90;
                    else
                        mAngle = 270;
                } else if (Math.abs(mY) < 30) {
                    if (mX > 0)
                        mAngle = 0;
                    else
                        mAngle = 180;
                } else if (mX > 0) {
                    if (mY > 0)
                        mAngle = 45;
                    else
                        mAngle = 315;
                } else {
                    if (mY > 0)
                        mAngle = 135;
                    else
                        mAngle = 225;
                }

                Matrix vMatrix = new Matrix();
                vMatrix.setRotate(mAngle);

                Bitmap mBitmap = Bitmap.createBitmap(pDirection, 0, 0, pDirection.getWidth(), pDirection.getHeight(), vMatrix, false);

                float X = dot.X - (mBitmap.getWidth() / 2);
                float Y = dot.Y - (mBitmap.getHeight() / 2);

                canvas.drawBitmap(mBitmap, X, Y, null);
            }
        }

        if (bitmap != null) {
            drawBitmap = bitmap;
            return true;
        } else {
            return false;
        }
    }
}
