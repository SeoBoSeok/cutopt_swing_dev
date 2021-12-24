import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/** 솔루션의 캔버스 표시 */
@SuppressWarnings("serial")
class JCanvas extends JPanel {

	Solution solution;
	Panel board;
	JSpinner SpinBox1;
	JSpinner SpinBox2;
	JLabel jLabel0;
	double zoom = 0.4;

	public JCanvas(Solution sol, JSpinner sp_board, JSpinner sp_cutLine, JLabel lbl){
		super();
		solution = sol;
		SpinBox1 = sp_board;
		SpinBox2 = sp_cutLine;
		jLabel0 = lbl;
		super.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (arg0.getWheelRotation()<0)
					zoom *= 1.5;
				else
					zoom /= 1.5;
				repaint();
			}});
	}

	public void paint(Graphics g) {

		if(solution == null || board == null) return;
		int fmax = solution.nbrBoard();
		if (fmax==0) return;

		Font f1, f2;
		f1 = new Font("deja vu sans",Font.PLAIN, 20);
		f2 = f1.deriveFont((float) 10);

		g.setColor(Color.white);

		Rectangle clip = g.getClipBounds();
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		if (! super.isPaintingForPrint()){
			int spinval = Integer.parseInt(SpinBox1.getValue().toString());
			if (spinval==0)
				spinval=1;

			SpinBox1.setModel(new SpinnerNumberModel(spinval,1,fmax,1));
			if (spinval > fmax)
				SpinBox1.setValue(fmax);
			jLabel0.setText("sur "+ (fmax));

			paintSheet(g, spinval-1, 0, 0, zoom, f1, f2);
		}
	}

	public void print(Graphics g, int pageNum){
		firePropertyChange("paintingForPrint", false, true);

		g.setColor(Color.white);

		Rectangle clip = g.getClipBounds();
		//g.fillRect(clip.x,clip.y,clip.width,clip.height);

		Font f1, f2;
		f1 = new Font("deja vu sans",Font.PLAIN, 10);
		f2 = f1.deriveFont((float) 7);

		double zm=0.15;
		int n0 = (int)(pageNum * clip.height/(board.height * zm + 10) );
		for (int n=n0 ; board.height * zm * (n-n0+1) < clip.height ; n++)
			paintSheet(g, n, 0, (int)((board.height * zm +10) * (n-n0)), zm, f1, f2);


		g.setFont(f1);
		g.drawString(I18N.getString("pied_de_page"), 0, (int)(clip.height - f1.getSize2D()-2));

		firePropertyChange("paintingForPrint", true, false);
	}

	public void paintSheet(Graphics g, int sheetNum, int posX, int posY, double scaleFactor, Font f1, Font f2){

		PanelPosition p;

		//직사각형
		for (int n=0 ; n<solution.proposition.size() ; n++){
			p= solution.proposition.get(n);
			if (p.numBoard != sheetNum) continue;
			g.setColor(Color.white);
			g.fillRect((int)(posX+ p.position.x* scaleFactor), (int)(posY+ p.position.y * scaleFactor), (int)(p.width * scaleFactor), (int)(p.height * scaleFactor));
			g.setColor(Color.darkGray);
			g.drawRect((int)(posX+ p.position.x * scaleFactor), (int)(posY+ p.position.y * scaleFactor), (int)(p.width * scaleFactor), (int)(p.height * scaleFactor));
		}
		for (int n = 0 ; n<solution.stocks.size() ; n++){
			p = solution.stocks.get(n);
			if( p.numBoard != sheetNum) continue;
			g.setColor(Color.darkGray);
			g.drawLine((int)(posX+ p.position.x * scaleFactor), (int)(posY+ p.position.y * scaleFactor),(int)(posX+ (p.position.x + p.width) * scaleFactor), (int)(posY+ (p.position.y + p.height)* scaleFactor));
			g.drawLine((int)(posX+ (p.position.x + p.width)* scaleFactor), (int)(posY+ p.position.y * scaleFactor),(int)(posX+ p.position.x * scaleFactor), (int)(posY+ (p.position.y + p.height)* scaleFactor));
			g.drawRect((int)(posX+ p.position.x * scaleFactor), (int)(posY+ p.position.y * scaleFactor), (int)(p.width * scaleFactor), (int)(p.height * scaleFactor));
		}

		//텍스트

		double coupval=Double.parseDouble(SpinBox2.getValue().toString());

		g.setColor(Color.black);
		for (int n=0 ; n<solution.proposition.size() ; n++){
			p= solution.proposition.get(n);
			if (p.numBoard != sheetNum) continue;
			//경계표
			g.setFont(f1);
			g.drawString(p.reference, (int)(posX+ p.position.x * scaleFactor + 2),(int)(posY+ p.position.y * scaleFactor+f1.getSize2D()+2));
			//치수
			g.setFont(f2);
			g.drawString(String.valueOf(p.width-coupval)+"x"+String.valueOf(p.height-coupval), (int)(posX+ p.position.x * scaleFactor + 2),(int)(posY+ p.position.y * scaleFactor+f1.getSize2D()+f2.getSize2D()+2));
		}
	}
}