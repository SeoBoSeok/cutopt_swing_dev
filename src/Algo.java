

import java.util.ArrayList;

/* 절단 계산 알고리즘: 재귀 무차별 대입 */
class Algo {

	/**
	 *
	 * @param Board : 사용 가능한 시트의 치수
	 * @param PanelList : 인출할 패널 목록
	 * @param quick : 알고리즘의 더 간단한 버전을 사용
	 * @return 지정된 치수의 시트에 배치된 패널 세트
	 */
	public static Solution Arrange(Panel Board, ArrayList<Panel> PanelList, boolean quick){
		return Algo.Arrange(Board, PanelList, quick, 0);
	}
	
	/**
	 *
	 * @param cas 어떤 경우에 계산을 제한해야 하는지
	 **/ 
	public static Solution Arrange(Panel Board, ArrayList<Panel> PanelList, boolean quick, int cas){

		Solution solution = new Solution();	//함수 결과 // Solution 형태를 가진 solution 객체
		Solution solutionp = new Solution(); //루프용 변수 // Solution 형태를 가진 solutionp 객체
		PanelPosition board_entire; // PanneauPositionne 형태를 가진 board_entire 객체
		
		//제안 목록 초기화
		solution.proposition = new ArrayList<PanelPosition>();
		//드롭 리스트 초기화
		solution.stocks = new ArrayList<PanelPosition>();


		//절단할 시트의 초기화
		board_entire = new PanelPosition(Board.width, Board.height);
		board_entire.position = new Point(0, 0);
		
		//모든 패널이 절단, 표면 순서대로 정렬
		solutionp.rest = Panel.TriSurface(PanelList);
		
		//절단할 패널이 있고 && 사용된 시트가 100장 미만
		while (solutionp.rest.size() > 0 && board_entire.numBoard<100){
			//가능한 모든 패널을 시트에 배치
			solutionp.proposition = new ArrayList<PanelPosition>();
			solutionp = RecArrange(board_entire, solutionp, quick, cas);
			
			solution.stocks.addAll(solutionp.stocks);
			if (solutionp.proposition.size()>0)
				//결과에서 위치를 전송
				solution.proposition.addAll(solutionp.proposition);
			else{
				//나머지 패널은 시트를 통과하지 않습니다
				for(int n=0 ; n<solutionp.rest.size(); n++){
					//나머지는 템플릿에서 선언하여 최종 결과로 전송
					solution.proposition.add(solutionp.rest.get(n).exceptBoard());
				}
				break;
			}
			//다음 시트로 이동
			board_entire.numBoard ++;
		}
		
		return solution;
	}

	/** 기존 솔루션에 패널 추가 */
	private static Solution PreparerSolution (Solution solution_1, PanelPosition sol){

		Solution newSolution = new Solution();
		
		//이미 배치된 모든 패널 전송
		newSolution.proposition.addAll(solution_1.proposition);
		newSolution.proposition.add(sol);
		
		//나머지 패널마다
		for (int n=0 ; n<solution_1.rest.size() ; n++){
			Panel tmp = solution_1.rest.get(n).clone();
			//통과하면
			if ((tmp.width==sol.width && tmp.height==sol.height)
			|| (tmp.width==sol.height && tmp.height==sol.width && ! tmp.grain)){
				//나머지에서 하나를 추론
				if (tmp.quantity>1){
					tmp.quantity--;
					System.out.println("tmp" + tmp);
					newSolution.rest.add(tmp);
				}else{
					System.out.println("remove " + n);
					solution_1.rest.remove(n);
				}
			}else{
				//통과하지 못하면 새 솔루션에 남아 있습니다.
				newSolution.rest.add(tmp);
			}
		}
		System.out.println("newSolution" + newSolution);
		return newSolution;

	}
	
	/**모든 경우를 계산하여 해결하기 위한 바로 가기 */
	private static Solution RecArrange(PanelPosition Board, Solution solution, boolean quick){
		return RecArrange(Board,solution,quick,0);
	}
			
