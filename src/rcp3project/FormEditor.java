package rcp3project;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import dataModel.Node;
import dataModel.SessionManager;

public class FormEditor extends EditorPart {
	public static String ID = "rcp3project.FormEditor";
	boolean isDirty=false;

	Composite mainComposite;
	Node currentReference = SessionManager.getCurrentRefrence();
	
	String name="Empty value";

	Label labelName, labelGroup, label, labelCity, labelResult, photoLabel;
	Text textName, textGroup, textAddress, textCity, textResult;
    Canvas canvas;
	Image photo;
	String emptyFileName ="src/photos/_noPhoto256x256.png";
		
	public void createPartControl(Composite parent) {
		GridLayout mainCompositeLayout = new GridLayout(7, true);
		mainCompositeLayout.horizontalSpacing = 5;
		mainCompositeLayout.verticalSpacing = 5;
		
		mainComposite = new Composite(parent, SWT.BORDER);
		mainComposite.setLayout(mainCompositeLayout);

		labelName = createLabel(mainComposite, "Name");
		textName = createText(mainComposite,currentReference.getName());
		setPartName(currentReference.getName());
		
		photoLabel = new Label(mainComposite, SWT.BORDER);
		photoLabel.setLayoutData(createPhotoGrid());	
		photoLabel.setImage(convertPhotoForLabel(currentReference.getPhoto()));
			
		labelGroup = createLabel(mainComposite, "Group");
		textGroup = createText(mainComposite, currentReference.getParent().getName());
		
		label = createLabel(mainComposite, "Address");
		textAddress = createText(mainComposite, currentReference.getAddress());
		
		labelCity = createLabel(mainComposite, "City");
		textCity = createText(mainComposite, currentReference.getCity());

		labelResult = createLabel(mainComposite, "Result");
		textResult = createText(mainComposite,String.valueOf(currentReference.getResult()));
	}
	
	public void setFields() {
//		textName.setText(name);
//		textGroup.setText(group);
//		textAddress.setText(address);
//		textCity.setText(city);
//		textResult.setText(String.valueOf(result));
		//photo = img;
	}
	
	public void refreshAll() {
		textName.redraw();
		textGroup.redraw();
		textAddress.redraw();
		textCity.redraw();
		textResult.redraw();
		photoLabel.redraw();	
	}

	@Override
	public void setFocus() {
		textName.setFocus();
	}
	
	private Label createLabel(Composite parent, String strLabel) {
		GridData labelGrid = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		labelGrid.grabExcessHorizontalSpace = true;
		labelGrid.grabExcessVerticalSpace = false;
		labelGrid.horizontalSpan = 1;
		labelGrid.minimumWidth = 30;
		labelGrid.heightHint=22;
		labelGrid.widthHint=60;
		
		Label label = new Label(mainComposite, SWT.NONE);
		label.setText(strLabel);
		label.setLayoutData(labelGrid);
		return label;
	}
	
	private Text createText(Composite parent, String label) {
		GridData textGrid = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_END);
		textGrid.grabExcessHorizontalSpace = true;
		textGrid.grabExcessVerticalSpace = false;
		textGrid.widthHint=120;
		textGrid.heightHint=18;
		textGrid.verticalAlignment = SWT.TOP;
		textGrid.horizontalSpan = 2;
		textGrid.minimumWidth = 60;
		
		Text text = new Text(parent, SWT.BORDER);
		text.setText(label);
		text.setLayoutData(textGrid);
		return text;
	}
	
	private GridData createPhotoGrid() {
		GridData photoGrid = new GridData(GridData.FILL_BOTH);
		photoGrid.grabExcessHorizontalSpace = true;
		photoGrid.grabExcessVerticalSpace = true;
		photoGrid.widthHint=180;
		photoGrid.heightHint=180;
		photoGrid.horizontalSpan = 4;
		photoGrid.verticalSpan = 5;
		photoGrid.minimumWidth = 100;
		return photoGrid;
	}

	private Image convertPhotoForLabel(String fileName) {
		if(fileName==null || fileName.isEmpty() || new File(fileName).isFile()) {
			fileName=emptyFileName;
		}
		
		Image  photo = new Image(photoLabel.getShell().getDisplay(), 
				Application.class.getClassLoader().getResourceAsStream(fileName));
//		int photoWidth = photo.getBounds().width;
//		int photoHeight = photo.getBounds().height;
//	
//		int labelWidth = 256;
//		int labelHeight = 256;
//		float k;
//		if(labelWidth>labelHeight) {
//			k=(Float.valueOf(labelHeight))/(Float.valueOf(photoHeight));
//			System.out.println("k= "+k+"Label H= "+Float.valueOf(labelHeight)+"; photo H= "+Float.valueOf(photoHeight));
//		} else {
//			k=(Float.valueOf(labelWidth))/(Float.valueOf(photoWidth));
//			System.out.println("k= "+k+"Label H= "+Float.valueOf(labelWidth)+"; photo H= "+Float.valueOf(photoWidth));
//		}
		//System.out.println("k= "+k+" w= "+(int)(photoWidth*k)+"; h= "+(int)(photoHeight*k));
		final Image answer = new Image(photoLabel.getShell().getDisplay(),photo.getImageData().scaledTo(256, 256));
		return answer;			
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		 if (!(input instanceof EditorInput)) {
	         throw new PartInitException("Invalid Input: Must be "
	                 + EditorInput.class.getName());
	     }
	     setSite(site);
	     setInput(input);	
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean dirty) {
		this.isDirty=dirty;
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}