import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
    try (Socket socket = new Socket("localhost", 1234);
BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter
(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {
            String line = reader.readLine();
            System.out.println(line);
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.equals("exit")) {
                    break;
                }
                String[] parts = input.split(",");
                String action = parts[0];
                if (action.equals("login")&& parts.length >= 3) {
                    String username = parts[1];
                    String password = parts[2];
                    writer.write(input + "\n");
                    writer.flush();
                    line = reader.readLine();
                    System.out.println(line);
                } else if (action.equals("logout")) {
                    writer.write(input + "\n");
                    writer.flush();
                    line = reader.readLine();
                    System.out.println(line);
                } else if (action.equals("insert")) {
                    writer.write(input + "\n");
                    writer.flush();
                    line = reader.readLine();
                    System.out.println(line);
                } else if (action.equals("delete")) {
                     writer.write(input + "\n");
                     writer.flush();
                    line = reader.readLine();
                    System.out.println(line);
                } else if (action.equals("update")) {
                   writer.write(input + "\n");
                   writer.flush();
                   line = reader.readLine();
                   System.out.println(line);
                } else if (action.equals("get")) {
                  writer.write(input + "\n");
                  writer.flush();
                 while ((line = reader.readLine()) != null) {
                 System.out.println(line);
                }
                } else {
                   System.out.println("Invalid action.");
                 }
            }
            } catch (IOException e) {
              e.printStackTrace();
          }}}
