import java.util.ArrayList;

/** �ַ���� �����ϴ� Ŭ���� */
class Solution {

	ArrayList<PanelPosition> proposition = new ArrayList<PanelPosition>(0);
	// �г������� ������ �����͸� ���� ����Ʈ //
	ArrayList<PanelPosition> stocks = new ArrayList<PanelPosition>(0);
	// �г� �����͸� ���� ����Ʈ
	ArrayList<Panel> rest = new ArrayList<Panel>(0);

	public int nbrBoard(){
		if (proposition==null) // Ȥ�ó� �� ������ ����� ����
			return 0;
		int fmax=1, m=0;
		for (int n=0 ; n<proposition.size() ; n++){
			m = proposition.get(n).numBoard+1;
			if (m > fmax)
				fmax = m;
		}
		return m;
	}
}