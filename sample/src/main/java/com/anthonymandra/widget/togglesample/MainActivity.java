package com.anthonymandra.widget.togglesample;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.ToggleGroup;

public class MainActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ToggleGroup alignment = findViewById(R.id.groupTextAlignment);
		final ToggleGroup format = findViewById(R.id.groupTextFormat);

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
