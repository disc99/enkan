package enkan.system.repl.pseudo;

import enkan.exception.FalteringEnvironmentException;
import enkan.system.ReplResponse;
import enkan.system.Transport;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.net.Socket;

/**
 * @author kawasima
 */
public class PseudoReplTransport implements Transport {
    private Socket socket;
    private Packer packer;
    private Unpacker unpacker;

    public PseudoReplTransport(Socket socket) throws IOException {
        this.socket = socket;
        MessagePack msgpack = new MessagePack();
        msgpack.register(ReplResponse.ResponseStatus.class);
        msgpack.register(ReplResponse.class);

        packer = msgpack.createPacker(socket.getOutputStream());
        unpacker = msgpack.createUnpacker(socket.getInputStream());
    }

    @Override
    public void send(ReplResponse response) {
        try {
            packer.write(response);
            socket.getOutputStream().flush();
        } catch (IOException ex) {
            throw FalteringEnvironmentException.create(ex);
        }
    }

    @Override
    public String recv(long timeout) {
        try {
            return unpacker.readString();
        } catch (IOException ex) {
            throw FalteringEnvironmentException.create(ex);
        }
    }

    public void close() throws IOException {
        try {
            socket.close();
        } catch (IOException ex) {
            throw FalteringEnvironmentException.create(ex);
        }
    }

}
