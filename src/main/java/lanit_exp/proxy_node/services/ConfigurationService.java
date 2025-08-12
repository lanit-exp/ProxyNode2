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

    private ConfigurationModel configurationModel;

    @Value("${node.conf.fileName}")
    private String confFileName;

    private static final String CREATE_CONFIG_MESSAGE = """
                 
                
                
                    ==============================================================================
                        Создан файл '%s' c дефолтной конфигурацией!
                        Отредактируйте файл, заполнив все параметры, и перезапустите ProxyNode.   
                    ==============================================================================
                    
                    
                    
            """;


    private static final String DEFAULT_CONFIG = """
            ############################################################################################################
            
                # Файл конфигурации Proxy Node
                # Необходимо заполнить все параметры описанные ниже
            
            ############################################################################################################
            
            # Уникальный id ноды, используется для точной идентификации ноды при проксировании запросов
            # Генерируется автоматически при создании файла
            
                node_id=%1$s

            # Теги ноды, используемы для фильтрации всех запущенных нод.
            # Применяется при запуске не на конкретной ноде,а на любой свободной.
            # Теги указывать через запятую (например: flanium,regress,chrome). (параметр опциональный)
            
                node_tags=

            ############################################################################################################
            
            # Адрес сервера Proxy Hub, указать только ip или адрес хоста (без http/https).
            # Например: 123.456.7.8 или proxyhub.lanit.ru
                        
                server_url=
                
            # Номер порта сервера Proxy Hub (0 - если порт не нужен)
                
                server_port=4448
                
            # Поддержка https/wss (значения: true/false)
                
                https=true   

            ############################################################################################################
            
            # IP адрес драйвера (желательно запускать Proxy Node там же, где и драйвер)
                       
                driver_url=127.0.0.1
                
            # Порт драйвера
                
                driver_port=9999
                
            ##################################     Как подключаться    #################################################
            
            # ProxyHub имеет два режима проксирования:
            
            # 1. id - Подключение к конкретной ноде по id. Запросы будут проксироваться без учета занятости ноды.
            #    Данный режим рекомендуется использовать для отладочного запуска на локальном компьютере.
            #    Для использования данного режима необходимо в качестве удаленного сервера указать следующий url:
            
            #   https://<server_url>:<server_port>/proxy/id/%1$s
            
            # 2. tag - в данном режиме будет происходить запуск на случайной свободной ноде, у которой есть указанный при запуске тег.
            #    Данный режим рекомендуется использовать для удаленного запуска.
            #    Для использования данного режима необходимо в качестве удаленного сервера указать следующий url, содержащий необходимый тег:
            
            #   https://<server_url>:<server_port>/proxy/tag/<tag1>
            
            """;


    public ConfigurationModel getConfiguration(){
        if (configurationModel == null) configurationModel = readConfiguration();
        return configurationModel;
    }

    private ConfigurationModel readConfiguration(){
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
        String nodeTags = properties.getProperty("node_tags", "");
        String serverUrl = checkNotEmptyValue(properties.getProperty("server_url"),"server_url");
        Integer serverPort = getIntValue(properties.getProperty("server_port"), "server_port");
        Boolean https = Boolean.parseBoolean(properties.getProperty("https", "false"));
        String driverUrl = checkNotEmptyValue(properties.getProperty("driver_url"),"driver_url");
        Integer driverPort = getIntValue(properties.getProperty("driver_port"),"driver_port");

        configurationModel.setNodeId(nodeId);
        configurationModel.setTags(nodeTags);
        configurationModel.setServerUrl(serverUrl);
        configurationModel.setServerPort(serverPort);
        configurationModel.setHttps(https);
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

    private Integer getIntValue(String value, String valueName){

        String intValue = (value == null || value.isEmpty()) ? "0" : value;

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
            System.out.printf((CREATE_CONFIG_MESSAGE) + "%n", confFileName);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать дефолтную конфигурацию в файл: " + confFileName,  e);
        }
    }


}
