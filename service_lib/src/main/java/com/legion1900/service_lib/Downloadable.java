package com.legion1900.service_lib;

public interface Downloadable {

    /*
    * whereTo - where file should be downloaded.
    * */
    void download(String whereTo);

    /*
    * Should return preferred checksum algorithm name
    * */
    String getChecksumAlgorithm();
}
