package com.r.dosc.domain.models

sealed class HomeItemDropDownList(
    val name: String
) {
    class Share : HomeItemDropDownList("Share")
    class Rename : HomeItemDropDownList("Rename")
    class Delete : HomeItemDropDownList("Delete")
}