package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.Dto;
import com.petproject.tasks.entity.EntityObj;

public interface TransformerDto <E extends EntityObj, D extends Dto> {
    D transform(E e);
}