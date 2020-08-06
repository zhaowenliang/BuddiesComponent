package cc.buddies.component.common.drawables;

import android.graphics.drawable.GradientDrawable;

/**
 * 创建shape drawable
 */
public class GradientDrawableManager {

    private Builder mBuilder;

    private GradientDrawableManager(Builder builder) {
        this.mBuilder = builder;
    }

    public static Builder with() {
        return new Builder();
    }

    public GradientDrawable create() {
        int strokeWidth = mBuilder.strokeWidth;
        int roundRadius = mBuilder.roundRadius;
        int strokeColor = mBuilder.strokeColor;
        int fillColor = mBuilder.fillColor;

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(fillColor);
        gradientDrawable.setCornerRadius(roundRadius);
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return gradientDrawable;
    }

    public static class Builder {
        int strokeWidth;
        int roundRadius;
        int strokeColor;
        int fillColor;

        public Builder setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Builder setRoundRadius(int roundRadius) {
            this.roundRadius = roundRadius;
            return this;
        }

        public Builder setStrokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Builder setFillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public GradientDrawableManager build() {
            return new GradientDrawableManager(this);
        }

    }

}
