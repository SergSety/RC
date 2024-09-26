package by.nca.rc.models.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class NewsResponse {

    private Map<String, Object> newsResponse;
}
