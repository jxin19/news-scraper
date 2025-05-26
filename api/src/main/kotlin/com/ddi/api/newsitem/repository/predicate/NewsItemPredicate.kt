package com.ddi.api.newsitem.repository.predicate

import com.ddi.api.newsitem.dto.NewsItemSearchServiceRequest
import com.ddi.core.newsitem.QNewsItemView
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate

class NewsItemPredicate {
    companion object {
        fun search(request: NewsItemSearchServiceRequest): Predicate {
            val qNewsItemView = QNewsItemView.newsItemView
            val builder = BooleanBuilder()

            request.title?.let {
                builder.and(qNewsItemView.title.like("%$it%"))
            }

            request.sourceId?.let {
                builder.and(qNewsItemView.sourceId.eq(request.sourceId))
            }

            request.keywordId?.let {
                builder.and(qNewsItemView.keywordId.eq(request.keywordId))
            }

            return builder
        }
    }
}