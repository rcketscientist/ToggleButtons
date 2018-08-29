package androidx.appcompat.widget;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Interface provided by ToggleGroup to implementations.
 * <p>
 * Necessary to resolve circular dependency between base ToggleGroup and platform implementations.
 */
public interface ToggleGroupDelegate
{
	void setGroupBackground(Drawable drawable);
	Drawable getGroupBackground();
	boolean getUseCompatPadding();
	boolean getPreventCornerOverlap();
	void setShadowPadding(int left, int top, int right, int bottom);
	void setMinWidthHeightInternal(int width, int height);
	View getToggleGroup();
}
