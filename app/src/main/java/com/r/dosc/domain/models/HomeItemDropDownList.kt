package com.r.dosc.domain.models

sealed class HomeItemDropDownList(
    val name: String
) {
    class Share : HomeItemDropDownList("Share")
    class Delete : HomeItemDropDownList("Delete")
}