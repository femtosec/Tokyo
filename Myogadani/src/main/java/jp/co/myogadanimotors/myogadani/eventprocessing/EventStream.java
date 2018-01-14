package jp.co.myogadanimotors.myogadani.eventprocessing;

import jp.co.myogadanimotors.myogadani.eventprocessing.fill.FillEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.fill.IFillEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.IOrderEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.orderevent.OrderEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.IReportEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.report.ReportEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.strategyinvoker.StrategyInvoker;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerevent.ITimerEventListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerevent.TimerEvent;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration.ITimerRegistrationListener;
import jp.co.myogadanimotors.myogadani.eventprocessing.timerregistration.TimerRegistration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class EventStream implements IEventStream {

    private final Logger logger;
    private final String eventStreamName;

    private final List<IOrderEventListener> orderEventListeners = new ArrayList<>();
    private final List<IReportEventListener> reportEventListeners = new ArrayList<>();
    private final List<IFillEventListener> fillEventListeners = new ArrayList<>();
    private final List<ITimerRegistrationListener> timerRegistrationListeners = new ArrayList<>();
    private final List<ITimerEventListener> timerEventListeners = new ArrayList<>();

    private final BlockingQueue<IEvent> eventQueue = new LinkedBlockingQueue<>();

    private boolean isRunning = false;

    public EventStream(String eventStreamName) {
        logger = LogManager.getLogger(getClass().getName() + "-" + eventStreamName);
        this.eventStreamName = eventStreamName;
    }

    @Override
    public final void put(IEvent event) {
        try {
            if (event == null) {
                logger.info("event is null.");
                return;
            }

            if (logger.getLevel() == Level.TRACE) {
                logger.trace("putting an event. ({})" + event.toString() + ")");
            }

            eventQueue.put(event);
        } catch (InterruptedException e) {
            logger.error(event.toString(), e);
        }
    }

    @Override
    public final int size() {
        return eventQueue.size();
    }

    @Override
    public String getEventStreamName() {
        return eventStreamName;
    }

    @Override
    public final void run() {
        logger.info("starting the event loop.");

        try {
            isRunning = true;
            while (isRunning) {
                IEvent event = eventQueue.take();

                if (logger.getLevel() == Level.TRACE) {
                    logger.trace("taking an event. ({}}", event.toString());
                }

                processEvent(event);
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException", e);
        }

        logger.info("the event loop terminated.");
    }

    private void processEvent(IEvent event) {
        switch (event.getEventType()) {
            case Order:
                processOrderEvent((OrderEvent) event);
                break;

            case Report:
                processReportEvent((ReportEvent) event);
                break;

            case Fill:
                processFillEvent((FillEvent) event);
                break;

            case MarketDataRequest:
                break;

            case MarketData:
                break;

            case TimerRegistration:
                processTimerRegistration((TimerRegistration) event);
                break;

            case TimerEvent:
                processTimerEvent((TimerEvent) event);
                break;

            case FieldUpdate:
                break;

            case StrategyInvocation:
                ((StrategyInvoker) event).invokeStrategy();
                break;

            case EventStreamTermination:
                isRunning = false;
                break;
        }
    }

    private void processOrderEvent(OrderEvent orderEvent) {
        switch (orderEvent.getOrderType()) {
            case New:
                callListeners(orderEventListeners, (listener) -> listener.processOrderNew(orderEvent));
                break;

            case Amend:
                callListeners(orderEventListeners, (listener) -> listener.processOrderAmend(orderEvent));
                break;

            case Cancel:
                callListeners(orderEventListeners, (listener) -> listener.processOrderCancel(orderEvent));
                break;
        }
    }

    private void processReportEvent(ReportEvent reportEvent) {
        switch (reportEvent.getReportType()) {
            case NewAck:
                callListeners(reportEventListeners, (listener) -> listener.processReportNewAck(reportEvent));
                break;

            case NewReject:
                callListeners(reportEventListeners, (listener) -> listener.processReportNewReject(reportEvent));
                break;

            case AmendAck:
                callListeners(reportEventListeners, (listener) -> listener.processReportAmendAck(reportEvent));
                break;

            case AmendReject:
                callListeners(reportEventListeners, (listener) -> listener.processReportAmendReject(reportEvent));
                break;

            case CancelAck:
                callListeners(reportEventListeners, (listener) -> listener.processReportCancelAck(reportEvent));
                break;

            case CancelReject:
                callListeners(reportEventListeners, (listener) -> listener.processReportCancelReject(reportEvent));
                break;

            case UnsolicitedCancel:
                callListeners(reportEventListeners, (listener) -> listener.processReportUnsolicitedCancel(reportEvent));
                break;
        }
    }

    private void processFillEvent(FillEvent fillEvent) {
        callListeners(fillEventListeners, (listener) -> listener.processFillEvent(fillEvent));
    }

    private void processTimerRegistration(TimerRegistration timerRegistration) {
        callListeners(timerRegistrationListeners, (listener) -> listener.processTimerRegistration(timerRegistration));
    }

    private void processTimerEvent(TimerEvent timerEvent) {
        callListeners(timerEventListeners, (listener) -> listener.processTimerEvent(timerEvent));
    }

    private static <T extends IEventListener> void callListeners(List<T> listeners, Consumer<T> consumer) {
        if (listeners == null) return;

        for (T listener : listeners) {
            consumer.accept(listener);
        }
    }

    @Override
    public final void terminate() {
        put(new EventStreamTermination());
    }

    @Override
    public void addOrderEventListener(IOrderEventListener orderEventListener) {
        orderEventListeners.add(orderEventListener);
    }

    @Override
    public void addReportEventListener(IReportEventListener reportEventListener) {
        reportEventListeners.add(reportEventListener);
    }

    @Override
    public void addFillEventListener(IFillEventListener fillEventListener) {
        fillEventListeners.add(fillEventListener);
    }

    @Override
    public void addTimerRegistrationListener(ITimerRegistrationListener timerRegistrationListener) {
        timerRegistrationListeners.add(timerRegistrationListener);
    }

    @Override
    public void addTimerEventListener(ITimerEventListener timerEventListener) {
        timerEventListeners.add(timerEventListener);
    }

    private class EventStreamTermination implements IEvent {

        @Override
        public long getEventId() {
            return 0;
        }

        @Override
        public EventType getEventType() {
            return EventType.EventStreamTermination;
        }

        @Override
        public String getEventSenderName() {
            return null;
        }

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public StringBuilder toStringBuilder() {
            return new StringBuilder().append("EventStreamTermination eventStreamName: ").append(eventStreamName);
        }
    }
}
