package android.support.v7.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.TintableCompoundButton;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;

import com.anthonymandra.widget.R;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

@SuppressWarnings("unused")
public class ToggleButton extends CompoundButton implements TintableCompoundButton {

	private final AppCompatCompoundButtonHelper mCompoundButtonHelper;
	private CharSequence mTextOn;
	private CharSequence mTextOff;

	public ToggleButton(Context context) {
		this(context, null);
	}

	public ToggleButton(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.toggleButtonStyle);
	}

	@SuppressLint("RestrictedApi")
	public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(TintContextWrapper.wrap(context), attrs, defStyleAttr);
		mCompoundButtonHelper = new AppCompatCompoundButtonHelper(this);
		mCompoundButtonHelper.loadFromAttributes(attrs, defStyleAttr);

		final TypedArray a = context.obtainStyledAttributes(
				attrs, R.styleable.ToggleButton, defStyleAttr, R.style.Widget_Material_ToggleButton);

		int resId = a.getResourceId(R.styleable.ToggleButton_buttonCompat, 0);
		if (resId != 0) {
			Drawable drawable = AppCompatResources.getDrawable(getContext(), resId);
			setButtonDrawable(drawable);
		}

		mTextOn = a.getText(R.styleable.ToggleButton_textOn);
		mTextOff = a.getText(R.styleable.ToggleButton_textOff);

		syncTextState();
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// If there's text of any sort resort to CompoundButton#onDraw
		if (getText() != null && getText().length() > 0 ||
			getTextOff() != null && getTextOff().length() > 0 ||
			getTextOff() != null && getTextOn().length() > 0) {
			super.onDraw(canvas);
		}
		// Otherwise override CompoundButton#onDraw entirely to allow properly aligned image toggles
		else {
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
				if (Build.VERSION.SDK_INT > 21 && background != null) {
					background.setHotspotBounds(left, top, right, bottom);
				}

				buttonDrawable.draw(canvas);
			}
		}
	}

	/* Official ToggleButton methods */

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);

		syncTextState();
	}

	private void syncTextState() {
		boolean checked = isChecked();
		if (checked && mTextOn != null) {
			setText(mTextOn);
		} else if (!checked && mTextOff != null) {
			setText(mTextOff);
		}
	}

	/**
	 * Returns the text for when the button is in the checked state.
	 *
	 * @return The text.
	 */
	@SuppressWarnings("WeakerAccess")
	public CharSequence getTextOn() {
		return mTextOn;
	}

	/**
	 * Sets the text for when the button is in the checked state.
	 *
	 * @param textOn The text.
	 */
	public void setTextOn(CharSequence textOn) {
		mTextOn = textOn;
	}

	/**
	 * Returns the text for when the button is not in the checked state.
	 *
	 * @return The text.
	 */
	@SuppressWarnings("WeakerAccess")
	public CharSequence getTextOff() {
		return mTextOff;
	}

	/**
	 * Sets the text for when the button is not in the checked state.
	 *
	 * @param textOff The text.
	 */
	public void setTextOff(CharSequence textOff) {
		mTextOff = textOff;
	}

	/* Official support library methods */

	@Override
	public void setButtonDrawable(Drawable buttonDrawable) {
		super.setButtonDrawable(buttonDrawable);
		if (mCompoundButtonHelper != null) {
			mCompoundButtonHelper.onSetButtonDrawable();
		}
	}

	@Override
	public void setButtonDrawable(@DrawableRes int resId) {
		setButtonDrawable(AppCompatResources.getDrawable(getContext(), resId));
	}

	@Override
	public int getCompoundPaddingLeft() {
		final int value = super.getCompoundPaddingLeft();
		return mCompoundButtonHelper != null
				? mCompoundButtonHelper.getCompoundPaddingLeft(value)
				: value;
	}

	@RestrictTo(LIBRARY_GROUP)
	@Override
	public void setSupportButtonTintList(@Nullable ColorStateList tint) {
		if (mCompoundButtonHelper != null) {
			mCompoundButtonHelper.setSupportButtonTintList(tint);
		}
	}

	@RestrictTo(LIBRARY_GROUP)
	@Nullable
	@Override
	public ColorStateList getSupportButtonTintList() {
		return mCompoundButtonHelper != null
				? mCompoundButtonHelper.getSupportButtonTintList()
				: null;
	}

	@RestrictTo(LIBRARY_GROUP)
	@Override
	public void setSupportButtonTintMode(@Nullable PorterDuff.Mode tintMode) {
		if (mCompoundButtonHelper != null) {
			mCompoundButtonHelper.setSupportButtonTintMode(tintMode);
		}
	}

	@RestrictTo(LIBRARY_GROUP)
	@Nullable
	@Override
	public PorterDuff.Mode getSupportButtonTintMode() {
		return mCompoundButtonHelper != null
				? mCompoundButtonHelper.getSupportButtonTintMode()
				: null;
	}
}
