package com.myapp;

import java.io.*;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class TestUtils {

    public static Object serializationRoutine(Object o) throws IOException, ClassNotFoundException {
        try (PipedInputStream inputStream = new PipedInputStream();
             PipedOutputStream outputStream = new PipedOutputStream(inputStream);
             ObjectOutputStream oos = new ObjectOutputStream(outputStream);
             ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            oos.writeObject(o);
            return ois.readObject();
        }
    }
}
