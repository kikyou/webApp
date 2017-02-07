package org.baidu.ecom.rpc;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;

/**
 * Created by baidu on 2017/2/6.
 */
public class NioRpc {

    static String decode(ByteBuffer buf) {
        try {
            Charset charset = Charset.forName("utf-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buf);
            return charBuffer.toString();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        return "decode-error";
    }

    static class RpcAcceptor extends Thread {
        public RpcAcceptor(int port) throws IOException {
            _port = port;
            _sockQueue = new LinkedBlockingQueue<SocketChannel>();
            _end = new AtomicBoolean(true);
            _selector = Selector.open();
        }

        public void end() {
            _end.set(false);
        }

        @Override
        public void run() {
            try {
                ServerSocketChannel acceptor = ServerSocketChannel.open();
                acceptor.socket().bind(new InetSocketAddress("localhost", _port), 1024);
                acceptor.configureBlocking(false);
                acceptor.register(_selector, SelectionKey.OP_ACCEPT);

                while (_end.get()) {
                    if (_selector.select(100) <= 0) {
                        continue;
                    }

                    Set selectedKeys = _selector.selectedKeys();
                    System.out.println(String.format("get active key[%d]", selectedKeys.size()));
                    Iterator keyIt = selectedKeys.iterator();

                    while (keyIt.hasNext()) {
                        SelectionKey key = (SelectionKey)keyIt.next();
                        if (key.isAcceptable()) {
                            ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                            SocketChannel socketChannel = channel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(_selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            _sockQueue.add((SocketChannel)key.channel());
                            key.cancel();
                        } else {
                            System.out.println("something wrong with the select");
                        }

                        keyIt.remove();
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public SocketChannel getChannel() throws InterruptedException {
            while (true) {
                SocketChannel sc = _sockQueue.poll(100, TimeUnit.MILLISECONDS);
                if (sc != null) {
                    return sc;
                } else if (!_end.get()) {
                    return null;
                }
            }
        }

        public void returnChannel(SocketChannel c) throws Exception {
            c.register(_selector, SelectionKey.OP_READ);
        }

        private Selector _selector;
        private int _port;
        private BlockingQueue<SocketChannel> _sockQueue;
        private AtomicBoolean _end;
    }

    static class RpcWorker extends Thread {
        public RpcWorker(int id, RpcAcceptor acceptor) {
            _id = id;
            _acceptor = acceptor;
        }

        public void run() {
            while (true) {
                try {
                    SocketChannel sc = _acceptor.getChannel();
                    if (sc == null) {
                        return ;
                    }
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    int bytesRead = sc.read(buf);
                    if (bytesRead < 0) {
                        sc.close();
                        continue;
                    }
                    buf.flip();
                    //System.out.println(String.format("worker[%d]: read message [%d]", _id, bytesRead));
                    System.out.println(String.format("worker[%d]: read message [%s]",
                            _id, decode(buf)));
                    Thread.sleep(100);

                    buf.clear();
                    buf.put(String.format("ack from worker %d", _id).getBytes());
                    buf.flip();
                    sc.write(buf);

                    _acceptor.returnChannel(sc);
                } catch (InterruptedException e) {
                    return ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private int _id;
        private RpcAcceptor _acceptor;
    }

    static class RpcClient extends Thread {
        public RpcClient(int id, int port) {
            _id = id;
            _port = port;
        }

        public void run() {
            for (int i = 0; i < 2; ++i) {
                try {
                    SocketChannel sc = SocketChannel.open();
                    sc.connect(new InetSocketAddress("localhost", _port));
                    for (int j = 0; j < 2; ++j) {
                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        buf.put(String.format("send from client[%d]", _id).getBytes());
                        buf.flip();
                        sc.write(buf);

                        buf.clear();
                        sc.read(buf);
                        buf.flip();
                        System.out.println(String.format("client[%d]: read message [%s]",
                                _id, decode(buf)));

                    }
                    sc.close();
                } catch (IOException e) {

                }

            }
        }

        private int _port;
        private int _id;
    }

    public static void main(String[] args) throws Exception{
        final int ClientNum = 3;
        final int WorkerNum = 6;

        RpcAcceptor acceptor = new RpcAcceptor(14567);
        acceptor.start();

        ArrayList<RpcWorker> workers = new ArrayList<RpcWorker>();
        for (int i = 0; i < WorkerNum; ++i) {
            RpcWorker worker = new RpcWorker(i, acceptor);
            worker.start();
            workers.add(worker);
        }

        ArrayList<RpcClient> clients = new ArrayList<RpcClient>();
        for (int i = 0; i < ClientNum; ++i) {
            RpcClient client = new RpcClient(i, 14567);
            client.start();
            clients.add(client);
        }

        for (int i = 0; i < ClientNum; ++i) {
            clients.get(i).join();
        }

        acceptor.end();
        for (int i = 0; i < WorkerNum; ++i) {
            workers.get(i).join();
        }

        acceptor.join();
    }
}
