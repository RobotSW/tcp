import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RcvThread implements Runnable{
    private static final int sizeBuf = 50;
    private Socket clientSock;
    private Logger logger;
    private SocketAddress clientAddress;

    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "222", PinState.LOW);
    final GpioPinDigitalOutput pin3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "333", PinState.LOW);
    final GpioPinDigitalOutput pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "444", PinState.LOW);
    final GpioPinDigitalOutput pin5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "555", PinState.LOW);
  
    public RcvThread(Socket clntSock, SocketAddress clientAddress, Logger logger) {
        this.clientSock = clntSock;
        this.logger = logger;
        this.clientAddress = clientAddress;
    }

    public void run(){
        try {
            InputStream ins   = clientSock.getInputStream();
            OutputStream outs = clientSock.getOutputStream();

            int rcvBufSize;
            byte[] rcvBuf = new byte[sizeBuf];
            while ((rcvBufSize = ins.read(rcvBuf)) != -1) {

                String rcvData = new String(rcvBuf, 0, rcvBufSize, "UTF-8");

                if (rcvData.compareTo("Up") == 0) {
                   pin2.high();
		   pin3.low();
		   pin4.low();
		   pin5.low();

             
                }

                if (rcvData.compareTo("LeftTurn") == 0) {
                   pin2.low();
		   pin3.high();
		   pin4.low();
		   pin5.low();

               
                }

                if (rcvData.compareTo("RightTurn") == 0) {
                   pin2.low();
		   pin3.low();
		   pin4.high();
		   pin5.low();

              
                }
                if (rcvData.compareTo("Down") == 0) {
                   pin2.low();
		   pin3.low();
		   pin4.low();
		   pin5.high();

                    
                }


                logger.info("Received data : " + rcvData + " (" + clientAddress + ")");
                outs.write(rcvBuf, 0, rcvBufSize);
            }
            logger.info(clientSock.getRemoteSocketAddress() + " Closed");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception in RcvThread", ex);
        } finally {
            try {
                clientSock.close();
                System.out.println("Disconnected! Client IP : " + clientAddress);
            } catch (IOException e) {}
        }
    }
}