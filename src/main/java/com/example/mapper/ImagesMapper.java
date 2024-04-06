package com.example.mapper;

import com.example.domain.ImageT;
import com.example.dto.Image;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImagesMapper {

  @Mapping(source = "id", target = "imageId")
  @Mapping(source = "name", target = "filename")
  @Mapping(source = "size", target = "size")
  Image imageTtoImage(ImageT imageT);

  List<Image> imagesToImagesDto(List<ImageT> imageTs);
}
