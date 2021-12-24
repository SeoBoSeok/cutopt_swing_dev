import java.util.ArrayList;

/**
 * 
 */
public class ThreadArrange extends Thread { 
	private Solution solut;
	private Panel board; //패널의 치수
	private ArrayList<Panel> panelList; //패널의 목록
	private boolean quick;
	private int cas;
	
	/**
	 * 
	 * @param board
	 * @param panelList
	 * @param quick
	 * @param align
	 * @param cas : 8개 분기 중 검색을 시작할 분기로 정의
	 */
	public ThreadArrange(Panel lBoard, ArrayList<Panel> lpanelList, boolean lquick, int lcas) { 
		board=lBoard;
		panelList=lpanelList;
		quick=lquick;
		cas=lcas;
	}
	
	public void run(){
		solut = Algo.Arrange(board, panelList, quick, cas); //Arrange(Panel Board, ArrayList<Panel> PanelList, boolean quick, int cas)
	}

	public Solution getSolution() {
		return solut;
	}

}
