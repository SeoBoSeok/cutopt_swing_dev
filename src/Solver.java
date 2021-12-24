import java.util.ArrayList;
import java.util.Iterator;


public class Solver {

	private ArrayList<ThreadArrange> lthreads; //lthreads 정의
	
	// lthreads 8개 생성(알고리즘 8번 돌릴 수 있도록) 
	Solver(Panel Board, ArrayList<Panel> PanelList, boolean quick){ //Panel에 Board 값을 넣은 값 , ArrayList<Panel>에 PanelList 값을 넣은 값, quick boolean
		lthreads=new ArrayList<ThreadArrange>(8);  //ThreadArrange(board, panelList, quick, cas)
		for (int i=1 ; i<9 ; i++)
			lthreads.add(new ThreadArrange(Board, PanelList,  quick,  i)); //lthreads 생성, ThreadArrange에서 cas를 1부터 8까지 순차적으로 입력 리스트
	}
	
	//스레드 시작
	//?? Iterator는 컬렉션에 저장된 요소를 읽어옴, lthreads를 순차적으로 모두 읽어옴
	public void go(){
		Iterator<ThreadArrange> it = lthreads.iterator(); 
		while (it.hasNext()){
			it.next().start();
		}
	}
	
	//활성 스레드의 수 반환
	public int isRunning(){
		int ret=0;
		Iterator<ThreadArrange> it = lthreads.iterator();
		while (it.hasNext()) {
			if (it.next().isAlive())
				ret++;
		}
		return ret;
	}
	

	public Solution getSolution(){
		//계산이 완료되지 않은 경우 유효한 솔루션 없음
		if (isRunning()>0) //?? 계산이 계속 진행되고 있으면 멈춰라?
			return null;
		
		//각 스레드에 저장된 솔루션 검색
		Iterator<ThreadArrange> it = lthreads.iterator();
		ArrayList<Solution> lsolut = new ArrayList<Solution>(lthreads.size()); //lsolut = lthreads개수 리스트?
		while (it.hasNext()){
			lsolut.add(it.next().getSolution()); //getSolution class에서는 solut값을 return
		}
		
		//최적의 솔루션 찾기
		int n = Algo.FiltreSolution(lsolut); //?? lsolut 의 값(들?)로 FiltreSolution 실행? 
		return lsolut.get(n); //최종 리턴값은?
	}
}
