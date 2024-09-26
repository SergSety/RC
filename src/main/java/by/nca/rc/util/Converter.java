package by.nca.rc.util;

import by.nca.rc.dto.NewsDto;
import by.nca.rc.dto.RemarkDto;
import by.nca.rc.models.News;
import by.nca.rc.models.Remark;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class Converter {

    private final ModelMapper modelMapper;

    public Converter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public NewsDto convertToNewsDto(News news) {

        return modelMapper.map(news, NewsDto.class);
    }

    public RemarkDto convertToRemarkDto(Remark remark) {

        return modelMapper.map(remark, RemarkDto.class);
    }
}
