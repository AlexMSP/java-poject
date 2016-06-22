package project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Enumeration;

class MyModel implements IModel {

    private int failedLinksCount;
    private int externalLinksCount;
    private DefaultTreeModel treeModel;
    private int maxRecursionDepth;
    private File saveLocation;

    MyModel() {
        maxRecursionDepth = 3;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void setTreeModel(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    @Override
    public DefaultMutableTreeNode tryAddNode(DefaultMutableTreeNode parent, String href) {
        if (href.isEmpty())
            return null;

        String parentHref = (String) parent.getUserObject();
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(href);

        boolean alreadyInTree = containsHref((DefaultMutableTreeNode) treeModel.getRoot(), href);
        boolean isNotExternalUrl = href.startsWith(parentHref);

        if (!alreadyInTree) {
            parent.add(newNode);

            if (isNotExternalUrl) {
                return newNode;
            } else {
                externalLinksCount++;
            }
        }
        
        return null;
    }

    @Override
    public void increaseFailedLinksCount() {
        failedLinksCount++;
    }

    @Override
    public void increaseExternalLinksCount() {
        externalLinksCount++;
    }

    private boolean containsHref(DefaultMutableTreeNode root, String href) {

        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            Object node = e.nextElement();
            if (node.toString().equalsIgnoreCase(href)) {
                return true;
            }
        }
        return false;
    }

    public int getFailedLinksCount() {
        return failedLinksCount;
    }

    public void setFailedLinksCount(int failedLinksCount) {
        this.failedLinksCount = failedLinksCount;
    }

    public int getExternalLinksCount() {
        return externalLinksCount;
    }

    public void setExternalLinksCount(int externalLinksCount) {
        this.externalLinksCount = externalLinksCount;
    }

    @Override
    public void setMaxRecursionDepth(int maxRecursionDepth) {
        this.maxRecursionDepth = maxRecursionDepth;
    }

    @Override
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public File getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(File saveLocation) {
        this.saveLocation = saveLocation;
    }
}
