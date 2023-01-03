package com.android.model.repository.dashboard

import com.android.model.repository.dashboard.entity.BudgetInfoEntity
import com.android.model.repository.dashboard.entity.ChartInfoEntity
import com.android.model.repository.dashboard.entity.PaycheckEntity

interface DashboardSource {

   suspend fun getBudgetInfo(): BudgetInfoEntity

   suspend fun getChatInfo(year: String): ChartInfoEntity

   suspend fun getPaycheckList(pageIndex: Int): List<PaycheckEntity>

   suspend fun getExpenditureList(pageIndex: Int): List<PaycheckEntity>
}