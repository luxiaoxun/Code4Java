package com.nettymq.message;

import java.nio.ByteBuffer;

public class Header {
	
	private byte[] msgLength; //The whole message length includes header
	
	private byte msgType; // one byte for message type

	public int getMsgLength() {
		ByteBuffer b = ByteBuffer.wrap(msgLength);
		return b.getInt();
	}

	public void setMsgLength(int msgLength) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(msgLength);
		this.msgLength = b.array();
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}
	
	public byte[] getBytes(){
		byte[] buffer = new byte[5];
		System.arraycopy(msgLength, 0, buffer, 0, 4);
		buffer[4] = msgType;
		return buffer;
	}
}
