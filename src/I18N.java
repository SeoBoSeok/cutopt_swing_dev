import java.util.Locale;
import java.util.ResourceBundle;

//����ȭ Ŭ����
//���ҽ� ������ ���ڿ��� �н��ϴ�.
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
