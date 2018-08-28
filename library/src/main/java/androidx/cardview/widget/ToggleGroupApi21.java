package androidx.cardview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ToggleGroupDelegate;
import androidx.appcompat.widget.ToggleGroupImpl;

@RequiresApi(21)
@TargetApi(21)
public class ToggleGroupApi21 implements ToggleGroupImpl
{
	@Override
	public void initialize(ToggleGroupDelegate groupView, Context context,
						   ColorStateList backgroundColor, float radius, float elevation, float maxElevation) {
		final RoundRectDrawable background = new RoundRectDrawable(backgroundColor, radius);
		groupView.setGroupBackground(background);

		View view = groupView.getToggleGroup();
		view.setClipToOutline(true);
		view.setElevation(elevation);
		setMaxElevation(groupView, maxElevation);
	}

	@Override
	public void setRadius(ToggleGroupDelegate groupView, float radius) {
		getCardBackground(groupView).setRadius(radius);
	}

	@Override
	public void initStatic() {
	}

	@Override
	public void setMaxElevation(ToggleGroupDelegate groupView, float maxElevation) {
		getCardBackground(groupView).setPadding(maxElevation,
				groupView.getUseCompatPadding(), groupView.getPreventCornerOverlap());
		updatePadding(groupView);
	}

	@Override
	public float getMaxElevation(ToggleGroupDelegate groupView) {
		return getCardBackground(groupView).getPadding();
	}

	@Override
	public float getMinWidth(ToggleGroupDelegate groupView) {
		return getRadius(groupView) * 2;
	}

	@Override
	public float getMinHeight(ToggleGroupDelegate groupView) {
		return getRadius(groupView) * 2;
	}

	@Override
	public float getRadius(ToggleGroupDelegate groupView) {
		return getCardBackground(groupView).getRadius();
	}

	@Override
	public void setElevation(ToggleGroupDelegate groupView, float elevation) {
		groupView.getToggleGroup().setElevation(elevation);
	}

	@Override
	public float getElevation(ToggleGroupDelegate groupView) {
		return groupView.getToggleGroup().getElevation();
	}

	@Override
	public void updatePadding(ToggleGroupDelegate groupView) {
		if (!groupView.getUseCompatPadding()) {
			groupView.setShadowPadding(0, 0, 0, 0);
			return;
		}
		float elevation = getMaxElevation(groupView);
		final float radius = getRadius(groupView);
		int hPadding = (int) Math.ceil(RoundRectDrawableWithShadow
				.calculateHorizontalPadding(elevation, radius, groupView.getPreventCornerOverlap()));
		int vPadding = (int) Math.ceil(RoundRectDrawableWithShadow
				.calculateVerticalPadding(elevation, radius, groupView.getPreventCornerOverlap()));
		groupView.setShadowPadding(hPadding, vPadding, hPadding, vPadding);
	}

	@Override
	public void onCompatPaddingChanged(ToggleGroupDelegate groupView) {
		setMaxElevation(groupView, getMaxElevation(groupView));
	}

	@Override
	public void onPreventCornerOverlapChanged(ToggleGroupDelegate groupView) {
		setMaxElevation(groupView, getMaxElevation(groupView));
	}

	@Override
	public void setBackgroundColor(ToggleGroupDelegate groupView, @Nullable ColorStateList color) {
		getCardBackground(groupView).setColor(color);
	}

	@Override
	public ColorStateList getBackgroundColor(ToggleGroupDelegate groupView) {
		return getCardBackground(groupView).getColor();
	}

	private RoundRectDrawable getCardBackground(ToggleGroupDelegate groupView) {
		return ((RoundRectDrawable) groupView.getGroupBackground());
	}
}
