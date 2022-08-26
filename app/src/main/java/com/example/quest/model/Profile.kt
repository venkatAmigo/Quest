package com.example.quest.model

data class Profile(
    val content: User,
    val meta: List<ListMeta>
) {
}

data class User(
    val id:   String,
    val email:   String,
    val firstName:   String? = null,
    val lastName:   String? = null,
    val nickName:   String? = null,
    val avatar:   String? = null,
    val city:   String? = null,
    val completedQuests:   List<Quest>? = null,
    val userQuests:   List<Quest>? = null,
    val authorRating: Int? = null,
    val userRating:Int? = null,
    val achievements:   List<Achievement>? = null
) {
}
data class Achievement (
    val id:   Int,
    val name:   String,
    val description:   String? = null,
    val icon:   String
) {
}
data class QuestsListItem (
    val id:   Int,
    val name:   String,
    val mainPhoto:   String,
    val rating:   Double,
    val authorName:   String,
    val category: QuestCategory
) {
}