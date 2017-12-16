package net;

import controller.Controller;
import model.ServerException;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;

class ClientHandler extends HttpServlet implements Runnable {
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
    }

    @Override
    public void run() {
        try {
            server.queueMsgToSend(this, controller.getWord());
        } catch (ServerException e) {
            System.err.println(e.getMessage());
        }
    }

    void sendMsg(ByteBuffer msg) throws ServerException, IOException {
        String message = "HTTP/1.1 200 " + extractMessageFromBuffer(msg) + "\r\nContent-Type: text/plain\r\n\r\n";
        clientChannel.write(ByteBuffer.wrap(message.getBytes()));
        System.out.println("Wrote " + message + " to client.");
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

    String extractMessageFromBuffer(ByteBuffer msg) {
        byte[] bytes = new byte[msg.remaining()];
        msg.get(bytes);
        String returnMsg = new String(bytes);
        msg.flip();
        return returnMsg;
    }

    void disconnectClient() throws IOException {
        clientChannel.close();
    }
}
