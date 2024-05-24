package server.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Runner extends Thread {

    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = in.readLine();
                if (input.equals("exit")) {
                    System.exit(0);
                    break;
                }




            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
