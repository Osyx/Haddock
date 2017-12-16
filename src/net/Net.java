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

class Net {
    private final int LINGER_TIME = 0;
    private final int PORT_NUMBER = 8080;
    private final String HOSTNAME = "192.168.10.218";
    private final String EXIT_MESSAGE = "CLOSE";
    private final String FORCE_EXIT_MESSAGE = "FORCE CLOSE";
    private ServerSocketChannel listeningSocketChannel;
    private Boolean sendAll = false;
    private Selector selector;

    public static void main(String[] args) {
        new Net().run();
    }

    public void run() {
        try {
            selector = Selector.open();
            initRecieve();
            int pelle = 0;
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
                        System.out.println("Is accept");
                    } else if (key.isReadable()) {
                        if(pelle++ < 10)
                            System.out.println("Is read");
                        recieveMsg(key);
                    } else if (key.isWritable()) {
                        System.out.println("Is write");
                        sendMsg(key);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    void recieveMsg(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.handler.receiveMsg();
        } catch (IOException | ServerException clientHasClosedConnection) {
            System.err.println(clientHasClosedConnection.getMessage());
            client.handler.disconnectClient();
        }
    }

    void sendMsg(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.sendAll();
            key.interestOps(SelectionKey.OP_READ);
        } catch (ServerException couldNotSendAllMessages) {
        } catch (IOException clientHasClosedConnection) {
                            client.handler.disconnectClient();
                            key.cancel();
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


