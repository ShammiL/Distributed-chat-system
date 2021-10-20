package org.example.services.server;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigReaderService {

    public void readFile(String serverConfig, String myServerId) {
        try {
            ColumnPositionMappingStrategy<ServerInfo> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(ServerInfo.class);
            CsvToBean<ServerInfo> csvToBean = new CsvToBean<>();
            ServerState.getInstance().setServersList(
                    csvToBean.parse(strategy, new CSVReader(new FileReader(serverConfig), '\t')),
                    myServerId);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
