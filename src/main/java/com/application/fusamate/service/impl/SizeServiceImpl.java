package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.SizeDto;
import com.application.fusamate.dto.UpdateSizeDto;
import com.application.fusamate.entity.Size;
import com.application.fusamate.model.SizeSearchCriteriaModel;
import com.application.fusamate.repository.SizeRepository;
import com.application.fusamate.repository.criteria.SizeCriteriaRepository;
import com.application.fusamate.service.SizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SizeServiceImpl implements SizeService {
    private final SizeRepository sizeRepository;
    private final SizeCriteriaRepository sizeCriteriaRepository;

    @Override
    public Size getSize(Integer id) {
        return sizeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Size does not exist!"));
    }

    @Override
    public Size createSize(SizeDto sizeDto) throws Exception {
        Size size = new Size();

        if (!sizeRepository.findByNameLike(sizeDto.getName().trim()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_SIZE);
        }

        BeanUtils.copyProperties(sizeDto, size);
        size.setName(size.getName().trim());
        size.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return sizeRepository.save(size);
    }

    @Override
    public Size updateSize(UpdateSizeDto updateSizeDto, Integer id) throws Exception {
        Size size = sizeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Size does not exist!"));

        if (!sizeRepository.findByNameLike(updateSizeDto.getName().trim()).isEmpty()) {
            if (!updateSizeDto.getName().trim().equalsIgnoreCase(size.getName().trim()))
                throw new Exception(Constants.DUPLICATED_SIZE);
        }

        BeanUtils.copyProperties(updateSizeDto, size);
        size.setName(size.getName().trim());
        size.setUpdatedAt(new Date());
        size.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return sizeRepository.save(size);
    }

    @Override
    public Page<Size> getColors(SizeSearchCriteriaModel sizeSearchCriteriaModel) {
        return sizeCriteriaRepository.findAllWithFilters(sizeSearchCriteriaModel);
    }
    @Override
    public List<Size> getAllSize() {
        return sizeRepository.findAll();
    }
}
