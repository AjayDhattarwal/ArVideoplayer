package com.ar.videoplayer;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GesturesOfVideos implements View.OnTouchListener {

    protected static ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private ScaleListener scaleListener;

    public interface ScaleListener {
        void onScale(ScaleGestureDetector detector);

    }
    public GesturesOfVideos(Context context,ScaleListener scaleListener) {
        this.scaleListener = scaleListener;
        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context,new ScaleGestureListener());
    }

    public final class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            if (scaleListener != null) {
                scaleListener.onScale(detector);
            }
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);

        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

    private void scalebegin() {
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            onDoubleTouch(e);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            onSingleTouch();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            onLongPressTouch();
            super.onLongPress(e);
        }
    }

    public void onDoubleTouch(MotionEvent motionEvent) {
        }

    public void onSingleTouch() {
    }
    public void onLongPressTouch(){
    }


}


//gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//
//@Override
//public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
//        if(playerView.isControllerVisible()){
//        playerView.hideController();
//        }
//        else{
//        playerView.showController();
//        }
//        return super.onSingleTapConfirmed(e);
//        }
//
//@Override
//public boolean onDoubleTap(MotionEvent e) {
//        float screenWidth = getResources().getDisplayMetrics().widthPixels;
//
//        if (e.getX() < screenWidth / 2) {
//        // Double-tap on the left side of the screen (backward)
//        seekBackward();
//        } else {
//        // Double-tap on the right side of the screen (forward)
//        seekForward();
//        }
//
//
//        return super.onDoubleTap(e);
//        }
//        });
//
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
//@Override
//public boolean onScale(ScaleGestureDetector detector) {
//        scaleFactor *= detector.getScaleFactor();
//        scaleFactor = Math.max(1.0f, Math.min(scaleFactor, maxScaleFactor));
//
////                // Apply scaling only to the PlayerView
////                playerView.getVideoSurfaceView().setScaleX(scaleFactor);
////                playerView.getVideoSurfaceView().setScaleY(scaleFactor);
//
//        if (scaleFactor > 1.0f && !isZoomedIn) {
//        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//        isZoomedIn = true;
//        } else if (scaleFactor <= 1.0f && isZoomedIn) {
//        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//        isZoomedIn = false;
//        }
//
//
//        return true;
//        }
//
//@Override
//public boolean onScaleBegin(ScaleGestureDetector detector) {
//        return true;
//        }
//
//@Override
//public void onScaleEnd(ScaleGestureDetector detector) {
//        // Scaling ended (if needed)
//        }
//        });
//
//        playerView.setOnTouchListener(new android.view.View.OnTouchListener() {
//@Override
//public boolean onTouch(android.view.View v, MotionEvent event) {
//        gestureDetector.onTouchEvent(event);
//        scaleGestureDetector.onTouchEvent(event);
//
//        return true;
//        }
//        });

