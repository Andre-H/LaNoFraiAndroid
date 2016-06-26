package com.andrehulse.lanofraiandroid.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.andrehulse.lanofraiandroid.R;

public class LaNoFraiMainActivity extends Activity {

	private Button mKDFraiButton;
	//private Button mKDEuButton;
	private Button mBlogButton;
	private Button mSobreButton;
	private Button mKDEuButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_lanofrai);
		
        mKDFraiButton = (Button) findViewById(R.id.cadele_frai_button);
        mKDFraiButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LaNoFraiMainActivity.this, CadeleFraiActivity.class);
				startActivity(i);
			}
		});
		/*
        mKDEuButton = (Button) findViewById(R.id.cadele_eu_button);
        mKDEuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LaNoFraiMainActivity.this, BlogActivity.class);
				startActivity(i);
			}
		});
		*/
        mBlogButton = (Button) findViewById(R.id.blog_button);
        mBlogButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LaNoFraiMainActivity.this, BlogActivity.class);
				startActivity(i);
			}
		});
        //mSobreButton = (Button) findViewById(R.id.sobre_button);
        //mSobreButton.setOnClickListener(new View.OnClickListener() {
		//	public void onClick(View v) {
				//Intent i = new Intent(HelloMVCCompassActivity.this, AndroidBotCompassActivity.class);
				//startActivity(i);
		//	}
		//});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_lanofrai, menu);
		return true;
	}

}
