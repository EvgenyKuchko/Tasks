package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.Dto;
import com.petproject.tasks.entity.EntityObj;

public interface TransformerEntity <E extends EntityObj, D extends Dto> {
    E transform(D d);
}