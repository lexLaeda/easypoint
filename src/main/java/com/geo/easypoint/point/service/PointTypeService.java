package com.geo.easypoint.point.service;

import com.geo.easypoint.common.mapper.EasyPointMapper;
import com.geo.easypoint.point.dto.PointTypeDto;
import com.geo.easypoint.point.repository.PointTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointTypeService {
    private final PointTypeRepository pointTypeRepository;

    public List<PointTypeDto> findAll() {

        return EasyPointMapper.toPointTypeDto(pointTypeRepository.findAll());
    }
}
