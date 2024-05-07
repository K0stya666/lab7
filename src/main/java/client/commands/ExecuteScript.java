package client.commands;

import client.managers.SocketClient;
import global.facility.Coordinates;
import global.facility.Location;
import global.facility.Request;
import global.facility.Route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Stack;

public class ExecuteScript {
    private static Stack<File> st = new Stack<>();

    public static void execute(String command, SocketChannel socketChannel) throws Exception {
        String[] splitCommand = command.split(" ");
        if (splitCommand.length < 2) {
            System.out.println("Введите название сценария");
            return;
        }

        File file = new File(splitCommand[1]);
        if (!file.canRead()) {
            //throw new Exception("У вас недостаточно прав для чтения этого файла");
            System.out.println("У вас недостаточно прав для чтения этого файла");
        }
        if (st.isEmpty() || !st.contains(file)) {
            st.add(file);
        }

        String path = splitCommand[1];
        var br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
        String line;
        String[] org = new String[10]; // Fixed array size to 10, it was previously 9 which is incorrect as per your code logic
        while ((line = br.readLine()) != null) {
            String mainCommand = line.split(" ")[0];
            if (mainCommand.equals("add") || mainCommand.equals("add_if_min") || mainCommand.equals("update_by_id")) {
                for (int n = 0; n < org.length; n++) {
                    if ((line = br.readLine()) != null) {
                        org[n] = line;
                    }
                }
                SocketClient.sendRequest(new Request(mainCommand, new Route(org[0], // name
                        new Coordinates(Float.parseFloat(org[1]), Float.parseFloat(org[2])), // coordinates
                        new Date(), // creationDate (assuming current date is the creation date)
                        new Location(Long.parseLong(org[3]), Integer.parseInt(org[4]), org[5]), // from
                        new Location(Long.parseLong(org[6]), Integer.parseInt(org[7]), org[8]), // to
                        Float.parseFloat(org[9]))), socketChannel); // Here org[9] is used assuming the org array size is 10
            } else {
                if (line.contains("execute_script")) {
                    String[] newCommandSplit = line.split(" ");
                    if (newCommandSplit.length < 2) {
                        System.out.println("Please write the name of the file in the script.");
                    } else {
                        File file_new = new File(newCommandSplit[1]);
                        if (!file_new.canRead()) {
                            //throw new Exception("У вас недостаточно прав для чтения этого файла");
                            System.out.println("У вас недостаточно прав для чтения этого файла");
                        }
                        if (st.contains(file_new)) {
                            System.out.println("Рекурсия файла " + file.getName() + " была пропущена");
                        } else {
                            execute(line, socketChannel);
                        }
                    }
                } else {
                    SocketClient.sendRequest(new Request(line, null), socketChannel);
                }
            }
        }
        st.pop();
    }

    public String getName() {
        return "execute_script file_name";
    }

}
