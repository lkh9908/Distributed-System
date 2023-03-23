package comp533.joiner;

import gradingTools.comp533s19.assignment0.AMapReduceTracer;
import util.trace.Tracer;

public class TokenCounterJoiner extends AMapReduceTracer implements Joiner {
	private int joinerCount;
    private int finishedCount;
    private int aNumThreads;

    @Override
    public String toString() {
        return AMapReduceTracer.JOINER;
    }
    
    public TokenCounterJoiner(final int newNumThreads) {
        this.joinerCount = newNumThreads;
        this.finishedCount = 0;
        this.traceJoinerCreated(this, newNumThreads);
        this.aNumThreads = newNumThreads;
    }

    public void finished() {
        this.finishedCount += 1;
        this.traceJoinerFinishedTask(this, this.joinerCount, this.finishedCount);
        if (this.finishedCount == this.joinerCount) {
            this.synchronizedNotify();
            //notify();
        }
        this.traceJoinerRelease(this, this.aNumThreads, this.finishedCount);
    }

    public synchronized void join() {
        try {
           this.traceJoinerWaitStart(this, this.joinerCount, this.finishedCount);
//           this.synchronizedWait();
           this.wait();
           this.traceJoinerWaitEnd(this, this.joinerCount, this.finishedCount);
           this.finishedCount = 0;
        } catch (InterruptedException ex){
            Tracer.error(ex.getMessage());
        }

    }
}
