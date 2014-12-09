package kbssm.synapse;

/**
 * Synapse 연결에 대한 Interface를 정의한다.
 * 
 * @author Yeonho.Kim
 *
 */
public interface ISynapseConnect {
	
	/**
	 * 상대 PC로 연결을 수행한다.
	 * @return
	 */
	boolean connect();
	
	/**
	 * 연결된 PC와의 접속을 종료한다.
	 * @return
	 */
	boolean disconnect();
	
	/**
	 * 재 접속을 시도한다.
	 * @return
	 */
	boolean reconnect();
	
	/**
	 * 연결을 잠시 중지한다.
	 * @return
	 */
	boolean pause();
	
	/**
	 * 연결을 재개한다.
	 * @return
	 */
	boolean resume();
	
	/**
	 * USB 테더링을 설정한다.
	 * @param enable
	 */
	void setUsbTethering(boolean enable);
}
