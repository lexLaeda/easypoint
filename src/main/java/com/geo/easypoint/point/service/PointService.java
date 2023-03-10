package com.geo.easypoint.point.service;

import com.geo.easypoint.area.dto.AreaDto;
import com.geo.easypoint.area.entity.AreaStructure;
import com.geo.easypoint.area.repository.AreaStructureRepository;
import com.geo.easypoint.common.exception.BadRequestException;
import com.geo.easypoint.common.exception.NotFoundException;
import com.geo.easypoint.common.mapper.EasyPointMapper;
import com.geo.easypoint.common.utill.UserUtils;
import com.geo.easypoint.point.dto.PointDto;
import com.geo.easypoint.point.dto.PointStates;
import com.geo.easypoint.point.dto.request.PointCreateRequestDto;
import com.geo.easypoint.point.entity.Point;
import com.geo.easypoint.point.entity.PointType;
import com.geo.easypoint.point.repository.PointRepository;
import com.geo.easypoint.point.repository.PointStateRepository;
import com.geo.easypoint.point.repository.PointTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final PointTypeRepository pointTypeRepository;
    private final PointStateRepository pointStateRepository;
    private final AreaStructureRepository areaStructureRepository;

    @Transactional(readOnly = true)
    public List<PointDto> findAll() {
        return EasyPointMapper.toPointDto(pointRepository.findAll(), this::findAll);
    }

    @Transactional
    public PointDto createPoint(PointCreateRequestDto request) {
        Point point = pointRepository.saveAndFlush(
                EasyPointMapper.toPoint(
                        request,
                        UserUtils.getCurrentEmployee(),
                        findPointType(request.pointTypeId()),
                        pointStateRepository.findByCode(PointStates.CREATED),
                        NotFoundException.orElseThrow(request.pointAreaId(), AreaStructure.class, areaStructureRepository::findById)
                )
        );
        return EasyPointMapper.toPointDto(point);
    }

    public void delete(Long pointId) {
        Point point = findPoint(pointId);
        if (isNotPossibleToDelete(point.getPointState().getCode())) {
            throw new BadRequestException(String.format(
                    "Point %s can't be deleted. It has state %s, that is not allowed for deletion",
                    point.getName(), point.getPointState().getName())
            );
        }
        pointRepository.delete(point);
    }

    private Point findPoint(Long id) {
        return pointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, Point.class));
    }

    private PointType findPointType(Long id) {
        return pointTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, PointType.class));
    }

    private boolean isNotPossibleToDelete(PointStates pointState) {
        return PointStates.CREATED != pointState;
    }

    private List<AreaDto> findAll(Point point) {
        List<AreaDto> areaDtos = new ArrayList<>();
        AreaStructure areaStructure = point.getAreaStructure();
        while (areaStructure != null) {
            areaDtos.add(EasyPointMapper.toAreaDto(areaStructure));
            areaStructure = areaStructure.getParent();
        }
        return areaDtos;
    }
}
