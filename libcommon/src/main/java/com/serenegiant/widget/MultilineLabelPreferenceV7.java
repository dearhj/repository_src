package com.serenegiant.widget;
/*
 * libcommon
 * utility/helper classes for myself
 *
 * Copyright (c) 2014-2018 saki t_saki@serenegiant.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class MultilineLabelPreferenceV7 extends Preference {
	public MultilineLabelPreferenceV7(final Context context) {
		super(context);
	}

	public MultilineLabelPreferenceV7(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public MultilineLabelPreferenceV7(final Context context, final AttributeSet attrs, final int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onBindViewHolder(final PreferenceViewHolder holder) {
		super.onBindViewHolder(holder);
//		if (DEBUG) Log.w(TAG, "onBindView:");
/*		RelativeLayout parent = null;
		final ViewGroup group = (ViewGroup)view;
		for (int i = group.getChildCount() - 1; i >= 0; i--) {
			final View v = group.getChildAt(i);
			if (v instanceof RelativeLayout) {
				parent = (RelativeLayout)v;
				break;
			}
		} */
		try {
			final TextView summary = (TextView)holder.findViewById(android.R.id.summary);
			summary.setSingleLine(false);
		} catch (final Exception e) {
		}
	}
}
