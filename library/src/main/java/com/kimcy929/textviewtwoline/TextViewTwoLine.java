package com.kimcy929.textviewtwoline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.settings_template_layout.R;

import java.util.Locale;

public class TextViewTwoLine extends View {

    private static final String TAG = TextViewTwoLine.class.getSimpleName();

    private String textTitle;
    private String textDescription;

    private TextPaint titleTextPaint;
    private StaticLayout titleLayout;

    private TextPaint desTextPaint;
    private StaticLayout desLayout;
    private float yStartDes;

    private Drawable leftDrawable;
    private int leftDrawableId;
    private int drawableTintColor = -1;
    private int drawablePadding = 0;

    private final int DRAWABLE_DEFAULT_SIZE = dpToPx(24f);

    private final int KEY_LINE_DEFAULT_SIZE = dpToPx(72f);

    private int paragraphLeading;

    private int textTitleColor;
    private int descriptionColor;

    private int titleTextAppearId;
    private int descriptionTextAppearId;

    private Typeface titleTypeFace;
    private Typeface descriptionTypeFace;

    private boolean keepDefaultLineSpacing;

    private static final String TEXT_VIEW_TWO_LINE_EXTRA = "TEXT_VIEW_TWO_LINE_EXTRA";
    private static final String TEXT_TITLE_EXTRA         = "TEXT_TITLE_EXTRA";
    private static final String TEXT_DESCRIPTION_EXTRA   = "TEXT_DESCRIPTION_EXTRA";
    private static final String LEFT_DRAWABLE_ID_EXTRA   = "LEFT_DRAWABLE_ID_EXTRA";

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    public void setLeftDrawable(@DrawableRes int leftDrawableId) {
        this.leftDrawableId = leftDrawableId;
        leftDrawable = AppCompatResources.getDrawable(getContext(), leftDrawableId);
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    public void setText(@Nullable String textTitle, @Nullable String textDescription) {
        this.textTitle = textTitle;
        this.textDescription = textDescription;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    // use this constructor if creating MyView programmatically
    public TextViewTwoLine(Context context) {
        super(context);
        initTwoTextView(context, null, 0);
    }

    // this constructor is used when created from xml
    public TextViewTwoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTwoTextView(context, attrs, 0);
    }

    public TextViewTwoLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTwoTextView(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextViewTwoLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTwoTextView(context, attrs, defStyleAttr);
    }

    //https://stackoverflow.com/a/46477727
    private TypedValue resolveThemeAttr(@AttrRes int attrRes) {
        Resources.Theme theme = getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

    @ColorInt
    private int resolveColorAttr(@AttrRes int colorAttr) throws Resources.NotFoundException {
        TypedValue resolvedAttr = resolveThemeAttr(colorAttr);
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        int colorRes = resolvedAttr.resourceId != 0 ? resolvedAttr.resourceId : resolvedAttr.data;
        return ContextCompat.getColor(getContext(), colorRes);
    }

    @SuppressWarnings("unused")
    @SuppressLint("LogNotTimber")
    private void initTwoTextView(Context context, AttributeSet attributeSet, int defStyleAttr) {

        try {
            textTitleColor = resolveColorAttr(android.R.attr.textColorPrimary);
        } catch (Resources.NotFoundException e) {
            textTitleColor = 0xDE000000;
            Log.e(TAG, "Error get textColorPrimary " + e.getMessage());
        }

        try {
            descriptionColor = resolveColorAttr(android.R.attr.textColorSecondary);
        } catch (Resources.NotFoundException e) {
            descriptionColor = 0x8A000000;
            Log.e(TAG, "Error get textColorSecondary " + e.getMessage());
        }

        int titleTextSize = spToPx(14f);
        int descriptionTextSize = spToPx(14f);

        int titleTextStyle;
        int descriptionTextStyle;

        if (attributeSet != null) {
            TypedArray a = null;
            try {

                a = context.obtainStyledAttributes(attributeSet, R.styleable.TextViewTwoLine);

                textTitle = a.getString(R.styleable.TextViewTwoLine_textTitle);
                textDescription = a.getString(R.styleable.TextViewTwoLine_textDescription);
                drawablePadding = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_drawablePadding, 0);
                drawableTintColor = a.getColor(R.styleable.TextViewTwoLine_drawableTintColor, -1);

                leftDrawableId = a.getResourceId(R.styleable.TextViewTwoLine_appCompatLeftDrawable, -1);
                if (leftDrawableId != -1) {
                    leftDrawable = AppCompatResources.getDrawable(context, leftDrawableId);
                }

                int fontId = a.getResourceId(R.styleable.TextViewTwoLine_titleFontFamily, -1);
                if (fontId != -1) {
                    try {
                        titleTypeFace = ResourcesCompat.getFont(getContext(), fontId);
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Not found title fontFamily");
                    }
                }

                titleTextStyle = a.getInt(R.styleable.TextViewTwoLine_TextViewTwoLineTitleTextStyle, 0); //regular
                if (titleTypeFace != null) {
                    if (titleTextStyle != 0) {
                        titleTypeFace = Typeface.create(titleTypeFace, titleTextStyle);
                    }
                } else if (titleTextStyle != 0) {
                    titleTypeFace = Typeface.defaultFromStyle(titleTextStyle);
                }

                fontId = a.getResourceId(R.styleable.TextViewTwoLine_descriptionFontFamily, -1);
                if (fontId != -1) {
                    try {
                        descriptionTypeFace = ResourcesCompat.getFont(getContext(), fontId);
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Not found description fontFamily");
                    }
                }

                descriptionTextStyle = a.getInt(R.styleable.TextViewTwoLine_TextViewTwoLineDescriptionTextStyle, 0); //regular
                if (descriptionTypeFace != null) {
                    if (descriptionTextStyle != 0) {
                        descriptionTypeFace = Typeface.create(descriptionTypeFace, descriptionTextStyle);
                    }
                } else if (descriptionTextStyle != 0) {
                    descriptionTypeFace = Typeface.defaultFromStyle(descriptionTextStyle);
                }

                titleTextAppearId = a.getResourceId(R.styleable.TextViewTwoLine_titleTextAppearance, -1);
                descriptionTextAppearId = a.getResourceId(R.styleable.TextViewTwoLine_descriptionTextAppearance, -1);

                titleTextSize = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_titleTextSize, spToPx(14f));
                descriptionTextSize = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_descriptionTextSize, spToPx(14f));

                textTitleColor = a.getColor(R.styleable.TextViewTwoLine_textTitleColor, textTitleColor);
                descriptionColor = a.getColor(R.styleable.TextViewTwoLine_textDescriptionColor, descriptionColor);

                keepDefaultLineSpacing = a.getBoolean(R.styleable.TextViewTwoLine_keepDefaultLineSpacing, false);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }

        // init for title
        titleTextPaint = new TextPaint();
        titleTextPaint.setAntiAlias(true);
        titleTextPaint.setTextSize(titleTextSize);
        titleTextPaint.setTextLocale(Locale.getDefault());

        // default to a single line of text
        if (!TextUtils.isEmpty(textTitle)) {
            int width = (int) titleTextPaint.measureText(textTitle);
            createTitleLayout(width);
        }


        // init for description
        desTextPaint = new TextPaint();
        desTextPaint.setAntiAlias(true);
        desTextPaint.setTextSize(descriptionTextSize);
        desTextPaint.setTextLocale(Locale.getDefault());

        // default to a single line of text
        if (!TextUtils.isEmpty(textDescription)) {
            int width = (int) desTextPaint.measureText(textDescription);
            createDescriptionLayout(width);
        }
    }

