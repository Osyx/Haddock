package net;

import controller.Controller;
import model.ServerException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

class ClientHandler implements Runnable {
    private final int MAX_MSG_LENGTH = 8192;
    private final SocketChannel clientChannel;
    private final ByteBuffer msgFromClient = ByteBuffer.allocateDirect(MAX_MSG_LENGTH);
    private Controller controller  = new Controller();
    private Net server;
    final Queue<ByteBuffer> messagesToSend;

    ClientHandler(Net net, SocketChannel clientChannel) {
        messagesToSend  = new ArrayDeque<>();
        server = net;
        this.clientChannel = clientChannel;
        initConnection();
    }

    @Override
    public void run() {
        try {
            server.queueMsgToSend(this, controller.getWord());
        } catch (ServerException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    private void initConnection() {
        try {
            server.queueMsgToSend(this, controller.newConnection());
        } catch (ServerException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    void sendMsg(ByteBuffer msg) throws ServerException, IOException {
        clientChannel.write(ByteBuffer.wrap("Pelle".getBytes()));
        if (msg.hasRemaining()) {
            throw new ServerException("Could not send message");
        }
    }

    void receiveMsg() throws IOException, ServerException {
        msgFromClient.clear();
        int numOfReadBytes = clientChannel.read(msgFromClient);
        if (numOfReadBytes == -1)
            throw new ServerException("Client has closed connection.");
        System.out.println(extractMessageFromBuffer());
        ForkJoinPool.commonPool().execute(this);
    }

    private String extractMessageFromBuffer() {
        msgFromClient.flip();
        byte[] bytes = new byte[msgFromClient.remaining()];
        msgFromClient.get(bytes);
        return new String(bytes);
    }

    void disconnectClient() throws IOException {
        clientChannel.close();
    }
}
