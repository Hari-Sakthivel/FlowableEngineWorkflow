package in.co.promon.flowable.delegates;

import in.co.promon.flowable.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component("fileWritingProcess")
public class FileWritingProcess  implements JavaDelegate {

    @Autowired
    private RestTemplate restTemplate;

    private final String databaseServiceUrl = "http://localhost:8089/api/employees";

    @Override
    public void execute(DelegateExecution execution){

        Employee[] employeesArray = restTemplate.getForObject(databaseServiceUrl,Employee[].class);
        List<Employee> employees = Arrays.asList(employeesArray);

        String dateString = (String) execution.getVariable("date");

        Date filterDate;

        try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                filterDate = dateFormat.parse(dateString);
            }
        catch (ParseException e) {
            log.info("Invalid date format : {} ", dateString, e);
                throw new RuntimeException(e);
            }

        List<Employee> filteredEmployees = employees.stream().filter(employee -> filterDate.equals(employee.getHiredate())).collect(Collectors.toList());

        int batchSize = 10;

        int totalBatches = (int) Math.ceil((double) filteredEmployees.size()/batchSize);

        for(int i = 0 ; i < totalBatches ; i++){

            int formIndex = i * batchSize;

            int toIndex = Math.min(formIndex + batchSize , filteredEmployees.size());

            List<Employee> batch = filteredEmployees.subList(formIndex, toIndex);

            writeToFile(batch, filterDate, i + 1);
        }



    }

    private void writeToFile(List<Employee> employees, Date filterDate, int batchNumber) {

        try{

            SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String fileName = "Employees_batch_ "+batchNumber+"_"+fileDateFormat.format(filterDate)+".txt";

            Path filePath = Paths.get("C:\\Users\\Hariharan\\Documents\\files",fileName);

            try(FileWriter writer = new FileWriter(filePath.toFile())){

                for(Employee employee : employees){
                    writer.write(employee.toString()+"\n");
                }

            }

            log.info("Batch {} written to file: {}", batchNumber, fileName);


        }catch (Exception e){

            e.printStackTrace();

        }

    }
}
