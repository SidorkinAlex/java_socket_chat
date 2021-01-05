package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Server {
    private final int PORT = 8189;

    private Vector<ClientHandler> clients;
    private ServerSocket server;
    private IAuthService authService;

    public Server() {
        Socket socket = null;
        clients = new Vector<>();

        try {
            server = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start();

            System.out.println("Сервер запущен");

            while (true) {
                System.out.println("Сервер ожидает подключение");

                socket = server.accept(); // Режим ожидания сервера, возвращает сокет, блокирет выполнение кода
                new ClientHandler(socket, this);

                System.out.println("Клиент подключился");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить сервер");
        } finally {
            try {
                server.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            authService.stop();
        }
    }

    public synchronized void broadcast(String msg) {
        for (ClientHandler c: clients) {
            c.sendMessage(msg);
        }
    }

    public synchronized void broadcast(String msg, String... nicks) {
        int countCurrent = 0;
        int countAll = nicks.length;

        for (ClientHandler c: clients) {
            for (String nick : nicks) {
                if (c.getName().equals(nick)) {
                    c.sendMessage(msg);

                    if (++countCurrent == countAll) {
                        return;
                    }
                }
            }
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler c: clients) {
            if (c.getName().equals(nick)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public IAuthService getAuthService() {
        return this.authService;
    }

    public void broadcastUserList() {
        StringBuffer sb = new StringBuffer("/user_list");

        ArrayList<String> logins = this.getAuthService().getUsersList();

        // В начале пишем только тех, что онлайн ...
        for (ClientHandler client: clients) {
            sb.append(" " + client.getName() + ":on");
            // ... по ходу дела исключая их общего списка их ...
            logins.remove(client.getName());
        }

        // ... выводим оставших, которые оффлайн.
        for (String login: logins) {
            sb.append(" " + login + ":off");
        }

        for (ClientHandler client: clients) {
            client.sendMessage(sb.toString());
        }
    }
}
