package toRemove;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class dndTest {
	public static void main(String[] args) {
		/**
		 * Get Node on current target TreeItem item = (TreeItem) event.item; Node node =
		 * (Node) item.getData();
		 */

		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Tree tree = createTree(shell);

// =============================================================================

		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		final DragSource source = new DragSource(tree, operations);
		source.setTransfer(types);
		final TreeItem[] dragSourceItem = new TreeItem[1];
//==============================================================================
		source.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0 && selection[0].getItemCount() == 0) {
					event.doit = true;
					dragSourceItem[0] = selection[0];
				} else {
					event.doit = false;
				}

			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = dragSourceItem[0].getText();
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE)
					dragSourceItem[0].dispose();
				dragSourceItem[0] = null;
			}
		});

// ==============================================================================

		DropTarget target = new DropTarget(tree, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void dragOver(DropTargetEvent event) {
				event.detail = DND.DROP_MOVE;

				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem) event.item;
					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					if (pt.y < bounds.y + bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}

//===================================================
			@Override
			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				String text = (String) event.data;
				if (event.item == null) {
					TreeItem item = new TreeItem(tree, SWT.NONE);
					item.setText(text);
					System.out.println("1st part drag out of tree");
				} else {
					TreeItem item = (TreeItem) event.item;
					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					TreeItem parent = item.getParentItem();
					if (parent != null) {
						TreeItem[] items = parent.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							TreeItem newItem = new TreeItem(parent, SWT.NONE, index);
							newItem.setText(text);
							System.out.println("2nd part if leaf dragged on 1/3 part put before target");
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							TreeItem newItem = new TreeItem(parent, SWT.NONE, index + 1);
							newItem.setText(text);
							System.out.println("3d part if leaf dragged on 3/3 part put after target");
						} else {
							event.detail = DND.DROP_MOVE;
							TreeItem newItem = new TreeItem(item, SWT.NONE);
							newItem.setText(text);
							System.out.println("4th part if dragged on 2/3 DONT create structrure under leaf");
						}

					} else {
						TreeItem[] items = tree.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							TreeItem newItem = new TreeItem(tree, SWT.NONE, index);
							newItem.setText(text);
							System.out.println("5th part");
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							TreeItem newItem = new TreeItem(tree, SWT.NONE, index + 1);
							newItem.setText(text);
							System.out.println("6th part");
						} else {
							event.detail = DND.DROP_NONE;
//							TreeItem newItem = new TreeItem(item, SWT.NONE);
//							newItem.setText(text);
							System.out.println("7th part DONT create structrure under leaf");
						}
					}

				}
			}
		});

		runLoop(shell, display);

	}

	// =====================

	public static Tree createTree(Shell shell) {
		final Tree tree = new Tree(shell, SWT.BORDER);
		for (int i = 0; i < 3; i++) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText("item " + i);
			for (int j = 0; j < 3; j++) {
				TreeItem subItem = new TreeItem(item, SWT.NONE);
				subItem.setText("item " + i + " " + j);
				for (int k = 0; k < 3; k++) {
					TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
					subsubItem.setText("item " + i + " " + j + " " + k);
				}
			}
		}
		return tree;
	}

	public static void runLoop(Shell shell, Display display) {
		shell.setSize(400, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}