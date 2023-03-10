package com.application.fusamate.service;

import com.application.fusamate.dto.SizeDto;
import com.application.fusamate.dto.UpdateSizeDto;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.entity.Size;
import com.application.fusamate.model.SizeSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SizeService {
    Size getSize(Integer id);
    Size createSize(SizeDto sizeDto) throws Exception;
    Size updateSize(UpdateSizeDto updateSizeDto, Integer id) throws Exception;
    Page<Size> getColors(SizeSearchCriteriaModel sizeSearchCriteriaModel);
    List<Size> getAllSize();

}
