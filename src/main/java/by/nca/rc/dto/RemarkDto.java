package by.nca.rc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemarkDto {

    private String text;
    private Date creationDate;
    private Date lastEditDate;
}
