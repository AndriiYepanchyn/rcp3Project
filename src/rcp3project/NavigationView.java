package rcp3project;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import dataModel.MyContentProvider2;
import dataModel.MyViewLabelProvider;
import dataModel.Node;
import dataModel.SessionManager;

public class NavigationView extends ViewPart {
	public static final String ID = "rcp3project.NavigationView";
	private TreeViewer viewer;
//	Node session = SessionManager.getSession();
	Node session = Node.makeDummyTree();
	Node currentNode = SessionManager.getCurrentRefrence();
	boolean expandStatus = false;

	@Override
	public void createPartControl(Composite parent) {
		SessionManager.setSession(session);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new MyContentProvider2());
		viewer.setLabelProvider(new MyViewLabelProvider());
		createColumn(viewer.getTree(), "Students");
		viewer.setInput(session);
		viewer.getTree().setHeaderBackground(new Color(120, 120, 120));
		viewer.getTree().setHeaderForeground(new Color(220, 220, 220));
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				currentNode = (Node) selection.getFirstElement();
				SessionManager.setCurrentRefrence(currentNode);
				if (currentNode.isLeaf()) {
					try {
						boolean isEditorAlreadyOpened = false;
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IEditorReference[] eRefs = page.getEditorReferences();
						for (IEditorReference ref : eRefs) {
							FormEditor editor1 = (FormEditor) ref.getEditor(false);
							if (editor1.getEditorsNode().hashCode() == currentNode.hashCode()) {
								page.activate(editor1);
								isEditorAlreadyOpened = true;
								break;
							}
						}
						if (!isEditorAlreadyOpened) {
							IEditorPart newEditorWindow = page.openEditor(new EditorInput(currentNode), FormEditor.ID);
						}
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				currentNode = (Node) selection.getFirstElement();
				SessionManager.setCurrentRefrence(currentNode);
			}
		});

		// MenuManager help to create context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		// set the menu on the SWT widget
		viewer.getTree().setMenu(menu);
		// register the menu with the framework
		getSite().registerContextMenu(menuManager, viewer);

		// make the viewer selection available
		getSite().setSelectionProvider(viewer);
	}

	public void createColumn(Tree tr, String text) {
		TreeColumn column = new TreeColumn(tr, SWT.NONE);
		column.setWidth(300);
		column.setText(text);
		tr.setHeaderVisible(true);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void redrawTree() {
		viewer.getTree().deselectAll();
		session = SessionManager.getSession();
		viewer.setInput(session);
//		if (expandStatus) {
//			viewer.expandAll();
//		} else {
//			viewer.collapseAll();
//		}
		viewer.refresh();
	}

	public Node getCurrentRecord() {
		return currentNode;
	}

//	public void setExpandStatus(boolean status) {
//		this.expandStatus = status;
////		if (expandStatus) {
////			viewer.expandAll();
////			System.out.println("Expanded");
////		} else {
////			
////			System.out.println("Collapsed");
////		}
//
//	}

//	public boolean getExpandStatus() {
//		return this.expandStatus;
//	}

}