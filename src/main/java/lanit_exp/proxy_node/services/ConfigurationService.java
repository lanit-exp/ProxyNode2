package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.models.ConfigurationModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

@Service
public class ConfigurationService {

    @Value("${node.conf.fileName}")
    private String confFileName;

    private static final String DEFAULT_CONFIG = """
            # Файл конфигурации Proxy Node
            # Необходимо заполнить все параметры описанные ниже
            
            # Уникальный id ноды, используется для точной идентификации ноды при проксировании запросов
            # Генерируется автоматически при создании файла
                node_id=%s

            # Адрес сервера Proxy Hub (указать только ip или адрес хоста: 123.456.7.8 или proxyhub.lanit.ru)            
                server_url=
                
            # Номер порта сервера Proxy Hub    
                server_port=4448

            # IP адрес драйвера (желательно запускать Proxy Node там же, где и драйвер)           
                driver_url=127.0.0.1
                
            # Порт драйвера    
                driver_port=9999
            
            """;




    public ConfigurationModel getConfiguration(){
        if(new File(confFileName).exists()){

            try (InputStream inputStream = new FileInputStream(confFileName)) {
                Properties properties = new Properties();
                properties.load(inputStream);

                return getConfModelFromProperties(properties);

            } catch (IOException e) {
                throw new RuntimeException("Ошибка чтения файла конфигурации: " + confFileName,e);
            }

        } else {
            writeDefaultConfigToFile();
            return null;
        }
    }

    private ConfigurationModel getConfModelFromProperties(Properties properties){
        ConfigurationModel configurationModel = new ConfigurationModel();

        String nodeId = checkNotEmptyValue(properties.getProperty("node_id"),"node_id");
        String serverUrl = checkNotEmptyValue(properties.getProperty("server_url"),"server_url");
        Integer serverPort = checkNotEmptyIntValue(properties.getProperty("server_port"), "server_port");
        String driverUrl = checkNotEmptyValue(properties.getProperty("driver_url"),"driver_url");
        Integer driverPort = checkNotEmptyIntValue(properties.getProperty("driver_port"),"driver_port");

        configurationModel.setNodeId(nodeId);
        configurationModel.setServerUrl(serverUrl);
        configurationModel.setServerPort(serverPort);
        configurationModel.setDriverUrl(driverUrl);
        configurationModel.setDriverPort(driverPort);

        return configurationModel;
    }

    private String checkNotEmptyValue(String value, String valueName){

        if (value == null || value.isEmpty()){
            throw new RuntimeException("Отсутствует или не заполнено значение параметра '%s' в файле конфигурации '%s'"
                    .formatted(valueName, confFileName));
        }

        return value;
    }

    private Integer checkNotEmptyIntValue(String value, String valueName){

        String intValue = checkNotEmptyValue(value, valueName);

        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Некорректно заполнено значение параметра '%s' в файле конфигурации '%s'"
                    .formatted(valueName, confFileName), e);
        }
    }


    private String getDefaultConfig(){
        return DEFAULT_CONFIG.formatted(UUID.randomUUID());
    }

    private void writeDefaultConfigToFile(){
        try {
            Files.writeString(Paths.get(confFileName), getDefaultConfig());
            printCreateConfFileMessage();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать дефолтную конфигурацию в файл: " + confFileName,  e);
        }
    }


    private void printCreateConfFileMessage(){
        String message = """
                
                
                
                    ==============================================================================
                        Создан файл '%s' c дефолтной конфигурацией!
                        Отредактируйте файл, заполнив все параметры, и перезапустите приложение.   
                    ==============================================================================
                    
                    
                    
                """.formatted(confFileName);

        System.out.println(message);
    }

}
