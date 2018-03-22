package net;

import model.ServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Net {
    private final int LINGER_TIME = 0;
    private final int PORT_NUMBER = 8080;
    private String HOSTNAME = "192.168.10.218";
    private ServerSocketChannel listeningSocketChannel;
    private Boolean sendAll = false;
    private Selector selector;

    public static void main(String[] args) {
        System.out.println("*** Haddock Server ***");
        if(args.length > 0)
            new Net().start(args[0]);
        else
            new Net().start("");
    }

    static String ip_check(String ipString) {
        String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipString);
        if (matcher.find()) {
            return matcher.group();
        } else{
            return "0.0.0.0";
        }
    }

    private void start(String ip) {
        String ipString;
        if(ip == null || ip.trim().isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter your IPv4 address:");
            ipString = scanner.nextLine().trim();
        } else
            ipString = ip;
        String checkedIP = ip_check(ipString);
        if(!checkedIP.equals("0.0.0.0"))
            setHOSTNAME(checkedIP);
        else {
            System.out.println("Please enter valid IP address, try again.\n");
            start("");
        }
        run();
    }

    void initRecieve() {
        try {
            listeningSocketChannel = ServerSocketChannel.open();
            listeningSocketChannel.configureBlocking(false);
            listeningSocketChannel.bind(new InetSocketAddress(HOSTNAME, PORT_NUMBER));
            listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendAll() {
        for (SelectionKey key : selector.keys()) {
            if (key.channel() instanceof SocketChannel && key.isValid()) {
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }
    }

    void acceptClient(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel clientChannel = serverSocketChannel.accept();
            clientChannel.configureBlocking(false);
            ClientHandler handler = new ClientHandler(this, clientChannel);
            clientChannel.register(selector, SelectionKey.OP_READ, new Client(handler));
            clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        System.out.println("\nServer running...\n");
        try {
            selector = Selector.open();
            initRecieve();
            while (true) {
                if (sendAll) {
                    sendAll();
                    sendAll = false;
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid())
                        continue;
                    if (key.isAcceptable()) {
                        acceptClient(key);
                    } else if (key.isReadable()) {
                        recieveMsg(key);
                    } else if (key.isWritable()) {
                        sendMsg(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void recieveMsg(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.handler.receiveMsg();
        } catch (IOException | ServerException clientHasClosedConnection) {
            System.out.println(clientHasClosedConnection.getMessage());
            System.out.println("Waiting for a new request...\n");
            client.handler.disconnectClient();
        }
    }

    void queueMsgToSend(ClientHandler clientHandler, String msg) {
        ByteBuffer bufferedMsg = ByteBuffer.wrap(msg.getBytes());
        synchronized (clientHandler.messagesToSend) {
            clientHandler.messagesToSend.add(bufferedMsg);
        }
        sendAll = true;
        selector.wakeup();
    }

    void sendMsg(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.sendAll();
        } catch (ServerException couldNotSendAllMessages) {
        } catch (IOException clientHasClosedConnection) {
            client.handler.disconnectClient();
            key.cancel();
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    private void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    private class Client {
        private final ClientHandler handler;

        private Client(ClientHandler handler) {
            this.handler = handler;
        }

        private void sendAll() throws IOException, ServerException {
            ByteBuffer msg;
            synchronized (handler.messagesToSend) {
                while ((msg = handler.messagesToSend.peek()) != null) {
                    handler.sendMsg(msg);
                    handler.messagesToSend.remove();
                }
            }
        }
    }
}


