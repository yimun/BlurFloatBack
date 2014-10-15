package com.yimu.blurfloatback;

import java.io.IOException;

import com.linwei.blurfloatback.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class TestActivity extends Activity {

	private Button btn_cap;
	private Button btn_test_cap;
	private boolean isCheck;
	private Bitmap srcImage;
	private Bitmap blurredImage;
	private WindowManager mWindowManager;
	private ImageView picView;
	private float alpha;
	private Animation mAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		try {
			Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btn_cap = (Button) findViewById(R.id.button1);
		btn_test_cap = (Button) findViewById(R.id.button2);
		isCheck = false;
		btn_cap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(TestActivity.this, MainService.class);
				if (isCheck) {
					isCheck = false;
					startService(in);
					Toast.makeText(getApplicationContext(), "open",
							Toast.LENGTH_SHORT).show();
				} else {
					isCheck = true;
					stopService(in);
					Toast.makeText(getApplicationContext(), "close",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		btn_test_cap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						testRunCapture();
					}
				}.start();
				
			}
		});
	}

	protected void testRunCapture() {
		
		blurredImage = Blur.backblur(this, 12);
		if(blurredImage != null)
			runOnUiThread(new Runnable() {
				public void run() {
					createFloatView();
				}
			});

	}

	private void createFloatView() {
		picView = new ImageView(this);
		picView.setImageBitmap(blurredImage);
		alpha = 1.0f;
		picView.setAlpha(1.0F);
		mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		LayoutParams mLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				LayoutParams.TYPE_SYSTEM_ERROR, LayoutParams.FLAG_NOT_FOCUSABLE
						| LayoutParams.FLAG_FULLSCREEN
						| LayoutParams.FLAG_LAYOUT_IN_SCREEN, // È«ÆÁÏÔÊ¾ÌØÕ÷
				PixelFormat.TRANSPARENT);

		picView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Handler h = new Handler();
				
				h.post(new Runnable() {
					public void run() {
						if (alpha > 0) {
							alpha -= 0.05;
							picView.setAlpha(alpha);
							h.postDelayed(this, 20);
						} else {
							picView.setVisibility(View.GONE);
							mWindowManager.removeView(picView);
						}
					}
				});
				 
				System.out.println("onCLick");
				// AlphaAnimation am = new AlphaAnimation( 1 , 0);

				/*mAnimation = AnimationUtils.loadAnimation(TestActivity.this,
						R.anim.floatdisappear);

				mAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						System.out.println("onStart");
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						picView.setVisibility(View.GONE);
						mWindowManager.removeView(picView);
						System.out.println("onEnd");
					}
				});
				picView.requestLayout();
				picView.startAnimation(mAnimation);
				*/

			}
		});

		mWindowManager.addView(picView, mLayoutParams);
		picView.setVisibility(View.VISIBLE);
	}

}
