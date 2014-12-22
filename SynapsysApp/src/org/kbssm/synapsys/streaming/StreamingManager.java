package org.kbssm.synapsys.streaming;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.kbssm.synapsys.streaming.jlibrtp.DataFrame;
import org.kbssm.synapsys.streaming.jlibrtp.DebugAppIntf;
import org.kbssm.synapsys.streaming.jlibrtp.Participant;
import org.kbssm.synapsys.streaming.jlibrtp.RTCPAppIntf;
import org.kbssm.synapsys.streaming.jlibrtp.RTPAppIntf;
import org.kbssm.synapsys.streaming.jlibrtp.RTPSession;

import android.util.Log;

/**
 * 
 * @author Yeonho.Kim
 *
 */
public class StreamingManager {

	public static final int INFLOW_RTP_PORT = 1113;
	public static final int INFLOW_RTCP_PORT = 1114;
	
	public static final int RTCP_APP_TYPE_TETHERED_COMPLETE = 0x7;
	
	public static final int RTCP_APP_TYPE_STREAM_START = 0xA;
	
	public static final int RTCP_APP_TYPE_STREAM_STOP = 0xB;
	
	
	
	private static StreamingManager sInstance;
	
	/**
	 * 새로운 {@link StreamingManager} 객체 및 RTP/RTCP 세션을 생성한다. 
	 * @return
	 */
	public static StreamingManager newInstance() {
		Thread thread = new Thread() {
			public void run() {
				sInstance = new StreamingManager();
			};
		};
		
		try {
			thread.start();
			thread.join(10);
			return sInstance;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	private RTPSession mRTPsession;
	private InetSocketAddress mConnectedIP;
	
	private StreamingManager() {
		init();
	}
	
	public void init() {
		try {
			DatagramSocket rtpSocket = new DatagramSocket(INFLOW_RTP_PORT);
			DatagramSocket rtcpSocket = new DatagramSocket(INFLOW_RTCP_PORT);
		
			mRTPsession = new RTPSession(rtpSocket, rtcpSocket);
			
			// TODO : Participant가 존재해야 내부에서 RTCP를 처리할 수 있는듯?
			//Participant part = new Participant("192.168.42.120",1113,1114);
			//mRTPsession.addParticipant(part);
			
			mRTPsession.RTPSessionRegister(mRTPinterface, mRTCPinterface, mDebugInterface);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 세션을 종료한다. 앱 종료시 이외엔 호출하시 마시오!
	 */
	public void destroy() {
		if (mRTPsession != null)
			mRTPsession.endSession();
	}
	
	public void requestStreamingStart() {
	
		//long ssrc, int type, byte[] name, byte[] data) 
		//mRTPsession.sendRTCPAppPacket(ssrc, type, name, data);
	}
	
	public void requestStreamingStop() {
		
	}
	
	/**
	 * RTP 이벤트에 대한 Interface를 구현한 필드 객체.
	 */
	private RTPAppIntf mRTPinterface = new RTPAppIntf() {
		
		@Override
		public void userEvent(int type, Participant[] participant) {
			// TODO Auto-generated method stub

			Log.d("UserEvent_Received", type + " : ");
		}
		
		@Override
		public void receiveData(DataFrame frame, Participant participant) {
			if (mOnDataStreamingListener != null) {
				byte[] data = frame.getConcatenatedData();
				mOnDataStreamingListener.onDataReceived(data, data.length);
			}
		}
		
		@Override
		public int frameSize(int payloadType) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	/**
	 * RTCP 이벤트에 대한 Interface를 구현한 필드 객체.
	 */
	private RTCPAppIntf mRTCPinterface = new RTCPAppIntf() {
		
		@Override
		public void SRPktReceived(long ssrc, long ntpHighOrder, long ntpLowOrder,
				long rtpTimestamp, long packetCount, long octetCount,
				long[] reporteeSsrc, int[] lossFraction, int[] cumulPacketsLost,
				long[] extHighSeq, long[] interArrivalJitter,
				long[] lastSRTimeStamp, long[] delayLastSR) {
			// TODO Auto-generated method stub

			Log.d("SR_Packet_Received", ssrc + " : ");
		}
		
		@Override
		public void SDESPktReceived(Participant[] relevantParticipants) {
			
			if (relevantParticipants != null) {
				for(Participant p : relevantParticipants) {
					
					String message = p.getSSRC() + " : " + p.getNAME() + " :" + p.getCNAME() + " : " + p.getPriv();
					Log.d("SDES_Packet_Received", message);
					
				}
			}
		}
		
		@Override
		public void RRPktReceived(long reporterSsrc, long[] reporteeSsrc,
				int[] lossFraction, int[] cumulPacketsLost, long[] extHighSeq,
				long[] interArrivalJitter, long[] lastSRTimeStamp,
				long[] delayLastSR) {
			// TODO Auto-generated method stub

			Log.d("RR_Packet_Received", reporterSsrc + " : ");
		}
		
		@Override
		public void BYEPktReceived(Participant[] relevantParticipants, String reason) {
			// TODO Auto-generated method stub

			Log.d("BYE_Packet_Received", reason);
		}
		
		@Override
		public void APPPktReceived(Participant part, int subtype, byte[] name, byte[] data) {

			Log.d("APP_Packet_Received", subtype + " : " + new String(name) + " : " + new String(data));
			switch (subtype) {
			case RTCP_APP_TYPE_TETHERED_COMPLETE:
				// 초기 연결 성립시, 스트리밍 요청.
				if (mConnectedIP == null) {
					// 연결 요청 수용
				} 
				break;
				
			case RTCP_APP_TYPE_STREAM_START:
			case RTCP_APP_TYPE_STREAM_STOP:
				if (mOnDataStreamingListener != null) {
					mOnDataStreamingListener.onControlReceived(subtype, data);
				}
				break;
			}
		}
	};
	
	/**
	 * Debug 이벤트에 대한 Interface를 구현한 필드 객체.
	 */
	private DebugAppIntf mDebugInterface = new DebugAppIntf() {
		
		@Override
		public void packetSent(int type, InetSocketAddress socket,
				String description) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void packetReceived(int type, InetSocketAddress socket, String description) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void importantEvent(int type, String description) {
			// TODO Auto-generated method stub
			
		}
	};
	

	/**
	 * 
	 * 
	 * @author Yeonho.Kim
	 *
	 */
	public interface OnDataStreamingListener {
		
		/**
		 * 
		 * @param data
		 */
		public void onDataReceived(byte[] data, int length);
		
		/**
		 * 
		 * @param type
		 */
		public void onControlReceived(int type, byte[] data);
		
	}

	private OnDataStreamingListener mOnDataStreamingListener;
	
	public void setOnHandleStreamingData(OnDataStreamingListener handle) {
		mOnDataStreamingListener = handle;
	}
	
}
