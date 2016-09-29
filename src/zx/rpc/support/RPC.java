package zx.rpc.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import zx.rpc.protocal.Invocation;


public class RPC {
	public static <T> T getProxy(final Class<T> clazz,String host,int port) {
		
		final Client client = new Client(host,port);
		InvocationHandler handler = new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Invocation invo = new Invocation();
				invo.setInterfaces(clazz);
				invo.setMethod(new zx.rpc.protocal.Method(method.getName(),method.getParameterTypes()));
				invo.setParams(args);
				client.invoke(invo);
				return invo.getResult();
			}
		};
		T t = (T) Proxy.newProxyInstance(RPC.class.getClassLoader(), new Class[] {clazz}, handler);
		return t;
	}
	
	
	public static class RPCServer implements Server{
		
		/*
		 * server��ʵ����
		 * ʵ�������·���
		 * 	
			public void start();//����һ��listener�̣߳��򿪷������˿ڣ����ϵ�ִ��   ��������---�ص� ���̣�ֱ����������isRunningΪfalse��
			public void register(Class interfaceDefiner,Class impl);	//ע������� ʵ���ࡣ ����newһ���������һ��hashmap�У��Ա����ʱʹ�á�
			public void call(Invocation invo);//��listenner�н��е��ã�����Invocation���ɿͻ��˴������Ķ���������listener��ִ�С�ִ�й�������
				������serviceEngine���õ�ʵ�����
			
			public boolean isRunning();
			public int getPort();
			public void stop();��running����Ϊfalse
		 */
		private int port = 20382;
		private Listener listener; 
		private boolean isRuning = true;
		private Map<String ,Object> serviceEngine = new HashMap<String, Object>();
		/**
		 * @param isRuning the isRuning to set
		 */
		public void setRuning(boolean isRuning) {
			this.isRuning = isRuning;
		}

		/**
		 * @return the port
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @param port the port to set
		 */
		public void setPort(int port) {
			this.port = port;
		}

		
		
		
		@Override
		public void call(Invocation invo) {
			System.out.println(invo.getClass().getName());
			Object obj = serviceEngine.get(invo.getInterfaces().getName());
			if(obj!=null) {
				try {
					Method m = obj.getClass().getMethod(invo.getMethod().getMethodName(), invo.getMethod().getParams());
					Object result = m.invoke(obj, invo.getParams());
					invo.setResult(result);
				} catch (Throwable th) {
					th.printStackTrace();
				}
			} else {
				throw new IllegalArgumentException("has no these class");
			}
		}

		@Override
		public void register(Class interfaceDefiner, Class impl) {
			try {
				this.serviceEngine.put(interfaceDefiner.getName(), impl.newInstance());
				System.out.println(serviceEngine);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		@Override
		public void start() {
			System.out.println("����������");
			listener = new Listener(this);
			this.isRuning = true;
			listener.start();
		}

		@Override
		public void stop() {
			this.setRuning(false);
		}

		@Override
		public boolean isRunning() {
			return isRuning;
		}
		
	}
}
	



	
	
	

