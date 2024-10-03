package in.co.promon.flowable.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;


@Data
public class Employee {

    private Long id;
    private String name;
    private String department;
    private Double salary;
    @JsonProperty("hiredate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date hiredate;

}
