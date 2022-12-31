package com.android.model.repository.account.entity

data class PermsWithJobIdEntity(
   val jobId: Int,
   val permissions: Set<UserPermissionEntity>
)