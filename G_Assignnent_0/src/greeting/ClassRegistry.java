package greeting;

public class ClassRegistry implements gradingTools.shared.testcases.greeting.GreetingClassRegistry
{
	@Override 
	public Class<?> getGreetingMain() {
		return Hello.class;
	}

}