	/**
	 * 재귀 계산
	 * @param Board : 패널을 배치할 시트 또는 스크랩
	 * @param solution : 솔루션 상자
	 * @param quick : 단순화된 방법의 사용
	 * @param cas : 8개 분기 중 검색을 시작할 분기로 정의, 모두 0
	 */
	private static Solution RecArrange(PanelPosition Board, Solution solution, boolean quick, int cas){

		PanelPosition stock1;
		PanelPosition stock2;
		PanelPosition sol;

		//가능성의 나무의 8가지
		ArrayList<Solution> nsolution = new ArrayList<Solution>(8);
		Solution currSol;

		Panel pan;
		boolean panpasse;

		//더이상 할 일이 없을 때

		if(Board.getSurface() == 0){ // (너비x폭 ==0) 이면 더이상자르지 않겠다
			return solution;
		}

		//모든 패널이 배치되고 현재 시트가 드롭
		if(solution.rest.size() == 0){
			solution.stocks.add(Board);
			return solution;
		}
		
		//가장 작은 패널은 통과하지 못하고 현재 시트는 사용할 수 없는 스크랩
		pan = solution.rest.get(solution.rest.size()-1); // solution.reste<a,b,c,d,e> reste에 마지막 데이터
		if(pan.getSurface() > Board.getSurface()){ // pan이 남은 면적
			solution.stocks.add(Board); // olution.stocks 리스트 <Board>
			return solution;
		}

		//가장 큰 것부터 가장 작은 것까지 패널 테스트
		// reste a , b, c , d 
		for(int n=0 ; n<solution.rest.size() ; n++){
			pan = solution.rest.get(n);  // reste <a,b,c,d,e> 차례대로 저장
			panpasse = false;

			//시트에 비해 패널이 너무 크면 다음 패널로 넘어갑니다.
			if (pan.getSurface() > Board.getSurface()) // reste의 현재 너비가 전달받은 팬의 너비보다 크다면 다음레스트 비교
				continue;
			
			//시트는 수평으로 간주 = 너비(x), 높이(y)

			// pan = a ( 자를거)  Board = 전체
			// 패널이 수평으로 들어가는 경우
			if (pan.width <= Board.width && pan.height <= Board.height) {
				sol = new PanelPosition(pan.width, pan.height, pan.reference, Board.numBoard);
				sol.position.set(Board.position);
				
				//첫 번째 사례: 수직 절단
				// 너비(x), 높이(y)
				stock1 = new PanelPosition(pan.width, Board.height - pan.height, "", Board.numBoard);
				// stock1의 패널데이터
				stock1.position.x = Board.position.x;
				stock1.position.y = Board.position.y + pan.height;
				// stock1의 좌표 저장
				stock2 = new PanelPosition(Board.width - pan.width, Board.height, "", Board.numBoard);
				// stock2의 패널데이터
				stock2.position.x = Board.position.x + pan.width;
				stock2.position.y = Board.position.y;
				// stock2의 좌표 저장
				System.out.println("stock1 x" + stock1.position.x);
				System.out.println("stock1 y" +  stock1.position.y);
				System.out.println("stock2 x" + stock2.position.x);
				System.out.println("stock2 y" + stock2.position.y);
				
				
				if (cas==0 || cas==1){
					System.out.println("cas==0 || cas==1");
					currSol=PreparerSolution(solution, sol); // nouvelleSolution 데이터 저장
					currSol=RecArrange(stock1, currSol, quick);  // stock1으로 재귀실행
					currSol=RecArrange(stock2, currSol, quick);  // stock2로 재귀실행
					nsolution.add(currSol);
				}
				if (! quick && (cas==0 || cas==2)){
					System.out.println("! quick && (cas==0 || cas==2");
					//우리가 시험하는 순서를 반대로 하여 같은 것
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock2, currSol, quick);
					currSol=RecArrange(stock1, currSol, quick); // 위와 다르게 stock2먼저, 그다음 stock1
					nsolution.add(currSol);
				}
				
				//두 번째 이벤트: 가로 컷 스루
				// 1)cas = 0,1,2 일때 Board = stock1, stock2
				// 2)cas = 0,1,2 가 아닌 숫자일때 -> Board = Board
				stock1 = new PanelPosition(Board.width, Board.height - pan.height, "", Board.numBoard);
				stock1.position.x = Board.position.x;
				stock1.position.y = Board.position.y + pan.height;
				stock2 = new PanelPosition(Board.width - pan.width, pan.height, "", Board.numBoard);
				stock2.position.x = Board.position.x + pan.width;
				stock2.position.y = Board.position.y;
				
				if (cas==0 || cas==3){
					System.out.println("cas==0 || cas==3");
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock1, currSol, quick);
					currSol=RecArrange(stock2, currSol, quick);
					nsolution.add(currSol);
				}

				if (! quick && (cas==0 || cas==4)){
					System.out.println("! quick && (cas==0 || cas==4");
					//우리가 시험하는 순서를 반대로 하여 같은 것
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock2, currSol, quick);
					currSol=RecArrange(stock1, currSol, quick);
					nsolution.add(currSol);
				}

