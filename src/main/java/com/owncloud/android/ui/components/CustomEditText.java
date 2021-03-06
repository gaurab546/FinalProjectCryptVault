/**
 * Nextcloud Android client application
 *
 * @author Mario Danic
 * Copyright (C) 2017 Mario Danic
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.owncloud.android.authentication.AuthenticatorActivity;

/**
 * Custom edit text to support fixed suffix or prefix
 */
public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    private Rect fixedRect = new Rect();
    private String fixedText = "";
    private boolean isPrefixFixed;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        String serverInputType = getResources().getString(com.owncloud.android.R.string.server_input_type);

        if (serverInputType.equals(AuthenticatorActivity.DIRECTORY_SERVER_INPUT_TYPE)) {
            isPrefixFixed = true;
            fixedText = getResources().getString(com.owncloud.android.R.string.server_url) + "/";
        } else if (serverInputType.equals(AuthenticatorActivity.SUBDOMAIN_SERVER_INPUT_TYPE)) {
            isPrefixFixed = false;
            fixedText = "." + getResources().getString(com.owncloud.android.R.string.server_url);
        }

        if (TextUtils.isEmpty(fixedText)) {
            setHint(com.owncloud.android.R.string.auth_host_url);
        }
    }

    public String getFullServerUrl() {
        if (TextUtils.isEmpty(fixedText)
                || getText().toString().startsWith(AuthenticatorActivity.HTTP_PROTOCOL)
                || getText().toString().startsWith(AuthenticatorActivity.HTTPS_PROTOCOL)) {
            return getText().toString();
        } else if (isPrefixFixed) {
            return (getResources().getString(com.owncloud.android.R.string.server_url) + "/" + getText().toString());
        } else {
            return (getText().toString() + "." + getResources().getString(com.owncloud.android.R.string.server_url));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!TextUtils.isEmpty(fixedText)) {
            getPaint().getTextBounds(fixedText, 0, fixedText.length(), fixedRect);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!getText().toString().startsWith(AuthenticatorActivity.HTTP_PROTOCOL)
                && !getText().toString().startsWith(AuthenticatorActivity.HTTPS_PROTOCOL)
                && !TextUtils.isEmpty(fixedText)) {
            if (isPrefixFixed) {
                canvas.drawText(fixedText,
                        super.getCompoundPaddingLeft(),
                        getBaseline(),
                        getPaint());
            } else {
                canvas.drawText(fixedText, super.getCompoundPaddingLeft()
                        + getPaint().measureText(getText().toString()), getBaseline(), getPaint());
            }
        }
    }

    @Override
    public int getCompoundPaddingLeft() {
        if (!TextUtils.isEmpty(fixedText) && isPrefixFixed) {
            return super.getCompoundPaddingLeft() + fixedRect.width();
        } else {
            return super.getCompoundPaddingLeft();
        }
    }
}
