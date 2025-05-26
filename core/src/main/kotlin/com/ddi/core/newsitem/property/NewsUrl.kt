package com.ddi.core.newsitem.property

import com.ddi.core.common.domain.property.Url
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@AttributeOverride(name = "_value", column = Column(name = "url", nullable = false, length = 1000))
class NewsUrl : Url {
    constructor() : super()
    constructor(url: String) : super(url)

}
