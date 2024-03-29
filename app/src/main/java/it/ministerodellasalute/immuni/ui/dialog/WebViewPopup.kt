/*
 * Copyright (C) 2020 Presidenza del Consiglio dei Ministri.
 * Please refer to the AUTHORS file for more information.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package it.ministerodellasalute.immuni.ui.dialog

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.navArgs
import it.ministerodellasalute.immuni.R
import it.ministerodellasalute.immuni.extensions.utils.ScreenUtils
import kotlinx.android.synthetic.main.web_view_dialog.*

class WebViewPopup : PopupDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setContentLayout(R.layout.web_view_dialog)

        val args = navArgs<WebViewPopupArgs>()
        webView.webViewClient = WebViewClient()
        webView.loadUrl(args.value.url)

        // force resize the web view to fix bottom sheet height
        webView.updateLayoutParams {
            height = (ScreenUtils.getScreenHeight(requireContext()) * 0.9f).toInt()
        }
    }
}
