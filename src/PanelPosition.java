class PanelPosition extends Panel {

	public Point position = new Point();
	public int numBoard;

	public PanelPosition(double width, double height) {
		super(width, height);
	}
	public PanelPosition(double width, double height, String reference, int numBoard) {
		super(width, height, reference);
		this.numBoard = numBoard;
	}
	public PanelPosition(Panel Panel) {
		super(Panel.width, Panel.height, Panel.reference, Panel.quantity, Panel.grain);
	}

}