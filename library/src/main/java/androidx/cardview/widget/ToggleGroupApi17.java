package androidx.cardview.widget;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.RequiresApi;

@RequiresApi(17)
@TargetApi(17)
public class ToggleGroupApi17 extends ToggleGroupApi9
{
	@Override
	public void initStatic() {
		RoundRectDrawableWithShadow.sRoundRectHelper
				= new RoundRectDrawableWithShadow.RoundRectHelper() {
			@Override
			public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint) {
				canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
			}
		};
	}
}
