package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.eventprocessing.fill.IFillEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.IOrderEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IReportEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerevent.ITimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration.ITimerRegistrationListener;

public interface IEventStream extends Runnable {
    /**
     * puts IEvent to IEventStream
     * this method is called only by IEventSender
     */
    void put(IEvent event);

    /**
     * returns event queue size
     */
    int size();

    /**
     * returns event stream name
     */
    String getEventStreamName();

    /**
     * terminates event stream
     */
    void terminate();

    /**
     * adds order event listener
     */
    void addOrderEventListener(IOrderEventListener orderEventListener);

    /**
     * adds report event listener
     */
    void addReportEventListener(IReportEventListener reportEventListener);

    /**
     * adds fill event listener
     */
    void addFillEventListener(IFillEventListener fillEventListener);

    /**
     * adds timer registration listener
     */
    void addTimerRegistrationListener(ITimerRegistrationListener timerRegistrationListener);

    /**
     * adds timer event listener
     */
    void addTimerEventListener(ITimerEventListener timerEventListener);
}
