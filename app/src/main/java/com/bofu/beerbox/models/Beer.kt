package com.bofu.beerbox.models

data class Beer (
    val id: Short,
    val name: String,
    val tagline: String,
    val description: String,
    val image_url: String
)