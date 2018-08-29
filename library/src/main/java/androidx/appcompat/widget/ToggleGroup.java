package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.anthonymandra.widget.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.cardview.widget.ToggleGroupApi17;
import androidx.cardview.widget.ToggleGroupApi21;
import androidx.cardview.widget.ToggleGroupApi9;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ToggleGroup extends LinearLayoutCompat
{
    private static final int[] COLOR_BACKGROUND_ATTR = {android.R.attr.colorBackground};
    private static final ToggleGroupImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new ToggleGroupApi21();
        } else if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new ToggleGroupApi17();
        } else {
            IMPL = new ToggleGroupApi9();
        }
        IMPL.initStatic();
    }

    private boolean mCompatPadding;

    private boolean mPreventCornerOverlap;

    /**
     * ToggleGroup requires to have a particular minimum size to draw shadows before API 21. If
     * developer also sets min width/height, they might be overridden.
     *
     * ToggleGroup works around this issue by recording user given parameters and using an internal
     * method to set them.
     */
    int mUserSetMinWidth, mUserSetMinHeight;

    final Rect mContentPadding = new Rect();

    final Rect mShadowBounds = new Rect();

    // holds all checked values in the case of nonexclusive mode; the selection is empty by default
    private final List<Integer> mCheckedIds = new ArrayList<>();

    private boolean mExclusive;
    private boolean mAllowUnselected;

    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private ToggleGroup.OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    private int mDividerWidth;
    private int mDividerHeight;

    public ToggleGroup(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        init();
    }

    public ToggleGroup(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toggleGroupStyle);
    }

    public ToggleGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.ToggleGroup, defStyleAttr, R.style.Widget_Material_ToggleGroup);

        final int index = attributes.getInt(R.styleable.ToggleGroup_orientation, HORIZONTAL);
        //noinspection WrongConstant
        setOrientation(index);

        mExclusive = attributes.getBoolean(R.styleable.ToggleGroup_exclusive, false);
        mAllowUnselected = attributes.getBoolean(R.styleable.ToggleGroup_allowUnselected, false);

        final float cornerRadius = attributes.getDimension(R.styleable.ToggleGroup_cornerRadius, 0);
        final float elevation = attributes.getDimension(R.styleable.ToggleGroup_toggleElevation, 0);
        final float maxElevation = attributes.getDimension(R.styleable.ToggleGroup_toggleMaxElevation, 0);

        mCompatPadding = attributes.getBoolean(R.styleable.ToggleGroup_toggleUseCompatPadding, false);
        mPreventCornerOverlap = attributes.getBoolean(R.styleable.ToggleGroup_togglePreventCornerOverlap, true);

        // This creates a background which is important for both elevation shadow and rounded corner clipping
        ColorStateList backgroundColor;
        if (attributes.hasValue(R.styleable.ToggleGroup_backgroundColor)) {
            backgroundColor = attributes.getColorStateList(R.styleable.ToggleGroup_backgroundColor);
        } else {
            // There isn't one set, so we'll compute one based on the theme
            final TypedArray aa = getContext().obtainStyledAttributes(COLOR_BACKGROUND_ATTR);
            final int themeColorBackground = aa.getColor(0, 0);
            aa.recycle();

            // If the theme colorBackground is light, use our own light color, otherwise dark
            final float[] hsv = new float[3];
            Color.colorToHSV(themeColorBackground, hsv);
            //noinspection deprecation
            backgroundColor = ColorStateList.valueOf(hsv[2] > 0.5f
                    ? getResources().getColor(R.color.toggleGroup_light_background)
                    : getResources().getColor(R.color.toggleGroup_dark_background));
        }

        int value = attributes.getResourceId(R.styleable.ToggleGroup_checkedButton, View.NO_ID);
        if (value != NO_ID) {
            addCheckedId(value); // We set this even if not exclusive to help with designer

            if (mExclusive)
                mCheckedIds.add(value);
        }

        attributes.recycle();
        init();

        IMPL.initialize(mToggleGroupDelegate, context, backgroundColor, cornerRadius,
                elevation, maxElevation);
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * When true the group will allow up to a single selection, deselecting existing ones when a new one is selected.
     * @param exclusive true if only one selection is valid.
     */
    public void setExclusive(boolean exclusive) {
        mExclusive = exclusive;
    }

	/**
	 * see {@link #setExclusive(boolean)}
	 * @return true if this group allows a single selection
	 */
	public boolean isExclusive() {
        return mExclusive;
    }

    /**
     * When true the group will allow no selection, otherwise "deselecting" a single selection will do nothing.
     * @param allowUnselected true if no selection is allowed.
     */
    public void setAllowUnselected(boolean allowUnselected) {
        mAllowUnselected = allowUnselected;
    }

	/**
	 * see {@link #setAllowUnselected(boolean)}
	 * @return true if this group allows no selection
	 */
	public boolean isUnselectedAllowed() {
        return mAllowUnselected;
    }

    private int getExclusiveCheckedId()
    {
	    return mCheckedIds.size() > 0 ? mCheckedIds.get(0) : NO_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
		int initialCheck = getExclusiveCheckedId();
        if (initialCheck != View.NO_ID) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(initialCheck, true);
            mProtectFromCheckedChange = false;
            addCheckedId(initialCheck);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof CompoundButton) {
            final CompoundButton button = (CompoundButton) child;
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
	            int currentCheck = getExclusiveCheckedId();
                if (mExclusive && currentCheck != View.NO_ID) {
                    setCheckedStateForView(currentCheck, false);
                }
                mProtectFromCheckedChange = false;
                addCheckedId(button.getId());
            }
        }

        super.addView(child, index, params);
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearChecked()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     *
     * @see #getCheckedId()
     * @see #clearChecked()
     */
    public void check(@IdRes int id) {
        if (mExclusive)
            checkExclusive(id);
        else
            checkMulti(id);
    }

    private void checkMulti(@IdRes int id)
    {
        if (id == NO_ID)
            removeAllChecked();
        else if (mCheckedIds.contains(id)) {
            setCheckedStateForView(id, false);
            removeCheckedId(id);
        }
        else {
            setCheckedStateForView(id, true);
            addCheckedId(id);
        }

        // If this group has dividers, request a redraw
        if (hasDivider())
            requestLayout();
    }

    private void checkExclusive(@IdRes int id) {
	    int currentCheck = getExclusiveCheckedId();
	    // If no id, then clear
	    if (id == NO_ID) {
		    removeAllChecked();
	    }
	    else if (id == currentCheck) {      // If we've been sent an already checked item
            if(mAllowUnselected) {          // If we don't allow unselected we simply do nothing in this method.
	            setCheckedStateForView(id, false);
	            removeCheckedId(id);
            }
        }
        else {                              // Unchecked item selected
		    if (currentCheck != NO_ID) {    // Uncheck existing selection
			    setCheckedStateForView(currentCheck, false);
		    }
		    setCheckedStateForView(id, true);
		    addCheckedId(id);
	    }
    }

    private void removeAllChecked() {
	    mProtectFromCheckedChange = true;
        for (int id : mCheckedIds)
            setCheckedStateForView(id, false);
        mCheckedIds.clear();
	    mProtectFromCheckedChange = false;
	    fireCheckedChanged();
    }

    private void removeCheckedId(@IdRes int id) {
        mCheckedIds.remove((Integer)id);
        fireCheckedChanged();
    }

    private void fireCheckedChanged()
    {
        if (mOnCheckedChangeListener != null) {
            int[] checked = new int[mCheckedIds.size()];
            for (int i = 0; i < mCheckedIds.size(); i++) {
                checked[i] = mCheckedIds.get(i);
            }
            mOnCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    private void addCheckedId(@IdRes int id) {
        if (id == NO_ID)
            mCheckedIds.clear();
        else
        {
	        if (mExclusive)
	        	mCheckedIds.clear();    // keep the checked list clean since listeners send the whole list
	        mCheckedIds.add(id);
        }

        fireCheckedChanged();
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView instanceof CompoundButton) {
            ((CompoundButton) checkedView).setChecked(checked);
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.
     *
     * Note: If this group is NOT exclusive this will return the first checked id,
     * there may be multiple checked ids!
     *
     * @return the unique id of the selected radio button in this group
     *
     * @see #check(int)
     */
    @IdRes
    public int getCheckedId() {
        return getExclusiveCheckedId();
    }

    /**
     * <p>Returns the identifiers of the selected toggles in this group.
     * Upon empty selection, the returned value is null.</p>
     *
     * @return the unique id of the selected radio button in this group
     *
     * @see #check(int)
     * @see #clearChecked()
     *
     */
    @Nullable
    @IdRes
    public int[] getCheckedIds() {
        if (mCheckedIds. size() == 0)
            return null;

        int[] checked = new int[mCheckedIds.size()];
        for (int i = 0; i < mCheckedIds.size(); i++) {
            checked[i] = mCheckedIds.get(i);
        }
        return checked;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedId()
     */
    public void clearChecked() {
        check(View.NO_ID);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(ToggleGroup.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToggleGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ToggleGroup.LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ToggleGroup.LayoutParams;
    }

    @Override
    protected LinearLayoutCompat.LayoutParams generateDefaultLayoutParams() {
        return new ToggleGroup.LayoutParams(ToggleGroup.LayoutParams.WRAP_CONTENT, ToggleGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return ToggleGroup.class.getName();
    }

    /**
     * <p>This set of layout parameters defaults the width and the height of
     * the children to {@link ViewGroup.LayoutParams#WRAP_CONTENT} when they are not specified in the
     * XML file. Otherwise, this class uses the value read from the XML file.</p>
     *
     */
    public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * <p>Fixes the child's width to
         * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and the child's
         * height to  {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
         * when not specified in the XML file.</p>
         *
         * @param a the styled attributes set
         * @param widthAttr the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * toggle changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked toggle has changed. When the
         * selection is cleared, checkedId is null.</p>
         *
         * @param group the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(ToggleGroup group, @IdRes int[] checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

	        mProtectFromCheckedChange = true;
	        int id = buttonView.getId();
	        int currentCheck = getExclusiveCheckedId();

	        // If this item was already checked by itself (implies !isChecked)
	        if (mCheckedIds.size() == 1
		            && mCheckedIds.contains(id)
			        && !mAllowUnselected) {         // If we don't allow unselected
		        setCheckedStateForView(id, true);   // then we need to undo the auto-toggle
	        }
	        else {
		        // If checked in exclusive mode we need to uncheck the prior selection (implies isChecked)
		        if (mExclusive && isChecked)
		        {
			        if (currentCheck != NO_ID)
			        {
				        setCheckedStateForView(currentCheck, false);    // Uncheck existing selection
				        mCheckedIds.remove((Integer) id);    // wait for add to fire listener once
			        }
		        }

		        if (isChecked)
			        addCheckedId(id);
		        else
			        removeCheckedId(id);
	        }
	        mProtectFromCheckedChange = false;

            if (hasDivider())
                requestLayout();
        }
    }

    /**
     * <p>A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.</p>
     */
    private class PassThroughHierarchyChangeListener implements
            ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            if (parent == ToggleGroup.this && child instanceof CompoundButton) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
	                if (Build.VERSION.SDK_INT < 17)
		                id = child.hashCode();
	                else
	                    id = View.generateViewId();
                    child.setId(id);
                }
                ((CompoundButton) child).setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == ToggleGroup.this && child instanceof CompoundButton) {
                ((CompoundButton) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDividerDrawable() == null) {
            return;
        }

        if (getOrientation() == VERTICAL) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    /**
     * Set a drawable to be used as a divider between items.
     *
     * @param divider Drawable that will divide each item.
     */
    @Override
    public void setDividerDrawable(Drawable divider) {
        // We need our own copies of the dimensions
        if (divider != null) {
            mDividerWidth = divider.getIntrinsicWidth();
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerWidth = 0;
            mDividerHeight = 0;
        }
        super.setDividerDrawable(divider);
    }

    protected boolean hasDivider() {
        return getDividerDrawable() != null;
    }

    CompoundButton getVisibleViewBeforeChildAt(int index) {
        index--;
        while (index >= 0)
        {
            final CompoundButton previous = (CompoundButton) getChildAt(index);
            if (previous.getVisibility() != GONE)
                return previous;
            index--;
        }
        return null;
    }

    /**
     * Determines where to position dividers between children. Note: this is an 'illegal' override
     * of a hidden method.
     *
     * @param childIndex Index of child to check for preceding divider
     * @return true if there should be a divider before the child at childIndex
     */
    protected boolean hasDividerBeforeChildAt(int childIndex) {
        final CompoundButton child = (CompoundButton) getChildAt(childIndex);
        if (child == null)
            return false;
        if (child.getVisibility() == GONE)
            return false;
        final CompoundButton previous = getVisibleViewBeforeChildAt(childIndex);
        if (previous == null)
            return false;

        // If both are checked, add a divider
        return child.isChecked() && previous.isChecked();
    }

    void drawDividersVertical(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CompoundButton child = (CompoundButton) getChildAt(i);
            if (hasDividerBeforeChildAt(i)) {
                final LinearLayoutCompat.LayoutParams lp = (LinearLayoutCompat.LayoutParams) child.getLayoutParams();
                final int top = child.getTop() - lp.topMargin - mDividerHeight;
                drawHorizontalDivider(canvas, top);
            }
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final CompoundButton child = (CompoundButton) getChildAt(i);
            if (hasDividerBeforeChildAt(i)) {
                final LinearLayoutCompat.LayoutParams lp = (LinearLayoutCompat.LayoutParams) child.getLayoutParams();
                final int left = child.getLeft() - lp.leftMargin - mDividerWidth;
                drawVerticalDivider(canvas, left);
            }
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        final Drawable divider = getDividerDrawable();
        final int dividerPadding = getDividerPadding();

        divider.setBounds(getPaddingLeft() + dividerPadding, top,
                getWidth() - getPaddingRight() - dividerPadding, top + mDividerHeight);
        divider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        final Drawable divider = getDividerDrawable();
        final int dividerPadding = getDividerPadding();

        divider.setBounds(left, getPaddingTop() + dividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - dividerPadding);
        divider.draw(canvas);
    }

    /**
     * Returns whether ToggleGroup will add inner padding on platforms Lollipop and after.
     *
     * @return <code>true</code> if ToggleGroup adds inner padding on platforms Lollipop and after to
     * have same dimensions with platforms before Lollipop.
     */
    public boolean getUseCompatPadding() {
        return mCompatPadding;
    }

    /**
     * ToggleGroup adds additional padding to draw shadows on platforms before Lollipop.
     * <p>
     * This may cause Groups to have different sizes between Lollipop and before Lollipop. If you
     * need to align ToggleGroup with other Views, you may need api version specific dimension
     * resources to account for the changes.
     * As an alternative, you can set this flag to <code>true</code> and ToggleGroup will add the same
     * padding values on platforms Lollipop and after.
     * <p>
     * Since setting this flag to true adds unnecessary gaps in the UI, default value is
     * <code>false</code>.
     *
     * @param useCompatPadding <code>true</code> if ToggleGroup should add padding for the shadows on
     *      platforms Lollipop and above.
     */
    public void setUseCompatPadding(boolean useCompatPadding) {
        if (mCompatPadding != useCompatPadding) {
            mCompatPadding = useCompatPadding;
            IMPL.onCompatPaddingChanged(mToggleGroupDelegate);
        }
    }

    /**
     * Returns whether ToggleGroup should add extra padding to content to avoid overlaps with rounded
     * corners on pre-Lollipop platforms.
     *
     * @return True if ToggleGroup prevents overlaps with rounded corners on platforms before Lollipop.
     *         Default value is <code>true</code>.
     */
    public boolean getPreventCornerOverlap() {
        return mPreventCornerOverlap;
    }

    /**
     * On pre-Lollipop platforms, ToggleGroup does not clip its bounds for the rounded
     * corners. Instead, it adds padding to content so that it won't overlap with the rounded
     * corners. You can disable this behavior by setting this field to <code>false</code>.
     * <p>
     * Setting this value on Lollipop and above does not have any effect unless you have enabled
     * compatibility padding.
     *
     * @param preventCornerOverlap Whether ToggleGroup should add extra padding to content to avoid
     *                             overlaps with the ToggleGroup corners.
     * @see #setUseCompatPadding(boolean)
     */
    public void setPreventCornerOverlap(boolean preventCornerOverlap) {
        if (preventCornerOverlap != mPreventCornerOverlap) {
            mPreventCornerOverlap = preventCornerOverlap;
            IMPL.onPreventCornerOverlapChanged(mToggleGroupDelegate);
        }
    }

    private final ToggleGroupDelegate mToggleGroupDelegate = new ToggleGroupDelegate() {
        private Drawable mGroupBackground;

        @Override
        public void setGroupBackground(Drawable drawable) {
            mGroupBackground = drawable;
            //noinspection deprecation
            setBackgroundDrawable(drawable);
        }

        @Override
        public boolean getUseCompatPadding() {
            return ToggleGroup.this.getUseCompatPadding();
        }

        @Override
        public boolean getPreventCornerOverlap() {
            return ToggleGroup.this.getPreventCornerOverlap();
        }

        @Override
        public void setShadowPadding(int left, int top, int right, int bottom) {
            mShadowBounds.set(left, top, right, bottom);
            ToggleGroup.super.setPadding(left + mContentPadding.left, top + mContentPadding.top,
                    right + mContentPadding.right, bottom + mContentPadding.bottom);
        }

        @Override
        public void setMinWidthHeightInternal(int width, int height) {
            if (width > mUserSetMinWidth) {
                ToggleGroup.super.setMinimumWidth(width);
            }
            if (height > mUserSetMinHeight) {
                ToggleGroup.super.setMinimumHeight(height);
            }
        }

        @Override
        public Drawable getGroupBackground() {
            return mGroupBackground;
        }

        @Override
        public View getToggleGroup() {
            return ToggleGroup.this;
        }
    };
}
