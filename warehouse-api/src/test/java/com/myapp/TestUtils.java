package com.myapp;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.util.Iterator;
import java.util.StringJoiner;

/**
 * <p>Created by MontolioV on 16.04.18.
 */
public class TestUtils {

    public static void showConstraintViolations(ConstraintViolationException e) throws Exception {
        StringJoiner sj = new StringJoiner("\n");
        Iterator<ConstraintViolation<?>> cvIterator = e.getConstraintViolations().iterator();
        while (cvIterator.hasNext()) {
            ConstraintViolation<?> nextViolation = cvIterator.next();
            sj.add(nextViolation.getMessage());
        }
        throw new Exception(sj.toString(), e);
    }

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
