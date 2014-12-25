package org.kbssm.synapse;

/**
 * Synapse 연결에 대한 Interface를 정의한다.
 * 
 * @author Yeonho.Kim
 *
 */
public interface ISynapse {
	
	public static final int INFLOW_SERVER_PORT = 11013;
	
	/**
	 * 상대 PC로 연결을 수행한다.
	 * @return
	 */
	boolean synapse();
	
	/**
	 * 연결된 PC와의 접속을 종료한다.
	 * @return
	 */
	boolean insynapse();
	
	/**
	 * 재 접속을 시도한다.
	 * @return
	 */
	boolean resynapse();
	
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
}
