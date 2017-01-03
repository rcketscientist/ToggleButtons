package android.support.v7.widget;

import android.content.res.ColorStateList;

/**
 * Quick hack to expose {@link RoundRectDrawable}
 */
public class ExposedRoundRectDrawable extends RoundRectDrawable {

	public ExposedRoundRectDrawable(ColorStateList backgroundColor, float radius) {
		super(backgroundColor, radius);
	}

}
