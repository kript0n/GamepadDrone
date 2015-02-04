package com.kripton.COM;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class COMReader implements Runnable, SerialPortEventListener {

	
	public COMReader(SerialPort _serial) {
		
		serial = _serial;
		
		try {
			inStr = serial.getInputStream();
			serial.addEventListener(this);
			serial.notifyOnDataAvailable(true);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public boolean isRead() {
		return isRead;
	}
	
	
	public void dropState() {
		isRead = false;
	}
	
	public void close() throws IOException {
		inStr.close();
	}
	
	
	@Override
	public void run() {
		
		while(!run) {
			synchronized(mutex) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	public void serialEvent(SerialPortEvent event) {
       
		switch(event.getEventType()) {
	        
			case SerialPortEvent.BI:
	        case SerialPortEvent.OE:
	        case SerialPortEvent.FE:
	        case SerialPortEvent.PE:
	        case SerialPortEvent.CD:
	        case SerialPortEvent.CTS:
	        case SerialPortEvent.DSR:
	        case SerialPortEvent.RI:
	        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
	            break;
	        
	        case SerialPortEvent.DATA_AVAILABLE:
	        	
	        	byte[] readBuffer = new byte[1024];
	            try {
	                while (inStr.available() > 0) {
	                    inStr.read(readBuffer);
	                }
	                System.out.print("Read from COM: ");
	                System.out.println(new String(readBuffer));
	                isRead = true;
	            } catch (IOException e) {
	            	System.out.println(e);
	            }
	            break;
        }
    }
	
	
	private SerialPort serial = null;
	private InputStream inStr = null;
	private volatile boolean isRead = false;
	private Object mutex = new Object();
	private boolean run = false;

	
}
