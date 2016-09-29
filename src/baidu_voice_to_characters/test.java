package baidu_voice_to_characters;

import tureing.robot.Tureing;

public class test
{
	public static void main(String[] args) throws Exception
	{
		getWords s = new getWords();
		String result=s.listen("zhangxu.mp3");
		result= Tureing.ask(result);
		System.out.println(result);
	}

}
