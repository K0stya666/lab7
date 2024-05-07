package server.tools;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import global.facility.Route;
import global.tools.Console;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class CSVparser {
    private final String fileName;
    private final Console console;

    public CSVparser(String fileName, Console console) {
        this.fileName = fileName;
        this.console = console;
    }

    /**
     * Преобразует коллекцию в CSV-строку.
     * @param collection коллекция
     * @return CSV-строка
     */
    private String collection2CSV(Collection<Route> collection) {
        try {
            StringWriter sw = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(sw);
            for (var e : collection) {
                csvWriter.writeNext(Route.toArray(e));
            }
            return sw.toString();
        } catch (Exception e) {
            console.printError("Ошибка сериализации");
            return null;
        }
    }

    /**
     * Записывает коллекцию в файл
     * @param collection коллекция
     */
    public void writeCollection(Collection<Route> collection) {
        BufferedWriter writer = null;
        try {
            var csv = collection2CSV(collection);
            if (csv == null) return;
            writer = new BufferedWriter(new FileWriter(fileName));
            try {
                writer.write(csv);
                writer.flush();
                console.println("Коллекция успешно сохранена в файл!");
            } catch (IOException e) {
                console.printError("Неожиданная ошибка сохранения");
            }
        } catch (NullPointerException | IOException e) {
            console.printError("Файл не найден");
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                console.printError("Ошибка закрытия файла");
            }
        }
    }

    /**
     * Преобразует CSV-строку в коллекцию.
     * @param s CSV-строка
     * @return коллекция
     */
    private LinkedList<Route> CSV2collection(String s) {
        try {
            StringReader sr = new StringReader(s);
            CSVReader csvReader = new CSVReader(sr);
            LinkedList<Route> ds = new LinkedList<>();
            String[] record = null;
            while ((record = csvReader.readNext()) != null) {
                Route route = Route.fromArray(record);
                if (route.validate())
                    ds.add(route);
                else
                    console.printError("Файл с коллекцией содержит не действительные данные");
            }
            csvReader.close();
            return ds;
        } catch (Exception e) {
            console.printError("Ошибка десериализации");
            return null;
        }
    }

    /**
     * Считывает коллекцию из файла
     * @param collection коллекция
     */
    public void readCollection(Collection<Route> collection) {
        if (fileName != null && !fileName.isEmpty()) {
            try (var fileReader = new InputStreamReader(new FileInputStream(fileName))) {
                var s = new StringBuilder();
                int c;
                while ((c = fileReader.read()) != -1) {
                    s.append((char) c);
                }

                collection.clear();
                collection.addAll(Objects.requireNonNull(CSV2collection(s.toString())));
                if (!collection.isEmpty()) {
                    console.println("Коллекция успешно загружена");
                    return;
                } else
                    console.println("В загрузочном файле не обнаружена необходимая коллекция");
            } catch (FileNotFoundException e) {
                console.printError("Загрузочный файл не найден");
            } catch (IOException e) {
                console.printError("Непредвиденная ошибка при чтении файла");
                System.exit(0);
            }
        } else {
            console.printError("Аргумент командной строки с загрузочным файлом не найден");
        }
        collection.clear();
    }
}
