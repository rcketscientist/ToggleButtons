 [ ![Download](https://api.bintray.com/packages/rcketscientist/maven/ToggleButtons/images/download.svg) ](https://bintray.com/rcketscientist/maven/ToggleButtons/_latestVersion)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)]()

#ToggleButtons

This library adds two custom widgets that adhere to the Material Design definition for toggle buttons.  

<p>You can add the library with:</p>
`compile 'com.anthonymandra:ToggleButtons:1.1.0'`
<p>You can use <code>ToggleGroup</code> much like you would a <code>RadioGroup</code> and fill it with <code>ToggleButton</code>, or anything extending <code>CompoundButton</code>.  See the Sample for examples.</p>

<p>When no text is present they will use a custom draw that allows proper image alignment.  When text is present they will behave like a traditional <code>CompoundButton</code>, or the old android <code>ToggleButton</code>.</p>
![image](https://cloud.githubusercontent.com/assets/4026030/22566665/2954ec68-e98d-11e6-9c23-765adeba4e74.png)

You can toggle exclusive selection or multi selection on a group and you can also allow unselecting (no selection at all).  Buttons support tint.  

Library is currently API 21+, but can be ported to 11+ (possibly 7 or 9).

![screenshot_1484163201](https://cloud.githubusercontent.com/assets/4026030/21863352/63fa290c-d83d-11e6-9210-4925986aab51.png)

<hr>
<b>Official Material Guidelines</b>

![materialtoggle](https://cloud.githubusercontent.com/assets/4026030/21650177/6c1a7536-d2a4-11e6-8d9f-eb523a7bae8f.png)
