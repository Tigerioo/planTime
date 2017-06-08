package cn.minihand.plantime.test;

import java.lang.reflect.TypeVariable;

public class ObjectTypeTest {

	public static void main(String[] args) {
		String a = "123";
		int b = 1;
		getType(a, b);
	}
	
	private static void getType(Object objName, Object objValue){
		
		System.out.println(objName.getClass().getSimpleName() + "=" + objValue);
	}
}
