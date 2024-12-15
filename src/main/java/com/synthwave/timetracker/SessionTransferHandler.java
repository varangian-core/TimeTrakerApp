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
            e.printStackTrace();
            return false;
        }

        JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();

        // Now we check if it's a RuntimeSession, not a Session
        Object userObject = parentNode.getUserObject();
        if (userObject instanceof RuntimeSession) {
            DefaultTreeModel model = (DefaultTreeModel) ((JTree) support.getComponent()).getModel();

            RuntimeSession runtimeSession = (RuntimeSession) userObject;
            for (Task task : tasks) {
                // Add the task to the runtime session
                runtimeSession.addTask(task);

                // Create a new node for the task and insert it under the session node
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);
                model.insertNodeInto(taskNode, parentNode, parentNode.getChildCount());
            }

            // Notify the model about changes to update the UI
            model.nodeChanged(parentNode);
            return true;
        }

        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList<?> list = (JList<?>) c;
            // Use getSelectedValuesList() to avoid deprecated APIs
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
