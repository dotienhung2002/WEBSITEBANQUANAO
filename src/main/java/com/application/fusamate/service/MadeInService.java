package com.application.fusamate.service;

import com.application.fusamate.dto.MadeInDto;
import com.application.fusamate.dto.UpdateMadeInDto;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.model.MadeInSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MadeInService {
    MadeIn getMadeIn(Integer id);
    MadeIn createMadeIn(MadeInDto madeInDto) throws Exception;
    MadeIn updateMadeIn(UpdateMadeInDto madeInDto, Integer id) throws Exception;
    Page<MadeIn> getMadeIns(MadeInSearchCriteriaModel madeInSearchCriteriaModel);
    List<MadeIn> getAllMadeIn();

}

