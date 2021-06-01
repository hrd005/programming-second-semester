package stepanoff.denis.lab5.client.net;

import stepanoff.denis.lab5.common.net.NetException;
import stepanoff.denis.lab5.common.util.TypedEntity;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * This class encapsulates all of networking
 */
public class ServerConnector {

    private static final String HOSTNAME = "192.168.1.85";
    private static final int    PORT     = 3080;

    private SocketChannel channel;

    /**
     * Connect to server
     * @throws NetException if server is unavailable
     */
    public void connect() throws NetException {
        try {
            SocketAddress address = new InetSocketAddress(HOSTNAME, PORT);
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
        } catch (Exception e) {
            throw new NetException("Looks like server is temporarily unavailable: " + e.getMessage());
        }
    }

    /**
     * Send Request and receive response
     * @param request -- request to send
     * @return Future with List of received Typed Entities
     * @throws NetException if request can't be sent
     */
    public Future<List<TypedEntity>> manageRequest(Request request) throws NetException {
        FutureTask<List<TypedEntity>> ft = new FutureTask<>(() -> {
            this.connect();
            try {
                this.send(request);
                return this.receive();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NetException("Request can't be sent: " + e.getMessage());
            } finally {
                this.channel.close();
                this.channel = null;
            }
        });
        Thread receiver = new Thread(ft);
        receiver.start();
        return ft;
    }

    private void send(Request request) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int i = 0; i < 4; i++) bos.write(0);
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        Queue<Object> objects = request.toSendingQueue();

        oos.writeInt(objects.size());

        while (!objects.isEmpty()) {
            oos.writeObject(objects.poll());
        }

        oos.flush();
        ByteBuffer bb = ByteBuffer.wrap(bos.toByteArray());
        bb.putInt(0, bos.size()-4);

        this.channel.write(bb);
        this.channel.shutdownOutput();
    }

    private List<TypedEntity> receive() {
        try {
            ByteBuffer b1;
            int c = 0;
            while (c == 0) {
                b1 = this.readBytes(4);
                c = b1.getInt(0);
            }

            LinkedList<TypedEntity> ret = new LinkedList<>();

            for (int i = 0; i < c; i++) {
//                System.out.println(ret);

                b1 = this.readBytes(4);
                int size = b1.getInt(0);
//                System.out.println(size);

                ByteBuffer b2 = this.readBytes(size);

                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b2.array()));
                ret.add((TypedEntity) ois.readObject());
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException(e.getMessage());
        }
    }

    private ByteBuffer readBytes(int count) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(count);
        b.clear();
        while (b.position() != count) {
            this.channel.read(b);
        }
        return b;
    }
}
