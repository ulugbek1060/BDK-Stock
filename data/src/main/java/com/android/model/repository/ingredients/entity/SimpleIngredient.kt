package com.android.model.repository.ingredients.entity

data class SimpleIngredient(
   val id: Long,
   val name: String? = null,
   val unit: String? = null
) : java.io.Serializable {

   var amount: String? = null
   override fun toString(): String {
      return name ?: "Undefined ingredient name!!!"
   }

}