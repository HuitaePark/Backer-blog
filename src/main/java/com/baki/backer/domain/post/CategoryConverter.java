package com.baki.backer.domain.post;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Category attribute) {
        return attribute == null ? null : attribute.intValue();
    }

    @Override
    public Category convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : Category.valueOf(dbData);
    }
}
