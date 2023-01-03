package com.android.source.network.dashboard

import com.android.model.repository.dashboard.DashboardSource
import com.android.model.repository.dashboard.entity.BudgetInfoEntity
import com.android.model.repository.dashboard.entity.ChartInfoEntity
import com.android.model.repository.dashboard.entity.PaycheckEntity
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.dashboard.entity.TableRequestEntity
import javax.inject.Inject

class DashboardSourceImpl @Inject constructor(
   private val dashboardApi: DashboardApi
) : BaseNetworkSource(), DashboardSource {

   override suspend fun getBudgetInfo(): BudgetInfoEntity = wrapNetworkException {
      val response = dashboardApi.getSumInfo()
      BudgetInfoEntity(
         totalSumInCard = response.totalSumInCard,
         totalSumInCash = response.totalSumInCash,
         expenditureInCard = response.expenditureInCard,
         expenditureInCash = response.expenditureInCash
      )
   }

   override suspend fun getChatInfo(year: String): ChartInfoEntity = wrapNetworkException {
      val body = TableRequestEntity(year = year)
      val response = dashboardApi.getTableInfo(body)
      ChartInfoEntity(
         january = response.january,
         february = response.february,
         march = response.march,
         april = response.april,
         may = response.may,
         june = response.june,
         july = response.july,
         avgust = response.avgust,
         september = response.september,
         october = response.october,
         november = response.november,
         december = response.december,
      )
   }

   override suspend fun getPaycheckList(pageIndex: Int): List<PaycheckEntity> =
      wrapNetworkException {
         dashboardApi.paycheckList(pageIndex).data.map {
            PaycheckEntity(
               id = it.id,
               orderId = it.orderId,
               name = it.name,
               amount = it.amount,
               comment = it.comment,
               payType = it.payType,
               createdAt = it.createdAt
            )
         }
      }

   override suspend fun getExpenditureList(pageIndex: Int): List<PaycheckEntity> =
      wrapNetworkException {
         dashboardApi.expenditureList(pageIndex).data.map {
            PaycheckEntity(
               id = it.id,
               orderId = it.orderId,
               name = it.name,
               amount = it.amount,
               comment = it.comment,
               payType = it.payType,
               createdAt = it.createdAt
            )
         }
      }
}