				panpasse = true; // cas != 0,1,2,3,4 // 길이는 맞지만 cas =5,6,7,8
			}

			//패널이 수직으로 들어가고 와이어를 유지하지 않는 경우
			if(pan.width <= Board.height && pan.height <= Board.width && ! pan.grain) {
				sol = new PanelPosition(pan.height, pan.width, pan.reference, Board.numBoard);
				sol.position.set(Board.position);

				//첫 번째 사례: 수직 절단
				stock1 = new PanelPosition(pan.height, Board.height - pan.width, "", Board.numBoard);
				stock1.position.x = Board.position.x;
				stock1.position.y = Board.position.y + pan.width;
				stock2 = new PanelPosition(Board.width - pan.height, Board.height, "", Board.numBoard);
				stock2.position.x = Board.position.x + pan.height;
				stock2.position.y = Board.position.y;
				
				System.out.println("stock1 x" + stock1.position.x);
				System.out.println("stock1 y" +  stock1.position.y);
				System.out.println("stock2 x" + stock2.position.x);
				System.out.println("stock2 y" + stock2.position.y);
				
				if (cas==0 || cas==5){
					System.out.println("cas==0 || cas==5");
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock1, currSol, quick);
					currSol=RecArrange(stock2, currSol, quick);
					nsolution.add(currSol);
				}

				if (! quick && (cas==0 || cas==6)){
					System.out.println("! quick && (cas==0 || cas==6");
					//우리가 시험하는 순서를 반대로 하여 같은 것
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock2, currSol, quick);
					currSol=RecArrange(stock1, currSol, quick);
					nsolution.add(currSol);
				}

				//두 번째 이벤트: 가로 컷 스루
				stock1 = new PanelPosition(Board.width, Board.height - pan.width, "", Board.numBoard);
				stock1.position.x = Board.position.x;
				stock1.position.y = Board.position.y + pan.width;
				stock2 = new PanelPosition(Board.width - pan.height, pan.width, "", Board.numBoard);
				stock2.position.x = Board.position.x + pan.height;
				stock2.position.y = Board.position.y;

				if (cas==0 || cas==7){
					System.out.println("cas==0 || cas==7");
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock1, currSol, quick);
					currSol=RecArrange(stock2, currSol, quick);
					nsolution.add(currSol);
				}

				if (! quick && (cas==0 || cas==8)){
					System.out.println("! quick && (cas==0 || cas==8");
					//우리가 시험하는 순서를 반대로 하여 같은 것
					currSol=PreparerSolution(solution, sol);
					currSol=RecArrange(stock2, currSol, quick);
					currSol=RecArrange(stock1, currSol, quick);
					nsolution.add(currSol);
				}

				panpasse = true;  // 수직으로 한게 길이가 맞았는데, cas=1,2,3,4면 true
			}

			if(panpasse)
				break;
		}

		if (nsolution.size() == 0){
			//신호 없음
			solution.stocks.add(Board);
			return solution;
		}

		int maxi2 = FiltreSolution(nsolution); // nsolution.size가 0이 아닐때
		
		for(int n = 0; n < nsolution.size(); n++) {
			System.out.println("nsolution output" + nsolution.get(n));
		}
		
		if(solution.stocks != null)  // solution.stocks 에 데이터가 있을때
			nsolution.get(maxi2).stocks.addAll(solution.stocks);
		return nsolution.get(maxi2);
	}
	
	/**
	 * 제공된 목록에서 최상의 솔루션
	 * @param 필터링할 솔루션 목록
	 * @return 최고의 솔루션 인덱스
	 */
	public static int FiltreSolution(ArrayList<Solution> nSolution){
		
		// debug
		for (int n = 0; n < nSolution.size(); n++) {
			if (nSolution.get(n).proposition.size() > 0) {
				System.out.println("nSolution proposition" + nSolution.get(n).proposition);
				for (int j = 0; j < nSolution.get(n).proposition.size(); j ++) {
					
					System.out.println(nSolution.get(n).proposition.get(j).getWidth());
					System.out.println(nSolution.get(n).proposition.get(j).getHeight());
					System.out.println(nSolution.get(n).proposition.get(j).getReference());
					System.out.println(nSolution.get(n).proposition.get(j).getQuantity());
					
				}
				System.out.println("nSolution rest" + nSolution.get(n).rest);
//				for (int j = 0; j < nSolution.get(n).rest.size(); j ++) {
//					
//					System.out.println(nSolution.get(n).rest.);
//					
//				}
			}
		}
		
		//가장 많은 패널을 가져오는 솔루션 선택
		//Solution 의 proposition이 가장 큰값 = maxi
		int maxi = 0;
		for (int n = 0 ; n<nSolution.size() ; n++){
			if ( nSolution.get(n) != null){
				if (nSolution.get(maxi).proposition.size() < nSolution.get(n).proposition.size()) {
					maxi = n;
				}else if (nSolution.get(maxi) == null) {
					maxi = n + 1;
				}
			}
		}

		//가장 많은 패널을 가져오는 솔루션 중 가장 적게 떨어지는 솔루션 선택
		// Solution 의 proposition랑 같은 데이터 중, stocks가 가장 작은값 = mini
		int mini = maxi;
		for (int n=0 ; n<nSolution.size() ; n++){
			if (nSolution.get(n) != null) {
				if (nSolution.get(n).proposition.size() == nSolution.get(maxi).proposition.size()) {
					if (nSolution.get(mini).stocks.size() > nSolution.get(n).stocks.size())
						mini = n;
				}
			}
		}

		//가장 적게 떨어지는 것 중에서 가장 크게 떨어지는 것을 선택하는 것
		// 현재 비교하는 nSolution의 cute의 개수가 가장작은 cute의 개수랑 같으면서, nSolution의 proposition이  가장 큰 proposition의 개수가 같을때
		int maxi2 = mini;
		double maxisurf = 0;
		for (int n=0 ; n<nSolution.size() ; n++)
			if (nSolution.get(n) != null)
				if (nSolution.get(n).stocks.size() == nSolution.get(mini).stocks.size()
				&& nSolution.get(n).proposition.size() == nSolution.get(maxi).proposition.size())
					for (int m=0 ; m<nSolution.get(n).stocks.size() ; m++)
						if (nSolution.get(n).stocks.get(m).getSurface() > maxisurf)
							maxi2 = n;
		System.out.println("maxi2" + maxi2);
		return maxi2;
	}


}