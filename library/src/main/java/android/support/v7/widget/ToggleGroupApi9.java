package android.support.v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

@RequiresApi(9)
@TargetApi(9)
public class ToggleGroupApi9 implements ToggleGroupImpl
{
	final RectF sCornerRect = new RectF();

	@Override
	public void initStatic() {
		// Draws a round rect using 7 draw operations. This is faster than using
		// canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
		// shapes.
		RoundRectDrawableWithShadow.sRoundRectHelper =
				new RoundRectDrawableWithShadow.RoundRectHelper() {
					@Override
					public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
					                          Paint paint) {
						final float twoRadius = cornerRadius * 2;
						final float innerWidth = bounds.width() - twoRadius - 1;
						final float innerHeight = bounds.height() - twoRadius - 1;
						if (cornerRadius >= 1f) {
							// increment corner radius to account for half pixels.
							float roundedCornerRadius = cornerRadius + .5f;
							sCornerRect.set(-roundedCornerRadius, -roundedCornerRadius, roundedCornerRadius,
									roundedCornerRadius);
							int saved = canvas.save();
							canvas.translate(bounds.left + roundedCornerRadius,
									bounds.top + roundedCornerRadius);
							canvas.drawArc(sCornerRect, 180, 90, true, paint);
							canvas.translate(innerWidth, 0);
							canvas.rotate(90);
							canvas.drawArc(sCornerRect, 180, 90, true, paint);
							canvas.translate(innerHeight, 0);
							canvas.rotate(90);
							canvas.drawArc(sCornerRect, 180, 90, true, paint);
							canvas.translate(innerWidth, 0);
							canvas.rotate(90);
							canvas.drawArc(sCornerRect, 180, 90, true, paint);
							canvas.restoreToCount(saved);
							//draw top and bottom pieces
							canvas.drawRect(bounds.left + roundedCornerRadius - 1f, bounds.top,
									bounds.right - roundedCornerRadius + 1f,
									bounds.top + roundedCornerRadius, paint);

							canvas.drawRect(bounds.left + roundedCornerRadius - 1f,
									bounds.bottom - roundedCornerRadius,
									bounds.right - roundedCornerRadius + 1f, bounds.bottom, paint);
						}
						// center
						canvas.drawRect(bounds.left, bounds.top + cornerRadius,
								bounds.right, bounds.bottom - cornerRadius , paint);
					}
				};
	}

	@Override
	public void initialize(ToggleGroupDelegate groupView, Context context,
	                       ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
		RoundRectDrawableWithShadow background = createBackground(context, backgroundColor, radius,
				elevation, maxElevation);
		background.setAddPaddingForCorners(groupView.getPreventCornerOverlap());
		groupView.setGroupBackground(background);
		updatePadding(groupView);
	}

	private RoundRectDrawableWithShadow createBackground(Context context,
	                                                     ColorStateList backgroundColor, float radius, float elevation,
	                                                     float maxElevation) {
		return new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius,
				elevation, maxElevation);
	}

	@Override
	public void updatePadding(ToggleGroupDelegate groupView) {
		Rect shadowPadding = new Rect();
		getShadowBackground(groupView).getMaxShadowAndCornerPadding(shadowPadding);
		groupView.setMinWidthHeightInternal((int) Math.ceil(getMinWidth(groupView)),
				(int) Math.ceil(getMinHeight(groupView)));
		groupView.setShadowPadding(shadowPadding.left, shadowPadding.top,
				shadowPadding.right, shadowPadding.bottom);
	}

	@Override
	public void onCompatPaddingChanged(ToggleGroupDelegate groupView) {
		// NO OP
	}

	@Override
	public void onPreventCornerOverlapChanged(ToggleGroupDelegate groupView) {
		getShadowBackground(groupView).setAddPaddingForCorners(groupView.getPreventCornerOverlap());
		updatePadding(groupView);
	}

	@Override
	public void setBackgroundColor(ToggleGroupDelegate groupView, @Nullable ColorStateList color) {
		getShadowBackground(groupView).setColor(color);
	}

	@Override
	public ColorStateList getBackgroundColor(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getColor();
	}

	@Override
	public void setRadius(ToggleGroupDelegate groupView, float radius) {
		getShadowBackground(groupView).setCornerRadius(radius);
		updatePadding(groupView);
	}

	@Override
	public float getRadius(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getCornerRadius();
	}

	@Override
	public void setElevation(ToggleGroupDelegate groupView, float elevation) {
		getShadowBackground(groupView).setShadowSize(elevation);
	}

	@Override
	public float getElevation(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getShadowSize();
	}

	@Override
	public void setMaxElevation(ToggleGroupDelegate groupView, float maxElevation) {
		getShadowBackground(groupView).setMaxShadowSize(maxElevation);
		updatePadding(groupView);
	}

	@Override
	public float getMaxElevation(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getMaxShadowSize();
	}

	@Override
	public float getMinWidth(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getMinWidth();
	}

	@Override
	public float getMinHeight(ToggleGroupDelegate groupView) {
		return getShadowBackground(groupView).getMinHeight();
	}

	private RoundRectDrawableWithShadow getShadowBackground(ToggleGroupDelegate groupView) {
		return ((RoundRectDrawableWithShadow) groupView.getGroupBackground());
	}
}
