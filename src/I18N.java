import java.util.Locale;
import java.util.ResourceBundle;

//국제화 클래스
//리소스 파일의 문자열을 읽습니다.
public class I18N {
	static Locale locale;
	static ResourceBundle res;
	
	public static void prepare(){
		locale = Locale.getDefault();
		res = ResourceBundle.getBundle("Ressources", locale);
	}
	
	public static String getString(String key){
		return res.getString(key);
	}
	
}
