package com.ddi.core.newssource.property

import com.ddi.core.common.domain.property.Url
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
@AttributeOverride(name = "_value", column = Column(name = "url", nullable = false, length = 255))
class NewsSourceUrl : Url {
    constructor() : super()
    constructor(url: String) : super(url)
}