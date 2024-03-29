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

package it.ministerodellasalute.immuni.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import it.ministerodellasalute.immuni.R
import it.ministerodellasalute.immuni.extensions.lifecycle.AppLifecycleObserver
import it.ministerodellasalute.immuni.extensions.notifications.PushNotificationManager
import it.ministerodellasalute.immuni.logic.exposure.ExposureManager
import it.ministerodellasalute.immuni.logic.user.UserManager
import it.ministerodellasalute.immuni.ui.home.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val context: Context,
    private val pushNotificationManager: PushNotificationManager,
    private val exposureManager: ExposureManager,
    appLifecycleObserver: AppLifecycleObserver,
    private val userManager: UserManager
) : ViewModel() {

    val homeListModel = MutableLiveData<List<HomeItemType>>(listOf())
    val exposureStatus = exposureManager.exposureStatus.asLiveData()

    init {
        combine(
            exposureManager.isBroadcastingActive,
            exposureManager.exposureStatus,
            appLifecycleObserver.isActive.filter { it }
        ) { broadcastingIsActive, status, isActive ->
            Triple(broadcastingIsActive, status, isActive)
        }.onEach { (broadcastingIsActive, _, _) ->
            val protectionActive = when (broadcastingIsActive) {
                null -> null
                else -> broadcastingIsActive && pushNotificationManager.areNotificationsEnabled()
            }
            homeListModel.postValue(homeListModel(protectionActive))
        }.launchIn(viewModelScope)
    }

    private fun homeListModel(protectionActive: Boolean?): List<HomeItemType> {
        val items = mutableListOf<HomeItemType>()
        protectionActive?.let {
            items.add(ProtectionCard(it, exposureManager.exposureStatus.value))
        }
        userManager.showDGCHome.value.let {
            if (it == true) {
                items.add(SectionHeader(context.getString(R.string.home_your_eu_dgc)))
                items.add(CertificateCard)
            }
        }
        items.add(SectionHeader(context.getString(R.string.home_what_do_you_want_today)))
        items.add(GreenPassCard)
        items.add(ReportPositivityCard)
        items.add(CountriesOfInterestCard)
        items.add(SectionHeader(context.getString(R.string.home_view_info_header_title)))
        items.add(HowItWorksCard)
        items.add(SelfCareCard)
        items.add(DisableExposureApi(protectionActive ?: false))
        return items
    }

    fun updateHomeListModel(addedHomeDGC: Boolean?) {
        if (homeListModel.value!!.isEmpty()) return
        val homeItems = homeListModel.value!!.toMutableList()
        val indexTitleCertificate = homeItems.indexOf(SectionHeader(context.getString(R.string.home_your_eu_dgc)))
        if (addedHomeDGC !== null && addedHomeDGC) {
            if (indexTitleCertificate == -1) {
                val indexTitle = homeItems.indexOf(SectionHeader(context.getString(R.string.home_what_do_you_want_today)))
                homeItems.add(indexTitle, SectionHeader(context.getString(R.string.home_your_eu_dgc)))
                homeItems.add(indexTitle + 1, CertificateCard)
                homeListModel.postValue(homeItems)
            }
        } else {
            if (indexTitleCertificate != -1) {
                homeItems.remove(SectionHeader(context.getString(R.string.home_your_eu_dgc)))
                homeItems.remove(CertificateCard)
                homeListModel.postValue(homeItems)
            }
        }
    }
}
