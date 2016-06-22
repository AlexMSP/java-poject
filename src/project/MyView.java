package project;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * GUI class for the application
 */
class MyView extends JFrame implements IView {

    private IPresenter presenter;
    private JTextField jURLTextField;
    private JButton jFetchButton;
    private JTree jTree;
    private JButton jSaveBtn;
    private JLabel jLabelFailedLinksCount;
    private JLabel jLabelExternalLinksCount;
    private String aboutMessage = "This is a lab assignment Java application " +
            "that displays all the links of a chosen website.";

    MyView() {
        initComponents();
    }

    /**
     * draws and initializes GUI components
     */
    private void initComponents() {
        setTitle("Full Link Spider Application");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("spider.png")));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());

        JLabel jUrlLabel = new JLabel("site URL: ");
        jURLTextField = new JTextField("http://www.ulbsibiu.ro", 13);
        jFetchButton = new JButton("fetch links");
        jFetchButton.addActionListener(e -> getPresenter().fetch());
        jSaveBtn = new JButton("save links to file");
        jSaveBtn.addActionListener(e -> getPresenter().saveLinksToFile());

        jTree = new JTree();
        jTree.setModel(null);
        jTree.setCellRenderer(new MyTreeCellRenderer());
        jTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = jTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = jTree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            if (selPath != null) {
                                desktop.browse(new URI(selPath.getLastPathComponent().toString()));
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            JOptionPane.showMessageDialog(null, "Invalid address, cannot open site in browser!",
                                    "URL processing error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        JScrollPane jScrollPane = new JScrollPane(jTree);
        jScrollPane.setPreferredSize(new Dimension(600, 400));
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JMenuBar jMenubar = new JMenuBar();
        JMenu jFile = new JMenu("File");
        JMenuItem jClose = new JMenuItem("Close");
        jClose.addActionListener(e -> this.close());
        jFile.add(jClose);
        JMenu jSettings = new JMenu("Settings");
        JMenuItem jChooseSaveLocation = new JMenuItem("Choose save location...");

        jChooseSaveLocation.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(null);
            chooser.setDialogTitle("Where to save the export file?");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                getPresenter().getModel().setSaveLocation(chooser.getSelectedFile());
            }
        });

        JMenuItem jChooseDepth = new JMenuItem("Choose recursive depth...");
        jChooseDepth.addActionListener(e -> {
            String depth = JOptionPane.showInputDialog(null, "What should be the depth of the search?");

            try {
                int maxRecursionDepth = Integer.parseInt(depth);
                getPresenter().getModel().setMaxRecursionDepth(maxRecursionDepth);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please insert a number next time, default value is 3!",
                        "Invalid Entry",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        });
        jSettings.add(jChooseSaveLocation);
        jSettings.add(jChooseDepth);
        JMenu jHelp = new JMenu("Help");
        JMenuItem jAbout = new JMenuItem("About");
        jHelp.add(jAbout);
        jAbout.addActionListener(e -> JOptionPane.showMessageDialog(null, aboutMessage, "What is this?",
                JOptionPane.INFORMATION_MESSAGE));
        jMenubar.add(jFile);
        jMenubar.add(jSettings);
        jMenubar.add(jHelp);

        jLabelFailedLinksCount = new JLabel("total failed links: 0");
        jLabelExternalLinksCount = new JLabel("total external links: 0");
        JPanel linksInfoPanel = new JPanel();
        linksInfoPanel.setLayout(new BoxLayout(linksInfoPanel, BoxLayout.Y_AXIS));
        linksInfoPanel.add(jLabelFailedLinksCount);
        linksInfoPanel.add(jLabelExternalLinksCount);

        getContentPane().add(jUrlLabel);
        getContentPane().add(jURLTextField);
        getContentPane().add(jFetchButton);
        getContentPane().add(jSaveBtn);
        getContentPane().add(jScrollPane);
        getContentPane().add(linksInfoPanel);
        setJMenuBar(jMenubar);
    }

    /**
     * helper method to expand the JTree
     */
    private void expandTree(JTree jTree) {
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    @Override
    public IPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateViewFromModel() {
        jTree.setModel(getPresenter().getModel().getTreeModel());
        jLabelFailedLinksCount.setText("Total failed links: " + String.valueOf(getPresenter().getModel().getFailedLinksCount()));
        jLabelExternalLinksCount.setText("Total external links: " + String.valueOf(getPresenter().getModel().getExternalLinksCount()));
    }

    @Override
    public void open() {
        setSize(620, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);
        updateModelFromView();
    }

    @Override
    public void close() {
        dispose();
    }

    @Override
    public void updateModelFromView() {
        getPresenter().getModel().setTreeModel((DefaultTreeModel) jTree.getModel());
    }

    @Override
    public String getSiteURL() {
        return jURLTextField.getText();
    }

    @Override
    public void expandTree() {
        expandTree(jTree);
    }

    @Override
    public void disableFetchButton() {
        jFetchButton.setEnabled(false);
    }

    @Override
    public void enableFetchButton() {
        jFetchButton.setEnabled(true);
    }

    @Override
    public void disableSaveLinksButton() {
        jSaveBtn.setEnabled(false);
    }

    @Override
    public void enableSaveLinkButton() {
        jSaveBtn.setEnabled(true);
    }


    private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.isLeaf())
                    setIcon(UIManager.getIcon("FileView.computerIcon"));
                else
                    setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("spider.png"))
                            .getScaledInstance(16, 15, Image.SCALE_SMOOTH)));
            }
            return this;
        }
    }
}
