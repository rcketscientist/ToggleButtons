 [ ![Download](https://api.bintray.com/packages/rcketscientist/maven/ToggleButtons/images/download.svg) ](https://bintray.com/rcketscientist/maven/ToggleButtons/_latestVersion)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()

# ToggleButtons

This library adds two custom widgets that adhere to the Material Design definition for toggle buttons.  Library is backwards compatible to API 9.

You can add the library with:

`compile 'com.anthonymandra:ToggleButtons:2.0.0'`

## Recent Changes

Version 2.0 adds backwards compatibility to API 9.  This changed the api for dividers slightly.
1. Divider is now in the custom namespace:  `app:divider`
2. Divider can no longer support tint.  You'll need to create custom drawbles.

## Basics

You can use <code>ToggleGroup</code> much like you would a <code>RadioGroup</code> and fill it with <code>ToggleButton</code>, or anything extending <code>CompoundButton</code>.  See the Sample for examples.

When no text is present they will use a custom draw that allows proper image alignment.  When text is present they will behave like a traditional <code>CompoundButton</code>, or the old android <code>ToggleButton</code>.

![image](https://cloud.githubusercontent.com/assets/4026030/22566665/2954ec68-e98d-11e6-9c23-765adeba4e74.png)

You can toggle exclusive selection or multi selection on a group and you can also allow unselecting (no selection at all).  Buttons support tint.  

## Sample

![screenshot_1500045520](https://user-images.githubusercontent.com/4026030/28218447-8ce95c32-6886-11e7-8a30-73cf679bb63f.png)

## Official Material Guidelines

![materialtoggle](https://cloud.githubusercontent.com/assets/4026030/21650177/6c1a7536-d2a4-11e6-8d9f-eb523a7bae8f.png)
