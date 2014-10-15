package com.yimu.blurfloatback;


import java.io.IOException;
import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class MainService extends Service implements SensorEventListener {

	private SensorManager mSensorManager;
	private boolean isHeadUp;
	private static final float HEAD_BETA_THRESHOLD = 40f;
	private String capturePicPath;
	private WindowManager mWindowManager;
	private ImageView picView;
	protected Bitmap blurredImage;
	Handler mhandler;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mhandler = new Handler();
		isHeadUp = false;
		//initSensor();
	}

	private void initSensor() {
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean success = false;
		success = mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
		if (!success)
			Toast.makeText(this, "Can not init sensor", Toast.LENGTH_SHORT)
					.show();
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		this.onHeadUP();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 判断是否满足抬头条件
		int type = event.sensor.getType();
		if (type == Sensor.TYPE_ORIENTATION) {
			float beta = RotateUtil.getHeadBeta(event.values);
			if (beta > HEAD_BETA_THRESHOLD) {
				if (!isHeadUp) {
					this.onHeadUP();
				}
			}
		}

	}

	private void onHeadUP() {
		isHeadUp = true;
		runCapture();
		new Handler().postDelayed(new Runnable(){
			public void run(){
				createPictureAndSet();
				
			}
		}, 2000);
	}
	
	private void createPictureAndSet() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// No image found => let's generate it!
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap srcImage = BitmapFactory.decodeFile(capturePicPath,
						options);
				if (srcImage == null) {
					/*runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Pic is null", Toast.LENGTH_SHORT).show();
						}
					});*/
					isHeadUp = false;
					return;
				}
				blurredImage = Blur.fastblur(MainService.this, srcImage, 12);
				mhandler.post(new Runnable() {
					public void run() {
						createFloatView();
					}
				});
			}
		}).start();
	}

	private void createFloatView() {
		picView = new ImageView(this);
		picView.setImageBitmap(blurredImage);
		picView.setAlpha(1.0F);
		mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		LayoutParams mLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				LayoutParams.TYPE_SYSTEM_ERROR, LayoutParams.FLAG_NOT_FOCUSABLE
						| LayoutParams.FLAG_FULLSCREEN
						| LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSPARENT); 
		mWindowManager.addView(picView, mLayoutParams);
		picView.setVisibility(View.VISIBLE);
		picView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picView.setVisibility(View.GONE);
				isHeadUp = false;
				stopSelf();
			}
		});
	}

	/**
	 * 截图动作
	 */
	private void runCapture() {
		new Thread() {
			public void run() {
				long capTime = Calendar.getInstance().getTimeInMillis();
				capturePicPath = Environment.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_PICTURES).getPath()
						+ "/" + capTime + ".png";
				try {
					Runtime.getRuntime().exec(
							new String[] { "su", "-c",
									"screencap "+ capturePicPath });

					// notify
					/*runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Save in " + capturePicPath,
									Toast.LENGTH_SHORT).show();
						}

					});*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
