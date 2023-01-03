package com.android.model.repository.dashboard

import androidx.paging.PagingData
import com.android.model.di.IoDispatcher
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.dashboard.entity.BudgetAndChart
import com.android.model.repository.dashboard.entity.PaycheckEntity
import com.android.model.utils.Results
import com.android.model.utils.wrapBackendExceptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DashboardRepository @Inject constructor(
   private val dashboardSource: DashboardSource,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository() {

   fun getBudgetInfo(year: String): Flow<Results<BudgetAndChart>> = flow {
      val result = coroutineScope {
         wrapBackendExceptions {
            val budgetInfo = async { dashboardSource.getBudgetInfo() }
            val chartInfo = async { dashboardSource.getChatInfo(year) }
            BudgetAndChart(
               budgetInfoEntity = budgetInfo.await(),
               chartInfoEntity = chartInfo.await()
            )
         }
      }
      emit(result)
   }.flowOn(ioDispatcher).asResult()

   fun getPaycheckList(): Flow<PagingData<PaycheckEntity>> = getPagerData {
      val dataLoader: DataLoader<PaycheckEntity> = { pageIndex ->
         dashboardSource.getPaycheckList(pageIndex = pageIndex)
      }
      BasePageSource(
         loader = dataLoader, defaultPageSize = DEFAULT_PAGE_SIZE
      )
   }

   fun getExpenditureList(): Flow<PagingData<PaycheckEntity>> = getPagerData {
      val dataLoader: DataLoader<PaycheckEntity> = { pageIndex ->
         dashboardSource.getExpenditureList(pageIndex = pageIndex)
      }
      BasePageSource(
         loader = dataLoader, defaultPageSize = DEFAULT_PAGE_SIZE
      )
   }
}