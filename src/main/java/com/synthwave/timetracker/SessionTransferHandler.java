package com.synthwave.timetracker;

import com.synthwave.timetracker.model.Task;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.List;

public class SessionTransferHandler extends TransferHandler {
    private final DataFlavor taskFlavor;

    public SessionTransferHandler() {
        taskFlavor = new DataFlavor(Task[].class, "Tasks");
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        return support.isDataFlavorSupported(taskFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        Transferable transferable = support.getTransferable();
        Task[] tasks;
        try {
            tasks = (Task[]) transferable.getTransferData(taskFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (userObject instanceof RuntimeSession) {
            RuntimeSession runtimeSession = (RuntimeSession) userObject;
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            // Add each dropped task to the runtime session and as a child node
            for (Task task : tasks) {
                runtimeSession.addTask(task);
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);
                // Insert the new task node
                model.insertNodeInto(taskNode, parentNode, parentNode.getChildCount());
            }

            // Notify the model that the structure under parentNode may have changed
            model.nodeStructureChanged(parentNode);

            // Optionally expand the parent node to show the newly added tasks
            tree.expandPath(dropLocation.getPath());

            return true;
        }

        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList<?> list = (JList<?>) c;
            // Use getSelectedValuesList() instead of deprecated getSelectedValues()
            List<?> values = list.getSelectedValuesList();
            Task[] tasks = values.toArray(new Task[0]);
            return new TaskTransferable(tasks);
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    private static class TaskTransferable implements Transferable {
        private final Task[] tasks;

        public TaskTransferable(Task[] tasks) {
            this.tasks = tasks;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{new DataFlavor(Task[].class, "Tasks")};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.getRepresentationClass() == Task[].class;
        }

        @Override
        public Task[] getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return tasks;
        }
    }
}
