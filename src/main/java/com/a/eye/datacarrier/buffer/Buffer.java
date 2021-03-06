package com.a.eye.datacarrier.buffer;

import com.a.eye.datacarrier.common.AtomicRangeInteger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wusheng on 2016/10/25.
 */
public class Buffer<T> {
    private final Object[]           buffer;
    private       BufferStrategy     strategy;
    private       AtomicRangeInteger index;

    Buffer(int bufferSize, BufferStrategy strategy) {
        buffer = new Object[bufferSize];
        this.strategy = strategy;
        index = new AtomicRangeInteger(0, bufferSize);
    }

    void setStrategy(BufferStrategy strategy) {
        this.strategy = strategy;
    }

    boolean save(T data) {
        int i = index.getAndIncrement();
        if (buffer[i] != null) {
            switch (strategy) {
                case BLOCKING:
                    while (buffer[i] != null) {
                        try {
                            Thread.sleep(1L);
                        } catch (InterruptedException e) {
                        }
                    }
                    break;
                case IF_POSSIBLE:
                    return false;
                case OVERRIDE:
                default:
            }
        }
        buffer[i] = data;
        return true;
    }

    public int getBufferSize() {
        return buffer.length;
    }

    public List<T> obtain(int start, int end) {
        List<T> result = new ArrayList<T>(end - start);
        for (int i = start; i < end; i++) {
            if(buffer[i] != null){
                result.add((T)buffer[i]);
                buffer[i] = null;
            }
        }
        return result;
    }

}
