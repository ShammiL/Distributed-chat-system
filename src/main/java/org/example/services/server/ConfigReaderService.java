package org.example.services.server;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigReaderService {

    private Logger logger = Logger.getLogger(ConfigReaderService.class);

    public void readFile(String serverConfig, String myServerId) {
        try {
            ColumnPositionMappingStrategy<ServerInfo> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(ServerInfo.class);
            CsvToBean<ServerInfo> csvToBean = new CsvToBean<>();
            ServerState.getInstance().setServersList(
                    csvToBean.parse(strategy, new CSVReader(new FileReader(serverConfig), '\t')),
                    myServerId);
            logger.info("Config file reading success");

        } catch (FileNotFoundException e) {
            logger.error("Read config file due to: " + e.getMessage());
        }
    }
}
