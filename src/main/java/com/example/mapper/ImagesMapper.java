package com.example.mapper;

import com.example.domain.ImageT;
import com.example.dto.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImagesMapper {

    @Mapping(source = "id", target = "imageId")
    @Mapping(source = "name", target = "filename")
    @Mapping(source = "size", target = "size")
    Image imageTToImage(ImageT imageT);

    List<Image> imagesToImagesDto(List<ImageT> imageTs);
}
