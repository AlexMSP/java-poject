package project;

interface IPresenter {
    IModel getModel();

    void setModel(IModel model);

    IView getView();

    void setView(IView view);

    void run();

    void fetch();

    void saveLinksToFile();
}
