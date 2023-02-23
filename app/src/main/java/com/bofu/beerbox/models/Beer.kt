package com.bofu.beerbox.models

data class Beer (
    val id: Short,
    val name: String,
    val tagline: String,
    val image_url: String,
    val description: String
)