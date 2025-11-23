package org.example.ez_ide;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleCapture {
    private PrintStream originalOut;
    private PrintStream originalErr;
    private ByteArrayOutputStream baos;

    public void startCapture() {
        originalOut = System.out;
        originalErr = System.err;
        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        System.setErr(ps);
    }

    public String stopCapture() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        String output = baos.toString();
        return output;
    }
}