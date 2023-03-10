package com.application.fusamate.service.impl;

import com.application.fusamate.configuration.Constants;
import com.application.fusamate.dto.MadeInDto;
import com.application.fusamate.dto.UpdateMadeInDto;
import com.application.fusamate.entity.MadeIn;
import com.application.fusamate.model.MadeInSearchCriteriaModel;
import com.application.fusamate.repository.MadeInRepository;
import com.application.fusamate.repository.criteria.MadeInCriteriaRepository;
import com.application.fusamate.service.MadeInService;
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
public class MadeInServiceImpl implements MadeInService {
    private final MadeInRepository madeInRepository;
    private final MadeInCriteriaRepository madeInCriteriaRepository;

    @Override
    public MadeIn getMadeIn(Integer id) {
        return madeInRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Made In does not exist!"));
    }

    @Override
    public MadeIn createMadeIn(MadeInDto madeInDto) throws Exception {
        MadeIn madeIn = new MadeIn();

        if (!madeInRepository.findByNameLike(madeInDto.getName().trim()).isEmpty()) {
            throw new Exception(Constants.DUPLICATED_MADE_IN);
        }

        BeanUtils.copyProperties(madeInDto, madeIn);
        madeIn.setName(madeIn.getName().trim());
        madeIn.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return madeInRepository.save(madeIn);
    }


    @Override
    public MadeIn updateMadeIn(UpdateMadeInDto madeInDto, Integer id) throws Exception {
        MadeIn madeIn = madeInRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Made in does not exist!"));

        if (!madeInRepository.findByNameLike(madeInDto.getName().trim()).isEmpty()) {
            if (!madeInDto.getName().trim().equalsIgnoreCase(madeIn.getName().trim()))
                throw new Exception(Constants.DUPLICATED_MADE_IN);
        }

        BeanUtils.copyProperties(madeInDto, madeIn);
        madeIn.setName(madeIn.getName().trim());
        madeIn.setUpdatedAt(new Date());
        madeIn.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        return madeInRepository.save(madeIn);
    }

    @Override
    public Page<MadeIn> getMadeIns(MadeInSearchCriteriaModel madeInSearchCriteriaModel) {
        return madeInCriteriaRepository.findAllWithFilters(madeInSearchCriteriaModel);
    }

    @Override
    public List<MadeIn> getAllMadeIn() {
        return madeInRepository.findAll();
    }
}
