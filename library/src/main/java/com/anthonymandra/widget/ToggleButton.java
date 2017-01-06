package com.anthonymandra.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.Gravity;

public class ToggleButton extends android.support.v7.widget.AppCompatRadioButton {

	public ToggleButton(Context context) {
		this(context, null);
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.Widget_Material_ToggleButton);
	}

	public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	//TODO: Eventually support library will support drawableTint and we can discard the buttonDrawable hack
	@Override
	protected void onDraw(Canvas canvas) {
		final Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(this);
		if (buttonDrawable != null) {
			final int verticalGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
			final int horizontalGravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
			final int drawableHeight = buttonDrawable.getIntrinsicHeight();
			final int drawableWidth = buttonDrawable.getIntrinsicWidth();

			final int top;
			switch (verticalGravity) {
				case Gravity.BOTTOM:
					top = getHeight() - drawableHeight;
					break;
				case Gravity.CENTER_VERTICAL:
					top = (getHeight() - drawableHeight) / 2;
					break;
				default:
					top = 0;
			}

			final int left;
			switch (horizontalGravity) {
				case Gravity.RIGHT:
				case Gravity.END:
					left = getWidth() - drawableWidth;
					break;
				case Gravity.CENTER_HORIZONTAL:
					left = (getWidth() - drawableWidth) / 2;
					break;
				default:
					left = 0;
			}

			final int bottom = top + drawableHeight;
			final int right = left + drawableWidth;

			buttonDrawable.setBounds(left, top, right, bottom);

			final Drawable background = getBackground();
			if (background != null) {
				background.setHotspotBounds(left, top, right, bottom);
			}

			if (buttonDrawable != null) {
				buttonDrawable.draw(canvas);
			}
		}
	}

	@Override
	public void toggle() {
		setChecked(!isChecked());	// back to default toggle behavior (can deselect)
	}
}
