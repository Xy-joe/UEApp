package ng.schooln.ueapp.utils;

import android.view.animation.Interpolator;

/**
 * Created by xyjoe on 9/24/18.
 */

public class BounceEffect implements Interpolator {

    private double mAmp = 1;
    private double mfreq = 10;

    public BounceEffect(double mAmp, double mfreq) {
        this.mAmp = mAmp;
        this.mfreq = mfreq;
    }

    @Override
    public float getInterpolation(float v) {
        return (float) (-1 * Math.pow(Math.E, -v/mAmp) * Math.cos(mfreq * v) + 1);
    }
}
