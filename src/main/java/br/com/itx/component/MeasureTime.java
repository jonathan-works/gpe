package br.com.itx.component;

import org.apache.commons.lang3.time.StopWatch;

public final class MeasureTime {
    private StopWatch watch;

    public MeasureTime() {
        watch = new StopWatch();
    }

    public MeasureTime(boolean start) {
        this();
        if (start) {
            start();
        }
    }

    public MeasureTime start() {
        watch.start();
        return this;
    }

    public long stop() {
        watch.stop();
        return watch.getTime();
    }

    public long getTime() {
        return watch.getTime();
    }

    public void reset() {
        watch.reset();
    }

    public void resetAndStart() {
        reset();
        start();
    }

}
