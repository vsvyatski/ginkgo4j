package com.github.paulcwarren.ginkgo4j.maven;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A ThreadPrintStream replaces the normal {@code System.out} and ensures
 * that output to {@code System.out} goes to a different PrintStream for
 * each thread.  It does this by using ThreadLocal to maintain a
 * PrintStream for each thread.
 */
public class ThreadPrintStream extends PrintStream {

    /**
     * Thread specific storage to hold a PrintStream for each thread
     */
    private final ThreadLocal<PrintStream> out;

    private ThreadPrintStream() {
        super(new ByteArrayOutputStream(0));
        out = new ThreadLocal<>();
    }

    /**
     * Changes {@code System.out} to a {@code ThreadPrintStream} which will
     * send output to a separate file for each thread.
     */
    public static void replaceSystemOut() {

        // Save the existing System.out
        PrintStream console = System.out;

        // Create a ThreadPrintStream and install it as System.out
        ThreadPrintStream threadOut = new ThreadPrintStream();
        System.setOut(threadOut);

        // Use the original System.out as the current thread's System.out
        threadOut.setThreadOut(console);
    }

    /**
     * Returns the PrintStream for the currently executing thread.
     */
    public PrintStream getThreadOut() {
        return this.out.get();
    }

    /**
     * Sets the PrintStream for the currently executing thread.
     */
    public void setThreadOut(PrintStream out) {
        this.out.set(out);
    }

    @Override
    public boolean checkError() {
        return getThreadOut().checkError();
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        getThreadOut().write(buf, off, len);
    }

    @Override
    public void write(int b) {
        getThreadOut().write(b);
    }

    @Override
    public void flush() {
        getThreadOut().flush();
    }

    @Override
    public void close() {
        getThreadOut().close();
    }
}
