package com.example.mapper;

import com.example.domain.Image;
import com.example.dto.ImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImagesMapper {

    ImageDto imageToImageDto(Image image);

    Image imageDtoToImage(ImageDto imageDto);

    List<ImageDto> imagesToImageDtos(List<Image> images);
}
