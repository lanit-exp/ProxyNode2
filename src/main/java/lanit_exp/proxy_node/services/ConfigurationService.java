package lanit_exp.proxy_node.services;

import lanit_exp.proxy_node.helpers.CollectionHelper;
import lanit_exp.proxy_node.models.ConfigurationModel;
import lanit_exp.proxy_node.models.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        
            #__________________________________  Настройки ноды --------------------------------------------------------
                        
            # Уникальный id ноды, используется для точной идентификации ноды при проксировании запросов
            # Генерируется автоматически при создании файла
                        
                node_id=%1$s

            # Теги ноды, используемы для фильтрации всех запущенных нод.
            # Применяется при запуске не на конкретной ноде,а на любой свободной.
            # Теги указывать через запятую (например: flanium,regress,chrome). (параметр опциональный)
                        
                node_tags=

            #__________________________________  Настройки хаба --------------------------------------------------------
                        
            # Адрес сервера Proxy Hub, указать только ip или адрес хоста (без http/https).
            # Например: 123.456.7.8 или proxyhub.lanit.ru
                        
                server_url=
                
            # Номер порта сервера Proxy Hub (0 - если порт не нужен)
                
                server_port=4448
                
            # Поддержка https/wss (значения: true/false)
                
                https=true   

            #__________________________________  Настройки драйверов ---------------------------------------------------
                        
            # driver_url - IP адрес драйвера (желательно запускать Proxy Node там же, где и драйвер)
                        
            # driver_port - Порт драйвера.
                        
            # driver_name - Имя драйвера. Используется для проксирования запросов к конкретному драйверу, 
            #               если на ноде зарегистрировано несколько драйверов. 
            #               Если при обращении к ProxyHub не указывать имя драйвера, то используется драйвер d0.
                            
                                                  
                d0.driver_url=127.0.0.1
                d0.driver_port=9999
                d0.driver_name=default
                
                d1.driver_url=
                d1.driver_port=
                d1.driver_name=
                
                d2.driver_url=
                d2.driver_port=
                d2.driver_name=
                
                d3.driver_url=
                d3.driver_port=
                d3.driver_name=
                
                d4.driver_url=
                d4.driver_port=
                d4.driver_name=
                
                
            ##################################     Как подключаться    #################################################
                        
            # ProxyHub имеет два режима проксирования:
                        
            # 1. id - Подключение к конкретной ноде по id. Запросы будут перенаправляться без учета занятости ноды.
            #    Данный режим рекомендуется использовать для отладочного запуска на локальном компьютере.
            #    Для использования данного режима необходимо в качестве удаленного сервера указать следующий url:
                        
            #   https://<server_url>:<server_port>/proxy/id/%1$s
                        
            # 2. tag - в данном режиме будет происходить запуск на случайной свободной ноде, у которой есть указанный при запуске тег.
            #    Данный режим рекомендуется использовать для удаленного запуска.
            #    Для использования данного режима необходимо в качестве удаленного сервера указать следующий url, содержащий необходимый тег:
                        
            #   https://<server_url>:<server_port>/proxy/tag/<tag1>
                        
            #   Для запуска на определенном драйвере необходимо добавить следующие Capabilities:
                        
            #    {
            #        "proxy_options": {
            #                          "run_id": "<уникальное имя запуска>",
            #                          "driver_name": "<driver_name>"
            #                         }
            #    }
                        
                        
            """;


    public ConfigurationModel getConfiguration() {
        if (configurationModel == null) configurationModel = readConfiguration();
        return configurationModel;
    }

    private ConfigurationModel readConfiguration() {
        if (new File(confFileName).exists()) {

            try (InputStream inputStream = new FileInputStream(confFileName)) {
                Properties properties = new Properties();
                properties.load(inputStream);

                return getConfModelFromProperties(properties);

            } catch (IOException e) {
                throw new RuntimeException("Ошибка чтения файла конфигурации: " + confFileName, e);
            }

        } else {
            writeDefaultConfigToFile();
            return null;
        }
    }

    private ConfigurationModel getConfModelFromProperties(Properties properties) {
        ConfigurationModel configurationModel = new ConfigurationModel();

        String nodeId = checkNotEmptyValue(properties.getProperty("node_id"), "node_id");
        String nodeTags = properties.getProperty("node_tags", "");
        String serverUrl = checkNotEmptyValue(properties.getProperty("server_url"), "server_url");
        Integer serverPort = getIntValue(properties.getProperty("server_port"), "server_port");
        Boolean https = Boolean.parseBoolean(properties.getProperty("https", "false"));

        configurationModel.setNodeId(nodeId);
        configurationModel.setTags(nodeTags);
        configurationModel.setServerUrl(serverUrl);
        configurationModel.setServerPort(serverPort);
        configurationModel.setHttps(https);

        configurationModel.setDrivers(getDrivers(properties));

        return configurationModel;
    }


    //------------------------------------------------------------------------------------------------------------------

    private String checkNotEmptyValue(String value, String valueName) {

        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Отсутствует или не заполнено значение параметра '%s' в файле конфигурации '%s'"
                    .formatted(valueName, confFileName));
        }

        return value;
    }

    private Integer getIntValue(String value, String valueName) {

        String intValue = (value == null || value.isEmpty()) ? "0" : value;

        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Некорректно заполнено значение параметра '%s' в файле конфигурации '%s'"
                    .formatted(valueName, confFileName), e);
        }
    }


    private String getDefaultConfig() {
        return DEFAULT_CONFIG.formatted(UUID.randomUUID());
    }

    private void writeDefaultConfigToFile() {
        try {
            Files.writeString(Paths.get(confFileName), getDefaultConfig());
            System.out.printf((CREATE_CONFIG_MESSAGE) + "%n", confFileName);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать дефолтную конфигурацию в файл: " + confFileName, e);
        }
    }

    private List<Driver> getDrivers(Properties properties) {
        Pattern pattern = Pattern.compile("^(d\\d)\\.driver_url$");

        List<Driver> drivers = properties.stringPropertyNames().stream()
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(matcher -> matcher.group(1))
                .sorted()
                .map(prefix -> createDriver(prefix, properties))
                .filter(Objects::nonNull)
                .toList();

        if (drivers.isEmpty()) throw new RuntimeException("В файле конфигурации не задан ни один драйвер!");

        Set<String> duplicates = CollectionHelper.getDuplicatesInList(drivers.stream()
                .map(Driver::getDriverName).toList());

        if (!duplicates.isEmpty())
            throw new RuntimeException(("В конфигурационном файле имеются дубли параметра driver_name: '%s'. " +
                    "Значения имени драйвера должны быть уникальны.").formatted(duplicates));

        return drivers;
    }

    private Driver createDriver(String prefix, Properties properties) {

        String url = properties.getProperty(prefix + ".driver_url");
        String port = properties.getProperty(prefix + ".driver_port");
        String name = properties.getProperty(prefix + ".driver_name");

        int portNum;

        if (url == null || url.isEmpty()) return null;

        try {
            portNum = Integer.parseInt(port);
            if (portNum < 1 || portNum > 65535)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new RuntimeException("Некорректное значение '%s.driver_port' = '%s'. Значение должно быть в диапазоне 1 - 65 535."
                    .formatted(prefix, port));
        }

        if (name == null || name.isEmpty())
            throw new RuntimeException("Отсутствует имя драйвера: '%s.driver_name'".formatted(prefix));

        return new Driver(url, portNum, name);
    }


}
