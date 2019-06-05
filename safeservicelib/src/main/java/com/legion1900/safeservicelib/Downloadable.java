package com.legion1900.safeservicelib;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public abstract class Downloadable implements Parcelable{

    public Downloadable(ArgsContainer container) {
        args = container;
    }

    public Downloadable(Parcel in) {
        String fileName = in.readString();
        String checksumAlg = in.readString();
        args = new ArgsContainer(fileName, checksumAlg);
    }

    /*
     * Container for file name and checksum algorithm name
     * */
    public final ArgsContainer args;

    /*
    * whereTo - where file should be downloaded.
    * */
    public abstract void download(File pathOnDevice);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(args.fileName);
        dest.writeString(args.checksumAlg);
    }

    public static final class ArgsContainer {

        public final String fileName;

        public final String checksumAlg;

        public ArgsContainer(String fileName, String checksumAlg) {
            this.fileName = fileName;
            this.checksumAlg = checksumAlg;
        }
    }
}
