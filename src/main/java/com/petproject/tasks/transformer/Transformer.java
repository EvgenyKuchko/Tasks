package com.petproject.tasks.transformer;

import com.petproject.tasks.dto.Dto;
import com.petproject.tasks.entity.EntityObj;

public interface Transformer<E extends EntityObj, D extends Dto> extends TransformerDto<E, D>, TransformerEntity<E, D> {
}