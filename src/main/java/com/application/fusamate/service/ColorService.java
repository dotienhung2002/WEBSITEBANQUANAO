package com.application.fusamate.service;

import com.application.fusamate.dto.ColorDto;
import com.application.fusamate.dto.UpdateColorDto;
import com.application.fusamate.entity.Brand;
import com.application.fusamate.entity.Color;
import com.application.fusamate.model.ColorSearchCriteriaModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ColorService {
    Color getColor(Integer id);
    Color createColor(ColorDto color) throws Exception;
    Color updateColor(UpdateColorDto updateColorDto, Integer id) throws Exception;
    Page<Color> getColors(ColorSearchCriteriaModel colorSearchCriteriaModel);

    List<Color> getAllColor();
}
