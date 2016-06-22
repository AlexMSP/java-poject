package project;

interface IView {
    IPresenter getPresenter();

    void setPresenter(IPresenter presenter);

    void updateViewFromModel();

    void open();

    void close();

    void updateModelFromView();

    String getSiteURL();

    void expandTree();

    void disableFetchButton();

    void enableFetchButton();

    void disableSaveLinksButton();

    void enableSaveLinkButton();
}
