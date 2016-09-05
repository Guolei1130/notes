package spread;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import gl.com.dmeo.R;

public class SpreadTextView extends LinearLayout implements View.OnClickListener{

    private int mDefault_lines = 4;
    private int mMin_lines = -1;
    private Context mContext;

    private TextView mContent;
    private LinearLayout mLayout;
    private TextView mHint;
    private ImageView mImage;

    private String mContentString;

    private boolean isOpen = false;

    public SpreadTextView(Context context) {
        this(context,null);
    }

    public SpreadTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SpreadTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }


    /** init view **/
    private void initView() {
        mContent = (TextView) findViewById(R.id.content);
        mLayout = (LinearLayout) findViewById(R.id.open);
        mHint = (TextView) findViewById(R.id.hint);
        mImage = (ImageView) findViewById(R.id.imageview);
        mLayout.setOnClickListener(this);
        mContent.setMaxLines(mMin_lines);

    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.SpreadTextView);
        mMin_lines = typedArray.getInt(R.styleable.SpreadTextView_min_line_count,mDefault_lines);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mContent.getLineCount() <= mMin_lines){
            mLayout.setVisibility(View.GONE);
        }else {
            mLayout.setVisibility(View.VISIBLE);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        RotateAnimation animation;
        if (!isOpen){
            mContent.setMaxLines(Integer.MAX_VALUE);
            animation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF, (float) 0.5,Animation.RELATIVE_TO_SELF, (float) 0.5);
            isOpen = true;
        } else {
            mContent.setMaxLines(mMin_lines);
            isOpen = false;
            animation = new RotateAnimation(-180,0,Animation.RELATIVE_TO_SELF, (float) 0.5,Animation.RELATIVE_TO_SELF, (float) 0.5);
        }
        animation.setDuration(300);
        animation.setFillAfter(true);
        mImage.setAnimation(animation);
        animation.start();
        // TODO: 16-9-6 View动画不好，考虑用属性动画
//        ObjectAnimator.ofFloat(mImage,"rotationY",0,180).start();
//        ObjectAnimator.ofFloat(mImage,"rotationY",-180,0).start();

    }

    public void bindData(@Nullable String text){
        mContentString = text;

        mContent.setText(mContentString);

        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        requestLayout();
    }
}
