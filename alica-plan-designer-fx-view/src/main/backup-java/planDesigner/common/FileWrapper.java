package de.uni_kassel.vs.cn.planDesigner.common;

import java.io.File;

public class FileWrapper {

    private File file;

    public FileWrapper(File file) {
        this.file = file;
    }

    public static FileWrapper wrap(String path) {
        return new FileWrapper(new File(path));
    }

    public File unwrap() {
        return file;
    }

    @Override
    public String toString() {
        String[] split = file.getAbsolutePath().split(File.separator);
        return split[split.length - 1];
    }
}
