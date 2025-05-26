package com.ddi.scraperss.model

import java.time.Instant

data class RssItem(
    val title: String,
    val url: String,
    val content: String?,
    val publishedDate: Instant?,
)