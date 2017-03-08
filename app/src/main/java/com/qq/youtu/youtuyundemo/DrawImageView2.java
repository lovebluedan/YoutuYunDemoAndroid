package com.qq.youtu.youtuyundemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;

public class DrawImageView2 extends ImageView{

	private Context context;

	public DrawImageView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	Paint paint = new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(Color.YELLOW);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);//设置线宽
		paint.setAlpha(100);
	};
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = manager.getDefaultDisplay().getWidth();
		int height = manager.getDefaultDisplay().getHeight();

//		canvas.drawRect(new Rect(width/4, height/4, 3*width/4, 3*height/4), paint);//绘制矩形
		canvas.drawRect(new Rect(425, 300, 625, 500), paint);//绘制矩形
		canvas.drawRect(new Rect(50, 250, 660, 580), paint);//绘制矩形
	}
	
	


	

}
