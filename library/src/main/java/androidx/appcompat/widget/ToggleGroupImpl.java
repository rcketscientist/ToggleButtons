package androidx.appcompat.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Interface for platform specific ToggleGroup implementations.
 */
@RequiresApi(9)
@TargetApi(9)
public interface ToggleGroupImpl
{
	void initialize(ToggleGroupDelegate groupView, Context context, ColorStateList backgroundColor,
	                float radius, float elevation, float maxElevation);

	void setRadius(ToggleGroupDelegate groupView, float radius);

	float getRadius(ToggleGroupDelegate groupView);

	void setElevation(ToggleGroupDelegate groupView, float elevation);

	float getElevation(ToggleGroupDelegate groupView);

	void initStatic();

	void setMaxElevation(ToggleGroupDelegate groupView, float maxElevation);

	float getMaxElevation(ToggleGroupDelegate groupView);

	float getMinWidth(ToggleGroupDelegate groupView);

	float getMinHeight(ToggleGroupDelegate groupView);

	void updatePadding(ToggleGroupDelegate groupView);

	void onCompatPaddingChanged(ToggleGroupDelegate groupView);

	void onPreventCornerOverlapChanged(ToggleGroupDelegate groupView);

	void setBackgroundColor(ToggleGroupDelegate groupView, @Nullable ColorStateList color);

	ColorStateList getBackgroundColor(ToggleGroupDelegate groupView);
}
