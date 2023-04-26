import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Server {	
    private static final String CREDENTIALS_FILE = "Server_Client_Model/data/credentials.txt";
    private static final String DATA_FILE = "Server_Client_Model/data/data.txt";
    private static List<String> authenticatedClients = new ArrayList<>();
    
    public static void main(String[] args) {
		System.out.println("Welcome to our server");
		System.out.println("Client can login and send the commands");
        if(!checkFile(CREDENTIALS_FILE,DATA_FILE)){
            System.out.println("Error-404 Files not found");
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFile(String file1,String file2){
        return Files.exists(Paths.get(CREDENTIALS_FILE)) && Files.exists(Paths.get(DATA_FILE));
    }
    
    private static boolean authenticate(String username, String password) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(CREDENTIALS_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean insertData(String data) {
        try {
            Files.write(Paths.get(DATA_FILE), (data + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean deleteData(String data) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            List<String> newLines = new ArrayList<>();
            boolean found = false;
            for (String line : lines) {
                if (line.equals(data)) {
                    found = true;
                } else {
                    newLines.add(line);
                }
            }
            if (found) {
                Files.write(Paths.get(DATA_FILE), String.join("\n", newLines).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static boolean updateData(String oldData, String newData) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            List<String> newLines = new ArrayList<>();
            boolean found = false;
            for (String line : lines) {
                if (line.equals(oldData)) {
                    newLines.add(newData);
                    found = true;
                } else {
                    newLines.add(line);
                }
            }
            if (found) {
                Files.write(Paths.get(DATA_FILE), String.join("\n", newLines).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        //Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                writer.write("Welcome to the chat server.\n");
                writer.flush();
                String username = null;
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] parts = line.split(",");
                    String action = parts[0];
                    if (action.equals("login")&& parts.length >= 3) {
                    username = parts[1];
                    String password = parts[2];
                    if (authenticate(username, password)) {
                    authenticatedClients.add(username);
                    writer.write("Login successful.\n");
                    writer.flush();
                   } else {
                     writer.write("Invalid username or password.\n");
                     writer.flush();
                   }
                   } else if (action.equals("logout")) {
                     authenticatedClients.remove(username);
                     writer.write("Logout successful.\n");
                     writer.flush();
                     break;
                  } else if (action.equals("insert")) {
                     if (authenticatedClients.contains(username)) {
                        String data = parts[1];
                        if (insertData(data)) {
                        writer.write("Data inserted successfully.\n");
                        writer.flush();
                   } else {
                   writer.write("Failed to insert data.\n");
                   writer.flush();
                   }
                   } else {
                   writer.write("You must be logged in to perform this action.\n");
                   writer.flush();
                   }
                   } else if (action.equals("delete")) {
                   if (authenticatedClients.contains(username)) {
                   String data = parts[1];
                   if (deleteData(data)) {
                   writer.write("Data deleted successfully.\n");
                   writer.flush();
                   } else {
                   writer.write("Failed to delete data.\n");
                   writer.flush();
                   }
                   } else {
                   writer.write("You must be logged in to perform this action.\n");
                   writer.flush();
                  }
                  } else if (action.equals("update")) {
                  if (authenticatedClients.contains(username)) {
                  String oldData = parts[1];
                  String newData = parts[2];
                  if (updateData(oldData, newData)) {
                  writer.write("Data updated successfully.\n");
                  writer.flush();
                  } else {
                  writer.write("Failed to update data.\n");
                  writer.flush();
                   }
                  } else {
                  writer.write("You must be logged in to perform this action.\n");
                  writer.flush();
                 }
                 } else if (action.equals("get")) {
                 List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
                 for (String dataline : lines) {
                 writer.write(dataline + "\n");
                 writer.flush();
                 }
                 } else {
                 writer.write("Invalid action.\n");
                 writer.flush();
                 }
             }
            } catch (IOException e) {
              e.printStackTrace();
              }
             }
           }
}