    @NonNull
    private Layout.Alignment getAlignment() {
        Layout.Alignment layoutAlign = Layout.Alignment.ALIGN_NORMAL;
        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            layoutAlign = Layout.Alignment.ALIGN_OPPOSITE;
        }
        return layoutAlign;
    }

    private void createTitleLayout(int width) {
        SpannableStringBuilder stringBuilder;

        if (titleTextAppearId != -1) {
            stringBuilder = getSpannableStringBuilder(textTitle, titleTextAppearId);
        } else {
            stringBuilder = new SpannableStringBuilder(textTitle);
        }

        if (textTitleColor != -1) {
            stringBuilder.setSpan(new ForegroundColorSpan(textTitleColor), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (titleTypeFace != null) {
            stringBuilder.setSpan(new CustomTypefaceSpan(titleTypeFace), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            TextDirectionHeuristic textDirection = TextDirectionHeuristics.LTR;
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                textDirection = TextDirectionHeuristics.RTL;
            }

            titleLayout = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), titleTextPaint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setTextDirection(textDirection)
                    .setLineSpacing(0, 1.0f) // multiplier, add
                    .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
                    .setIncludePad(false)
                    .build();
        } else {
            Layout.Alignment layoutAlign = getAlignment();
            titleLayout = new StaticLayout(stringBuilder, titleTextPaint, width, layoutAlign, 1.0f, 0, false);
        }
    }

    private void createDescriptionLayout(int width) {
        SpannableStringBuilder stringBuilder;

        if (descriptionTextAppearId != -1) {
            stringBuilder = getSpannableStringBuilder(textDescription, descriptionTextAppearId);
        } else {
            stringBuilder = new SpannableStringBuilder(textDescription);
        }

        if (descriptionColor != -1) {
            stringBuilder.setSpan(new ForegroundColorSpan(descriptionColor), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (descriptionTypeFace != null) {
            stringBuilder.setSpan(new CustomTypefaceSpan(descriptionTypeFace), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            TextDirectionHeuristic textDirection = TextDirectionHeuristics.LTR;
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                textDirection = TextDirectionHeuristics.RTL;
            }

            desLayout = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), desTextPaint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setTextDirection(textDirection)
                    .setLineSpacing(0, 1.0f) // multiplier,
                    .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
                    .setIncludePad(false)
                    .build();

        } else {
            Layout.Alignment layoutAlign = getAlignment();
            desLayout = new StaticLayout(stringBuilder, desTextPaint, width, layoutAlign, 1.0f, 0, false);
        }
    }

    @NonNull
    private SpannableStringBuilder getSpannableStringBuilder(@NonNull String text, @StyleRes int textAppearStyle) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        stringBuilder.setSpan(new TextAppearanceSpan(getContext(), textAppearStyle), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            updateContentBounds();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = View.resolveSize(getDesireWidth(widthMeasureSpec), widthMeasureSpec);

        int height = View.resolveSize(getDesireHeight(heightMeasureSpec), heightMeasureSpec);

        setMeasuredDimension(width, height);

        updateContentBounds();
    }

    private int getDesireWidth(int widthMeasureSpec) {

        int desireWidth = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            desireWidth = widthRequirement;
        } else {

            int drawableSize = 0;
            if (leftDrawable != null) {
                drawableSize = leftDrawable.getIntrinsicWidth();
            }

            int keyLine = getPaddingStart() + drawableSize + drawablePadding;

            desireWidth += keyLine;

            if (titleLayout != null && desLayout != null) {
                desireWidth += Math.max(titleLayout.getWidth(), desLayout.getWidth());
            } else if (titleLayout != null) {
                desireWidth += titleLayout.getWidth();
            } else if (desLayout != null) {
                desireWidth += desLayout.getWidth();
            } else {
                if (leftDrawable != null) {
                    desireWidth = leftDrawable.getIntrinsicWidth();
                }
            }

            desireWidth += getPaddingEnd();

            if (widthMode == MeasureSpec.AT_MOST) {
                if (desireWidth > widthRequirement) {
                    desireWidth = widthRequirement;
                }
            } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                desireWidth = getDefaultSize(KEY_LINE_DEFAULT_SIZE, widthMeasureSpec);
            }
        }

        return desireWidth;
    }

    private int getDesireHeight(int heightMeasureSpec) {

        int desireHeight = 0;

        desireHeight += getPaddingEnd() + getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            desireHeight += heightRequirement;
        } else {
            int titleTextHeight;
            int descriptionTextHeight;
            int totalTextHeight = 0;

            if (titleLayout != null) {
                titleTextHeight = titleLayout.getHeight();
                totalTextHeight += titleTextHeight;
            }

            if (desLayout != null) {
                descriptionTextHeight = desLayout.getHeight();
                if (titleLayout != null) {
                    paragraphLeading = (int) (titleTextPaint.getFontMetrics().bottom * (keepDefaultLineSpacing ? 1.0f : 1.2f));
                    totalTextHeight += descriptionTextHeight + paragraphLeading;
                } else {
                    totalTextHeight += descriptionTextHeight;
                }
            }

            if (leftDrawable != null) {
                desireHeight += Math.max(leftDrawable.getIntrinsicHeight(), totalTextHeight);
            } else {
                desireHeight = totalTextHeight;
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                desireHeight = Math.min(desireHeight, heightRequirement);
            } else if (heightMode == MeasureSpec.UNSPECIFIED) {
                desireHeight = getDefaultSize(DRAWABLE_DEFAULT_SIZE, heightMeasureSpec);
            }
        }

        return desireHeight;
    }

    private int xStartTitle, yStartTitle;

    private void updateContentBounds() {

        int drawableSize = 0;
        int left = getPaddingStart();

        if (leftDrawable != null) {
            drawableSize = leftDrawable.getIntrinsicWidth();
            if (drawableTintColor != -1) {
                leftDrawable.mutate().setColorFilter(drawableTintColor, PorterDuff.Mode.SRC_IN);
            } else {
                leftDrawable.mutate().setColorFilter(null);
            }
        }

        int textWidth = getMeasuredWidth() - getPaddingStart()
                - drawableSize - drawablePadding - getPaddingEnd(); // keyLine = paddingStart + drawableSize + drawablePadding = 16dp + 24dp + 32dp even view hasn't a drawable

        if (!TextUtils.isEmpty(textTitle)) {
            createTitleLayout(textWidth);
        } else {
            titleLayout = null;
        }

        if (!TextUtils.isEmpty(textDescription)) {
            createDescriptionLayout(textWidth);
        } else {
            desLayout = null;
        }

        int titleLayoutWidth = 0;
        int titleLayoutHeight = 0;

        if (titleLayout != null) {
            titleLayoutHeight = titleLayout.getHeight();
            titleLayoutWidth = titleLayout.getWidth();
        } else if (desLayout != null) {
            titleLayoutWidth = desLayout.getWidth();
        }

        if (titleLayout != null) {
            if (desLayout != null) {
                yStartTitle = (getMeasuredHeight() - getPaddingBottom()
                        - desLayout.getHeight() - paragraphLeading
                        - titleLayoutHeight - getPaddingTop()) / 2;
            } else {
                yStartTitle = (getMeasuredHeight() - titleLayoutHeight) / 2;
            }
        }

        if (desLayout != null) {
            if (titleLayout != null) {
                yStartDes = yStartTitle + titleLayoutHeight + paragraphLeading;
            } else {
                yStartDes = (getMeasuredHeight() - desLayout.getHeight()) / 2;
            }
        }

        // default is LTR
        xStartTitle = left + drawableSize + drawablePadding; //left + drawableSize + drawablePadding; //72dp
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL) {
            left = getMeasuredWidth() - drawableSize - left;
            xStartTitle = getMeasuredWidth() - xStartTitle - titleLayoutWidth;
        }

        if (leftDrawable != null) {
            int top = (getMeasuredHeight() - drawableSize) / 2;
            leftDrawable.setBounds(left, top, left + drawableSize, top + drawableSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // do as little as possible inside onDraw to improve performance

        if (leftDrawable != null) {
            leftDrawable.draw(canvas);
        }

        if (titleLayout != null) {
            canvas.save();
            canvas.translate(xStartTitle, yStartTitle);
            titleLayout.draw(canvas);
            canvas.restore();
        }

        if (desLayout != null) {
            canvas.save();
            canvas.translate(xStartTitle, yStartDes);
            desLayout.draw(canvas);
            canvas.restore();
        }
    }

    //https://stackoverflow.com/a/8127813

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TEXT_VIEW_TWO_LINE_EXTRA, super.onSaveInstanceState());
        bundle.putString(TEXT_TITLE_EXTRA, textTitle);
        bundle.putString(TEXT_DESCRIPTION_EXTRA, textDescription);
        bundle.putInt(LEFT_DRAWABLE_ID_EXTRA, leftDrawableId);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            textTitle = bundle.getString(TEXT_TITLE_EXTRA, null);
            textDescription = bundle.getString(TEXT_DESCRIPTION_EXTRA, null);
            leftDrawableId = bundle.getInt(LEFT_DRAWABLE_ID_EXTRA, -1);
            if (leftDrawableId != -1) {
                leftDrawable = AppCompatResources.getDrawable(getContext(), leftDrawableId);
            }
            state = bundle.getParcelable(TEXT_VIEW_TWO_LINE_EXTRA);
        }
        super.onRestoreInstanceState(state);
    }


    private int dpToPx(final float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }
}