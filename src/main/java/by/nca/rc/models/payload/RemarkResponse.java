package by.nca.rc.models.payload;

import by.nca.rc.dto.NewsDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RemarkResponse {

    private NewsDto news;
    private Map<String, Object> remark;
}
