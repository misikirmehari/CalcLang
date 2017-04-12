import java.util.HashMap;

public class memory {
	@SuppressWarnings("rawtypes")
	public static HashMap hash = new HashMap();
	public static String name ;
	public static double number;


	@SuppressWarnings("unchecked")
	public static void insert(String s , double num){

		name = s;
		number = num;
		hash.put(name, number);

	}
}
