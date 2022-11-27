package com.android.model.repository.ingredients.entity

data class SimpleIngredient(
   val id: Int,
   val name: String,
   val unit: String
){
   override fun toString(): String {
      return name
   }
}