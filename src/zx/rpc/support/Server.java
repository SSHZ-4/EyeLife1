package zx.rpc.support;

import zx.rpc.protocal.Invocation;
/*
 * server�ӿڣ������·���������ʵ������RPC���ڲ���RPCServer��
 * 
 */
public interface Server {
	public void stop();
	public void start();
	public void register(Class interfaceDefiner,Class impl);
	public void call(Invocation invo);
	public boolean isRunning();
	public int getPort();
}
