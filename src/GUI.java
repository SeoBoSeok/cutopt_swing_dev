

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

class GUI {
	JFrame mainFrame = null;

	//시트의 컨트롤
	JCanvas jc;
	JSpinner SpinBox1;
	JSpinner SpinBox2;
	JTable jt;
	JLabel jLabel0;
	JCheckBox jCheck0;
	JCheckBox jCheck1;
	JProgressBar jProgress;
	JButton btn;
	JButton btnPrint;
	JButton btnReset;
	JComboBox<String> combo1;
	JPanel hp;
	JPanel rp;

	//테이블 데이터
	Object[][] rawdata = new Object[1000][5];

	//계산 대상
	Solution solution;
	Panel board = new Panel(2440, 1220);
	ArrayList<Panel> panelList;
	JProgressBar progress;

	@SuppressWarnings("serial")
	public GUI() {

		mainFrame = new JFrame();
		mainFrame.setTitle("PECO");

		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(0);
			}
		});

		//기본 화면 크기 설정
		mainFrame.setSize(800, 800);

		//컨트롤 초기화
		//표시할 SpinBox1 시트
		SpinBox1 = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
		SpinBox1.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged( ChangeEvent e ) {
				jc.repaint();
			}
		});
		
		//톱날두께 설정
		SpinBox2 = new JSpinner(new SpinnerNumberModel(3, 0, 10, 0.1));

		//계산 버튼 실행
		btn = new JButton(I18N.getString("Calculer"));

		ActionListener actCalculer=new ActionListener(){ //ActionListener는 버튼 누르면 실행
			public void actionPerformed(ActionEvent e) {
				int row=0; //기본 열 값은 0
				try {
					
					//패널리스트 생성
					if (panelList != null) //목록이 비어 있지 않으면? 
						panelList.clear(); //목록을 비워라
					else
						panelList = new ArrayList<Panel>(); //panelList는 (Reference, Quantity, Width, Height, Grain)을 ArrayList형태

					double cutLine =  Double.parseDouble(SpinBox2.getValue().toString()); //SpinBox2 는 톱 두께 값 get - string으로 - double로

					String[] format = ((String)combo1.getEditor().getItem()).split("x"); //??combo1은 판재의 크기, ["2500" , "1220"]
					board=new Panel(Double.parseDouble(format[0])+cutLine ,Double.parseDouble(format[1])+cutLine); // [2503 , 1223]
					
					//패널리스트에 추가
					for (row=0 ; row<jt.getRowCount() ; row++) //jtable 전체행을 panelList에 입력해라
						if (jt.getValueAt(row, 0) != null){ //jtable 행이 비어있지 않다면
							
							panelList.add( new Panel((Double)jt.getValueAt(row, 2)+cutLine, //Width+톱두께
									(Double)jt.getValueAt(row, 3)+cutLine, //Height+톱두께
									(String)jt.getValueAt(row, 0), //Reference
									(Integer)jt.getValueAt(row, 1), //Quantity
									(Boolean)jt.getValueAt(row, 4) )); //Grain
						}

					
					Thread t = new Thread() {
						public void run() {
							//커서 변경 및 사용제한
							mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); //커서로딩표시
							for (int n=0 ; n<hp.getComponentCount(); n++){ //hp=jpanel
								hp.getComponent(n).setEnabled(false);
							}
							for (int n=0 ; n<rp.getComponentCount(); n++){ //rp=jpanel
								rp.getComponent(n).setEnabled(false);
							}
							jt.setEnabled(false); //jtable 사용 제한
							jProgress.setString(null); //jprogressBar 초기화
							jProgress.setValue(0);
							
							Solver solv=new Solver(board, panelList, jCheck0.isSelected()); //solver 실행 jCheck0는 quick boolean
							solv.go();
							
							int t;
							
							while((t=solv.isRunning())>0){ //solv 실행된 최종값을 t로 반환
								jProgress.setValue(8-t); // jprogressBar 8/8까지 로딩 화면 진행, 연산 끝날 때 까지 대기 시킴
							}
							setSolution(solv.getSolution()); // return lsolut.get(n)


							jProgress.setValue(0);
							jProgress.setString("Terminé");
							jt.setEnabled(true);
							for (int n=0 ; n<hp.getComponentCount(); n++){
								hp.getComponent(n).setEnabled(true);
							}
							for (int n=0 ; n<rp.getComponentCount(); n++){
								rp.getComponent(n).setEnabled(true);
							}
							mainFrame.setCursor(null);
						}
					};
					t.start();
				}
				catch(Exception err){
					JOptionPane.showMessageDialog(null, String.format(I18N.getString("Erreur_format"),row+1));
				}
			}
		};

		btn.addActionListener(actCalculer);

		//인쇄 버튼
		btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				PrinterJob pj = PrinterJob.getPrinterJob();
				pj.setJobName("MDF");
				pj.setCopies(1);
				PageFormat format = pj.defaultPage();
				format.setOrientation(PageFormat.PORTRAIT);

				pj.setPrintable(new Printable() {
					public int print(Graphics pg, PageFormat pf, int pageNum){
						if (pageNum > ((board.height * 0.15 + 10) * solution.nbrBoard() / pf.getImageableHeight())){
							return Printable.NO_SUCH_PAGE;
						}

						Graphics2D g2 = (Graphics2D) pg;
						g2.setBackground(Color.white);
						g2.translate(pf.getImageableX(), pf.getImageableY());
						jc.print(g2, pageNum);
						return Printable.PAGE_EXISTS;
					}
				});
				if (pj.printDialog() == false)
					return;

				try {
					pj.print();
				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});

		//리셋 버튼
		btnReset = new JButton(I18N.getString("Reinitialiser"));
		btnReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				rawdata= new Object[1000][7];
				jt.repaint();
			}
		});

		////
		//시트 선택

		//기본 목록
		if (! new File(_H.SheetsFile).exists()){
			String[] tmp_lst={"2500x1220", "2440x1220","2500x1250", "2440x1250","3050x1530", "2800x2070",
					"2800x1196", "2400x675", "2500x675","2400x600","2000x910","2050x925","2400x1205","5000x1000",
					"5000x1250","5000x2000", "5000x2050","2035x905"};
			try {
				//시트 치수를 파일에 저장
				PrintWriter out = new PrintWriter(new BufferedWriter( new FileWriter(_H.SheetsFile)));
				for (int i=0; i<tmp_lst.length; i++){
					out.write(tmp_lst[i]);
					out.println();
				}
				out.flush();
				out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		final ArrayList<String> predef = new ArrayList<String>();

		//파일에서 시트 치수 로드
		try {
			BufferedReader in = new BufferedReader(new FileReader(_H.SheetsFile));
			String ln;
			while ((ln=in.readLine()) != null)
				predef.add(ln);
			in.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		//시트 크기 드롭다운 목록
		combo1 = new JComboBox(predef.toArray());
		combo1.setEditable(true);
		combo1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//편집할 때
				JComboBox cb = (JComboBox)e.getSource();
				String format = (String)cb.getEditor().getItem();
				if (format.indexOf("x") != -1){
					//새 시트 저장
					if (predef.indexOf(format)==-1){
						predef.add(format);
						try {
							PrintWriter out = new PrintWriter(new BufferedWriter( new FileWriter(_H.SheetsFile, true)));
							out.write(format);
							out.println();
							out.flush();
							out.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				else
					JOptionPane.showMessageDialog(null,I18N.getString("Format_incorrect"));
			}
		});

		//상단 바
		hp= new JPanel();
		hp.add(new JLabel(I18N.getString("Taille_board")));
		hp.add(combo1);
		jCheck0 = new JCheckBox(I18N.getString("Methode_simplifiee"),false);
		hp.add(jCheck0);
		hp.add(new JPanel());
		hp.add(new JLabel(I18N.getString("Largeur_cutLine")));
		hp.add(SpinBox2); 
		jProgress=new JProgressBar();
		jProgress.setMaximum(8);
		hp.add(jProgress);
		mainFrame.getContentPane().add(hp, BorderLayout.NORTH);

		//오른쪽 막대
		rp = new JPanel();
		rp.setLayout(new BoxLayout(rp,BoxLayout.PAGE_AXIS));
		rp.add(btnReset);
		rp.add(btn);
		rp.add(btnPrint);
		rp.add(new JLabel(I18N.getString("Afficher_board")));
		JPanel srp=new JPanel();
		srp.setMaximumSize(new Dimension(80,20));
		srp.setLayout(new BoxLayout(srp,BoxLayout.LINE_AXIS));
		srp.add(SpinBox1);
		jLabel0 = new JLabel(String.format(I18N.getString("sur"),0));
		srp.add(jLabel0);
		rp.add(srp);
		mainFrame.getContentPane().add(rp, BorderLayout.EAST);

		//테이블
		jt = new JTable();
		jt.setModel(new AbstractTableModel(){
			public String getColumnName(int col) {
				String[] names = {I18N.getString("Repere"),I18N.getString("Quantite"),
						I18N.getString("Largeur"),I18N.getString("Hauteur"),I18N.getString("Garder_fil")};
				return names[col];
			}
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return Double.class;
                    case 3:
                        return Double.class;
                    default:
                        return Boolean.class;
                }
            }
			public int getColumnCount() {
				return 5;
			}
			public int getRowCount() {
				return 100;
			}
			public Object getValueAt(int row, int col) {
				if (row < rawdata.length){
					if (row==jt.getEditingRow() && col==jt.getEditingColumn())
						jt.getCellEditor().stopCellEditing();
					if (col == 4){
						if (rawdata[row][4]==null)
							rawdata[row][4]=false;
					}
					return rawdata[row][col];
				}else
					return null;
			}
			public boolean isCellEditable(int row, int col) {
				return true;
			}
			public void setValueAt(Object aValue, int row, int col){
				rawdata[row][col]=aValue;
				jt.repaint();
			}
		});
		jt.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		jt.setRowSelectionAllowed(false);
		jt.setColumnSelectionAllowed(false);
		jt.setCellSelectionEnabled(true);

		JScrollPane scrollpane = new JScrollPane(jt);
		mainFrame.getContentPane().add(scrollpane, BorderLayout.CENTER);

		//광고 게시판
		jc = new JCanvas(solution, SpinBox1, SpinBox2, jLabel0);
		jc.setBackground(Color.WHITE);
		jc.setPreferredSize(new Dimension(400,600));
		mainFrame.getContentPane().add(jc,BorderLayout.SOUTH);

		//클립보드 지원
		@SuppressWarnings("unused")
		ExcelAdapter myAd = new ExcelAdapter(jt);

		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
	}

	public void afficher() {
		mainFrame.setVisible(true);
	}

	public void setTitle(String title) {
		mainFrame.setTitle(title);
	}

	public void setSolution(final Solution sol){
		solution= sol;

		jc.solution = sol;
		jc.board = board;
		jc.repaint(1);
	}

	public void setBoard(Panel feui){
		board= feui;
	}
}