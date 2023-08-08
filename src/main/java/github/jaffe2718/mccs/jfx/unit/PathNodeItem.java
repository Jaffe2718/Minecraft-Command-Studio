package github.jaffe2718.mccs.jfx.unit;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * @author jaffe2718
 *  Use this class to create a file tree
 */
public class PathNodeItem {

    private final SimpleStringProperty fileName;
    private final SimpleBooleanProperty isDir; // is Dir or File
    private File mFile;

    /**
     * The icon of the file
     */
    public Node icon;

    /**
     * @param path the path of the file
     */
    public PathNodeItem(String path) {
        this.mFile = new File(path);
        this.fileName = new SimpleStringProperty(this.getName());
        this.isDir = new SimpleBooleanProperty(this.mFile.isDirectory());
        if (this.isDirectory()) {
            icon = new ImageView(new Image("assets/mccs/jfx/textures/directory.png"));
        } else {
            icon = new ImageView(new Image("assets/mccs/jfx/textures/file.png"));
        }
    }

    /**
     * get the name of the file
     * @return the name of the file
     */
    public String getName() {
        return mFile.getName();
    }

    /**
     * get the File object of the file
     * @return the File object of the file
     */
    public File getFile() {
        return this.mFile;
    }

    /**
     * if the file is a directory
     * @return if the file is a directory
     */
    public boolean isDirectory() {
        return this.isDir.get();
    }

    /**
     * get the name of the file
     * @return the name of the file
     */
    @Override
    public String toString() {
        return this.mFile.getName();
    }

    /**
     * set the destination file
     * @param newFile the destination file object
     */
    public void setFile(File newFile) {
        this.mFile = newFile;
        this.fileName.set(this.getName());
        this.isDir.set(this.mFile.isDirectory());
        if (this.isDirectory()) {
            icon = new ImageView(new Image("assets/mccs/jfx/textures/directory.png"));
        } else {
            icon = new ImageView(new Image("assets/mccs/jfx/textures/file.png"));
        }
    }
}
