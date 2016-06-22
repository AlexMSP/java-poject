package project;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

interface IModel {
    DefaultTreeModel getTreeModel();

    void setTreeModel(DefaultTreeModel treeModel);

    DefaultMutableTreeNode tryAddNode(DefaultMutableTreeNode parent, String href);

    void increaseFailedLinksCount();

    void increaseExternalLinksCount();

    int getFailedLinksCount();

    void setFailedLinksCount(int failedLinksCount);

    int getExternalLinksCount();

    void setExternalLinksCount(int externalLinksCount);

    void setMaxRecursionDepth(int maxRecursionDepth);

    int getMaxRecursionDepth();

    File getSaveLocation();

    void setSaveLocation(File saveLocation);
}