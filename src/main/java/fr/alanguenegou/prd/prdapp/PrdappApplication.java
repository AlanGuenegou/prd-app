package fr.alanguenegou.prd.prdapp;

import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.dbaccess.UserDataDataAccess;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PrdappApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrdappApplication.class, args);

        GraphDataAccess graphDataAccess = new GraphDataAccess();
        UserDataDataAccess userDataDataAccess = new UserDataDataAccess();
        graphDataAccess.printNumOfRows();
        userDataDataAccess.printNumOfRows();
    }

}
