package gmail.developer_formal.freeappblocker.objects;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoResizeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public AutoResizeTextView(Context context) {
        super(context);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void resizeText() {
        int targetWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (targetWidth <= 0) return;

        float textSize = getTextSize();
        Paint paint = new Paint();
        paint.set(getPaint());

        float width = paint.measureText(getText().toString());
        while (width > targetWidth) {
            textSize -= 1;
            paint.setTextSize(textSize);
            width = paint.measureText(getText().toString());
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resizeText();
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        resizeText();
    }
}