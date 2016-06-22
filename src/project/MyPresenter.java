package project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Enumeration;

/**
 * Class used as intermediary layer between GUI and model.
 */
class MyPresenter implements IPresenter {

    private IView view;
    private IModel model;

    @Override
    public IModel getModel() {
        return model;
    }

    @Override
    public void setModel(IModel model) {
        this.model = model;
    }

    @Override
    public IView getView() {
        return view;
    }

    @Override
    public void setView(IView view) {
        this.view = view;
    }

    @Override
    /**
     * opens application GUI
     */
    public void run() {
        model.setTreeModel(new DefaultTreeModel(null));
        view.setPresenter(this);
        view.updateViewFromModel();
        view.open();
        view.disableSaveLinksButton();
    }

    @Override
    /**
     * triggers the recursive fetching of links
     */
    public void fetch() {
        String siteURL = view.getSiteURL();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(siteURL);
        model.getTreeModel().setRoot(root);

        view.disableFetchButton();
        SwingUtilities.invokeLater(() -> {
            fetchRecursive(root, siteURL, 0, getModel().getMaxRecursionDepth());
            view.expandTree();
            view.updateViewFromModel();
            JOptionPane.showMessageDialog(null, "Finished retrieving links!");
            view.enableFetchButton();
            view.enableSaveLinkButton();
        });
    }

    @Override
    public void saveLinksToFile() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) (model.getTreeModel().getRoot());
        Enumeration e = root.depthFirstEnumeration();
        try {
            File file = new File(getModel().getSaveLocation() + "/" + "exports.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

            while (e.hasMoreElements()) {
                DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) e.nextElement();
                bufferedWriter.write(String.valueOf(nextNode.getUserObject()));
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            int dialogResult = JOptionPane.showConfirmDialog(null, "Links were saved to file," +
                            " do you want to open that file for inspection?",
                    "Links saved", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (dialogResult == JOptionPane.YES_OPTION) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } else {
                JOptionPane.showMessageDialog(null, "Links saved.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Something went wrong with export.txt."
                            + ex.getMessage(), "Exporting error...",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Uses JSoup to seek links and populate a model accordingly, and follows recursion only of non-external links.
     *
     * @param root         model root node
     * @param siteURL      root URL to begin fetching the links in depth
     * @param currentDepth recursive function current depth, used to stop recursion
     * @param maxDepth     max depth for the spider to fetch links, used to stop recursion
     */
    private void fetchRecursive(DefaultMutableTreeNode root, String siteURL, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            return;
        }
        try {
            Document document = Jsoup.connect(siteURL).get();
            Elements links = document.select("a[href]");

            for (Element link : links) {
                String href = link.attr("abs:href");

                DefaultMutableTreeNode newNode = model.tryAddNode(root, href);

                if (newNode != null) {
                    fetchRecursive(newNode, href, ++currentDepth, maxDepth);
                }
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (MalformedURLException e) {
            model.increaseFailedLinksCount();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "JSoup failed to retrieve site content.", "JSoup exception",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}