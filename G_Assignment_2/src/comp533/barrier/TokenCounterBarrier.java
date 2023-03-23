package comp533.barrier;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class TokenCounterBarrier extends AMapReduceTracer implements Barrier {
    private int barrierCount;
    private int threadCount;

    public TokenCounterBarrier(final int aNumThreads) {
        this.barrierCount = aNumThreads;
        this.threadCount = 0;
        this.traceBarrierCreated(this, aNumThreads);
    }

    public synchronized void barrier() {
        this.threadCount += 1;
        try {
            if (this.threadCount == this.barrierCount) {
                this.notifyAll();
                this.threadCount = 0;
                this.traceBarrierReleaseAll(this.barrierCount, this.barrierCount, this.threadCount);
            } else {
                this.traceBarrierWaitStart(this, this.barrierCount, this.threadCount);
                this.wait();
                this.traceBarrierWaitEnd(this, this.barrierCount, this.threadCount);
            }
        } catch (InterruptedException ex) {
            Tracer.error(ex.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return AMapReduceTracer.BARRIER;
    }
}