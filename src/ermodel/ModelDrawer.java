package ermodel;

import static ermodel.Common.*;

import java.text.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;

public class ModelDrawer {
	
	protected Shell shell;
	protected Canvas canvas;
	
	private MouseObject mouseObject = MouseObject.NONE;
	private Point mousePos = new Point(0, 0);
	private ArrayList<DrawObject> drawables = new ArrayList<>();
	private ArrayList<Connection> connections = new ArrayList<>();
	private Optional<DrawObject> selected = Optional.empty();
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ModelDrawer window = new ModelDrawer();
			window.open();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(2, false));
		
		Group tools = new Group(shell, SWT.NONE);
		tools.setLayout(new FillLayout(SWT.VERTICAL));
		tools.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		Button selectButton = new Button(tools, SWT.NONE);
		selectButton.setText("Select");
		
		Button entityButton = new Button(tools, SWT.NONE);
		entityButton.setText("Entity");
		
		Button relationButton = new Button(tools, SWT.NONE);
		relationButton.setText("Relation");
		
		Button attributeButton = new Button(tools, SWT.NONE);
		attributeButton.setText("Attribute");
		
		Button exportButton = new Button(tools, SWT.NONE);
		exportButton.setText("Export");
		
		Group canvasGroup = new Group(shell, SWT.NONE);
		canvasGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		canvasGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		canvas = new Canvas(canvasGroup, SWT.NONE);
		canvas.setFont(SWTResourceManager.getFont("Liberation Mono", 10, SWT.NORMAL));
		canvas.addMouseMoveListener(event -> {
			mousePos = new Point(event.x, event.y);
			if(mouseObject != MouseObject.NONE) {
				redraw();
			}
		});
		
		selectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mouseObject = MouseObject.NONE;
			}
		});
		entityButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mouseObject = MouseObject.ENTITY;
			}
		});
		relationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mouseObject = MouseObject.RELATION;
			}
		});
		attributeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				mouseObject = MouseObject.ATTRIBUTE;
			}
		});
		exportButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Image img = new Image(Display.getDefault(), canvas.getSize().x, canvas.getSize().y);
				GC gc = new GC(canvas);
				gc.copyArea(img, 0, 0);
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { img.getImageData() };
				SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
				loader.save(fileFormat.format(new Date()) + ".bmp", SWT.IMAGE_BMP);
				gc.dispose();
				img.dispose();
			}
		});
		
		canvas.addPaintListener(event -> {
			GC gc = event.gc;
			gc.setForeground(COLOUR_DEFAULT);
			
			draw(gc, new DrawObject(mouseObject, mousePos));			
			
			for(Connection con : connections) {
				gc.drawLine(con.from.position.x, con.from.position.y, con.to.position.x, con.to.position.y);
			}
			
			for(DrawObject drawObject : drawables) {
				// debug ovals for connectors
//				gc.setForeground(COLOUR_SELECTED);
//				for(Connector con : drawObject.connectors) {
//					gc.drawOval(con.position.x - 5, con.position.y - 5, 10, 10);
//				}
				
				gc.setForeground((selected.isPresent() && drawObject.equals(selected.get())) ? COLOUR_SELECTED : COLOUR_DEFAULT);
				
				draw(gc, drawObject);
			}
		});
		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// right click
				if(e.button == 3) {
					selected = Optional.empty();
					mouseObject = MouseObject.NONE;
					redraw();
					return;
				}
				
				var objUnderMouse = selectObject(mousePos);
				if(mouseObject == MouseObject.NONE) {
					if(selected.isPresent() && objUnderMouse.isPresent() && selected.get().type.isAllowedConnection(objUnderMouse.get().type)) {
						Connection newCon = selected.get().connectTo(objUnderMouse.get());
						if(connections.contains(newCon)) {
							connections.remove(newCon);
						} else {
							connections.add(newCon);
						}
						selected = Optional.empty();
						redraw();
						return;
					}
					
					selected = objUnderMouse;
					redraw();
					return;
				}
				
				if(objUnderMouse.isEmpty()) {
					drawables.add(new DrawObject(mouseObject, mousePos));
					mouseObject = MouseObject.NONE;
					selected = Optional.empty();
					redraw();
				}
			}
		});
		
		KeyAdapter removeSelected = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(selected.isEmpty()) {
					return;
				}
				
				// DEL
				if(e.keyCode == 127) {
					DrawObject toRemove = selected.get();
					Connection con;
					int i = 0;
					while(i < connections.size()) {
						con = connections.get(i);
						if(con.isConnectedTo(toRemove)) {
							connections.remove(con);
							continue;
						}
						i++;
					}
					drawables.remove(toRemove);
					selected = Optional.empty();
				}
				
				// ENTER
				if(e.keyCode == 13) {
					selected = Optional.empty();
				}
				
				if(e.keyCode == 8) {
					selected.get().removeLastChar();
				}
				
				if(Character.isLetter(e.character)) {
					selected.get().addCharacter(e.character);
				}
				redraw();
			}
		};
		// just adding it to the shell doesn't work
		canvas.addKeyListener(removeSelected);
		selectButton.addKeyListener(removeSelected);
		entityButton.addKeyListener(removeSelected);
		relationButton.addKeyListener(removeSelected);
		attributeButton.addKeyListener(removeSelected);
	}
	
	private static void exec(Runnable action) {
		Display.getDefault().asyncExec(action);
	}
	
	private void redraw() {
		exec(() -> canvas.redraw());
	}
	
	private void draw(GC gc, DrawObject obj) {
		if(obj.type == MouseObject.ATTRIBUTE) {
			int[] dimensions = obj.getPolygon();
			gc.drawOval(obj.position.x + obj.type.xOffset, obj.position.y + obj.type.yOffset, dimensions[0], dimensions[1]);
		} else {
			gc.drawPolygon(obj.getPolygon());
		}
		String text = obj.getText();
		gc.drawString(text, obj.position.x - (text.length() * FONT_X_CHARCTER_OFFSET / 2), obj.position.y - FONT_Y_OFFSET, true);
	}
	
	public static int[] movePolygon(int[] polygon, int xOffset, int yOffset) {
		for(int i = 0; i < polygon.length; i++) {
			if((i & 1) == 0) {
				polygon[i] += xOffset;
			} else {
				polygon[i] += yOffset;
			}
		}
		return polygon;
	}
	
	public Optional<DrawObject> selectObject(Point position) {
		for(DrawObject drawObject : drawables) {
//			System.out.printf("looking at %s%n", drawObject);
			Optional<Rectangle> bounds = drawObject.getBounds();
			
			if(bounds.isPresent() && bounds.get().contains(position)) {
//				System.out.printf("found %s with bounds %s at %s%n", drawObject, bounds, position);
				return Optional.of(drawObject);
			}
		}
		return Optional.empty();
	}
}
