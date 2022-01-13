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

        // connects to GraphDataSource and populates the Graph object
        GraphDataAccess graphDataAccess = new GraphDataAccess();
        graphDataAccess.populateGraph();

        // TODO faire la partie UserData
        UserDataDataAccess userDataDataAccess = new UserDataDataAccess();

        //graphDataAccess.printNumOfRows();
        //userDataDataAccess.printNumOfRows();
    }

}
