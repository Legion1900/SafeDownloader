package com.legion1900.service_lib;

import java.io.File;

public abstract class Downloadable {

    public Downloadable(ArgsContainer container) {
        args = container;
    }

    /*
     * Container for file name and checksum algorithm name
     * */
    public final ArgsContainer args;

    /*
    * whereTo - where file should be downloaded.
    * */
    public abstract void download(File whereTo);

    public static final class ArgsContainer {

        public final String fileName;

        public final String checksumAlg;

        public ArgsContainer(String fileName, String checksumAlg) {
            this.fileName = fileName;
            this.checksumAlg = checksumAlg;
        }
    }
}
