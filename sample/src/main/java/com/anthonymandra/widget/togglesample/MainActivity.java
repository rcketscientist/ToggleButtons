package com.anthonymandra.widget.togglesample;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ToggleGroup;

public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ToggleGroup alignment = (ToggleGroup) findViewById(R.id.groupTextAlignment);
		final ToggleGroup format = (ToggleGroup) findViewById(R.id.groupTextFormat);

		((ToggleGroup)findViewById(R.id.groupOrientation)).setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(ToggleGroup group, @IdRes int[] checkedId) {
				int orientation = checkedId[0] == R.id.buttonHorizontal ?
						LinearLayoutCompat.HORIZONTAL : LinearLayoutCompat.VERTICAL;
				alignment.setOrientation(orientation);
				format.setOrientation(orientation);
			}
		});

		((ToggleGroup)findViewById(R.id.groupExclusive)).setOnCheckedChangeListener(new ToggleGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(ToggleGroup group, @IdRes int[] checkedId) {
				boolean isExclusive = checkedId[0] == R.id.buttonExclusive;
				alignment.setExclusive(isExclusive);
				format.setExclusive(isExclusive);
			}
		});
	}
}
