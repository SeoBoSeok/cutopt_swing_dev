import java.util.ArrayList;

/** 솔루션을 저장하는 클래스 */
class Solution {

	ArrayList<PanelPosition> proposition = new ArrayList<PanelPosition>(0);
	// 패널포지션 형태의 데이터를 가진 리스트 //
	ArrayList<PanelPosition> stocks = new ArrayList<PanelPosition>(0);
	// 패널 데이터를 가진 리스트
	ArrayList<Panel> rest = new ArrayList<Panel>(0);

	public int nbrBoard(){
		if (proposition==null) // 혹시나 모를 오류를 대비해 쓴것
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