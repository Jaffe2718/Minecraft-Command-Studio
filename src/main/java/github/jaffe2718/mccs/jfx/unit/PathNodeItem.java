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
 * @date 2023/3/21
 * @description
 *  Use this class to create a file tree
 */
public class PathNodeItem {
    public SimpleStringProperty fileName;
    private SimpleBooleanProperty isDir; // is Dir or File
    private File mFile;

    public Node icon;

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

    public String getName() {
        return mFile.getName();
    }


    public File getFile() {
        return this.mFile;
    }

    public boolean isDirectory() {
        return this.isDir.get();
    }

    @Override
    public String toString() {
        return this.mFile.getName();
    }

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
