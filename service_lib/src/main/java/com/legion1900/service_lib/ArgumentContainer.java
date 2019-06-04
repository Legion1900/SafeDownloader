package com.legion1900.service_lib;

public abstract class ArgumentContainer {

    /*
    * whereTo - where file should be downloaded.
    * */
    public abstract void download(String whereTo);

    /*
    * Should return preferred checksum algorithm name
    * */
    public abstract String getChecksumAlgorithm();
}
