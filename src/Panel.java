import java.util.ArrayList;

class Panel {

	public double width; // 너비
	public double height; // 높이
	public String reference; // 입력한 셀에서의 레퍼런스(참조할 데이터)
	public int quantity; // 수량
	public boolean grain; // 나무결 고려여부

	public double getSurface(){
		return width*height;
	}

	public Panel(Panel pan){
		width = pan.width;
		height = pan.height;
		reference = pan.reference;
		quantity = pan.quantity;
		grain = pan.grain;
	}

	public Panel(double width2, double height2) {
		width = width2;
		height = height2;
	}

	public Panel(double width2, double height2, String reference2) {
		width = width2;
		height = height2;
		reference = reference2;
	}

	public Panel(double width2, double height2, String reference2, int quantity2) {
		width = width2;
		height = height2;
		reference = reference2;
		quantity = quantity2;
	}
	
	public Panel(double width2, double height2, String reference2, int quantity2, boolean grain2) {
		width = width2;
		height = height2;
		reference = reference2;
		quantity = quantity2;
		grain = grain2;
	}

	/**시트 번호를 -1로 설정하여 패널이 들어가지 않음을 나타냅니다.*/ 
	public PanelPosition exceptBoard(){
		PanelPosition p = new PanelPosition(this);
		p.numBoard = -1;
		return p;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isGrain() {
		return grain;
	}

	public void setGrain(boolean grain) {
		this.grain = grain;
	}

	public Panel clone(){
		Panel res = new Panel(this);
		return res;
	}

	/**제공된 Panelx 목록을 증가하는 영역 순으로 정렬
	 * @param tabl : 정렬할 목록
	 * @return 새로운 정렬 목록 */ 
	public static ArrayList<Panel> TriSurface(ArrayList<Panel> tabl){

		Panel p;
		int j;
		
		for(int i = 1 ; i<tabl.size() ; i++){
			p = tabl.get(i);
			j = i;
			while (j > 0){ // b.너비x폭 > a.너비x폭
				if (p.getSurface() > tabl.get(j-1).getSurface()){ // b에다가 a 데이터를 저장하겠다
					tabl.set(j, tabl.get(j-1)); // j에 j-1을 세팅(저장)
					j -= 1;
				}else{
					break;
				}
			}
			tabl.set(j, p); // 자기 자신에 자기 값을 넣는거라 무의미해보임
		}
		System.out.println("tabl" + tabl);
		return tabl;
	}
}